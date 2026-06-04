package com.minecraftbutmagnetic.gravity;

import com.minecraftbutmagnetic.config.GravityConfig;
import com.minecraftbutmagnetic.entity.EntityFilter;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Applies a gravity pull to valid entities near a player.
 */
public final class GravityManager {
    private GravityManager() {
    }

    public static void applyGravityField(Player player, GravityConfig config) {
        if (player == null || config == null || player.level().isClientSide()) {
            return;
        }

        Level world = player.level();
        double radius = Math.max(0.0, config.radius);
        AABB box = player.getBoundingBox().inflate(radius);

        List<Entity> nearbyEntities = world.getEntities(
            player,
            box,
            entity -> EntityFilter.canBePulled(player, entity, config)
        );

        for (Entity entity : nearbyEntities) {
            applyPull(player, entity, config);
        }

        if (!nearbyEntities.isEmpty() && world instanceof ServerLevel serverWorld) {
            playEffects(serverWorld, player, config);
        }
    }

    private static void applyPull(Player player, Entity entity, GravityConfig config) {
        Vec3 direction = player.position().add(0, 1.0, 0).subtract(entity.position());
        double distance = direction.length();

        if (distance <= 0.1) {
            return;
        }

        double strength = getAdjustedStrength(entity, config);
        Vec3 pull = direction.normalize().scale(strength);

        entity.addDeltaMovement(new Vec3(pull.x, pull.y + config.verticalPullStrength, pull.z));
        entity.setDeltaMovement(capVelocity(entity.getDeltaMovement(), config.maxPullVelocity));
    }

    private static double getAdjustedStrength(Entity entity, GravityConfig config) {
        if (entity instanceof ExperienceOrb) {
            return config.pullStrength * 1.35;
        }

        if (entity instanceof Projectile) {
            return config.pullStrength * 0.75;
        }

        if (entity instanceof Boat || entity instanceof AbstractMinecart) {
            return config.pullStrength * 0.6;
        }

        if (entity instanceof FallingBlockEntity || entity instanceof PrimedTnt) {
            return config.pullStrength * 0.5;
        }

        return config.pullStrength;
    }

    private static Vec3 capVelocity(Vec3 velocity, double maxPullVelocity) {
        double max = Math.max(0.0, maxPullVelocity);

        if (max == 0.0 || velocity.lengthSqr() <= max * max) {
            return velocity;
        }

        return velocity.normalize().scale(max);
    }

    private static void playEffects(ServerLevel world, Player player, GravityConfig config) {
        if (config.particleEffects) {
            world.sendParticles(
                ParticleTypes.PORTAL,
                player.getX(),
                player.getY() + 1.0,
                player.getZ(),
                8,
                0.55,
                0.75,
                0.55,
                0.02
            );
        }

        if (config.soundEffects && player.tickCount % 20 == 0) {
            world.playSound(
                null,
                player.blockPosition(),
                SoundEvents.BEACON_AMBIENT,
                SoundSource.PLAYERS,
                0.35F,
                1.35F
            );
        }
    }
}
