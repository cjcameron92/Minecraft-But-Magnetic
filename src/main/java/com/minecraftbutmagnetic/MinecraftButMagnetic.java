package com.minecraftbutmagnetic;

import com.minecraftbutmagnetic.command.GravityCommand;
import com.minecraftbutmagnetic.config.GravityConfig;
import com.minecraftbutmagnetic.config.GravityConfigManager;
import com.minecraftbutmagnetic.gravity.GravityManager;
import com.minecraftbutmagnetic.gravity.GravityState;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MinecraftButMagnetic implements ModInitializer {
    public static final String MOD_ID = "minecraftbutmagnetic";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static GravityConfig config;
    private static final GravityState STATE = new GravityState();
    private int ticks;

    @Override
    public void onInitialize() {
        config = GravityConfigManager.load();
        GravityCommand.register(STATE, MinecraftButMagnetic::getConfig, MinecraftButMagnetic::reloadConfig);
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
    }

    public static GravityConfig getConfig() {
        return config;
    }

    public static GravityConfig reloadConfig() {
        config = GravityConfigManager.load();
        return config;
    }

    private void onServerTick(MinecraftServer server) {
        GravityConfig currentConfig = getConfig();
        int interval = Math.max(1, currentConfig.tickInterval);
        ticks++;

        if (ticks % interval != 0) {
            return;
        }

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (STATE.isEnabled(player, currentConfig)) {
                GravityManager.applyGravityField(player, currentConfig);
            }
        }
    }
}
