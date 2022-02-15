package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context;

import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.transformations.AbstractTransformationCanvas;

/**
 * Acts as a kind of compound canvas to reduce the number of GL state changes
 */
public class TransformCanvas extends AbstractStandardCanvas {

    private final AbstractTransformationCanvas[] transformations;

    public TransformCanvas(AbstractCanvas parentCanvas, AbstractTransformationCanvas[] transformations, RenderContext ctx) {
        super(parentCanvas, ctx);
        this.transformations = transformations;
    }

    private AbstractCanvas getLastTransformationOrParent() {
        return this.transformations.length == 0 ? this.parentCanvas : this.transformations[transformations.length - 1];
    }

    @Override
    public float getTrueScale() {
        return this.getLastTransformationOrParent().getTrueScale();
    }

    @Override
    public int getTrueX() {
        return this.getLastTransformationOrParent().getTrueX();
    }

    @Override
    public int getTrueY() {
        return this.getLastTransformationOrParent().getTrueY();
    }

    @Override
    public int getTrueWidth() {
        return this.getLastTransformationOrParent().getTrueWidth();
    }

    @Override
    public int getTrueHeight() {
        return this.getLastTransformationOrParent().getTrueHeight();
    }

    @Override
    public int getRelativeX() {
        return this.getLastTransformationOrParent().getRelativeX();
    }

    @Override
    public int getRelativeY() {
        return this.getLastTransformationOrParent().getRelativeY();
    }

    @Override
    public int getWidth() {
        return this.getLastTransformationOrParent().getWidth();
    }

    @Override
    public int getHeight() {
        return this.getLastTransformationOrParent().getHeight();
    }

    @Override
    public boolean isAbsoluteTrueCoordsWithin(int x, int y) {
        return this.getLastTransformationOrParent().isAbsoluteTrueCoordsWithin(x, y);
    }

    @Override
    public AbstractStandardCanvas open() {
        AbstractStandardCanvas superResult = super.open();
        for (AbstractTransformationCanvas transformation : this.transformations) {
            transformation.apply();
        }
        return superResult;
    }
}
