package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture;

import com.cecer1.projects.mc.cecermclib.common.CecerMCLib;
import com.cecer1.projects.mc.cecermclib.common.modules.logger.LoggerModule;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.RenderContext;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.TransformCanvas;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.nslice.NSliceResourceMetadata;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.nslice.NSliceResourceMetadataReader;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.slots.Slot;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.slots.SlotsResourceMetadata;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.slots.SlotsResourceMetadataReader;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.tags.TagMapTexture;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.tags.TagResourceMetadata;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.tags.TagResourceMetadataReader;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.utils.renderer.IScalableRenderer;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.utils.renderer.NSliceRenderer;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.utils.renderer.SimpleRenderer;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.utils.rescaler.ICoordRescaler;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.utils.rescaler.NSliceCoordRescaler;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.utils.rescaler.NullCoordRescaler;
import com.google.common.base.Suppliers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

public class SmartTexture {
    
    // <editor-fold desc="[Resource and Texture management]">
    private static final ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
    private static final TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
    // </editor-fold>

    private final Identifier textureIdentifier;
    private final NSliceResourceMetadata nsliceMetadata;
    private final SlotsResourceMetadata slotsMetadata;
    private final TagResourceMetadata tagsMetadata;
    
    private final Supplier<TagMapTexture> tagMapTexture;
    
    private final IScalableRenderer renderer;
    private final ICoordRescaler coordRescaler;
    
    private final int textureWidth;
    private final int textureHeight;

    public static SmartTexture fromIdentifier(Identifier identifier) {
        try {
            return new SmartTexture(identifier);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private SmartTexture(Identifier textureIdentifier) throws IOException {
        this.textureIdentifier = textureIdentifier;

        Resource resource = SmartTexture.resourceManager.getResource(textureIdentifier);
        try {
            int textureWidth, textureHeight;
            try(InputStream inputStream = resourceManager.getResource(textureIdentifier).getInputStream()) {
                BufferedImage image = ImageIO.read(inputStream);
                textureWidth = image.getWidth();
                textureHeight = image.getHeight();
            } catch (IOException e) {
                CecerMCLib.get(LoggerModule.class).getChannel(SmartTexture.class).log("Failed to read texture dimensions for %s. This may cause graphic errors or even crashes.", textureIdentifier);
                e.printStackTrace();
                textureWidth = 0;
                textureHeight = 0;
            }
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            
            this.nsliceMetadata = resource.getMetadata(NSliceResourceMetadataReader.getInstance());
            this.slotsMetadata = resource.getMetadata(SlotsResourceMetadataReader.getInstance());
            this.tagsMetadata = resource.getMetadata(TagResourceMetadataReader.getInstance());
        } catch (Exception e) {
            CecerMCLib.get(LoggerModule.class).getChannel(this.getClass()).log("Failed to load SmartTexture at %s!", textureIdentifier);
            throw e;
        }
        
        String path = textureIdentifier.getPath();
        if (path.endsWith(".png")) {
            path = path.substring(0, path.length() - 4) + ".tagmap.png";
            Identifier tagMapIdentifier = new Identifier(textureIdentifier.getNamespace(), path);
            this.tagMapTexture = Suppliers.memoize(() -> new TagMapTexture(tagMapIdentifier, resourceManager));
        } else {
            this.tagMapTexture = () -> null;
        }
        
        if (this.nsliceMetadata != null) {
            this.renderer = new NSliceRenderer(this.nsliceMetadata); 
            this.coordRescaler = new NSliceCoordRescaler(this.nsliceMetadata);
        } else {
            this.renderer = SimpleRenderer.INSTANCE;
            this.coordRescaler = NullCoordRescaler.INSTANCE;
        }
    }
    
    private AbstractTexture getTexture() {
        return SmartTexture.textureManager.getTexture(this.textureIdentifier);
    }

    public int getTextureWidth() {
        return this.textureWidth;
    }
    public int getTextureHeight() {
        return this.textureHeight;
    }

    public void draw(MatrixStack matrixStack, int targetWidth, int targetHeight) {
        this.renderer.draw(matrixStack, this.getTexture(), targetWidth, targetHeight);
    }


    private TransformCanvas selectSlot(@NotNull String name, RenderContext ctx) {
        if (this.slotsMetadata != null) {
            Slot slot = this.getSlot(name);
            if (slot != null) {
                return this.selectSlot(slot, ctx);
            }
        }

        // No slot metadata or no slot, do nothing.
        return ctx.getCanvas().transform().openTransformation();
    }
    public TransformCanvas selectSlot(@NotNull Slot slot, RenderContext ctx) {
        int width = ctx.getCanvas().getWidth();
        int height = ctx.getCanvas().getHeight();
        
        return ctx.getCanvas().transform()
                .translate(
                        this.coordRescaler.scaleX(slot.x(), width),
                        this.coordRescaler.scaleY(slot.y(), height))
                .absoluteResize(
                        this.coordRescaler.scaleX(slot.width(), width),
                        this.coordRescaler.scaleY(slot.height(), height),
                        true)
                .openTransformation();
    }

    public Set<String> getTagsAt(int x, int y, int targetWidth, int targetHeight) {
        if (this.tagsMetadata == null) {
            // No tag metadata, do nothing.
            return Collections.emptySet();
        }
        
        int color = this.tagMapTexture.get().getPixelColor(
                this.coordRescaler.scaleX(x, targetWidth),
                this.coordRescaler.scaleY(y, targetHeight));
        
        return this.tagsMetadata.getTags(color);
    }
    
    public @Nullable Slot getSlot(String slotName) {
        if (this.slotsMetadata == null) {
            return null;
        }
        return this.slotsMetadata.getSlot(slotName);
    }

//    @Deprecated
//    public int getSlotMarginX(String name) {
//        return this.getSlotMarginX(name, this.minWidth);
//    }
//    @Deprecated
//    public int getSlotMarginY(String name) {
//        return this.getSlotMarginY(name, this.minHeight);
//    }
//
//    @Deprecated
//    public int getSlotMarginX(String name, int totalWidth) {
//        SlotsResourceMetadata.Slot slot = this.slotsMetadata.getSlot(name);
//        return totalWidth - (this.coordRescaler.scaleX(slot.x + slot.width, totalWidth, 1) - this.coordRescaler.scaleX(slot.x, totalWidth, 1));
//    }
//    @Deprecated
//    public int getSlotMarginY(String name, int totalHeight) {
//        SlotsResourceMetadata.Slot slot = this.slotsMetadata.getSlot(name);
//        return totalHeight - (this.coordRescaler.scaleY(slot.y + slot.height, 1, totalHeight) - this.coordRescaler.scaleY(slot.y, 1, totalHeight));
//    }
//
//
//    @Deprecated
//    public int getSlotWidth(String name, int totalWidth) {
//        SlotsResourceMetadata.Slot slot = this.slotsMetadata.getSlot(name);
//        return this.coordRescaler.scaleX(slot.x + slot.width, totalWidth, 1) - this.coordRescaler.scaleX(slot.x, totalWidth, 1);
//    }
//    @Deprecated
//    public int getSlotHeight(String name, int totalHeight) {
//        SlotsResourceMetadata.Slot slot = this.slotsMetadata.getSlot(name);
//        return this.coordRescaler.scaleY(slot.y + slot.height, 1, totalHeight) - this.coordRescaler.scaleY(slot.y, 1, totalHeight);
//    }
}
