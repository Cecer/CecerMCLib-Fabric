package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context;

import net.minecraft.client.MinecraftClient;

public class RootCanvas extends AbstractStandardCanvas {

    private float correctiveScaleFactor;

    public RootCanvas(RenderContext ctx) {
        super(null, ctx);
    }

    @Override
    public TransformCanvasBuilder transform() {
        return super.transform();
    }

    @Override
    public float getTrueScale() {
        return 1.0f;
    }

    @Override
    public int getTrueX() {
        return 0;
    }

    @Override
    public int getTrueY() {
        return 0;
    }

    @Override
    public int getTrueWidth() {
        return MinecraftClient.getInstance().getWindow().getWidth();
    }

    @Override
    public int getTrueHeight() {
        return MinecraftClient.getInstance().getWindow().getHeight();
    }

    @Override
    public int getRelativeX() {
        return 0;
    }

    @Override
    public int getRelativeY() {
        return 0;
    }

    @Override
    public int getWidth() {
        return MinecraftClient.getInstance().getWindow().getWidth();
    }

    @Override
    public int getHeight() {
        return MinecraftClient.getInstance().getWindow().getHeight();
    }

    @Override
    public AbstractStandardCanvas open() {
        super.open();
        this.correctiveScaleFactor = (float) (1.0f / MinecraftClient.getInstance().getWindow().getScaleFactor());
        this.ctx.getMatrixStack().scale(this.correctiveScaleFactor, this.correctiveScaleFactor, 1);
        return this;
    }
}
