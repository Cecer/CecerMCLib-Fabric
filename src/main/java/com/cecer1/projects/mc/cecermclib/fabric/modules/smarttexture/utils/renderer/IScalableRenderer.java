package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.utils.renderer;

import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;

public interface IScalableRenderer {
    void draw(MatrixStack matrixStack, AbstractTexture texture, int targetWidth, int targetHeight);
    void draw(MatrixStack matrixStack, AbstractTexture texture, int targetWidth, int targetHeight, float alpha);
}
