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
    private Stack<AbstractCanvas> lastCanvases; // Used for logging in the event of exceptions during rendering
    
    private AbstractCanvas canvas;

    public RenderContext(MatrixStack matrixStack, float partialTicks) {
        this.matrixStack = matrixStack;
        this.partialTicks = partialTicks;
        this.lastCanvases = new Stack<>();
        
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
        if (!this.lastCanvases.isEmpty()) {
            while (this.lastCanvases.peek() != this.canvas) {
                this.lastCanvases.pop();
            }
        }
        
        this.lastCanvases.push(canvas);
        this.canvas = canvas;
    }
    void popCanvas() {
        this.canvas = this.canvas.getParentCanvas();
    }

    @InternalUseOnly
    public Stack<AbstractCanvas> getLastCanvases() {
        return this.lastCanvases;
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