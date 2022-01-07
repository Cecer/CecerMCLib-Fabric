package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
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

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;

        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix, x, endY, 0.0f).color(f, f1, f2, f3).next();
        bufferBuilder.vertex(matrix, endX, endY, 0.0f).color(f, f1, f2, f3).next();
        bufferBuilder.vertex(matrix, endX, y, 0.0f).color(f, f1, f2, f3).next();
        bufferBuilder.vertex(matrix, x, y, 0.0f).color(f, f1, f2, f3).next();
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
}
