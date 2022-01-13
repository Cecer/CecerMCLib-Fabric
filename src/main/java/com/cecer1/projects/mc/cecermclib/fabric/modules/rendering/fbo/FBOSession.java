package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.fbo;

import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.RenderContext;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

public class FBOSession implements AutoCloseable {

    private final int restoreFrameBufferId;
    private final int restoreDepthBufferId;

    private final FBO fbo;
    private final FBOResources resources;
    private final float correctiveScaleFactorX;
    private final float correctiveScaleFactorY;
    private final MatrixStack matrixStack;

    protected FBOSession(FBO fbo, FBOResources resources, @NotNull RenderContext ctx) {
        this(fbo, resources, ctx.getMatrixStack(), ctx.getCanvas().getTrueScale());
    }
    protected FBOSession(FBO fbo, FBOResources resources, MatrixStack matrixStack) {
        this(fbo, resources, matrixStack, (float) MinecraftClient.getInstance().getWindow().getScaleFactor());
    }
    private FBOSession(FBO fbo, FBOResources resources, MatrixStack matrixStack, float currentScale) {
        this.fbo = fbo;
        this.resources = resources;
        this.matrixStack = matrixStack;

        this.restoreFrameBufferId = FBO.getCurrentFrameBufferId();
        this.restoreDepthBufferId = FBO.getCurrentDepthBufferId();

        System.out.printf("Frame: %d => %d%n", this.restoreFrameBufferId, this.resources.getFrameBufferId());
        System.out.printf("Depth: %d => %d%n", this.restoreDepthBufferId, this.resources.getDepthBufferId());
        System.out.println();
        GlStateManager._glBindFramebuffer(GL33.GL_FRAMEBUFFER, this.resources.getFrameBufferId());
        GlStateManager._glBindRenderbuffer(GL33.GL_RENDERBUFFER, this.resources.getDepthBufferId());

        int status = GlStateManager.glCheckFramebufferStatus(GL33.GL_FRAMEBUFFER);
        if (status != GL33.GL_FRAMEBUFFER_COMPLETE) {
            this.close();
            throw new RuntimeException("Framebuffer creation incomplete! Unsupported graphics card or drivers? (Status code: " + status + ")");
        }

        this.correctiveScaleFactorX = 1.0f / currentScale * MinecraftClient.getInstance().getWindow().getFramebufferWidth() / this.resources.getBufferWidth();
        this.correctiveScaleFactorY = 1.0f / currentScale * MinecraftClient.getInstance().getWindow().getFramebufferHeight() / this.resources.getBufferHeight();
        GL11.glViewport(0, 0, this.resources.getBufferWidth(), this.resources.getBufferHeight());
        this.matrixStack.scale(this.correctiveScaleFactorX, this.correctiveScaleFactorY, 1.0f);
    }

    public FBOSession clear() {
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        return this;
    }

    @Override
    public void close() {
        this.matrixStack.scale(1f/this.correctiveScaleFactorX, 1f/this.correctiveScaleFactorY, 1f);
        GL11.glViewport(0, 0, MinecraftClient.getInstance().getWindow().getFramebufferWidth(), MinecraftClient.getInstance().getWindow().getFramebufferHeight());
        GlStateManager._glBindFramebuffer(GL33.GL_FRAMEBUFFER, this.restoreFrameBufferId);
        GlStateManager._glBindRenderbuffer(GL33.GL_RENDERBUFFER, this.restoreDepthBufferId);
    }
}