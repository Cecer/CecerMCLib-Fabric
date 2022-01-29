package com.cecer1.projects.mc.cecermclib.fabric.environment;

import com.cecer1.projects.mc.cecermclib.common.config.ICecerMCLibConfig;
import com.cecer1.projects.mc.cecermclib.common.environment.IClientEnvironment;
import com.cecer1.projects.mc.cecermclib.common.modules.ModuleRegistrationCallback;
import com.cecer1.projects.mc.cecermclib.fabric.modules.input.InputModule;
import com.cecer1.projects.mc.cecermclib.fabric.modules.rendering.RenderingModule;

public class FabricClientEnvironment extends AbstractFabricEnvironment implements IClientEnvironment {

    public FabricClientEnvironment() {
        super(Side.CLIENT);
    }

    @Override
    public void registerModules(ModuleRegistrationCallback.RegistrationContext ctx) {
        super.registerModules(ctx);
        this.registerSideModules(ctx);
        ctx.registerModule(new InputModule());
        ctx.registerModule(new RenderingModule());
//        ctx.registerModule(new OverlayTestModule());
    }

    @Override
    public ICecerMCLibConfig getConfig() {
        return new ICecerMCLibConfig() {
            @Override
            public boolean isLongChatBackportEnabled() {
                return false; // TODO: Enable this or move it out of CecerMCLib
            }

            @Override
            public boolean isFastWorldSwitchEnabled() {
                return false; // TODO: No longer needed in 1.16? I should remove this if so
            }
        };
    }
}
