package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.transformations;

import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.AbstractCanvas;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.RenderContext;

public class RelativeResizeTransformationCanvas extends AbstractTransformationCanvas {

    private int x;
    private int y;

    public RelativeResizeTransformationCanvas(AbstractCanvas parentCanvas, RenderContext ctx, int deltaX, int deltaY, boolean allowGrowth) {
        super(parentCanvas, ctx);
        if (!allowGrowth && (deltaX > 0 || deltaY > 0)) {
            throw new IllegalArgumentException(String.format("Growing of the canvas is not allowed. {parentWidth=%d; parentHeight=%d; deltaX=%d; deltaY=%d}",
                    this.getParentCanvas().getWidth(),
                    this.getParentCanvas().getHeight(),
                    deltaX, deltaY));
        }
        
        this.x = deltaX;
        this.y = deltaY;
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
