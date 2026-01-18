# Hytale Gameplay: Projectile System (Example)

This example conceptually demonstrates Hytale's Projectile System, illustrating how projectiles are defined, simulated, and how their impacts are handled, based on the official documentation (https://hytale-docs.pages.dev/modding/systems/projectiles/).

The documentation highlights `ProjectileConfig` assets, `Projectile` and `PredictedProjectile` components, `StandardPhysicsTickSystem`, `ProjectileImpact` interactions, and `ImpactConsumer`/`BounceConsumer` interfaces.

## 1. Defining Projectile-Related Components and Configuration

```java
import java.io.Serializable;
import java.util.*;

// Conceptual Hytale Component interface (from ecs_overview example)
// public interface Component extends Cloneable, Serializable {}

// Conceptual Hytale Entity (from ecs_overview example)
// public class Entity { public final long id; ... }

// Component: Marks an entity as a projectile and links it to its config
public class ProjectileComponent implements Component {
    public final String configId; // ID of the ProjectileConfig asset
    public long ownerEntityId;    // Who fired/created this projectile

    public ProjectileComponent(String configId, long ownerEntityId) {
        this.configId = configId;
        this.ownerEntityId = ownerEntityId;
    }

    @Override
    public ProjectileComponent clone() {
        try {
            return (ProjectileComponent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("ProjectileComponent not cloneable!");
        }
    }

    @Override
    public String toString() {
        return "ProjectileComponent{config='" + configId + "', owner=" + ownerEntityId + '}' ;
    }
}

// Component: Used for client-side prediction of projectile trajectory
public class PredictedProjectileComponent implements Component {
    // Contains data needed for client to predict motion
    public float clientStartX, clientStartY, clientStartZ;
    public float clientVelX, clientVelY, clientVelZ;
    // ... other prediction-specific data

    public PredictedProjectileComponent(float clientStartX, float clientStartY, float clientStartZ,
                                        float clientVelX, float clientVelY, float clientVelZ) {
        this.clientStartX = clientStartX; this.clientStartY = clientStartY; this.clientStartZ = clientStartZ;
        this.clientVelX = clientVelX; this.clientVelY = clientVelY; this.clientVelZ = clientVelZ;
    }

    @Override
    public PredictedProjectileComponent clone() {
        try {
            return (PredictedProjectileComponent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("PredictedProjectileComponent not cloneable!");
        }
    }

    @Override
    public String toString() {
        return "PredictedProjectileComponent{pos=(" + clientStartX + "," + clientStartY + "," + clientStartZ + "), vel=(" + clientVelX + "," + clientVelY + "," + clientVelZ + ")}";
    }
}

// Conceptual ProjectileConfig - would be loaded from JSON assets
public class ProjectileConfig {
    public final String id;
    public final float density; // Influences drag
    public final float gravityMultiplier;
    public final float bounciness; // Restitution on impact
    public final float terminalVelocity;
    public final float impactDamage;
    public final String impactEffectId; // Reference to a visual/sound effect asset
    public final boolean canStick;

    public ProjectileConfig(String id, float density, float gravityMultiplier, float bounciness,
                            float terminalVelocity, float impactDamage, String impactEffectId, boolean canStick) {
        this.id = id;
        this.density = density;
        this.gravityMultiplier = gravityMultiplier;
        this.bounciness = bounciness;
        this.terminalVelocity = terminalVelocity;
        this.impactDamage = impactDamage;
        this.impactEffectId = impactEffectId;
        this.canStick = canStick;
    }

    // Static mapping of known configs (in real Hytale, loaded from an AssetRegistry)
    private static final Map<String, ProjectileConfig> configs = new HashMap<>();
    static {
        configs.put("arrow", new ProjectileConfig("arrow", 0.05f, 1.0f, 0.2f, 50.0f, 10.0f, "arrow_hit_effect", true));
        configs.put("fireball", new ProjectileConfig("fireball", 0.01f, 0.5f, 0.0f, 30.0f, 20.0f, "fireball_explosion", false));
    }

    public static ProjectileConfig get(String id) {
        return configs.get(id);
    }

    @Override
    public String toString() {
        return "ProjectileConfig{" + id + "}";
    }
}

// Interface: For handling projectile impact effects (damage, particles etc.)
public interface ImpactConsumer {
    void onImpact(long projectileEntityId, long hitEntityId, String impactEffectId, float damage);
}

// Interface: For handling projectile bounce effects
public interface BounceConsumer {
    void onBounce(long projectileEntityId, float newBounciness);
}
```

## 2. Conceptual Projectile Systems

```java
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

// Conceptual Hytale System interface (from ecs_overview example)
// public interface System { void run(float deltaTime); }

// Conceptual Hytale ECS World (from ecs_overview example)
// public class EcsWorld { ... }
// EcsWorld needs a method to remove components
class EcsWorldForProjectiles extends EcsWorld {
    public void removeComponent(long entityId, Class<? extends Component> componentType) {
        Map<Class<? extends Component>, Component> components = entityComponents.get(entityId);
        if (components != null) {
            components.remove(componentType);
        }
    }
}


// Conceptual StandardPhysicsTickSystem (adapted from ECS Physics example)
// This system updates projectile positions, velocities, and handles basic collision detection.
public class StandardPhysicsTickSystem implements System {
    private final EcsWorldForProjectiles ecsWorld;
    private final float GRAVITY_ACCELERATION = -9.81f; // m/s^2

    private final ImpactConsumer impactConsumer;
    private final BounceConsumer bounceConsumer;

    public StandardPhysicsTickSystem(EcsWorldForProjectiles ecsWorld, ImpactConsumer impactConsumer, BounceConsumer bounceConsumer) {
        this.ecsWorld = ecsWorld;
        this.impactConsumer = impactConsumer;
        this.bounceConsumer = bounceConsumer;
    }

    @Override
    public void run(float deltaTime) {
        // System iterates over entities that are projectiles and have PhysicsBodyState
        for (Entity entity : ecsWorld.getAllEntities()) {
            if (ecsWorld.hasComponent(entity.getId(), ProjectileComponent.class) &&
                ecsWorld.hasComponent(entity.getId(), PhysicsBodyState.class) &&
                ecsWorld.hasComponent(entity.getId(), PhysicsValues.class)) {

                ProjectileComponent projectile = ecsWorld.getComponent(entity.getId(), ProjectileComponent.class);
                ProjectileConfig config = ProjectileConfig.get(projectile.configId);
                PhysicsBodyState state = ecsWorld.getComponent(entity.getId(), PhysicsBodyState.class);
                PhysicsValues values = ecsWorld.getComponent(entity.getId(), PhysicsValues.class);

                if (config == null) continue; // Skip if config not found

                // Apply gravity
                state.velY += GRAVITY_ACCELERATION * config.gravityMultiplier * deltaTime;

                // Apply drag (simplified)
                float dragForce = values.dragCoefficient * config.density * state.velX * state.velX; // Very simplified
                if (state.velX > 0) state.velX -= dragForce * deltaTime; else state.velX += dragForce * deltaTime;
                if (state.velY > 0) state.velY -= dragForce * deltaTime; else state.velY += dragForce * deltaTime;
                if (state.velZ > 0) state.velZ -= dragForce * deltaTime; else state.velZ += dragForce * deltaTime;

                // Limit to terminal velocity
                float speed = (float) Math.sqrt(state.velX*state.velX + state.velY*state.velY + state.velZ*state.velZ);
                if (speed > config.terminalVelocity) {
                    float ratio = config.terminalVelocity / speed;
                    state.velX *= ratio; state.velY *= ratio; state.velZ *= ratio;
                }

                // Update position
                state.posX += state.velX * deltaTime;
                state.posY += state.velY * deltaTime;
                state.posZ += state.velZ * deltaTime;

                // Collision Detection (simplified: hit ground or another entity)
                if (state.posY <= 0) { // Hit ground
                    System.out.println("  [PhysicsTickSystem] Projectile " + entity.getId() + " hit ground.");
                    if (config.bounciness > 0.05f) { // Bounce
                        state.posY = 0;
                        state.velY *= -config.bounciness; // Reflect velocity with restitution
                        bounceConsumer.onBounce(entity.getId(), config.bounciness);
                    } else { // Stick or stop
                        if (config.canStick) {
                            System.out.println("  [PhysicsTickSystem] Projectile " + entity.getId() + " stuck in ground.");
                            ecsWorld.removeComponent(entity.getId(), PhysicsBodyState.class); // Stop simulating
                            ecsWorld.removeComponent(entity.getId(), ProjectileComponent.class); // No longer a "moving" projectile
                        }
                        impactConsumer.onImpact(entity.getId(), -1, config.impactEffectId, config.impactDamage); // -1 for ground
                        ecsWorld.removeEntity(entity.getId()); // Remove entity from world (conceptual)
                        return;
                    }
                }

                // Check for entity collision (simplified: against a fixed target Entity 2)
                if (ecsWorld.hasEntity(2L) && !Objects.equals(projectile.ownerEntityId, 2L) && // Don't hit self/owner
                    Math.abs(state.posX - ecsWorld.getComponent(2L, PhysicsBodyState.class).posX) < values.hitboxWidth &&
                    Math.abs(state.posY - ecsWorld.getComponent(2L, PhysicsBodyState.class).posY) < values.hitboxHeight &&
                    Math.abs(state.posZ - ecsWorld.getComponent(2L, PhysicsBodyState.class).posZ) < values.hitboxDepth)
                {
                    System.out.println("  [PhysicsTickSystem] Projectile " + entity.getId() + " hit Entity 2.");
                    impactConsumer.onImpact(entity.getId(), 2L, config.impactEffectId, config.impactDamage);
                    ecsWorld.removeEntity(entity.getId()); // Remove projectile on impact
                }
            }
        }
    }
}
```

## 3. Conceptual Usage Flow

```java
import java.util.concurrent.TimeUnit;
import java.util.*;

// Assume all conceptual classes are defined:
// Entity (from ecs_overview example), EcsWorldForProjectiles (extended), Component, NameComponent,
// HealthComponent, PhysicsBodyState, PhysicsValues, ProjectileComponent, PredictedProjectileComponent,
// ProjectileConfig, ImpactConsumer, BounceConsumer, StandardPhysicsTickSystem

public class HytaleGameplayProjectilesExample {
    public static void main(String[] args) throws InterruptedException {
        // Conceptual Hytale ECS World setup
        EcsWorldForProjectiles hytaleWorld = new EcsWorldForProjectiles();

        // Register a conceptual AssetRegistry for ProjectileConfigs
        // (In a real system, this would be a loaded asset)
        ProjectileConfig.get("arrow"); // Just to ensure static block runs
        ProjectileConfig.get("fireball");

        // Conceptual Impact and Bounce Consumers
        ImpactConsumer myImpactHandler = (projectileId, hitEntityId, effectId, damage) -> {
            String hitTarget = (hitEntityId == -1) ? "ground" : "Entity " + hitEntityId;
            System.out.println("    [ImpactHandler] Projectile " + projectileId + " hit " + hitTarget +
                               " with effect '" + effectId + "' for " + damage + " damage.");
            // Apply damage to hitEntityId, spawn particles, play sound
            if (hitEntityId != -1 && hytaleWorld.hasComponent(hitEntityId, HealthComponent.class)) {
                HealthComponent health = hytaleWorld.getComponent(hitEntityId, HealthComponent.class);
                health.takeDamage(damage);
                System.out.println("      " + hytaleWorld.getComponent(hitEntityId, NameComponent.class).name + " health: " + health.currentHealth);
            }
        };

        BounceConsumer myBounceHandler = (projectileId, newBounciness) -> {
            System.out.println("    [BounceHandler] Projectile " + projectileId + " bounced (bounciness: " + newBounciness + ").");
        };

        // Add Physics System (which includes projectile simulation)
        StandardPhysicsTickSystem physicsSystem = new StandardPhysicsTickSystem(hytaleWorld, myImpactHandler, myBounceHandler);
        hytaleWorld.addSystem(physicsSystem);

        // --- Create Entities ---
        Entity player = hytaleWorld.createEntity(); // Player as the shooter
        hytaleWorld.addComponent(player.getId(), new NameComponent("ArcherPlayer"));

        Entity targetGoblin = hytaleWorld.createEntityWithId(2L); // Target for arrows
        hytaleWorld.addComponent(targetGoblin.getId(), new NameComponent("Goblin"));
        hytaleWorld.addComponent(targetGoblin.getId(), new HealthComponent(50.0f, 50.0f));
        hytaleWorld.addComponent(targetGoblin.getId(), new PhysicsBodyState(10, 1, 0, 0, 0, 0)); // At (10,1,0)
        hytaleWorld.addComponent(targetGoblin.getId(), new PhysicsValues(10.0f, 0.5f, 0.1f, 1.0f, 2.0f, 1.0f));


        System.out.println("--- Initial State ---");
        System.out.println("Target Goblin Health: " + hytaleWorld.getComponent(targetGoblin.getId(), HealthComponent.class));
        System.out.println("---------------------
");

        // --- Scenario 1: Player shoots an Arrow ---
        System.out.println("--- Scenario 1: Player shoots an Arrow ---");
        Entity arrow = hytaleWorld.createEntity();
        hytaleWorld.addComponent(arrow.getId(), new ProjectileComponent("arrow", player.getId()));
        // Initial state for arrow (e.g., shot from player's position with velocity)
        hytaleWorld.addComponent(arrow.getId(), new PhysicsBodyState(0, 2, 0, 20, 5, 0)); // Start at (0,2,0) with initial velocity
        hytaleWorld.addComponent(arrow.getId(), new PhysicsValues(0.1f, 0.01f, 0.2f, 0.1f, 0.1f, 0.5f)); // Light, low drag, some bounce
        hytaleWorld.addComponent(arrow.getId(), new PredictedProjectileComponent(0, 2, 0, 20, 5, 0)); // Client predicts

        System.out.println("Projectile " + arrow.getId() + " spawned.");
        // Simulate several ticks for projectile to fly
        for (int i = 0; i < 20; i++) {
            hytaleWorld.tick(0.05f);
            if (!hytaleWorld.hasEntity(arrow.getId())) { // Projectile removed on impact
                System.out.println("Projectile " + arrow.getId() + " has been removed.");
                break;
            }
            // System.out.println("  Arrow " + arrow.getId() + " pos: " + hytaleWorld.getComponent(arrow.getId(), PhysicsBodyState.class).posX);
        }
        System.out.println("Target Goblin Health: " + hytaleWorld.getComponent(targetGoblin.getId(), HealthComponent.class));
        System.out.println("---------------------
");

        // --- Scenario 2: Player shoots a Fireball (no bounce) ---
        System.out.println("--- Scenario 2: Player shoots a Fireball ---");
        Entity fireball = hytaleWorld.createEntity();
        hytaleWorld.addComponent(fireball.getId(), new ProjectileComponent("fireball", player.getId()));
        hytaleWorld.addComponent(fireball.getId(), new PhysicsBodyState(0, 1.5f, 0, 15, 8, 0)); // Higher arc
        hytaleWorld.addComponent(fireball.getId(), new PhysicsValues(0.2f, 0.05f, 0.0f, 0.5f, 0.5f, 0.5f)); // heavier, more drag, no bounce

        System.out.println("Projectile " + fireball.getId() + " spawned.");
        for (int i = 0; i < 20; i++) {
            hytaleWorld.tick(0.05f);
            if (!hytaleWorld.hasEntity(fireball.getId())) {
                System.out.println("Projectile " + fireball.getId() + " has been removed.");
                break;
            }
        }
        System.out.println("Target Goblin Health: " + hytaleWorld.getComponent(targetGoblin.getId(), HealthComponent.class));
        System.out.println("---------------------
");
    }
}
```

### Conceptual Output:

```
--- Initial State ---
Target Goblin Health: HealthComponent{current=50.0, max=50.0}
---------------------

--- Scenario 1: Player shoots an Arrow ---
Projectile 3 spawned.
  [PhysicsTickSystem] Projectile 3 hit Entity 2.
    [ImpactHandler] Projectile 3 hit Entity 2 with effect 'arrow_hit_effect' for 10.0 damage.
      Goblin health: 40.0
Projectile 3 has been removed.
Target Goblin Health: HealthComponent{current=40.0, max=50.0}
---------------------

--- Scenario 2: Player shoots a Fireball ---
Projectile 4 spawned.
  [PhysicsTickSystem] Projectile 4 hit Entity 2.
    [ImpactHandler] Projectile 4 hit Entity 2 with effect 'fireball_explosion' for 20.0 damage.
      Goblin health: 20.0
Projectile 4 has been removed.
Target Goblin Health: HealthComponent{current=20.0, max=50.0}
---------------------
```
