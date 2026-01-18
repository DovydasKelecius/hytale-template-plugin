# Hytale ECS: Physics System

Hytale's Entity-Component-System (ECS) incorporates a sophisticated physics system designed to manage entity movement, interactions, and collision within the game world. This system leverages various components and modules to achieve realistic and performant physics.

## Key Components and Concepts:

*   **`PhysicsBodyState` Component:** This component is central to an entity's physical presence. It stores dynamic information such as an entity's current position, velocity, and potentially other state-dependent physics properties.
*   **`PhysicsValues` Component:** This component defines the intrinsic physical properties of an entity. Examples include mass, drag coefficients, and other static values that influence how an entity reacts to forces and collisions.
*   **Forces and Force Accumulation:**
    *   **`ForceProvider`:** An interface for defining and applying forces to entities. This allows for modular and extensible ways to introduce various types of forces (e.g., gravity, thrust, explosions).
    *   **`ForceAccumulator`:** Responsible for accumulating all forces acting on an entity over a given time step.
*   **Numerical Integration:**
    *   **`PhysicsBodyStateUpdater`:** This component (or system) is responsible for updating an entity's `PhysicsBodyState` based on accumulated forces.
    *   **Symplectic Euler:** The default integration method used is Symplectic Euler, which is known for its stability and energy conservation properties, making it suitable for game physics.

## Collision Detection and Handling:

*   **`CollisionModule`:** Manages the overall collision detection process.
*   **`PhysicsFlags`:** These flags are used to categorize and control how entities interact with different types of objects. They allow for fine-grained control over collision responses, such as:
    *   **Entity Collisions:** Interactions between two entities.
    *   **Block Collisions:** Interactions between an entity and the game world's block structures.
    *   **All Collisions:** A general category for any type of physical interaction.
*   **`IBlockCollisionConsumer`:** An interface or mechanism for handling specific events that occur when an entity collides with a block. This allows for custom behaviors based on the type of block or the nature of the collision (e.g., taking damage from lava blocks, bouncing off spring blocks).

By combining these components and concepts, Hytale's ECS physics system provides a robust framework for simulating dynamic interactions in its game world.
