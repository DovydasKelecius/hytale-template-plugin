# Hytale Gameplay Systems: Movement & Locomotion

Hytale's Movement & Locomotion System is responsible for governing how entities, particularly players, move and interact with the game world. It is built upon the ECS architecture, utilizing specific components and systems to manage diverse movement states and behaviors.

## Core Components and Concepts:

*   **`MovementStatesComponent`:** This component is attached to entities capable of movement. It serves as a data container that tracks an entity's current active movement states.
*   **`MovementStates` Class:** This class, often used within or in conjunction with `MovementStatesComponent`, defines a collection of boolean flags representing various movement states. These states are crucial for driving animations, applying physics modifications, and triggering specific gameplay logic. Examples of `MovementStates` include:
    *   `idle`
    *   `walking`
    *   `sprinting`
    *   `jumping`
    *   `crouching`
    *   `gliding`
    *   `swimming`

## Systems for Movement Management:

Dedicated systems interact with the `MovementStatesComponent` to manage its lifecycle and synchronize states:

*   **`AddSystem`:** Responsible for initializing the `MovementStatesComponent` when an entity is created or added to the world.
*   **`TickingSystem`:** Continuously monitors and updates the `MovementStatesComponent` during gameplay. This system is crucial for:
    *   Synchronizing movement states between the server and clients, ensuring a consistent experience.
    *   Applying physics calculations based on the current movement state.
    *   Triggering relevant animations.
*   **`PlayerInitSystem`:** Specifically for players, this system handles the restoration of saved player movement states (e.g., if a player logs back in, their movement state might be restored to their last known state).

## Specific Movement Mechanics:

Hytale implements various advanced movement mechanics, each managed by dedicated systems that leverage the core movement components:

*   **Sprinting:** A system that detects player input for sprinting, updates the `sprinting` flag in `MovementStates`, and potentially modifies movement speed or stamina consumption.
*   **Crouching:** A system that handles crouching input, updates the `crouching` flag, and might adjust the entity's hitbox or allow access to smaller spaces.
*   **Mantling:** A system that enables entities to climb over small obstacles automatically. This involves detecting nearby ledges, adjusting entity position, and updating movement states accordingly.
*   **Gliding:** A system that allows entities to glide through the air, often after jumping or falling. This would involve specific `gliding` states, reduced fall damage, and potentially custom physics interactions.

These systems work in concert to provide a rich and responsive movement experience, allowing for a wide range of player and entity locomotion behaviors within the Hytale world.
