package com.cecer1.projects.mc.cecermclib.fabric.environment;

import com.cecer1.projects.mc.cecermclib.common.CecerMCLib;
import com.cecer1.projects.mc.cecermclib.common.environment.AbstractEnvironment;
import com.cecer1.projects.mc.cecermclib.common.modules.text.TextModule;
import com.cecer1.projects.mc.cecermclib.fabric.modules.text.FabricTextAdapter;

public abstract class AbstractFabricEnvironment extends AbstractEnvironment {

    public AbstractFabricEnvironment(AbstractEnvironment.Side side) {
        super(side);
    }
    public void registerModules() {
        super.registerModules();
        
        CecerMCLib
                .registerModule(new TextModule(new FabricTextAdapter()));
//                .registerModule(new MoreEventsModule());
    }
}
