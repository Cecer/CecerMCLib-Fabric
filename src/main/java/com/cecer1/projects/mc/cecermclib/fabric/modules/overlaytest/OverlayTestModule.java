package com.cecer1.projects.mc.cecermclib.fabric.modules.overlaytest;

import com.cecer1.projects.mc.cecermclib.common.environment.AbstractEnvironment;
import com.cecer1.projects.mc.cecermclib.common.modules.IModule;
import com.cecer1.projects.mc.cecermclib.fabric.environment.FabricClientEnvironment;
import com.cecer1.projects.mc.cecermclib.fabric.modules.overlaytest.fbo.FBO;
import com.cecer1.projects.mc.cecermclib.fabric.modules.overlaytest.fbo.FBOSession;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

public class OverlayTestModule implements IModule {
    
//    private Framebuffer framebuffer;
    private FBO fbo;
    
    @Override
    public boolean isEnvironmentSupported(AbstractEnvironment environment) {
        return environment instanceof FabricClientEnvironment;
    }
    
//    private Framebuffer getFramebuffer(int width, int height) {
//        if (this.framebuffer == null) {
//            this.framebuffer = new Framebuffer(width, height, false, MinecraftClient.IS_SYSTEM_MAC);
//            return this.framebuffer;
//        }
//        
//        if (this.framebuffer.viewportWidth != width || this.framebuffer.viewportHeight != height) {
//            this.framebuffer.resize(width, height, MinecraftClient.IS_SYSTEM_MAC);
//            return this.framebuffer;
//        }
//        
//        return this.framebuffer;
//    }
    
    private int frameCounter = 0;
    
    @Override
    public void onModuleRegistered() {
        HudRenderCallback.EVENT.register((matrices, tickDelta) -> { 
            Window window = MinecraftClient.getInstance().getWindow();
            this.drawTest(matrices, -1, -1, tickDelta, window.getScaledWidth(), window.getScaledHeight());
        });
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.beforeRender(screen).register((ignore, matrices, mouseX, mouseY, tickDelta) -> {
                if (MinecraftClient.getInstance().currentScreen == null) {
                    this.drawTest(matrices, mouseX, mouseY, tickDelta, scaledWidth, scaledHeight);
                }
            });
//            ScreenEvents.afterRender(screen).register((ignore, matrices, mouseX, mouseY, tickDelta) -> {
//                this.drawTest(matrices, mouseX, mouseY, tickDelta, scaledWidth, scaledHeight, true);
//            });
        });
    }
    
    public void drawTest(MatrixStack matrices, int mouseX, int mouseY, float tickDelta, int scaledWidth, int scaledHeight) {
        boolean needsRerender = false;
        if (this.fbo == null) {
            // We do not have an FBO allocated. We'll need to render to it after we allocate 
            this.fbo = new FBO(1920, 1080);
            needsRerender = true;
        } else if (this.fbo.setSize(1920, 1080) || this.frameCounter % 60 == 0) {
            // The allocated FBO size changed. We'll need to render to it.
            needsRerender = true;
        } else if (this.frameCounter % 60 == 1) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        if (needsRerender) {
            String testText = String.format("FBO{Frame Counter: %d; Timestamp: %d}", this.frameCounter, System.currentTimeMillis());
            try (FBOSession session = this.fbo.openSession(matrices)) {
                session.clear();
//                DrawableHelper.fill(matrices, -5000, -5000, 5000, 5000, 0x10ff0000);
//                DrawableHelper.fill(matrices, 0, 0, 1000, 50, 0x1000ff00);
                
                for (int x = -1000; x <= 1000; x += 50) {
                    for (int y = -1008; y <= 1000; y += 12) {
                        DrawableHelper.drawStringWithShadow(matrices, MinecraftClient.getInstance().textRenderer, "\u00a7a" + x + "\u00a7c" + y, x, y, 0xffffffff);
                    }
                }
//                for (int i = -50; i < 50; i++) {
//                    DrawableHelper.drawStringWithShadow(matrices, MinecraftClient.getInstance().textRenderer, i + " " + testText, 0, i * 11, 0xffffffff);
//                }
            }
        }

        this.fbo.draw(matrices, 0, 0);
        
        OrderedText testText2 = Text.of(String.format("DIRECT{Frame Counter: %d; Timestamp: %d}", this.frameCounter, System.currentTimeMillis())).asOrderedText();
        DrawableHelper.drawCenteredTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, testText2, scaledWidth / 2, 35, 0xffffffff);
        
//        if (this.frameCounter % 300 >= 150) {
//            this.drawLoadedTextures(matrices, scaledWidth, scaledHeight);
//        }

        this.frameCounter++;
    }

    private void drawLoadedTextures(MatrixStack matrices, int scaledWidth, int scaledHeight) {
        int thumbnailHeight = 45;
        int thumbnailWidth = 80;
        int gap = 10;

        int rowSize = (scaledWidth - gap) / (thumbnailWidth + gap);
        int maxToShow = ((scaledHeight - gap) / (thumbnailHeight + gap)) * rowSize;
//        maxToShow = 10;

        for (int i = 0; i < maxToShow; i++) {
            if (GL11.glIsTexture(i)) {
                int x = gap + ((i % rowSize) * (thumbnailWidth + gap));
                int y = gap + ((i / rowSize) * (thumbnailHeight + gap));

                DrawableHelper.drawTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, Text.of("#" + i), (int) x, (int) y - gap, 0xffffffff);
                RenderSystem.setShaderTexture(0, i);
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                DrawableHelper.drawTexture(matrices, x, y, 0, 0, thumbnailWidth, thumbnailHeight, thumbnailWidth, thumbnailHeight);
            }
        }
    }
}
