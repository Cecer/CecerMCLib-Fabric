package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.utils.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class SimpleRenderer implements IScalableRenderer {
    
    public static SimpleRenderer INSTANCE = new SimpleRenderer();
    
    private SimpleRenderer() {}

    @Override
    public void draw(MatrixStack matrixStack, AbstractTexture texture, int targetWidth, int targetHeight) {
        this.draw(matrixStack, texture, targetWidth, targetHeight, 1);;
    }

    @Override
    public void draw(MatrixStack matrixStack, AbstractTexture texture, int targetWidth, int targetHeight, float alpha) {
        int x = 0;
        int y = 0;
        int x2 = x + targetWidth;
        int y2 = y + targetHeight;

        float u = 0.0f;
        float v = 0.0f;
        float u2 = 1.0f;
        float v2 = 1.0f;
        
        // GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); // From 1.8.9
        texture.bindTexture();
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(matrix, x, y2, 0).texture(u, v2).color(1.0f, 1.0f, 1.0f, alpha).next();
        bufferBuilder.vertex(matrix, x2, y2, 0).texture(u2, v2).color(1.0f, 1.0f, 1.0f, alpha).next();
        bufferBuilder.vertex(matrix, x2, y, 0).texture(u2, v).color(1.0f, 1.0f, 1.0f, alpha).next();
        bufferBuilder.vertex(matrix, x, y, 0).texture(u, v).color(1.0f, 1.0f, 1.0f, alpha).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }
}
