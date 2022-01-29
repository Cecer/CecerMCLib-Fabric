package com.cecer1.projects.mc.cecermclib.fabric.environment.mod;

import com.cecer1.projects.mc.cecermclib.common.CecerMCLib;
import com.cecer1.projects.mc.cecermclib.fabric.environment.FabricServerEnvironment;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

@Environment(EnvType.SERVER)
public class CecerMCLibFabricServerMod extends BaseCecerMCLibFabricMod implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::onStarting);
    }

    protected void onStarting(MinecraftServer minecraftServer) {
        CecerMCLib.initEnvironment(new FabricServerEnvironment(minecraftServer));
    }
}
