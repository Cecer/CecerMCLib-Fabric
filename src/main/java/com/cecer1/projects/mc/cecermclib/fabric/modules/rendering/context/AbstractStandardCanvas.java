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
        this.setOpenTrace();
        this.ctx.getMatrixStack().push();
        this.ctx.pushCanvas(this);
        return this;
    }

    /**
     * Unappplies this canvas from the render context.
     */
    @Override
    public void close() {
        this.ctx.popCanvas();
        this.ctx.getMatrixStack().pop();
    }
}
