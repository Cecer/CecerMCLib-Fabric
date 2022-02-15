package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context;

import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.transformations.AbstractTransformationCanvas;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.transformations.RelativeResizeTransformationCanvas;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.transformations.ScaleTransformationCanvas;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.transformations.TranslateTransformationCanvas;

import java.util.List;
import java.util.ArrayList;

public class TransformCanvasBuilder {

    private final AbstractCanvas parentCanvas;
    private final RenderContext ctx;

    private final List<AbstractTransformationCanvas> lastTransformations;

    public TransformCanvasBuilder(AbstractCanvas parentCanvas, RenderContext ctx) {
        this.parentCanvas = parentCanvas;
        this.ctx = ctx;
        this.lastTransformations = new ArrayList<>();
    }

    private void addTransformation(AbstractTransformationCanvas transformationCanvas) {
        transformationCanvas.setOpenTrace();
        this.lastTransformations.add(transformationCanvas);
    }

    private AbstractCanvas getLastTransformationOrParent() {
        return this.lastTransformations.isEmpty() ? this.parentCanvas : this.lastTransformations.get(this.lastTransformations.size()-1);
    }

    public TransformCanvasBuilder translate(int x, int y) {
        this.addTransformation(new TranslateTransformationCanvas(this.getLastTransformationOrParent(), this.ctx, x, y));
        return this;
    }

    public TransformCanvasBuilder absoluteResize(int width, int height) {
        return this.absoluteResize(width, height, false);
    }
    public TransformCanvasBuilder absoluteResize(int width, int height, boolean allowGrowth) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException(String.format("Negative canvas size is not allowed. {width=%d; height=%d}", width, height));
        }
        
        int deltaX = width - this.getLastTransformationOrParent().getWidth();
        int deltaY = height - this.getLastTransformationOrParent().getHeight();
        return this.relativeResize(deltaX, deltaY, allowGrowth);
    }

    public TransformCanvasBuilder relativeResize(int deltaX, int deltaY) {
        return this.relativeResize(deltaX, deltaY, false);
    }
    public TransformCanvasBuilder relativeResize(int deltaX, int deltaY, boolean allowGrowth) {
        this.addTransformation(new RelativeResizeTransformationCanvas(this.getLastTransformationOrParent(), this.ctx, deltaX, deltaY, allowGrowth));
        return this;
    }

    public TransformCanvasBuilder margin(int top, int right, int bottom, int left) {
        return this.relativeResize(-(left + right), -(top + bottom)).translate(left, top);
    }

    public TransformCanvasBuilder scale(float scaleFactor) {
        this.addTransformation(new ScaleTransformationCanvas(this.getLastTransformationOrParent(), this.ctx, scaleFactor));
        return this;
    }

    public TransformCanvas openTransformation() {
        TransformCanvas canvas = new TransformCanvas(this.parentCanvas, this.lastTransformations.toArray(new AbstractTransformationCanvas[0]), ctx);
        canvas.open();
        return canvas;
    }
}
