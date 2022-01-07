package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.transformations;

import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.AbstractCanvas;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.AbstractStandardCanvas;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.RenderContext;

public class TranslateTransformationCanvas extends AbstractTransformationCanvas {

    private int x;
    private int y;

    public TranslateTransformationCanvas(AbstractCanvas parentCanvas, RenderContext ctx, int x, int y) {
        super(parentCanvas, ctx);
        this.x = x;
        this.y = y;
    }
    
    @Override
    public int getRelativeX() {
        return this.x;
    }

    @Override
    public int getRelativeY() {
        return this.y;
    }

    @Override
    public void apply() {
        this.ctx.getMatrixStack().translate(this.x, this.y, 0);
    }
}
