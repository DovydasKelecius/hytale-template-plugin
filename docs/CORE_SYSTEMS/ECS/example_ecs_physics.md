# Hytale ECS Physics System (Example)

This example illustrates how Hytale's Physics System integrates within the ECS, based on the concepts and named components/interfaces described in the official documentation (https://hytale-docs.pages.dev/modding/ecs/physics/).

The documentation details `PhysicsBodyState`, `PhysicsValues`, `ForceProvider`, `ForceAccumulator`, `PhysicsBodyStateUpdater`, `CollisionModule`, `PhysicsFlags`, and `IBlockCollisionConsumer`.

## 1. Defining Physics Components and Interfaces

```java
import java.io.Serializable;
import java.util.*;

// Conceptual Hytale Component interface
// public interface Component extends Cloneable, Serializable {}

// Conceptual Hytale Entity (from ecs_overview example)
// public class Entity { public final long id; ... }

// Component: Stores the dynamic physical state of an entity
public class PhysicsBodyState implements Component {
    public float posX, posY, posZ;
    public float velX, velY, velZ;
    public float accumulatedForceX, accumulatedForceY, accumulatedForceZ; // For ForceAccumulator

    public PhysicsBodyState(float posX, float posY, float posZ, float velX, float velY, float velZ) {
        this.posX = posX; this.posY = posY; this.posZ = posZ;
        this.velX = velX; this.velY = velY; this.velZ = velZ;
        this.accumulatedForceX = 0; this.accumulatedForceY = 0; this.accumulatedForceZ = 0;
    }

    @Override
    public PhysicsBodyState clone() {
        try {
            return (PhysicsBodyState) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("PhysicsBodyState is not cloneable!");
        }
    }

    @Override
    public String toString() {
        return String.format("PhysicsBodyState{pos=(%.2f,%.2f,%.2f), vel=(%.2f,%.2f,%.2f), accForce=(%.2f,%.2f,%.2f)}",
                posX, posY, posZ, velX, velY, velZ, accumulatedForceX, accumulatedForceY, accumulatedForceZ);
    }
}

// Component: Stores static physical properties of an entity
public class PhysicsValues implements Component {
    public float mass;
    public float dragCoefficient;
    public float restitution; // Bounciness (0.0 to 1.0)
    public float hitboxWidth, hitboxHeight, hitboxDepth;

    public PhysicsValues(float mass, float dragCoefficient, float restitution,
                         float hitboxWidth, float hitboxHeight, float hitboxDepth) {
        this.mass = mass;
        this.dragCoefficient = dragCoefficient;
        this.restitution = restitution;
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;
        this.hitboxDepth = hitboxDepth;
    }

    @Override
    public PhysicsValues clone() {
        try {
            return (PhysicsValues) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("PhysicsValues is not cloneable!");
        }
    }

    @Override
    public String toString() {
        return "PhysicsValues{" +
               "mass=" + mass + ", drag=" + dragCoefficient +
               ", restitution=" + restitution +
               ", hitbox=(" + hitboxWidth + "x" + hitboxHeight + "x" + hitboxDepth + ")" +
               '}';
    }
}

// Interface: Provides forces to entities
public interface ForceProvider {
    /**
     * Calculates and returns the force (Fx, Fy, Fz) to apply to an entity.
     * @param entityId The ID of the entity.
     * @param currentPhysicsState The current physics state of the entity.
     * @param currentPhysicsValues The static physics values of the entity.
     * @param deltaTime The time step.
     * @return A float array {Fx, Fy, Fz}.
     */
    float[] getForce(long entityId, PhysicsBodyState currentPhysicsState, PhysicsValues currentPhysicsValues, float deltaTime);
}

// Enum: Represents categories for collision filtering
enum PhysicsFlags {
    ENTITY_COLLISION,
    BLOCK_COLLISION,
    ALL_COLLISIONS
}

// Interface: Consumer for block collision events
public interface IBlockCollisionConsumer {
    void onBlockCollision(long entityId, float blockX, float blockY, float blockZ, String blockType, float impactForce);
}
```

## 2. Conceptual Physics Systems

```java
import java.util.Collection;

// Conceptual system for accumulating forces on entities
class ForceAccumulatorSystem {
    private final Collection<ForceProvider> forceProviders;
    private final EcsWorld ecsWorld; // Access to entity components

    public ForceAccumulatorSystem(EcsWorld ecsWorld, Collection<ForceProvider> forceProviders) {
        this.ecsWorld = ecsWorld;
        this.forceProviders = forceProviders;
    }

    public void run(float deltaTime) {
        for (Entity entity : ecsWorld.getAllEntities()) {
            if (ecsWorld.hasComponent(entity.getId(), PhysicsBodyState.class) &&
                ecsWorld.hasComponent(entity.getId(), PhysicsValues.class)) {
                PhysicsBodyState state = ecsWorld.getComponent(entity.getId(), PhysicsBodyState.class);
                PhysicsValues values = ecsWorld.getComponent(entity.getId(), PhysicsValues.class);

                // Reset accumulated forces for this tick
                state.accumulatedForceX = 0;
                state.accumulatedForceY = 0;
                state.accumulatedForceZ = 0;

                // Apply all forces from providers
                for (ForceProvider provider : forceProviders) {
                    float[] force = provider.getForce(entity.getId(), state, values, deltaTime);
                    state.accumulatedForceX += force[0];
                    state.accumulatedForceY += force[1];
                    state.accumulatedForceZ += force[2];
                }
            }
        }
    }
}

// Conceptual system for updating physics state using Symplectic Euler
class PhysicsBodyStateUpdaterSystem {
    private final EcsWorld ecsWorld; // Access to entity components

    public PhysicsBodyStateUpdaterSystem(EcsWorld ecsWorld) {
        this.ecsWorld = ecsWorld;
    }

    public void run(float deltaTime) {
        for (Entity entity : ecsWorld.getAllEntities()) {
            if (ecsWorld.hasComponent(entity.getId(), PhysicsBodyState.class) &&
                ecsWorld.hasComponent(entity.getId(), PhysicsValues.class)) {
                PhysicsBodyState state = ecsWorld.getComponent(entity.getId(), PhysicsBodyState.class);
                PhysicsValues values = ecsWorld.getComponent(entity.getId(), PhysicsValues.class);

                // Calculate acceleration (F=ma => a=F/m)
                float accX = state.accumulatedForceX / values.mass;
                float accY = state.accumulatedForceY / values.mass;
                float accZ = state.accumulatedForceZ / values.mass;

                // Symplectic Euler Integration:
                // 1. Update velocity first using current acceleration
                state.velX += accX * deltaTime;
                state.velY += accY * deltaTime;
                state.velZ += accZ * deltaTime;

                // 2. Update position using the *new* velocity
                state.posX += state.velX * deltaTime;
                state.posY += state.velY * deltaTime;
                state.posZ += state.velZ * deltaTime;

                // Reset accumulated forces after they've been used
                state.accumulatedForceX = 0;
                state.accumulatedForceY = 0;
                state.accumulatedForceZ = 0;

                // Basic ground collision (conceptual)
                if (state.posY < 0) {
                    state.posY = 0;
                    state.velY *= -values.restitution; // Bounce based on restitution
                    if (Math.abs(state.velY) < 0.1f) state.velY = 0; // Dampen
                }
            }
        }
    }
}

// Conceptual Collision Module/System
class CollisionModuleSystem {
    private final EcsWorld ecsWorld;
    private final Collection<IBlockCollisionConsumer> blockConsumers;

    public CollisionModuleSystem(EcsWorld ecsWorld, Collection<IBlockCollisionConsumer> blockConsumers) {
        this.ecsWorld = ecsWorld;
        this.blockConsumers = blockConsumers;
    }

    public void run(float deltaTime) {
        // --- Conceptual Entity-Entity Collision Detection ---
        // (Simplified O(N^2) for illustration)
        List<Entity> physicsEntities = new ArrayList<>();
        for (Entity entity : ecsWorld.getAllEntities()) {
            if (ecsWorld.hasComponent(entity.getId(), PhysicsBodyState.class) &&
                ecsWorld.hasComponent(entity.getId(), PhysicsValues.class)) {
                physicsEntities.add(entity);
            }
        }

        for (int i = 0; i < physicsEntities.size(); i++) {
            for (int j = i + 1; j < physicsEntities.size(); j++) {
                Entity e1 = physicsEntities.get(i);
                Entity e2 = physicsEntities.get(j);

                PhysicsBodyState s1 = ecsWorld.getComponent(e1.getId(), PhysicsBodyState.class);
                PhysicsValues v1 = ecsWorld.getComponent(e1.getId(), PhysicsValues.class);
                PhysicsBodyState s2 = ecsWorld.getComponent(e2.getId(), PhysicsBodyState.class);
                PhysicsValues v2 = ecsWorld.getComponent(e2.getId(), PhysicsValues.class);

                // Simple AABB overlap check
                boolean overlapX = Math.abs(s1.posX - s2.posX) * 2 < (v1.hitboxWidth + v2.hitboxWidth);
                boolean overlapY = Math.abs(s1.posY - s2.posY) * 2 < (v1.hitboxHeight + v2.hitboxHeight);
                boolean overlapZ = Math.abs(s1.posZ - s2.posZ) * 2 < (v1.hitboxDepth + v2.hitboxDepth);

                if (overlapX && overlapY && overlapZ) {
                    System.out.println("  [Collision] Entity " + e1.getId() + " collided with Entity " + e2.getId());
                    // Trigger events, apply damage, resolve interpenetration, etc.
                }
            }
        }

        // --- Conceptual Block Collision Detection and Consumption ---
        // For simplicity, let's assume one entity hits a "ground block"
        for (Entity entity : physicsEntities) {
            PhysicsBodyState state = ecsWorld.getComponent(entity.getId(), PhysicsBodyState.class);
            if (state.posY <= 0.0f) { // Entity is on or below ground level
                for (IBlockCollisionConsumer consumer : blockConsumers) {
                    consumer.onBlockCollision(entity.getId(), state.posX, 0, state.posZ, "ground_block", Math.abs(state.velY));
                }
            }
        }
    }
}
```

## 3. Conceptual Usage

```java
import java.io.Serializable;
import java.util.*;

// Assume Entity (from ecs_overview example), Component, EcsWorld (from ecs_overview example),
// PhysicsBodyState, PhysicsValues, ForceProvider, ForceAccumulatorSystem, PhysicsBodyStateUpdaterSystem,
// CollisionModuleSystem, IBlockCollisionConsumer, PhysicsFlags are all defined above.

public class HytaleEcsPhysicsExample {
    public static void main(String[] args) {
        // Conceptual ECS World setup
        EcsWorld hytaleWorld = new EcsWorld();

        // Conceptual Force Providers
        ForceProvider gravity = (entityId, state, values, deltaTime) -> new float[]{0, -9.81f * values.mass, 0}; // F = mg

        // Conceptual Block Collision Consumer
        IBlockCollisionConsumer terrainCollisionHandler = (entityId, blockX, blockY, blockZ, blockType, impactForce) -> {
            System.out.printf("  [BlockCollisionHandler] Entity %d hit %s block at (%.0f,%.0f,%.0f) with impact force %.2f\n",
                    entityId, blockType, blockX, blockY, blockZ, impactForce);
            // Example: apply fall damage if impactForce is high
        };
        Collection<IBlockCollisionConsumer> blockConsumers = Collections.singletonList(terrainCollisionHandler);


        // Add Physics Systems to the world
        hytaleWorld.addSystem(new ForceAccumulatorSystem(hytaleWorld, Collections.singletonList(gravity)));
        hytaleWorld.addSystem(new PhysicsBodyStateUpdaterSystem(hytaleWorld));
        hytaleWorld.addSystem(new CollisionModuleSystem(hytaleWorld, blockConsumers));


        // Create entities with physics components
        Entity fallingRock = hytaleWorld.createEntity();
        hytaleWorld.addComponent(fallingRock.getId(), new PhysicsBodyState(0, 10, 0, 0, 0, 0)); // Start high up
        hytaleWorld.addComponent(fallingRock.getId(), new PhysicsValues(5.0f, 0.1f, 0.5f, 1.0f, 1.0f, 1.0f)); // Heavy, some drag, moderate bounce

        Entity player = hytaleWorld.createEntity();
        hytaleWorld.addComponent(player.getId(), new PhysicsBodyState(1, 1, 0, 0, 0, 0)); // Player, slightly above ground
        hytaleWorld.addComponent(player.getId(), new PhysicsValues(70.0f, 0.3f, 0.2f, 0.6f, 1.8f, 0.6f)); // Standard player physics


        System.out.println("--- Initial State ---");
        System.out.println("Falling Rock: " + hytaleWorld.getComponent(fallingRock.getId(), PhysicsBodyState.class));
        System.out.println("Player: " + hytaleWorld.getComponent(player.getId(), PhysicsBodyState.class));
        System.out.println("---------------------\n");

        // Simulate game ticks
        float deltaTime = 0.05f; // Small time step for physics
        for (int i = 0; i < 50; i++) { // Simulate 2.5 seconds
            System.out.printf("--- Game Tick %d (Time: %.2fs) ---", i + 1, (i + 1) * deltaTime);
            hytaleWorld.tick(deltaTime);
            System.out.println("Falling Rock: " + hytaleWorld.getComponent(fallingRock.getId(), PhysicsBodyState.class));
            System.out.println("Player: " + hytaleWorld.getComponent(player.getId(), PhysicsBodyState.class));
            System.out.println("-----------------------------------\n");
        }
    }
}
```

### Conceptual Output (Partial, as physics simulation is verbose):

```
--- Initial State ---
Falling Rock: PhysicsBodyState{pos=(0.00,10.00,0.00), vel=(0.00,0.00,0.00), accForce=(0.00,0.00,0.00)}
Player: PhysicsBodyState{pos=(1.00,1.00,0.00), vel=(0.00,0.00,0.00), accForce=(0.00,0.00,0.00)}
---------------------

--- Game Tick 1 (Time: 0.05s) ---
  [BlockCollisionHandler] Entity 2 hit ground_block at (1,0,0) with impact force 0.00
Falling Rock: PhysicsBodyState{pos=(0.00,9.99,0.00), vel=(0.00,-0.49,0.00), accForce=(0.00,-49.05,0.00)}
Player: PhysicsBodyState{pos=(1.00,1.00,0.00), vel=(0.00,0.00,0.00), accForce=(0.00,-686.70,0.00)}
-----------------------------------

... (many more ticks) ...

--- Game Tick 40 (Time: 2.00s) ---
  [Collision] Entity 1 collided with Entity 2
  [BlockCollisionHandler] Entity 1 hit ground_block at (0,0,0) with impact force 4.81
  [BlockCollisionHandler] Entity 2 hit ground_block at (1,0,0) with impact force 0.00
Falling Rock: PhysicsBodyState{pos=(0.00,0.00,0.00), vel=(0.00,2.40,0.00), accForce=(0.00,-49.05,0.00)}
Player: PhysicsBodyState{pos=(1.00,1.00,0.00), vel=(0.00,0.00,0.00), accForce=(0.00,-686.70,0.00)}
-----------------------------------