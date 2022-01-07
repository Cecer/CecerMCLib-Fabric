package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context;

public abstract class AbstractCanvas {

    protected final AbstractCanvas parentCanvas;
    public AbstractCanvas getParentCanvas() {
        return this.parentCanvas;
    }

    protected final RenderContext ctx;

    protected AbstractCanvas(AbstractCanvas parentCanvas, RenderContext ctx) {
        this.parentCanvas = parentCanvas;
        this.ctx = ctx;
    }

    public TransformCanvasBuilder transform() {
        return new TransformCanvasBuilder(this, this.ctx);
    }

    // <editor-fold desc="True/unscaled properties">
    public float getTrueScale() {
        return this.getParentCanvas().getTrueScale();
    }

    public int getTrueX() {
        return (int) (this.getParentCanvas().getTrueX() + (this.getParentCanvas().getTrueScale() * this.getRelativeX()));
    }
    public int getTrueY() {
        return (int) (this.getParentCanvas().getTrueY() + (this.getParentCanvas().getTrueScale() * this.getRelativeY()));
    }
    public int getTrueWidth() {
        return (int) (this.getTrueScale() * this.getWidth());
    }
    public int getTrueHeight() {
        return (int) (this.getTrueScale() * this.getHeight());
    }
    // </editor-fold>

    public int getRelativeX() {
        return 0;
    }
    public int getRelativeY() {
        return 0;
    }
    public int getWidth() {
        return this.parentCanvas.getWidth();
    }
    public int getHeight() {
        return this.parentCanvas.getHeight();
    }

    public boolean isAbsoluteTrueCoordsWithin(int x, int y) {
        int x1 = this.getTrueX();
        int y1 = this.getTrueY();
        int x2 = this.getTrueX() + this.getTrueWidth();
        int y2 = this.getTrueY() + this.getTrueHeight();

        return (x >= x1 && y >= y1 && x <= x2 && y <= y2);
    }
}

