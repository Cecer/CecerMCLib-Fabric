package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context;

import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.transformations.AbstractTransformationCanvas;

/**
 * Acts as a kind of compound canvas to reduce the number of GL state changes
 */
public class TransformCanvas extends AbstractStandardCanvas {

    private final AbstractTransformationCanvas[] transformations;

    public TransformCanvas(AbstractTransformationCanvas[] transformations, RenderContext ctx) {
        super(transformations[transformations.length-1], ctx);
        this.transformations = transformations;
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
