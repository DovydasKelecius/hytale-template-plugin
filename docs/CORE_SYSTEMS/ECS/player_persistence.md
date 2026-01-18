# Hytale ECS: Player Persistence

In Hytale, player data persistence is seamlessly integrated into the Entity-Component-System (ECS) architecture, ensuring that player-specific information can be saved and loaded across game sessions and different parts of the game world.

## How Player Data is Managed:

*   **`PlayerStorage`:** This is the core mechanism responsible for saving and loading player data. It acts as an interface between the persistent storage and the active game state.
*   **`Holder<EntityStore>` and `Ref<EntityStore>`:** `PlayerStorage` interacts with these structures to manage player entities. `Holder<EntityStore>` likely represents a more global or stable reference to player data, while `Ref<EntityStore>` might be a dynamic reference within a specific context (like a `World` instance).
*   **`Universe` and `World` Instances:** Player entities, along with their associated data, are managed across the entire game `Universe` (global scope) and individual `World` instances (local scope, e.g., a specific server or dimension).

## Storing Player Configuration and Custom Data:

*   **`PlayerConfigData`:** This component is used to store persistent player information that can be either global (applying across all worlds) or per-world specific. This might include settings, achievements, or other configurable player attributes.
*   **Custom Data with Components:** The ECS design allows for significant extensibility when it comes to player data. Developers can store custom player data by:
    1.  **Defining New Components:** Create new component types that hold the specific data they wish to persist (e.g., a `QuestLogComponent`, `CustomInventoryComponent`).
    2.  **Registering Components with `BuilderCodec`:** To ensure these custom components can be correctly saved and loaded, they must be registered with a `BuilderCodec`. This codec provides the necessary serialization and deserialization logic, allowing the `PlayerStorage` system to handle the custom data effectively.

By utilizing the ECS for player persistence, Hytale offers a modular and flexible system for managing diverse player data, from basic configurations to complex custom information, all while ensuring data integrity and consistency.
