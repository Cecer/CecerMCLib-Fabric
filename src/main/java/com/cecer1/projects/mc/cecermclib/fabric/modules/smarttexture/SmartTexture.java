package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture;

import com.cecer1.projects.mc.cecermclib.common.CecerMCLib;
import com.cecer1.projects.mc.cecermclib.common.modules.logger.LoggerModule;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.RenderContext;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.TransformCanvas;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.nslice.NSliceResourceMetadata;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.nslice.NSliceResourceMetadataReader;
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
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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
    
    private IScalableRenderer renderer;
    private ICoordRescaler coordRescaler;

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

    public void draw(MatrixStack matrixStack, int targetWidth, int targetHeight) {
        this.renderer.draw(matrixStack, this.getTexture(), targetWidth, targetHeight);
    }
    public TransformCanvas selectSlot(@NotNull String name, int targetWidth, int targetHeight, RenderContext ctx) {
        if (this.slotsMetadata == null) {
            // No slot metadata, do nothing.
            return ctx.getCanvas().transform().buildTransformation();
        }
        
        SlotsResourceMetadata.Slot slot = this.slotsMetadata.getSlot(name);
        return ctx.getCanvas().transform()
                .translate(
                        this.coordRescaler.scaleX(slot.x, targetWidth, targetHeight),
                        this.coordRescaler.scaleY(slot.y, targetWidth, targetHeight))
                .absoluteResize(
                        slot.x - this.coordRescaler.scaleX(slot.x + slot.width, targetWidth, targetHeight),
                        slot.y - this.coordRescaler.scaleY(slot.y + slot.height, targetWidth, targetHeight))
                .buildTransformation();
    }

    public Set<String> getTagAt(int x, int y, int targetWidth, int targetHeight) {
        if (this.tagsMetadata == null) {
            // No tag metadata, do nothing.
            return Collections.emptySet();
        }
        
        int color = this.tagMapTexture.get().getPixelColor(
                this.coordRescaler.scaleX(x, targetWidth, targetHeight),
                this.coordRescaler.scaleY(y, targetWidth, targetHeight));
        
        return this.tagsMetadata.getTags(color);
    }
    
    public int getSlotMarginX(String name) {
        SlotsResourceMetadata.Slot slot = this.slotsMetadata.getSlot(name);
        return 1000000 - (this.coordRescaler.scaleX(slot.x + slot.width, 1000000, 1) - this.coordRescaler.scaleX(slot.x, 1000000, 1));
    }
    public int getSlotMarginY(String name) {
        SlotsResourceMetadata.Slot slot = this.slotsMetadata.getSlot(name);
        return 1000000 - (this.coordRescaler.scaleY(slot.y + slot.height, 1, 1000000) - this.coordRescaler.scaleY(slot.y, 1, 1000000));
    }
    

    public int getSlotWidth(String name, int totalWidth) {
        SlotsResourceMetadata.Slot slot = this.slotsMetadata.getSlot(name);
        return this.coordRescaler.scaleX(slot.x + slot.width, totalWidth, 1) - this.coordRescaler.scaleX(slot.x, totalWidth, 1);
    }
    public int getSlotHeight(String name, int totalHeight) {
        SlotsResourceMetadata.Slot slot = this.slotsMetadata.getSlot(name);
        return this.coordRescaler.scaleY(slot.y + slot.height, 1, totalHeight) - this.coordRescaler.scaleY(slot.y, 1, totalHeight);
    }
}
