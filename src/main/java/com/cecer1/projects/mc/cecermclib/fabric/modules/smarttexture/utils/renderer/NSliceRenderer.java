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


        for (int row = 0; row < rowHeights.sizesPixels.length; row++) {
            srcX = 0.0f;
            targetX = 0;

            for (int column = 0; column < columnWidths.sizesPixels.length; column++) {
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

                srcX += columnWidths.sizesNormalized[column];
                targetX += columnWidths.sizesPixels[column];
            }
            srcY += rowHeights.sizesNormalized[row];
            targetY += rowHeights.sizesPixels[row];
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
        int x2 = x + columnWidths.sizesPixels[columnIndex];
        int y2 = y + rowHeights.sizesPixels[rowIndex];

        float u = srcX;
        float v = srcY;
        float u2 = u + columnWidths.sizesNormalized[columnIndex];
        float v2 = v + rowHeights.sizesNormalized[rowIndex];

        switch (columnMetadata.growBehaviour) {
            case NONE: {
                if (columnWidths.sizesPixels[columnIndex] > columnMetadata.size) {
                    // If this code is reached then the target size is larger than the column configuration.
                    // In order to ensure the texture is drawn exactly as in the texture (which is the whole point of NONE), we
                    //   recalculate u2 and x2 as if the source and target sizes were set to the same value as the column configuration.
                    // Essentially we perform a Math.min on the source and target widths to ensure they are not larger than the configuration.
                    u2 = u + columnWidths.sizesNormalized[columnIndex];
                    x2 = x + columnMetadata.size;
                }
                break;
            }
            case REPEAT: {
                // TODO: Use GL_REPEAT which'll require splitting textures or simply draw multiple times.
                // GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                throw new UnsupportedOperationException("Not implemented yet!");
            }
            case STRETCH: {
                if (columnWidths.sizesPixels[columnIndex] > columnMetadata.size) {
                    // If this code is reached then the target size is larger than the column configuration.
                    // In order to ensure that only the pixels from this column's area of the texture are drawn, we
                    //   recalculate u2 as if the source and target sizes were set to the same value as the column configuration.
                    // Essentially we perform a Math.min on the source width to ensure it is not larger than the configuration.
                    //
                    // This differs from NONE in that we do not change the target width. This causes the source texture
                    //   to be stretched out over a larger target area.

                    u2 = u + columnWidths.sizesNormalized[columnIndex];
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown column GrowBehaviour: " + columnMetadata.growBehaviour);
            }
        }

        switch (rowMetadata.growBehaviour) {
            case NONE: {
                if (rowHeights.sizesPixels[rowIndex] > rowMetadata.size) {
                    // If this code is reached then the target size is larger than the row configuration.
                    // In order to ensure the texture is drawn exactly as in the texture (which is the whole point of NONE), we
                    //   recalculate v2 and y2 as if the source and target sizes were set to the same value as the row configuration.
                    // Essentially we perform a Math.min on the source and target height to ensure they are not larger than the configuration.
                    v2 = v + rowHeights.sizesNormalized[rowIndex];
                    y2 = y + rowMetadata.size;
                }
                break;
            }
            case REPEAT: {
                // TODO: Use GL_REPEAT which may require splitting textures or simply draw multiple times.
                // GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                throw new UnsupportedOperationException("Not implemented yet!");
            }
            case STRETCH: {
                if (rowHeights.sizesPixels[rowIndex] > rowMetadata.size) {
                    // If this code is reached then the target size is larger than the row configuration.
                    // In order to ensure that only the pixels from this row's area of the texture are drawn, we
                    //   recalculate v2 as if the source and target sizes were set to the same value as the row configuration.
                    // Essentially we perform a Math.min on the source height to ensure it is not larger than the configuration.
                    //
                    // This differs from NONE in that we do not change the target height. This causes the source texture
                    //   to be stretched out over a larger target area.

                    v2 = v + rowHeights.sizesNormalized[rowIndex];
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown row GrowBehaviour: " + rowMetadata.growBehaviour);
            }
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
