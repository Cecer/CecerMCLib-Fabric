package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering;

import com.cecer1.projects.mc.cecermclib.common.events.CMLEvent;
import com.cecer1.projects.mc.cecermclib.common.events.CMLEventFactory;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.RenderContext;

public interface SafeHudRenderCallback {
    CMLEvent<SafeHudRenderCallback> EVENT = CMLEventFactory.createArrayBacked(SafeHudRenderCallback.class, 
        listeners -> (renderContext) -> {
            for (SafeHudRenderCallback listener : listeners) {
                listener.handle(renderContext);
            }
        });

    void handle(RenderContext renderContext);
}