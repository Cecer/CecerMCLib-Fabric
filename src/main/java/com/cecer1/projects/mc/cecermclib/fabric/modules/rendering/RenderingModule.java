package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering;

import com.cecer1.projects.mc.cecermclib.common.CecerMCLib;
import com.cecer1.projects.mc.cecermclib.common.environment.AbstractEnvironment;
import com.cecer1.projects.mc.cecermclib.common.modules.IModule;
import com.cecer1.projects.mc.cecermclib.common.modules.logger.LoggerModule;
import com.cecer1.projects.mc.cecermclib.common.modules.text.TextModule;
import com.cecer1.projects.mc.cecermclib.fabric.environment.FabricClientEnvironment;
import com.cecer1.projects.mc.cecermclib.fabric.modules.input.InputModule;
import com.cecer1.projects.mc.cecermclib.fabric.modules.input.mouse.MouseRegionHandler;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.AbstractCanvas;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.AbstractStandardCanvas;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.RenderContext;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.RootCanvas;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;

public class RenderingModule implements IModule {
    
    private static final Identifier CONTEXT_BEGIN_EVENT_ORDER = new Identifier("cecermclib", "framestart");
    private static final Identifier CONTEXT_END_EVENT_ORDER = new Identifier("cecermclib", "frameend");

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
        HudRenderCallback.EVENT.addPhaseOrdering(CONTEXT_BEGIN_EVENT_ORDER, Event.DEFAULT_PHASE);
        HudRenderCallback.EVENT.addPhaseOrdering(Event.DEFAULT_PHASE, CONTEXT_END_EVENT_ORDER);

        HudRenderCallback.EVENT.register(CONTEXT_BEGIN_EVENT_ORDER, this::beginContext);
        HudRenderCallback.EVENT.register(this::renderHud);
        HudRenderCallback.EVENT.register(CONTEXT_END_EVENT_ORDER, (matrixStack, tickDelta) -> this.endContext());
        
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            Event<ScreenEvents.BeforeRender> preEvent = ScreenEvents.beforeRender(screen);
            Event<ScreenEvents.AfterRender> postEvent = ScreenEvents.afterRender(screen);
            
            preEvent.addPhaseOrdering(CONTEXT_BEGIN_EVENT_ORDER, Event.DEFAULT_PHASE);
            preEvent.addPhaseOrdering(Event.DEFAULT_PHASE, CONTEXT_END_EVENT_ORDER);
            
            preEvent.register(CONTEXT_BEGIN_EVENT_ORDER, (s, matrices, mouseX, mouseY, tickDelta) -> this.beginContext(matrices, tickDelta));
            postEvent.register(this::renderScreen);
            postEvent.register(CONTEXT_END_EVENT_ORDER, (s, matrices, mouseX, mouseY, tickDelta) -> this.endContext());
        });
    }

    private void safeRender(Consumer<RenderContext> event) {
        try {
            event.accept(this.currentRenderContext);
        } catch (Exception e) {
            LoggerModule.Channel channel = CecerMCLib.get(LoggerModule.class).getChannel(RenderingModule.class);
            channel.log("A crash was ignored during rendering. Expect instability and errors until the game is restarted!");
            e.printStackTrace();


            Stack<AbstractCanvas> canvases = this.currentRenderContext.getLastCanvases();
            if (!canvases.isEmpty()) {
                channel.log("Dumping canvas stack:");
                while (!canvases.isEmpty()) {
                    final AbstractCanvas canvas = canvases.pop();
                    channel.log("  " + canvas.getClass().getName());
                    StackTraceElement[] trace = canvas.getOpenTrace();
                    for (int i = 2; i < trace.length; i++) {
                        channel.log("    " + trace[i]);
                    }
                }
            } else {
                channel.log("No canvas stack found!");
            }
        }
    }
    private void renderHud(MatrixStack matrices, float tickDelta) {
        this.safeRender(SafeHudRenderCallback.EVENT.invoker()::handle);
    }

    private void renderScreen(Screen screen, MatrixStack matrixStack, int mouseX, int mouseY, float tickDelta) {
        this.safeRender(SafeScreenRenderCallback.EVENT.invoker()::handle);
    }

    private RenderContext currentRenderContext;

    private void beginContext(MatrixStack matrixStack, float partialTicks) {
        if (this.currentRenderContext != null) {
            CecerMCLib.get(LoggerModule.class).getChannel(RenderingModule.class).log("Ignoring call to beginContext before ending the previous context!");
            return;
        }
        
        Profiler mcProfiler = MinecraftClient.getInstance().getProfiler();
        mcProfiler.push("cecermclib");
        mcProfiler.push("rendering");
        
        CecerMCLib.get(InputModule.class).getMouseInputManager().clearHandlers();
        this.currentRenderContext = new RenderContext(matrixStack, partialTicks);
    }
    private void endContext() {
        if (this.currentRenderContext == null) {
            CecerMCLib.get(LoggerModule.class).getChannel(RenderingModule.class).log("Ignoring call to endContext before beginning one!");
            return;
        }
        
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
                        .openTransformation()) {


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



