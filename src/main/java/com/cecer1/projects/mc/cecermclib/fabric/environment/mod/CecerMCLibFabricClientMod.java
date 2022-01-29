package com.cecer1.projects.mc.cecermclib.fabric.environment.mod;

import com.cecer1.projects.mc.cecermclib.common.CecerMCLib;
import com.cecer1.projects.mc.cecermclib.fabric.environment.FabricClientEnvironment;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class CecerMCLibFabricClientMod extends BaseCecerMCLibFabricMod implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        ClientLifecycleEvents.CLIENT_STARTED.register(this::onStarting);
    }
    
    protected void onStarting(MinecraftClient minecraftClient) {
        CecerMCLib.initEnvironment(new FabricClientEnvironment());
    }
}
