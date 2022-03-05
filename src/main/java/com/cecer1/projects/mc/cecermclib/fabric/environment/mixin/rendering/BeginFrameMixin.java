package com.cecer1.projects.mc.cecermclib.fabric.environment.mixin.rendering;

import com.cecer1.projects.mc.cecermclib.common.CecerMCLib;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.RenderingModule;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class BeginFrameMixin {
    
    @Inject(method = "render", at = @At(value = "HEAD"))
    public void render(MatrixStack matrixStack, float tickDelta, CallbackInfo callbackInfo) {
        CecerMCLib.get(RenderingModule.class).beginFrame(matrixStack, tickDelta);
    }
}
