//package com.cecer1.projects.mc.cecermclib.fabric.environment.mixin.rendering;
//
//import com.cecer1.projects.mc.cecermclib.common.CecerMCLib;
//import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.RenderingModule;
//import net.minecraft.client.render.GameRenderer;
//import net.minecraft.client.util.math.MatrixStack;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.ModifyVariable;
//import org.spongepowered.asm.mixin.injection.Slice;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//@Mixin(GameRenderer.class)
//public class GameRendererFrameMixin {
//    
//    private float mostRecentTickDelta;
//
//    @Inject(
//            method = "render",
//            at = @At("HEAD"))
//    public void captureTickDelta(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
//        this.mostRecentTickDelta = tickDelta;
//    }
//
//    @ModifyVariable(
//            method = "render",
//            slice = @Slice(
//                    from = @At(
//                            value = "INVOKE",
//                            target = "Lnet/minecraft/client/render/DiffuseLighting;enableGuiDepthLighting()V"
//                    )
//            ),
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/client/util/math/MatrixStack;<init>()V",
//                    shift = At.Shift.AFTER
//            ),
//            ordinal = 0
//    )
//    public MatrixStack beginFrame(MatrixStack matrixStack) {
//        CecerMCLib.get(RenderingModule.class).beginFrame(matrixStack, this.mostRecentTickDelta); // TODO: Turn this into an event probably. I don't like the public method call. It feels so dirty.
//        return matrixStack;
//    }
//
//    @Inject(
//            method = "render",
//            at = @At("RETURN")
//    )
//    public void endFrame(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
//        CecerMCLib.get(RenderingModule.class).endFrame(); // TODO: Turn this into an event probably. I don't like the public method call. It feels so dirty.
//    }
//}
