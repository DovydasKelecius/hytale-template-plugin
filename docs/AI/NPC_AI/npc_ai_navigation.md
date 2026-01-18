# Hytale Modding: AI Navigation System

Hytale's AI Navigation System provides the tools and framework for Non-Player Characters (NPCs) to move intelligently and dynamically within the game world. It encompasses pathfinding, movement control, and interaction with the environment to enable realistic locomotion.

## Core Concepts and Architecture:

*   **`MovementController`:** This component is central to an NPC's movement. It manages the NPC's desired movement and path, translating high-level goals into actual locomotion.
*   **Pathfinding:** The system is built upon a robust pathfinding algorithm that allows NPCs to navigate complex terrain, avoiding obstacles and finding optimal routes to their destinations.
*   **Movement Targets:** NPCs can be given various types of movement targets, including specific coordinates, other entities, or points of interest.

## Key Hytale APIs/Classes:

*   **`MovementController` Component:**
    *   This is an ECS component that would be attached to an `NPCEntity`. It's where modders primarily interact to control an NPC's movement.
    *   It offers methods to set destinations, manage speeds, and query pathfinding status.
*   **`PathfindingOptions`:** A configuration object that allows modders to customize the pathfinding behavior for an NPC. This can include:
    *   Movement speed.
    *   Obstacle avoidance parameters.
    *   Pathfinding precision.
    *   Whether the NPC can jump or climb.
*   **`NavigationComponent` (Implied/Related):** While not explicitly detailed as a primary API in the summary, a `NavigationComponent` would likely exist to hold the current path and pathfinding state, working in conjunction with the `MovementController`.

## Setting Movement Targets:

Modders can instruct NPCs to move to various types of targets:

*   **`Vector3f` Destination:** Moving to a specific 3D coordinate.
*   **`Ref<EntityStore>` Target:** Following or moving to the location of another entity.
*   **`BlockPos` Target:** Moving to a specific block location.

## Movement Logic:

The Navigation System:

*   Calculates paths asynchronously to avoid blocking the game thread.
*   Handles dynamic obstacle avoidance (e.g., other entities, player-placed blocks).
*   Manages various movement modes (walking, running, flying, swimming).

## Code Examples:

The document explicitly mentions code examples for:

*   **Movement Controller:** Demonstrating how to get and use the `MovementController`.
*   **Pathfinding:** Illustrating how pathfinding is initiated.
*   **Setting Movement Targets:** Showing how to direct an NPC to a destination.
*   **Pathfinding Options:** Configuring pathfinding behavior.
