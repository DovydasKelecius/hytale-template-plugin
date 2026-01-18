# Hytale Server Project Structure Documentation

This document provides a comprehensive overview of the Hytale server's package structure and the functionalities contained within each package. The server is built in Java and utilizes Maven for dependency management.

## Root Level Overview

The project is primarily organized into:
*   `src`: Contains all source code (main and test).
*   `pom.xml`: Maven project configuration.
*   `GEMINI.md`: Initial high-level project summary.
*   `docs`: Directory containing this detailed documentation.

## Package-wise Breakdown

### `com.hypixel.fastutil`
This package contains utility classes for primitive types, based on the fastutil library. It provides specialized and efficient ConcurrentHashMaps and functional interfaces for byte, char, double, float, int, long, and short types. It aims to improve performance by avoiding boxing and unboxing of primitive types.

### `com.hypixel.hytale`
This is the core of the Hytale server, containing all game logic and server functionality. It is further broken down into numerous sub-packages, each handling a specific domain of the game server.

#### `com.hypixel.hytale.assetstore`
This package manages and stores game assets. It provides functionalities for loading, decoding, storing, and validating assets, handling asset dependencies, and monitoring file changes.
**Key Classes:** `AssetStore`, `AssetMap`, `AssetPack`, `AssetRegistry`, `JsonAsset`, `RawAsset`.

#### `com.hypixel.hytale.builtin`
This package contains built-in game features and functionality, each organized into its own sub-package.

##### `com.hypixel.hytale.builtin.adventure`
Contains features related to adventure mode, encompassing quests, farming, and reputation systems.
*   **`camera`**: Manages camera effects like shakes and view bobbing.
*   **`farming`**: Implements crop growth, harvesting, and animal breeding.
*   **`memories`**: Handles player memories, progress tracking, and related UI.
*   **`npcobjectives`**: Defines and manages objectives and tasks for NPCs.
*   **`npcreputation`**: Manages NPC reputation systems and attitudes.
*   **`npcshop`**: Implements shops run by NPCs.
*   **`objectivereputation`**: Manages reputation changes tied to objective completion.
*   **`objectives`**: The core quest and objective system, including tasks, triggers, rewards, and UI.
*   **`objectiveshop`**: Implements shops where objectives can be acquired or submitted.
*   **`reputation`**: A general system for player reputation with factions.
*   **`shop`**: A general-purpose shop system with asset definitions and UI.
*   **`shopreputation`**: Manages reputation based on player interactions with shops.
*   **`stash`**: Implements personal item storage for players.
*   **`teleporter`**: Provides functionality for in-game teleporters.
*   **`worldlocationcondition`**: Defines conditions based on in-world geographical locations.

##### `com.hypixel.hytale.builtin.ambience`
Manages ambient sounds and music within the game world.

##### `com.hypixel.hytale.builtin.asseteditor`
Contains the in-game asset editor for real-time asset modification.

##### `com.hypixel.hytale.builtin.beds`
Implements bed functionality for sleeping and setting spawn points.

##### `com.hypixel.hytale.builtin.blockphysics`
Manages block physics simulations like gravity and falling blocks.

##### `com.hypixel.hytale.builtin.blockspawner`
Provides systems for creating and managing block spawners.

##### `com.hypixel.hytale.builtin.blocktick`
Handles scheduling and execution of block ticks for dynamic block behaviors.

##### `com.hypixel.hytale.builtin.buildertools`
Offers advanced tools for in-game world building and editing, including prefab and brush systems.

##### `com.hypixel.hytale.builtin.commandmacro`
Enables creation and management of in-game command macros.

##### `com.hypixel.hytale.builtin.crafting`
Implements the crafting system, including recipes, stations, and UI.

##### `com.hypixel.hytale.builtin.creativehub`
Manages the creative hub, a central space for creative mode and showcasing player creations.

##### `com.hypixel.hytale.builtin.crouchslide`
Adds player mechanics for crouching and sliding.

##### `com.hypixel.hytale.builtin.deployables`
Manages deployable items like turrets and traps.

##### `com.hypixel.hytale.builtin.fluid`
Simulates fluid dynamics for in-game liquids like water and lava.

##### `com.hypixel.hytale.builtin.hytalegenerator`
The primary world generation library, including biomes, chunks, density, and world structures.

##### `com.hypixel.hytale.builtin.instances`
Manages instanced worlds for quests and isolated gameplay.

##### `com.hypixel.hytale.builtin.landiscovery`
Facilitates discovery of Hytale servers on a local area network.

##### `com.hypixel.hytale.builtin.mantling`
Enables players to mantle over obstacles.

##### `com.hypixel.hytale.builtin.model`
Manages 3D models within the game, including model assets and UI for selection.

##### `com.hypixel.hytale.builtin.mounts`
Implements mounting mechanics for entities, such as horses and minecarts.

##### `com.hypixel.hytale.builtin.npccombatactionevaluator`
Evaluates NPC combat actions to determine their in-fight responses.

##### `com.hypixel.hytale.builtin.npceditor`
Provides an in-game editor for Non-Player Characters (NPCs).

##### `com.hypixel.hytale.builtin.parkour`
Supports player parkour activities with checkpoints and related commands.

##### `com.hypixel.hytale.builtin.path`
Manages pathfinding functionalities for NPCs and other entities.

##### `com.hypixel.hytale.builtin.portals`
Implements portal systems for inter-area or inter-dimensional travel.

##### `com.hypixel.hytale.builtin.safetyroll`
Allows players to perform safety rolls to mitigate fall damage.

##### `com.hypixel.hytale.builtin.sprintforce`
Manages player sprinting mechanics.

##### `com.hypixel.hytale.builtin.tagset`
Provides a system for managing collections of tags applied to game elements.

##### `com.hypixel.hytale.builtin.teleport`
Handles player teleportation, including warps and teleport history.

##### `com.hypixel.hytale.builtin.weather`
Manages in-game weather conditions and their effects.

##### `com.hypixel.hytale.builtin.worldgen`
Specific built-in features related to world generation, complementing `hytalegenerator`.

#### `com.hypixel.hytale.codec`
Provides a framework for encoding and decoding game data, supporting schema-driven validation, inheritance, and various data formats.

#### `com.hypixel.hytale.common`
A collection of general-purpose utility classes for common tasks, such as data structures, benchmarking, threading, and string manipulation.

#### `com.hypixel.hytale.component`
The core Entity-Component System (ECS) framework for managing game entities, their data (components), and logic (systems).

#### `com.hypixel.hytale.event`
Provides a flexible and extensible event bus system for broadcasting and handling events within the server.

#### `com.hypixel.hytale.function`
Contains specialized functional interfaces, extending standard Java interfaces for primitive types and higher arity.

#### `com.hypixel.hytale.logger`
A custom logging framework for the server, offering file logging, Sentry integration, and log message formatting.

#### `com.hypixel.hytale.math`
A collection of utility classes and data structures for mathematical operations relevant to 3D game development, including vectors, matrices, quaternions, and geometric calculations.

#### `com.hypixel.hytale.metrics`
Provides a system for collecting and reporting server performance and operational metrics.

#### `com.hypixel.hytale.plugin`
The framework for loading, managing, and interacting with modular plugins to extend server functionality.

#### `com.hypixel.hytale.procedurallib`
A library of tools and algorithms for procedural content generation, including noise functions, conditions, and randomization.

#### `com.hypixel.hytale.protocol`
Defines the network communication protocol between the Hytale server and client, encompassing packets, data structures, and serialization/deserialization.

#### `com.hypixel.hytale.registry`
A generic system for registering and managing various game objects and configurations, providing centralized access.

#### `com.hypixel.hytale.server`
The central package for the Hytale server's core implementation, handling its lifecycle, client management, world management, entity management, command processing, and integration with other systems.

#### `com.hypixel.hytale.sneakythrow`
A utility package for "sneakily" throwing checked exceptions as unchecked, bypassing standard Java exception handling (use with caution).

#### `com.hypixel.hytale.storage`
Provides utilities for managing data storage, specifically indexed data structures.

#### `com.hypixel.hytale.unsafe`
Contains utility classes that offer direct access to low-level memory operations using `sun.misc.Unsafe` for performance-critical scenarios, requiring careful handling due to potential risks.
