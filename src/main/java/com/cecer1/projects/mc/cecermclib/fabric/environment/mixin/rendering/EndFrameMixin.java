package com.cecer1.projects.mc.cecermclib.fabric.environment.mixin.rendering;

import com.cecer1.projects.mc.cecermclib.common.CecerMCLib;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.RenderingModule;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class EndFrameMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onFrameEnd(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        CecerMCLib.get(RenderingModule.class).endFrame(); // TODO: Turn this into an event probably. I don't like the public method call. It feels so dirty.
    }
}
