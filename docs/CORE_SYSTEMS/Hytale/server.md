# com.hypixel.hytale.server

This package contains the core server implementation.

## Overview

The `server` package is the heart of the Hytale server, encapsulating its core logic, networking, and game management functionalities. It's a complex and extensive package responsible for orchestrating almost all server-side operations.

### Key Concepts

*   **Server Lifecycle:** Manages the startup, shutdown, and ongoing operation of the server.
*   **Client Management:** Handles connections, authentication, and communication with clients.
*   **Game World Management:** Oversees the loading, saving, and ticking of game worlds.
*   **Entity Management:** Manages all entities within the game world, including players, NPCs, and items.
*   **Command Handling:** Processes and executes player and console commands.
*   **Asset Management:** Integrates with the `assetstore` to provide game assets.
*   **Plugin Integration:** Provides the necessary hooks and context for plugins to extend server functionality.

### Key Classes

*   `HytaleServer`: The main server class, responsible for initialization and orchestration.
*   `HytaleServerConfig`: Configuration settings for the server.
*   `Options`: Command-line option parsing for server startup.
*   `ServerManager`: Manages network connections and client sessions.
*   `Universe`: Manages all game worlds and their states.

### Sub-packages

*   `core`: Contains the fundamental components and utilities of the server.
*   `flock`: Implements AI for groups of entities (flocking behavior).
*   `migrations`: Handles data migration between different server versions.
*   `npc`: Contains the core NPC (Non-Player Character) logic and AI.
*   `spawning`: Manages the spawning of entities in the world.
*   `worldgen`: Integrates with the world generation library (`hytalegenerator`).
