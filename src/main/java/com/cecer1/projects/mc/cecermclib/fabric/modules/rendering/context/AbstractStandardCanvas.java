package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context;

public abstract class AbstractStandardCanvas extends AbstractCanvas implements AutoCloseable {

    protected AbstractStandardCanvas(AbstractCanvas parentCanvas, RenderContext ctx) {
        super(parentCanvas, ctx);
    }

    /**
     * Applies this canvas to the render context.
     * @return This canvas
     */
    public AbstractStandardCanvas open() {
        this.ctx.getMatrixStack().push();
        this.ctx.setCanvas(this);
        return this;
    }

    /**
     * Unappplies this canvas from the render context.
     */
    @Override
    public void close() {
        this.ctx.setCanvas(this.getParentCanvas());
        this.ctx.getMatrixStack().pop();
    }
}
