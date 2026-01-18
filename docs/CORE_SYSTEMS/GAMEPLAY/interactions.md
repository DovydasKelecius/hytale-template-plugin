# Hytale Gameplay Systems: Interaction System

Hytale's Interaction System is a robust and flexible framework built on the ECS architecture, managing how players and entities interact with the game world and each other. It encompasses various types of interactions, triggered by different events and processed through a sophisticated pipeline.

## Core Components:

*   **`InteractionModule`:** Likely the central module orchestrating the interaction system.
*   **`Interactions Component`:** This component is attached to entities that can perform or be the target of interactions. It defines what interactions an entity is capable of or sensitive to.
*   **`InteractionManager Component`:** Manages the active interaction chains for an entity, tracking their state and progression.

## Types of Interactions:

Hytale categorizes interactions based on their trigger and execution context:

*   **Input-based Interactions:** Triggered directly by player input.
    *   `Primary` (e.g., Left-click)
    *   `Secondary` (e.g., Right-click)
    *   `Tertiary` (e.g., Middle-click)
    *   `Ability1-4` (Player-defined abilities)
    *   `ProjectileImpact`, `ProjectileSpawn` (Related to projectile events)
*   **Client-Side Interactions:** Executed on the client, often with server-side validation to prevent cheating. These are typically fast, responsive actions:
    *   `PlaceBlock`, `BreakBlock`, `UseBlock`, `UseEntity`
    *   `Charging`, `Chaining` (for sequential actions)
    *   `ChangeBlock`, `ChangeState`, `FirstClick`, `ApplyForce`
*   **Server-Side Interactions:** Executed exclusively on the server, ensuring authoritative gameplay logic. These often involve significant state changes or impact multiple clients:
    *   `DamageEntity`, `LaunchProjectile`, `SpawnPrefab`, `ApplyEffect`, `ClearEntityEffect`
    *   `ChangeStat`, `OpenContainer`, `OpenPage`, `EquipItem`, `ModifyInventory`, `SendMessage`
    *   `Door`, `LaunchPad`
*   **Control Flow Interactions:** These manage the execution order and logic of interaction chains, allowing for complex behaviors:
    *   `Condition`, `Serial`, `Parallel`, `Repeat`, `Select`
    *   `Replace`, `CancelChain`, `Interrupt`

## Interaction Triggering and Processing:

*   **"Root Interactions":** These serve as entry points for specific interaction types. They define what actions occur when an interaction is activated and can include rules such as cooldowns.
*   **Interaction Chains:** Interactions can be linked together to create complex sequences of actions, enabling sophisticated gameplay mechanics.
*   **Selectors:** Determine which entities are targeted by an interaction. Examples include:
    *   `Raycast`: Targets entities along a line of sight.
    *   `AOECircle`: Targets entities within an Area of Effect (AoE) circle.

## ECS Processing Systems:

Several systems within the ECS are dedicated to processing interactions:

*   **`PlayerAddManagerSystem`:** Responsible for adding `InteractionManager` components to entities when needed.
*   **`TickInteractionManagerSystem`:** Updates active interaction chains on each game tick, advancing their state and executing their logic.
*   **`CleanUpSystem`:** Removes completed or expired interaction data to maintain system efficiency.
*   **`TrackerTickSystem`:** Updates interaction tracking for networked entities, ensuring consistency across the network.

## Additional Features:

The Interaction System also integrates:

*   **Interaction Effects:** Visual effects (particles), sounds, and animations associated with specific interactions.
*   **Context Information:** Provides relevant data about the interaction (e.g., the interacting entity, the target, the interaction point).
*   **Block Interaction Utilities:** Specialized tools for handling common block-related interactions like placing, harvesting, and tracking block changes.

This comprehensive interaction system allows Hytale to support a wide array of dynamic and engaging player and entity interactions, crucial for its sandbox and RPG elements.
