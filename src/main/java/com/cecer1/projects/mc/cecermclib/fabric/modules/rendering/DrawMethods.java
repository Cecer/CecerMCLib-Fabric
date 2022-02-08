package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;
import org.lwjgl.opengl.GL11;

public class DrawMethods {

    /**
     * Draws a solid color rectangle with the specified coordinates and color (ARGB format).
     */
    public static void drawSolidRect(MatrixStack matrixStack, int x, int y, int width, int height, int color) {
        int endX = x + width;
        int endY = y + height;

        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x, endY, 0.0f).color(color).next();
        bufferBuilder.vertex(matrix, endX, endY, 0.0f).color(color).next();
        bufferBuilder.vertex(matrix, endX, y, 0.0f).color(color).next();
        bufferBuilder.vertex(matrix, x, y, 0.0f).color(color).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }

    /**
     * Draws a hollow color rectangle with the specified coordinates and color (ARGB format).
     */
    public static void drawHollowRect(MatrixStack matrixStack, int x, int y, int width, int height, int thickness, int color) {
        drawSolidRect(matrixStack, x, y, width, thickness, color); // Top line
        drawSolidRect(matrixStack, x, y+thickness, thickness, height - (thickness * 2), color); // Left line
        drawSolidRect(matrixStack, x, y+height-thickness, width, thickness, color); // Bottom line
        drawSolidRect(matrixStack, x+width-thickness, y+thickness, thickness, height - (thickness * 2), color); // Right line
    }

    public static void drawAlphaTexturedQuad(MatrixStack matrixStack, AbstractTexture texture, int x, int y, int width, int height, float srcX, float srcY, float srcWidth, float srcHeight, float alpha) {
        drawAlphaTexturedQuad(matrixStack, texture.getGlId(), x, y, width, height, srcX, srcY, srcWidth, srcHeight, alpha);
    }
    public static void drawAlphaTexturedQuad(MatrixStack matrixStack, int textureId, int x, int y, int width, int height, float srcX, float srcY, float srcWidth, float srcHeight, float alpha) {
        int color = 0xffffff | (((int)alpha * 255) << 24);
        drawColoredTexturedQuad(matrixStack, textureId, x, y, width, height, srcX, srcY, srcWidth, srcHeight, color);
    }
    public static void drawColoredTexturedQuad(MatrixStack matrixStack, AbstractTexture texture, int x, int y, int width, int height, float srcX, float srcY, float srcWidth, float srcHeight, int color) {
        drawColoredTexturedQuad(matrixStack, texture.getGlId(), x, y, width, height, srcX, srcY, srcWidth, srcHeight, color);
    }
    public static void drawColoredTexturedQuad(MatrixStack matrixStack, int textureId, int x, int y, int width, int height, float srcX, float srcY, float srcWidth, float srcHeight, int color) {
        int x1 = x + width;
        int y1 = y + height;
        
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        
        RenderSystem.setShaderTexture(0, textureId);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(matrix, (float)x, (float)y1, 0).texture(srcX, srcHeight).color(color).next();
        bufferBuilder.vertex(matrix, (float)x1, (float)y1, 0).texture(srcY, srcHeight).color(color).next();
        bufferBuilder.vertex(matrix, (float)x1, (float)y, 0).texture(srcY, srcWidth).color(color).next();
        bufferBuilder.vertex(matrix, (float)x, (float)y, 0).texture(srcX, srcWidth).color(color).next();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
    }
}
