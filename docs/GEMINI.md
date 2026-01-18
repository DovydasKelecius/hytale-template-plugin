# Hytale Server Documentation

This document provides a high-level overview of the Hytale Server project structure, which is a Java-based server for the game Hytale.

## Project Overview

The project is a Hytale server written in Java, using Maven for dependency management. The source code is organized into a modular structure, with a core engine and various plugins for different game features.

### Key Technologies

*   **Java**: The primary programming language.
*   **Maven**: Used for dependency management and building the project.
*   **fastutil**: A library for efficient primitive type collections.

## Development Conventions

The project follows standard Java coding conventions. The codebase is organized into a hierarchical package structure, with each package responsible for a specific domain of the game server. The project heavily utilizes an Entity-Component System (ECS) architecture.

## Key Packages

*   `com.hypixel.hytale.server`: The core server implementation.
*   `com.hypixel.hytale.component`: The Entity-Component System (ECS) framework.
*   `com.hypixel.hytale.plugin`: The plugin framework for extending server functionality.
*   `com.hypixel.hytale.protocol`: The network protocol for client-server communication.
*   `com.hypixel.hytale.builtin`: Built-in game features, such as adventure mode, crafting, and weather.

This `GEMINI.md` file provides a starting point for understanding the project. For more detailed information, please refer to the `GEMINI_full_summary.md` file.
