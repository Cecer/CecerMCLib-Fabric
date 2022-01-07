package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering;

import com.cecer1.projects.mc.cecermclib.common.modules.text.TextState;
import com.cecer1.projects.mc.cecermclib.common.modules.text.parts.components.AbstractXmlTextComponent;
import com.cecer1.projects.mc.cecermclib.common.modules.text.parts.components.TextXmlTextComponent;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.RenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.profiler.Profiler;
import org.lwjgl.opengl.GL11;

public abstract class RenderLayer {

    private static final AbstractXmlTextComponent RENDER_ERROR_TEXT = new TextXmlTextComponent(TextState.newRootState(), "<red>An exception was thrown during rendering. The game may now be unstable!</red>");

    private final String profilerEntryName = this.getClass().getSimpleName() + " [" + this.getClass().getName().replace('.', '-') + "]";

    public void prepareFbo() {

    }

    /**
     * A render order below 0 will render behind all of the Minecraft HUD.
     * A render order above 10,000 will render above all of the Minecraft HUD.
     * The different components of the Minecraft HUD are assigned render orders 0-10000.
     */
    public abstract int getRenderOrder();

    protected abstract void render0(RenderContext ctx);

    /**
     * Wraps {@link #render0(RenderContext)} in profiler sections, a try-catch-finally and a prepared GL state.
     * In most cases you want to override {@link #render0(RenderContext)} instead of this class.
     */
    public final void doRender(RenderContext ctx) {
        Profiler mcProfiler = MinecraftClient.getInstance().getProfiler();
        mcProfiler.push("renderlayer");
        mcProfiler.push(this.profilerEntryName);

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            this.render0(ctx);
        } catch (Exception e) {
            // RENDER_ERROR_TEXT
            e.printStackTrace();
        } finally {
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }

        mcProfiler.pop();
        mcProfiler.pop();
    }
}
