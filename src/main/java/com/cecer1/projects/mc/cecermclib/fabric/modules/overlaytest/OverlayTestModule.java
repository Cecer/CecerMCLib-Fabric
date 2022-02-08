package com.cecer1.projects.mc.cecermclib.fabric.modules.overlaytest;

import com.cecer1.projects.mc.cecermclib.common.environment.AbstractEnvironment;
import com.cecer1.projects.mc.cecermclib.common.modules.IModule;
import com.cecer1.projects.mc.cecermclib.fabric.environment.FabricClientEnvironment;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.fbo.FBO;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
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
