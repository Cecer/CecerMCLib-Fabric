package com.cecer1.projects.mc.cecermclib.fabric.environment.mod;

import com.cecer1.projects.mc.cecermclib.common.CecerMCLib;
import com.cecer1.projects.mc.cecermclib.fabric.environment.FabricServerEnvironment;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

@Environment(EnvType.SERVER)
public class CecerMCLibFabricServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::onStart);
    }

    private void onStart(MinecraftServer minecraftServer) {
        CecerMCLib.initEnvironment(new FabricServerEnvironment(minecraftServer));
    }
}
