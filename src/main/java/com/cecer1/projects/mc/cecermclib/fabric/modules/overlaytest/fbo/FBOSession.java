package com.cecer1.projects.mc.cecermclib.fabric.modules.overlaytest.fbo;

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
    private final float correctiveScaleFactor;
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
//        RenderSystem.enableBlend();
//        RenderSystem.blendFunc(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA);
//        RenderSystem.blendEquation(GL21.GL_FUNC_ADD);
        GlStateManager._glBindFramebuffer(GL33.GL_FRAMEBUFFER, this.resources.getFrameBufferId());
        GlStateManager._glBindRenderbuffer(GL33.GL_RENDERBUFFER, this.resources.getDepthBufferId());

        int status = GlStateManager.glCheckFramebufferStatus(GL33.GL_FRAMEBUFFER);
        if (status != GL33.GL_FRAMEBUFFER_COMPLETE) {
            this.close();
            throw new RuntimeException("Framebuffer creation incomplete! Unsupported graphics card or drivers? (Status code: " + status + ")");
        }

//        this.correctiveScaleFactor = 1.0f;
        this.correctiveScaleFactor = 1.0f / currentScale;
        int height = MinecraftClient.getInstance().getWindow().getHeight();
        this.matrixStack.scale(this.correctiveScaleFactor, this.correctiveScaleFactor, 1.0f);
//        this.matrixStack.translate(0, height, 0);
//        this.matrixStack.translate(0, -this.resources.getBufferHeight(), 0);
//        if (height <= this.resources.getBufferHeight()) {
//            this.matrixStack.translate(0, 10, 0);
//        }
//        this.matrixStack.translate(0, (this.resources.getBufferHeight() - height) * 0.5, 0);
//        this.matrixStack.translate(0, height, 0);
//        this.matrixStack.translate(0, this.resources.getBufferHeight(), 0);
//        this.matrixStack.translate(0, 11, 0);

        RenderSystem.viewport(0, 0, this.resources.getBufferWidth(), this.resources.getBufferHeight());
    }

    public FBOSession clear() {
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);
        return this;
    }

    @Override
    public void close() {
        this.matrixStack.scale(1f/this.correctiveScaleFactor, 1f/this.correctiveScaleFactor, 1f);
        int height = MinecraftClient.getInstance().getWindow().getHeight();
//        this.matrixStack.translate(0, -height, 0);
//        this.matrixStack.translate(0, this.resources.getBufferHeight(), 0);
//        if (height <= this.resources.getBufferHeight()) {
//            this.matrixStack.translate(0, -10, 0);
//        }
//        this.matrixStack.translate(0, -((this.resources.getBufferHeight() - height)) * 0.5, 0);
//        this.matrixStack.translate(0, -height, 0);
//        this.matrixStack.translate(0, -11, 0);
        GlStateManager._glBindFramebuffer(GL33.GL_FRAMEBUFFER, this.restoreFrameBufferId);
        GlStateManager._glBindRenderbuffer(GL33.GL_RENDERBUFFER, this.restoreDepthBufferId);
    }
}