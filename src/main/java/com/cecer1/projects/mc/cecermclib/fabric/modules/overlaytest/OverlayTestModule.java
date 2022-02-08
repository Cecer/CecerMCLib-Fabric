package com.cecer1.projects.mc.cecermclib.fabric.modules.overlaytest;

import com.cecer1.projects.mc.cecermclib.common.environment.AbstractEnvironment;
import com.cecer1.projects.mc.cecermclib.common.modules.IModule;
import com.cecer1.projects.mc.cecermclib.fabric.environment.FabricClientEnvironment;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.fbo.FBO;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.SmartTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public class OverlayTestModule implements IModule {
    
    private FBO fbo;

    private int frameCounter = 0;
    
    @Override
    public boolean isEnvironmentSupported(AbstractEnvironment environment) {
        return environment instanceof FabricClientEnvironment;
    }
    
    @Override
    public void onModuleRegister() {
        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> { 
            Window window = MinecraftClient.getInstance().getWindow();
            this.drawTest(matrixStack, -1, -1, tickDelta, window.getScaledWidth(), window.getScaledHeight());
        });
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.beforeRender(screen).register((ignore, matrixStack, mouseX, mouseY, tickDelta) -> {
                if (MinecraftClient.getInstance().currentScreen == null) {
                    this.drawTest(matrixStack, mouseX, mouseY, tickDelta, scaledWidth, scaledHeight);
                }
            });
        });
    }
    
    public void drawTest(MatrixStack matrices, int mouseX, int mouseY, float tickDelta, int scaledWidth, int scaledHeight) {
        final Identifier textureId = new Identifier("cecermclib", "textures/gui/test/background.png");
        MinecraftClient.getInstance().getTextureManager().getTexture(textureId);

        matrices.translate(5, 0, 0);
        if (true) {
            matrices.translate(0, 5, 0);
            RenderSystem.setShaderTexture(0, textureId);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            DrawableHelper.drawTexture(matrices, 0, 0, 0, 0, 300, 200, 300, 200);
            matrices.translate(0, -5, 0);
        }
        if (true) {
            matrices.translate(0, 210, 0);
            SmartTexture smartTexture = SmartTexture.fromIdentifier(textureId);
            smartTexture.draw(matrices, 300, 200);
            matrices.translate(0, -210, 0);
        }
        if (true) {
            matrices.translate(305, 0, 0);
            this.drawLoadedTextures(matrices, scaledWidth - 260, scaledHeight);
            matrices.translate(-305, 0, 0);
        }
        matrices.translate(-5, 0, 0);
        
        
//        boolean needsRerender = false;
//        if (this.fbo == null) {
//            // We do not have an FBO allocated. We'll need to render to it after we allocate 
//            this.fbo = new FBO(1920, 1080);
//            needsRerender = true;
//        } else if (this.fbo.setSize(1920, 1080) || this.frameCounter % 60 == 0) {
//            // The allocated FBO size changed. We'll need to render to it.
//            needsRerender = true;
//        } else if (this.frameCounter % 60 == 1 && false) {
//            try {
//                Thread.sleep(200);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        
//        if (needsRerender || MinecraftClient.getInstance().player.isSneaking()) {
//            String testText = String.format("FBO{Frame Counter: %d; Timestamp: %d}", this.frameCounter, System.currentTimeMillis());
//            try (FBOSession session = this.fbo.openSession(matrices)) {
//                session.clear();
////                DrawableHelpert.fill(matrices, -5000, -5000, 5000, 5000, 0x10ff0000);
//                DrawableHelper.fill(matrices, 0, 0, 1000, 50, 0x80008000);
//                
//                for (int x = -1000; x <= 1000; x += 50) {
//                    for (int y = -1008; y <= 1000; y += 12) {
//                        DrawableHelper.drawStringWithShadow(matrices, MinecraftClient.getInstance().textRenderer, "\u00a7a" + x + "\u00a7c" + y, x, y, 0xffffffff);
//                    }
//                }
//                RenderSystem.enableBlend();
//                RenderSystem.disableTexture();
//                RenderSystem.defaultBlendFunc();
//            }
//        }
//        this.fbo.draw(matrices, 0, 0);
//        
//        OrderedText testText2 = Text.of(String.format("DIRECT{Frame Counter: %d; Timestamp: %d}", this.frameCounter, System.currentTimeMillis())).asOrderedText();
//        DrawableHelper.drawCenteredTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, testText2, scaledWidth / 2, 35, 0xffffffff);
        
//        if (this.frameCounter % 300 >= 150) {
//            this.drawLoadedTextures(matrices, scaledWidth, scaledHeight);
//        }

        this.frameCounter++;
    }

    private void drawLoadedTextures(MatrixStack matrixStack, int scaledWidth, int scaledHeight) {
        int thumbnailHeight = 50;
        int thumbnailWidth = 100;
        int gap = 4;
        
        int rowSize = (scaledWidth - gap) / (thumbnailWidth + gap);
        int maxToShow = ((scaledHeight - gap) / (thumbnailHeight + gap)) * rowSize;
//        maxToShow = 10;

        for (int i = 0; i < maxToShow; i++) {
            if (GL11.glIsTexture(i)) {
                int x = gap + ((i % rowSize) * (thumbnailWidth + gap));
                int y = gap + ((i / rowSize) * (thumbnailHeight + gap));

                RenderSystem.setShaderTexture(0, i);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                DrawableHelper.drawTexture(matrixStack, x, y, 0, 0, thumbnailWidth, thumbnailHeight, thumbnailWidth, thumbnailHeight);
                DrawableHelper.drawTextWithShadow(matrixStack, MinecraftClient.getInstance().textRenderer, Text.of("#" + i), x, y - gap, 0xffffffff);
            }
        }
    }
}
