package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.fbo;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL33;

class FBOResources {

    /**
     * Handle to the frame buffer resource
     */
    private int frameBufferId;

    /**
     * Handle to the depth buffer resource
     */
    private int depthBufferId;

    /**
     * The ID of the texture that the FBO will render to
     */
    private int textureId = -1;

    /**
     * Width for the current frame buffer, used so we know to regenerate it if the frame size changes
     */
    private int bufferWidth = -1;
    /**
     * Height for the current frame buffer, used so we know to regenerate it if the frame size changes
     */
    private int bufferHeight = -1;

    public int getFrameBufferId() {
        return this.frameBufferId;
    }
    public int getDepthBufferId() {
        return this.depthBufferId;
    }
    public int getTextureId() {
        return this.textureId;
    }
    public int getBufferWidth() {
        return this.bufferWidth;
    }
    public int getBufferHeight() {
        return this.bufferHeight;
    }

    protected FBOResources() {
    }

    protected boolean ensureReady(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("An FBO cannot have zero or negative size");
        }

        if (width != this.bufferWidth || height != this.bufferHeight) {
            // Size changed - Dispose the old resources
            this.dispose();
        }

        if (this.isReady()) {
            // Already ready. Nothing to do.
            return false;
        }

        this.bufferWidth = width;
        this.bufferHeight = height;

        this.textureId = TextureUtil.generateTextureId();
        TextureUtil.prepareImage(this.textureId, 0, this.bufferWidth, this.bufferHeight);

        int restoreFrameBufferId = FBO.getCurrentFrameBufferId();
        int restoreDepthBufferId = FBO.getCurrentDepthBufferId(); // .
        
        this.frameBufferId = GlStateManager.glGenFramebuffers();
        this.depthBufferId = GlStateManager.glGenRenderbuffers(); // .

        GlStateManager._glBindFramebuffer(GL33.GL_FRAMEBUFFER, this.frameBufferId);
        GlStateManager._glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.textureId, 0);

        GlStateManager._glBindRenderbuffer(GL33.GL_RENDERBUFFER, this.depthBufferId); // .
        GlStateManager._glRenderbufferStorage(GL33.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, this.bufferWidth, this.bufferHeight); // .
        GlStateManager._glFramebufferRenderbuffer(GL33.GL_FRAMEBUFFER, GL33.GL_DEPTH_ATTACHMENT, GL33.GL_RENDERBUFFER, this.depthBufferId); // .

        GlStateManager._glBindFramebuffer(GL33.GL_FRAMEBUFFER, restoreFrameBufferId);
        GlStateManager._glBindRenderbuffer(GL33.GL_RENDERBUFFER, restoreDepthBufferId); // .


/*
        int restoreFrameBufferId = FBO.getCurrentFrameBufferId();
        this.frameBufferId = GlStateManager.glGenFramebuffers();
        GlStateManager._glBindFramebuffer(GL33.GL_FRAMEBUFFER, this.frameBufferId);
        GlStateManager._glFramebufferTexture2D(GL33.GL_FRAMEBUFFER, GL33.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.textureId, 0);
        GlStateManager._glBindFramebuffer(GL33.GL_FRAMEBUFFER, restoreFrameBufferId);

        // TODO: Make depth testing optional for improved performance?
        int restoreDepthBufferId = FBO.getCurrentDepthBufferId(); // .
        this.depthBufferId = GlStateManager.glGenRenderbuffers(); // .
        GlStateManager._glBindRenderbuffer(GL33.GL_RENDERBUFFER, this.depthBufferId); // .
        GlStateManager._glRenderbufferStorage(GL33.GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, this.bufferWidth, this.bufferHeight); // .
        GlStateManager._glFramebufferRenderbuffer(GL33.GL_FRAMEBUFFER, GL33.GL_DEPTH_ATTACHMENT, GL33.GL_RENDERBUFFER, this.depthBufferId); // .
        GlStateManager._glBindRenderbuffer(GL33.GL_RENDERBUFFER, restoreDepthBufferId); // .
 */




        return true;
    }

    public void dispose() {
        if (this.frameBufferId != -1) {
            GlStateManager._glDeleteFramebuffers(this.frameBufferId);
            this.frameBufferId = -1;
        }
        if (this.depthBufferId != -1) {
            GlStateManager._glDeleteRenderbuffers(this.depthBufferId);
            this.depthBufferId = -1;
        }
        if (this.textureId != -1) {
            TextureUtil.releaseTextureId(this.textureId);
            this.textureId = -1;
        }
    }

    public boolean isReady() {
        // TODO: Add check for FBO support
//        if (!OpenGlHelper.framebufferSupported) {
//            return false;
//        }

        if (this.frameBufferId == -1) {
            return false;
        }
        if (this.depthBufferId == -1) {
            return false;
        }
        if (this.textureId == -1) {
            return false;
        }
        return true;
    }
}