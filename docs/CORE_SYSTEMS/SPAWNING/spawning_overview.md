# Hytale Modding: Spawning System

Hytale's Spawning System is responsible for dynamically creating and managing entities within the game world. This includes both static placements and procedural generation of creatures, NPCs, and other game objects. Modders can extensively customize spawning behaviors through various APIs and data-driven configurations.

## Core Concepts:

*   **Dynamic Entity Creation:** The system facilitates the programmatic creation of entities at specific locations or under certain conditions.
*   **Spawn Rules:** Allows modders to define conditions and criteria for when and where entities should spawn.
*   **Spawn Groups:** Organizes entities into logical groups for collective spawning or management.

## Key Hytale APIs/Classes:

*   **`SpawnManager`:** The central service responsible for managing all spawning operations.
*   **`SpawnableEntity`:** An interface or class representing an entity that can be spawned (e.g., an `NPCEntity`).
*   **`SpawnRule`:** An interface or class defining the conditions for a spawn (e.g., time of day, biome, player proximity).
*   **`SpawnGroup`:** A configuration object that defines a collection of `SpawnRule`s and the entities they can spawn.
*   **`World.spawnEntity(entity)`:** The primary method for directly spawning an entity into the game world.

## Custom Spawning:

Modders can create custom spawning logic by:

*   **Defining Custom `SpawnRule`s:** Implementing new conditions for spawning entities.
*   **Creating Custom `SpawnGroup`s:** Grouping various spawn rules and entities.
*   **Programmatic Spawning:** Directly calling `SpawnManager` or `World` methods to spawn entities under specific circumstances.

## Spawning Configuration:

Many aspects of spawning are configured through asset files:

*   **Entity Prefabs:** Definitions of entities that are spawned.
*   **Spawn Biome Conditions:** Specifying which biomes an entity can spawn in.
*   **Time of Day Restrictions:** Limiting spawns to day or night cycles.
*   **Population Caps:** Controlling the maximum number of a certain entity type that can exist.

## Code Examples:

The document explicitly mentions code examples for:

*   **Spawning an entity:** Demonstrating the basic method for creating an entity instance in the world.
*   **Custom spawn rules:** Illustrating how to define conditions for spawning.
*   **Spawn groups:** Showing how to organize entities for collective spawning.
