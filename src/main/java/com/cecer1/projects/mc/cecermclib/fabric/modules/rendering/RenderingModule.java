package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering;

import com.cecer1.projects.mc.cecermclib.common.CecerMCLib;
import com.cecer1.projects.mc.cecermclib.common.environment.AbstractEnvironment;
import com.cecer1.projects.mc.cecermclib.common.modules.IModule;
import com.cecer1.projects.mc.cecermclib.common.modules.text.TextModule;
import com.cecer1.projects.mc.cecermclib.fabric.environment.FabricClientEnvironment;
import com.cecer1.projects.mc.cecermclib.fabric.modules.input.InputModule;
import com.cecer1.projects.mc.cecermclib.fabric.modules.input.mouse.MouseRegionHandler;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.AbstractStandardCanvas;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.RenderContext;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.RootCanvas;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.profiler.Profiler;

import java.util.Set;

public class RenderingModule implements IModule {

    @Override
    public Set<Class<? extends IModule>> getDependencies() {
        //noinspection unchecked
        return Sets.newHashSet(
                InputModule.class,
                TextModule.class
        );
    }

    @Override
    public boolean isEnvironmentSupported(AbstractEnvironment environment) {
        return environment instanceof FabricClientEnvironment;
    }

    @Override
    public void onModuleRegister() {
        HudRenderCallback.EVENT.register(this::renderGameOverlay);
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenEvents.beforeRender(screen).register((ignore, matrices, mouseX, mouseY, tickDelta) -> {
                if (MinecraftClient.getInstance().currentScreen == null) {
                    this.renderGameOverlay(matrices, tickDelta);
                }
            });
//            ScreenEvents.afterRender(screen).register((ignore, matrices, mouseX, mouseY, tickDelta) -> {
//                this.drawTest(matrices, mouseX, mouseY, tickDelta, scaledWidth, scaledHeight, true);
//            });
        });
    }

    private void renderGameOverlay(MatrixStack matrices, float tickDelta) {
        this.beingFrame(matrices, tickDelta);
        GameOverlayRenderCallback.EVENT.invoker().handle(this.currentRenderContext);
        this.endFrame();
    }

    private RenderContext currentRenderContext;

    void beingFrame(MatrixStack matrixStack, float partialTicks) {
        Profiler mcProfiler = MinecraftClient.getInstance().getProfiler();
        mcProfiler.push("cecermclib");
        mcProfiler.push("rendering");
        
        CecerMCLib.get(InputModule.class).getMouseInputManager().clearHandlers();
        this.currentRenderContext = new RenderContext(matrixStack, partialTicks);
    }
    void endFrame() {
        this.renderMouseInputDebug(this.currentRenderContext);
        this.currentRenderContext = null;

        Profiler mcProfiler = MinecraftClient.getInstance().getProfiler();
        mcProfiler.pop();
        mcProfiler.pop();
    }

    // <editor-fold desc="[MOUSE CLICK DEBUG]">
    private static int[][] clist = {
            new int[] { 0xd0ff0000, 0xa0800000 }, // red
            new int[] { 0xd00000ff, 0xa0000080 }, // blue
            new int[] { 0xd07030b0, 0xa0381858 }, // purple
            new int[] { 0xd0d080c0, 0xa0704060 }, // pink
            new int[] { 0xd0604020, 0xa0302010 }, // brown
            new int[] { 0xd000ff00, 0xa000ff00 }, // green
            new int[] { 0xd0ff8844, 0xa0804422 }  // orange
    };
    private void renderMouseInputDebug(RenderContext ctx) {
        if (true) return;
        int colorIndex = 0;
        
        try (AbstractStandardCanvas rootCanvas = ((RootCanvas)ctx.getCanvas()).open()) { // Apply descaling
            for (MouseRegionHandler handler : CecerMCLib.get(InputModule.class).getMouseInputManager().getHandlersIterator()) {
                try (AbstractStandardCanvas canvas = ctx.getCanvas().transform()
                        .translate(handler.getMinX(), handler.getMinY())
                        .absoluteResize(handler.getMaxX() - handler.getMinX(), handler.getMaxY() - handler.getMinY())
                        .openTransformation()
                        .open()) {


                    RenderSystem.enableBlend();
                    
                    DrawMethods.drawSolidRect(ctx.getMatrixStack(), 0, 0, canvas.getWidth(), canvas.getHeight(), clist[colorIndex % clist.length][0]);
                    DrawMethods.drawHollowRect(ctx.getMatrixStack(), 0, 0, canvas.getWidth(), canvas.getHeight(), 2, clist[colorIndex % clist.length][1]);
                    ctx.getFontRenderer().draw(ctx.getMatrixStack(), "\u00a7f#" + colorIndex, 2, 2, 0xffffffff);

                    colorIndex++;
                }
            }
        }
    }
    // </editor-fold>
}



