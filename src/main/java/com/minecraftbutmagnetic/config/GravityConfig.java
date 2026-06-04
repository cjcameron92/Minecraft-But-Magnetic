package com.minecraftbutmagnetic.config;

/**
 * Runtime settings for the gravity field.
 */
public class GravityConfig {
    public boolean defaultEnabled = false;
    public double radius = 12.0;
    public double pullStrength = 0.08;
    public double verticalPullStrength = 0.03;
    public double maxPullVelocity = 0.6;

    public boolean affectLivingEntities = true;
    public boolean affectDroppedItems = true;
    public boolean affectExperienceOrbs = true;
    public boolean affectProjectiles = true;
    public boolean affectVehicles = true;
    public boolean affectFallingBlocks = true;
    public boolean affectTnt = true;
    public boolean affectPlayers = false;

    public boolean ignoreBosses = true;
    public boolean requireLineOfSight = true;
    public boolean particleEffects = true;
    public boolean soundEffects = false;
    public int tickInterval = 1;
}
