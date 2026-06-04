package com.minecraftbutmagnetic.gravity;

import com.minecraftbutmagnetic.config.GravityConfig;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.world.entity.player.Player;

public final class GravityState {
    private final Map<UUID, Boolean> playerStates = new HashMap<>();

    public boolean isEnabled(Player player, GravityConfig config) {
        return playerStates.getOrDefault(player.getUUID(), config.defaultEnabled);
    }

    public boolean toggle(Player player, GravityConfig config) {
        boolean enabled = !isEnabled(player, config);
        setEnabled(player, config, enabled);
        return enabled;
    }

    public void setEnabled(Player player, GravityConfig config, boolean enabled) {
        if (enabled == config.defaultEnabled) {
            playerStates.remove(player.getUUID());
        } else {
            playerStates.put(player.getUUID(), enabled);
        }
    }
}
