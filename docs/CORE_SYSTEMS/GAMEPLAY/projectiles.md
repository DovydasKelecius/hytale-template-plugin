# Hytale Gameplay Systems: Projectile System

Hytale's Projectile System is a comprehensive framework for managing all aspects of projectiles in the game, from their initial launch to their impact. It leverages the ECS architecture and asset-based configuration to provide a flexible and customizable system.

## Key Components and Concepts:

*   **`ProjectileConfig` JSON Assets:** The behavior of projectiles is primarily defined through `ProjectileConfig` JSON assets. These assets allow modders to specify a wide range of properties, including:
    *   **Physics Properties:** Density, gravity, bounciness, and terminal velocity.
    *   **Visuals:** Model, textures, particle effects.
    *   **Sound Effects:** Sounds associated with launch, flight, and impact.
*   **`Projectile` Component:** This component identifies an entity as a projectile within the ECS. It holds references to its `ProjectileConfig` and other runtime data.
*   **`PredictedProjectile` Component:** For client-side prediction, this component allows the client to locally simulate the projectile's trajectory and behavior, reducing perceived latency. The server still performs authoritative checks.

## Trajectory and Physics:

*   **`StandardPhysicsTickSystem`:** This system is responsible for simulating the trajectory and physics of projectiles. It processes various factors based on the `ProjectileConfig` and a `StandardPhysicsConfig`:
    *   **Gravity:** Applies gravitational forces.
    *   **Velocity:** Manages the projectile's speed and direction.
    *   **Drag:** Simulates air resistance, slowing the projectile over time.
    *   **Rotation:** Handles the visual rotation of the projectile model as it flies.

## Collision and Impact:

*   **`ProjectileImpact` Interactions:** When a projectile collides with an entity or the environment, it triggers a `ProjectileImpact` interaction. This interaction can then initiate a chain of effects.
*   **Configurable Bounce and Sticking:** The physics properties defined in `ProjectileConfig` determine how the projectile behaves upon collision. This can include:
    *   **Bouncing:** The projectile reflects off the surface.
    *   **Sticking:** The projectile embeds itself in the surface.
    *   **Ricocheting:** A combination of bouncing with a change in trajectory.
*   **Impact Effects:**
    *   **`ImpactConsumer`:** An interface or mechanism that processes the general effects of a projectile impact (e.g., dealing damage, spawning particles, playing sounds).
    *   **`BounceConsumer`:** A specific consumer for handling effects when a projectile bounces.

The Projectile System offers modders a powerful toolset to create a diverse range of ranged weapons, magical spells, and other flying objects with customizable behaviors and visual feedback.
