package com.cecer1.projects.mc.cecermclib.fabric.modules.rendering;

import com.cecer1.projects.mc.cecermclib.common.events.CMLEvent;
import com.cecer1.projects.mc.cecermclib.common.events.CMLEventFactory;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.context.RenderContext;

public interface SafeScreenRenderCallback {
    CMLEvent<SafeScreenRenderCallback> EVENT = CMLEventFactory.createArrayBacked(SafeScreenRenderCallback.class, 
        listeners -> (renderContext) -> {
            for (SafeScreenRenderCallback listener : listeners) {
                listener.handle(renderContext);
            }
        });

    void handle(RenderContext renderContext);
}