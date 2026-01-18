# Hytale Modding: NPC Role System

Hytale's NPC Role System defines the high-level behaviors, attributes, and capabilities of non-player characters. A role encapsulates an NPC's core identity, including its AI logic (instructions, state machines), combat parameters, and potentially its visual and auditory characteristics. Roles are asset-driven, allowing modders to create diverse NPC types.

## Core Concepts:

*   **Behavioral Blueprint:** A role serves as a comprehensive blueprint for an NPC's behavior, dictating how it perceives the world, makes decisions, and executes actions.
*   **Data-Driven:** Roles are primarily configured through asset files (e.g., JSON), enabling modders to define and modify NPC behaviors without direct code changes.
*   **Modularity:** Roles integrate with other AI systems like Instructions, Decision Makers, Navigation, and Animation, providing a cohesive framework for NPC intelligence.

## Key Hytale APIs/Classes:

*   **`Role` Class:** The base class for all NPC roles. Custom roles will extend this.
*   **`RoleType`:** A unique identifier for each role, similar to `InstructionType` or `ComponentType`.
*   **`NPCPlugin`:** Responsible for registering custom roles.
*   **`NPCEntity` Component:** This component, attached to an `NPCEntity`, holds the `Role` instance for that NPC.

## Role Configuration:

A `Role` typically encapsulates references to various configuration objects:

*   **Instructions:** The set of atomic actions an NPC can perform.
*   **Decision Makers:** The logic that determines which instructions to execute based on conditions.
*   **State Machines:** For defining complex behavioral sequences and transitions.
*   **Combat Parameters:** Such as attack ranges, damage values, defensive behaviors.
*   **Visual/Auditory Assets:** References to models, animations, sounds specific to the role.

## Custom Roles:

Modders can define custom roles to create entirely new NPC behaviors. This involves:

*   **Defining a Custom `Role` Class:** Extending the base `Role` class and adding specific fields or logic.
*   **Creating a `BuilderCodec`:** For serializing/deserializing the custom role from its JSON definition.
*   **Registering the `Role`:** Registering the custom role with the `NPCPlugin` during plugin setup.

## Code Examples:

The document explicitly mentions code examples for:

*   **Registering a custom role:** Demonstrating how to make a new role type known to the server.
*   **Defining a custom role:** Illustrating the structure and properties of a custom `Role` class.
