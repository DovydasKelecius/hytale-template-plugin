# Hytale Modding: AI Instructions System

Hytale's AI Instructions System provides the fundamental building blocks for NPC behaviors. Instructions are granular, atomic actions that an NPC can execute, forming the operational layer of its artificial intelligence. This system is tightly integrated with Decision Makers, which decide which instructions to give an NPC.

## Core Concepts:

*   **Atomic Actions:** Instructions represent single, focused tasks (e.g., "move to a point," "attack a target," "play an animation"). They are designed to be composable and reusable.
*   **Execution Context:** Instructions execute within the context of an NPC, accessing its components (like `NavigationComponent`, `CombatComponent`) and the AI's `Blackboard` (for shared information).
*   **Lifecycle:** An instruction typically has a lifecycle, including `start()`, `tick()`, and `finish()` or `cancel()` methods.
*   **Success/Failure:** Instructions usually report a success or failure status upon completion, which Decision Makers can use to inform subsequent actions.

## API and Implementation:

*   **`IInstruction` Interface:** The main interface for defining custom instructions. It provides methods like:
    *   `start(NPC, Blackboard, ...)`: Called when the instruction begins.
    *   `tick(NPC, Blackboard, deltaTime, ...)`: Called every game tick while the instruction is active, where the main logic resides.
    *   `finish(NPC, Blackboard, ...)`: Called when the instruction successfully completes.
    *   `cancel(NPC, Blackboard, ...)`: Called if the instruction is interrupted or fails.
*   **`InstructionType`:** Each instruction needs a unique type, allowing it to be registered and referenced (e.g., in asset files).
*   **Codec Registration:** Instructions, like other data-driven elements in Hytale, are registered with a codec system, enabling their configuration via asset files.

## Integration with Other AI Systems:

*   **Decision Makers:** Decision Makers are responsible for evaluating conditions and queuing instructions for an NPC to execute.
*   **Blackboard:** Instructions often read from and write to the Blackboard, updating shared knowledge based on their actions or observations.
*   **Navigation System:** Movement-related instructions would interact with the Navigation System to perform pathfinding and movement.
*   **Animation System:** Instructions might trigger specific animations to accompany their actions.

## Custom Instructions:

Modders can create custom instructions to define unique behaviors for their NPCs. This involves:

*   Implementing the `IInstruction` interface.
*   Defining the instruction's unique `InstructionType`.
*   Registering the instruction with the appropriate codec.

## Code Examples:

The document explicitly mentions "custom instructions" examples, demonstrating how to define and implement specific instruction logic.
