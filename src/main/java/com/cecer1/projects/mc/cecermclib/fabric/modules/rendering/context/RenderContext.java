package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context;

import com.cecer1.projects.mc.cecermclib.common._misc.annotations.InternalUseOnly;
import com.cecer1.projects.mc.cecermclib.common.modules.text.WrappedComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Stack;

public class RenderContext {
    
    private final MatrixStack matrixStack;
    
    private float partialTicks;
    private WrappedComponent<?> hoverTextComponent;
    private Stack<StackTraceElement[]> canvasStack;
    
    private AbstractCanvas canvas;

    public RenderContext(MatrixStack matrixStack, float partialTicks) {
        this.matrixStack = matrixStack;
        this.partialTicks = partialTicks;
        this.canvasStack = new Stack<>();
        
        this.pushCanvas(new RootCanvas(this));
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
    void pushCanvas(AbstractCanvas canvas) {
        this.canvas = canvas;
        this.canvasStack.push(new Exception().getStackTrace()); // TODO: Disable this during normal operation
    }
    void popCanvas() {
        this.canvas = this.canvas.getParentCanvas();
        this.canvasStack.pop(); // TODO: Disable this during normal operation
    }

    @InternalUseOnly
    public Stack<StackTraceElement[]> getCanvasStack() {
        return this.canvasStack;
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