# com.hypixel.hytale.component

This package contains the entity-component system (ECS) framework.

## Overview

The `component` package provides a robust and flexible Entity-Component System (ECS) framework for managing game entities and their behaviors. This architecture promotes modularity, reusability, and performance by separating data (components) from logic (systems).

### Key Concepts

*   **Entity:** A unique identifier for a game object.
*   **Component:** Data associated with an entity.
*   **System:** Logic that operates on entities based on their components.
*   **Archetype:** A unique combination of components.
*   **Resource:** Global data accessible by systems.

### Key Classes

*   `Component`: The base interface for all components.
*   `ComponentRegistry`: Manages component types and their registrations.
*   `System`: The base interface for all systems.
*   `SystemGroup`: Organizes and executes systems in a defined order.
*   `Archetype`: Represents a unique set of components an entity possesses.
*   `Resource`: Base class for global data accessible by systems.

### Sub-packages

*   `data`: Contains data classes related to the ECS.
*   `dependency`: Contains classes for managing system dependencies.
*   `event`: Contains events related to the ECS.
*   `metric`: Contains classes for collecting ECS performance metrics.
*   `query`: Contains classes for querying entities based on their components.
*   `spatial`: Contains classes for spatial partitioning and queries within the ECS.
*   `system`: Contains classes for defining and managing systems.
*   `task`: Contains classes for parallel task execution within the ECS.
