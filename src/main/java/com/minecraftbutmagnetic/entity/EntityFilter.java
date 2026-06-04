package com.minecraftbutmagnetic.entity;

import com.minecraftbutmagnetic.config.GravityConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Decides whether any entity type can be pulled by a player's gravity field.
 */
public final class EntityFilter {
    private EntityFilter() {
    }

    public static boolean canBePulled(Player source, Entity entity, GravityConfig config) {
        if (source == null || entity == null || config == null) {
            return false;
        }

        if (entity == source || entity.isRemoved() || !entity.isAlive()) {
            return false;
        }

        if (source.distanceToSqr(entity) > config.radius * config.radius) {
            return false;
        }

        if (entity instanceof Player player) {
            if (player.isSpectator() || !config.affectPlayers) {
                return false;
            }
        }

        if (config.ignoreBosses && isBoss(entity)) {
            return false;
        }

        if (!isMovableSafely(entity)) {
            return false;
        }

        if (!isTypeEnabled(entity, config)) {
            return false;
        }

        return !config.requireLineOfSight || hasLineOfSight(source, entity);
    }

    private static boolean isTypeEnabled(Entity entity, GravityConfig config) {
        if (entity instanceof Player) {
            return config.affectPlayers;
        }

        if (entity instanceof ItemEntity) {
            return config.affectDroppedItems;
        }

        if (entity instanceof ExperienceOrb) {
            return config.affectExperienceOrbs;
        }

        if (entity instanceof Projectile) {
            return config.affectProjectiles;
        }

        if (entity instanceof Boat || entity instanceof AbstractMinecart) {
            return config.affectVehicles;
        }

        if (entity instanceof FallingBlockEntity) {
            return config.affectFallingBlocks;
        }

        if (entity instanceof PrimedTnt) {
            return config.affectTnt;
        }

        if (entity instanceof LivingEntity) {
            return config.affectLivingEntities;
        }

        return true;
    }

    private static boolean isBoss(Entity entity) {
        return entity instanceof EnderDragon || entity instanceof WitherBoss;
    }

    private static boolean isMovableSafely(Entity entity) {
        return entity.getType() != EntityType.AREA_EFFECT_CLOUD
            && !(entity instanceof ArmorStand armorStand && armorStand.isMarker());
    }

    private static boolean hasLineOfSight(Player source, Entity entity) {
        Vec3 start = source.getEyePosition();
        Vec3 end = entity.getBoundingBox().getCenter();
        ClipContext context = new ClipContext(
            start,
            end,
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            source
        );

        return source.level().clip(context).getType() == HitResult.Type.MISS;
    }
}
