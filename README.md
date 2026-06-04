# Minecraft-But-Magnetic

Minecraft But Magnetic is a Fabric mod for Minecraft 26.1. When a player activates magnetic gravity, nearby entities are pulled toward the player.

## Build

Use Java 25.

```sh
./gradlew build
```

The built mod jar is written to:

```txt
build/libs/minecraft-but-magnetic-1.0.0.jar
```

## Usage

Install the jar with Fabric Loader and Fabric API for Minecraft 26.1.

Use these commands in game:

```txt
/magneticgravity
/magneticgravity toggle
/magneticgravity on
/magneticgravity off
/magneticgravity reload
```

The mod creates its editable config at:

```txt
config/minecraftbutmagnetic.json
```

## Entity Targeting Update

The gravity field must work for **all entities**, not just mobs.

When a player activates gravity, the field should pull in nearby entities such as:

```txt
- Hostile mobs
- Passive mobs
- Animals
- Monsters
- Dropped items
- XP orbs
- Arrows/projectiles
- Boats
- Minecarts
- Falling blocks
- TNT
- Other players, only if enabled in config
```

Use the base `Entity` class when scanning, not only `LivingEntity`.

Example:

```java
AABB box = player.getBoundingBox().inflate(radius);

List<Entity> nearbyEntities = world.getEntities(
    player,
    box,
    entity -> EntityFilter.canBePulled(player, entity, config)
);
```

Update config to support broader entity types:

```json
{
  "defaultEnabled": false,
  "radius": 12.0,
  "pullStrength": 0.08,
  "verticalPullStrength": 0.03,
  "maxPullVelocity": 0.6,

  "affectLivingEntities": true,
  "affectDroppedItems": true,
  "affectExperienceOrbs": true,
  "affectProjectiles": true,
  "affectVehicles": true,
  "affectFallingBlocks": true,
  "affectTnt": true,
  "affectPlayers": false,

  "ignoreBosses": true,
  "requireLineOfSight": true,
  "particleEffects": true,
  "soundEffects": false,
  "tickInterval": 1
}
```

Update filtering rules:

```txt
Ignore:
- The player creating the gravity field
- Removed/dead entities
- Spectator players
- Creative players if affectPlayers is false
- Boss entities if ignoreBosses is true
- Entities outside the radius
- Entities blocked by walls if requireLineOfSight is true
- Entities that cannot be moved safely
```

Velocity should be applied to all valid entities using:

```java
Vec3 direction = player.position().add(0, 1.0, 0).subtract(entity.position());
double distance = direction.length();

if (distance > 0.1) {
    Vec3 pull = direction.normalize().scale(strength);
    entity.addDeltaMovement(new Vec3(pull.x, pull.y + verticalPullStrength, pull.z));
}
```

For non-living entities, make sure the pull feels correct:

```txt
- Dropped items should fly toward the player smoothly
- Projectiles should curve toward the player
- Boats and minecarts should slide toward the player but remain capped by maxPullVelocity
- Falling blocks and TNT should be pulled but not glitch excessively
- XP orbs can be pulled more easily since they already move naturally
```

Rename the feature description from:

```txt
nearby living mobs are pulled toward the player
```

to:

```txt
nearby entities are pulled toward the player
```

Also update the class description:

```txt
EntityFilter should decide whether any entity type can be pulled based on config.
GravityManager should operate on Entity, not LivingEntity.
```

Final requirement:

```txt
Do not hardcode this only for mobs. The system must be generic and work with Minecraft's Entity base class so future entity types can also be pulled.
```
