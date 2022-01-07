package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.transformations;

import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.AbstractCanvas;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.RenderContext;

public class RelativeResizeTransformationCanvas extends AbstractTransformationCanvas {

    private int x;
    private int y;

    public RelativeResizeTransformationCanvas(AbstractCanvas parentCanvas, RenderContext ctx, int x, int y) {
        super(parentCanvas, ctx);
        this.x = x;
        this.y = y;
    }

    @Override
    public int getWidth() {
        return this.getParentCanvas().getWidth() + this.x;
    }
    @Override
    public int getHeight() {
        return this.getParentCanvas().getHeight() + this.y;
    }
}
