package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context;

import com.cecer1.projects.mc.cecermclib.common.modules.text.WrappedComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class RenderContext {
    
    private final MatrixStack matrixStack;
    
    private float partialTicks;
    private AbstractCanvas canvas;
    private WrappedComponent<?> hoverTextComponent;

    public RenderContext(MatrixStack matrixStack, float partialTicks) {
        this.matrixStack = matrixStack;
        this.partialTicks = partialTicks;
        this.canvas = new RootCanvas(this);
    }

    public MatrixStack getMatrixStack() {
        return this.matrixStack;
    }

    public float getPartialTicks() {
        return this.partialTicks;
    }
    public AbstractCanvas getCanvas() {
        return this.canvas;
    }
    protected void setCanvas(AbstractCanvas canvas) {
        this.canvas = canvas;
    }

    public WrappedComponent<?> getHoverTextComponent() {
        return hoverTextComponent;
    }
    public void setHoverTextComponent(WrappedComponent<?> hoverTextComponent) {
        this.hoverTextComponent = hoverTextComponent;
    }

    // TODO: Abstract text rendering for cross platform support
    public TextRenderer getFontRenderer() {
        return MinecraftClient.getInstance().textRenderer;
    }
}