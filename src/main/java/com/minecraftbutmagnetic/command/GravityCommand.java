package com.minecraftbutmagnetic.command;

import com.minecraftbutmagnetic.config.GravityConfig;
import com.minecraftbutmagnetic.gravity.GravityState;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.literal;

public final class GravityCommand {
    private GravityCommand() {
    }

    public static void register(GravityState state, Supplier<GravityConfig> configSupplier, Supplier<GravityConfig> reloadConfig) {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            register(dispatcher, state, configSupplier, reloadConfig)
        );
    }

    private static void register(
        CommandDispatcher<CommandSourceStack> dispatcher,
        GravityState state,
        Supplier<GravityConfig> configSupplier,
        Supplier<GravityConfig> reloadConfig
    ) {
        dispatcher.register(literal("magneticgravity")
            .executes(context -> toggle(context.getSource(), state, configSupplier.get()))
            .then(literal("toggle")
                .executes(context -> toggle(context.getSource(), state, configSupplier.get())))
            .then(literal("on")
                .executes(context -> set(context.getSource(), state, configSupplier.get(), true)))
            .then(literal("off")
                .executes(context -> set(context.getSource(), state, configSupplier.get(), false)))
            .then(literal("reload")
                .executes(context -> reload(context.getSource(), reloadConfig))));
    }

    private static int toggle(CommandSourceStack source, GravityState state, GravityConfig config) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        boolean enabled = state.toggle(player, config);
        sendStateMessage(source, enabled);
        return enabled ? 1 : 0;
    }

    private static int set(CommandSourceStack source, GravityState state, GravityConfig config, boolean enabled) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        state.setEnabled(player, config, enabled);
        sendStateMessage(source, enabled);
        return enabled ? 1 : 0;
    }

    private static int reload(CommandSourceStack source, Supplier<GravityConfig> reloadConfig) {
        reloadConfig.get();
        source.sendSuccess(() -> Component.literal("Magnetic gravity config reloaded."), false);
        return 1;
    }

    private static void sendStateMessage(CommandSourceStack source, boolean enabled) {
        source.sendSuccess(
            () -> Component.literal("Magnetic gravity " + (enabled ? "enabled." : "disabled.")),
            false
        );
    }
}
