package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.utils.renderer;

import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.nslice.NSliceResourceMetadata;
import com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.utils.rescaler.ScaledDivisionData;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class NSliceRenderer implements IScalableRenderer {
    private final NSliceResourceMetadata metadata;

    public NSliceRenderer(NSliceResourceMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public void draw(MatrixStack matrixStack, AbstractTexture texture, int targetWidth, int targetHeight) {
        this.draw(matrixStack, texture, targetWidth, targetHeight, 1);
    }
    
    @Override
    public void draw(MatrixStack matrixStack, AbstractTexture texture, int targetWidth, int targetHeight, float alpha) {
        ScaledDivisionData columnWidths = ScaledDivisionData.calculate(targetWidth, this.metadata.getColumns());
        ScaledDivisionData rowHeights = ScaledDivisionData.calculate(targetHeight, this.metadata.getRows());

        float srcX = 0.0f;
        float srcY = 0.0f;
        int targetX = 0;
        int targetY = 0;


        for (int row = 0; row < rowHeights.targetSizesPixels.length; row++) {
            srcX = 0.0f;
            targetX = 0;

            for (int column = 0; column < columnWidths.targetSizesPixels.length; column++) {
                this.drawSlice(
                        matrixStack,
                        
                        // Source texture
                        texture,

                        // Source offsets
                        srcX, srcY,

                        // Target offsets
                        targetX, targetY,

                        // Segment indexes
                        column, row,

                        // Segment sizes
                        columnWidths, rowHeights,

                        // Raw metadata
                        metadata.getColumns()[column], metadata.getRows()[row],
                        
                        // Alpha value
                        alpha);

                srcX += columnWidths.sourceSizesNormalized[column];
                targetX += columnWidths.targetSizesPixels[column];
            }
            srcY += rowHeights.sourceSizesNormalized[row];
            targetY += rowHeights.targetSizesPixels[row];
        }
    }

    private void drawSlice(
            MatrixStack matrixStack,
            AbstractTexture texture,
            float srcX, float srcY,
            int targetX, int targetY,
            int columnIndex, int rowIndex,
            ScaledDivisionData columnWidths, ScaledDivisionData rowHeights,
            NSliceResourceMetadata.Slice columnMetadata, NSliceResourceMetadata.Slice rowMetadata,
            float alpha) {


        int x = targetX;
        int y = targetY;
        int x2 = x + columnMetadata.size;
        int y2 = y + rowMetadata.size;

        float u = srcX;
        float v = srcY;
        float u2 = u + columnWidths.sourceSizesNormalized[columnIndex];
        float v2 = v + rowHeights.sourceSizesNormalized[rowIndex];

        switch (columnMetadata.growBehaviour) {
            case NONE -> {
                if (columnWidths.targetSizesPixels[columnIndex] < columnMetadata.size) {
                    // If the target size is smaller than the source size then we crop the source size so that it fits
                    u2 *= ((float) columnWidths.targetSizesPixels[columnIndex] / columnMetadata.size);
                }
            }
            // TODO: Use GL_REPEAT which'll require splitting textures or simply draw multiple times.
            // GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            case REPEAT -> throw new UnsupportedOperationException("Not implemented yet!");
            case STRETCH -> {
                x2 = x + columnWidths.targetSizesPixels[columnIndex];
            }
            default -> throw new IllegalArgumentException("Unknown column GrowBehaviour: " + columnMetadata.growBehaviour);
        }

        switch (rowMetadata.growBehaviour) {
            case NONE -> {
                if (rowHeights.targetSizesPixels[rowIndex] < rowMetadata.size) {
                    // If the target size is smaller than the source size then we crop the source size so that it fits
                    v2 *= ((float) rowHeights.targetSizesPixels[rowIndex] / rowMetadata.size);
                }
            }
            // TODO: Use GL_REPEAT which may require splitting textures or simply draw multiple times.
            // GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
            case REPEAT -> throw new UnsupportedOperationException("Not implemented yet!");
            case STRETCH -> {
                y2 = y + rowHeights.targetSizesPixels[rowIndex];
            }
            default -> throw new IllegalArgumentException("Unknown row GrowBehaviour: " + rowMetadata.growBehaviour);
        }
        
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
