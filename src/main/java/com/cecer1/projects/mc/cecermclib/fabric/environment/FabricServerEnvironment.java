package com.cecer1.projects.mc.cecermclib.fabric.environment;

import com.cecer1.projects.mc.cecermclib.common.config.ICecerMCLibConfig;
import com.cecer1.projects.mc.cecermclib.common.environment.IServerEnvironment;
import net.minecraft.server.MinecraftServer;

public class FabricServerEnvironment extends AbstractFabricEnvironment implements IServerEnvironment {

    private final MinecraftServer minecraftServer;

    public FabricServerEnvironment(MinecraftServer minecraftServer) {
        super(Side.SERVER);
        this.minecraftServer = minecraftServer;
    }

    @Override
    public void registerModules() {
        super.registerModules();
        this.registerSideModules();
    }

    public MinecraftServer getMinecraftServer() {
        return this.minecraftServer;
    }

    @Override
    public ICecerMCLibConfig getConfig() {
        return new ICecerMCLibConfig() {
            @Override
            public boolean isLongChatBackportEnabled() {
                return false; // TODO: Move this to another mod?
            }

            @Override
            public boolean isFastWorldSwitchEnabled() {
                return false; // Meaningless on servers
            }
        };
    }
}
