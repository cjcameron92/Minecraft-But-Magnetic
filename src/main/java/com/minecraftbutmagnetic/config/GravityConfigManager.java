package com.minecraftbutmagnetic.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.minecraftbutmagnetic.MinecraftButMagnetic;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public final class GravityConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
        .getConfigDir()
        .resolve("minecraftbutmagnetic.json");

    private GravityConfigManager() {
    }

    public static GravityConfig load() {
        if (!Files.exists(CONFIG_PATH)) {
            GravityConfig config = new GravityConfig();
            save(config);
            return config;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            GravityConfig config = GSON.fromJson(reader, GravityConfig.class);
            return config == null ? new GravityConfig() : config;
        } catch (IOException exception) {
            MinecraftButMagnetic.LOGGER.warn("Failed to load magnetic gravity config, using defaults", exception);
            return new GravityConfig();
        }
    }

    public static void save(GravityConfig config) {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(config, writer);
            }
        } catch (IOException exception) {
            MinecraftButMagnetic.LOGGER.warn("Failed to save magnetic gravity config", exception);
        }
    }
}
