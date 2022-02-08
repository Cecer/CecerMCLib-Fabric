package com.cecer1.projects.mc.cecermclib.fabric.modules.smarttexture.utils.renderer;

import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.DrawMethods;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;

public class SimpleRenderer implements IScalableRenderer {
    
    public static SimpleRenderer INSTANCE = new SimpleRenderer();
    
    private SimpleRenderer() {}

    @Override
    public void draw(MatrixStack matrixStack, AbstractTexture texture, int targetWidth, int targetHeight) {
        this.draw(matrixStack, texture, targetWidth, targetHeight, 1);;
    }

    @Override
    public void draw(MatrixStack matrixStack, AbstractTexture texture, int targetWidth, int targetHeight, float alpha) {
        int targetX = 0;
        int targetY = 0;;

        DrawMethods.drawColoredTexturedQuad(matrixStack, texture,
                targetX, targetY,
                targetWidth, targetHeight,
                0, 0, 1, 1,
                ~(~((int)alpha * 255) << 24)); // To ARGB with RGB all being 1
    }
}
