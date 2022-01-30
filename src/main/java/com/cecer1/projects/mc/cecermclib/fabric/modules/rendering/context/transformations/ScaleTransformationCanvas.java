package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.transformations;

import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.AbstractCanvas;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.RenderContext;

public class ScaleTransformationCanvas extends AbstractTransformationCanvas {

    private float scaleFactor;

    public ScaleTransformationCanvas(AbstractCanvas parentCanvas, RenderContext ctx, float scaleFactor) {
        super(parentCanvas, ctx);
        if (scaleFactor <= 0) {
            throw new IllegalArgumentException(String.format("Negative or zero scale factors are not allowed. {scaleFactor=%f}", scaleFactor));
        }
        
        this.scaleFactor = scaleFactor;
    }

    @Override
    public float getTrueScale() {
        return super.getTrueScale() * this.scaleFactor;
    }

    @Override
    public int getWidth() {
        return (int) (this.getParentCanvas().getWidth() / this.scaleFactor);
    }

    @Override
    public int getHeight() {
        return (int) (this.getParentCanvas().getHeight() / this.scaleFactor);
    }

    public void apply(RenderContext ctx) {
        ctx.getMatrixStack().scale(this.scaleFactor, this.scaleFactor, 1);
    }
}
