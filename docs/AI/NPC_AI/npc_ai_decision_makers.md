# Hytale Modding: AI Decision Makers System

Hytale's AI Decision Makers system is a central component for defining NPC intelligence, providing a powerful framework for condition evaluation and state-based logic. It enables NPCs to dynamically assess their environment and choose appropriate actions, forming the "brain" of complex AI behaviors. The system is found within the `com.hypixel.hytale.server.npc.decisionmaker` package.

## Core Approaches to AI Logic:

The system supports two main paradigms for structuring NPC AI:

*   **Condition-Based Evaluation:** This approach relies on a core conditions system where `Condition` objects are evaluated. These conditions determine if a specific state is true (e.g., "target is visible," "health is low") or if a particular action should be considered.
*   **State Evaluation:** This is used for implementing more complex finite state machines (FSMs). State evaluators define how an NPC transitions between different behavioral states (e.g., "Idle," "Patrolling," "Attacking") based on evaluated conditions and their priorities.

## The `Condition` Interface:

The `Condition` interface is fundamental for all evaluable criteria in the Decision Makers system:

*   **`evaluate(NPC, Blackboard, ...)` method:** This method is implemented by concrete `Condition`s and contains the logic to determine if the condition is currently met for a given NPC in its current context.
*   **Codec Registration:** Conditions are registered with a codec system, allowing them to be defined and configured externally via asset files (e.g., JSON). This promotes data-driven AI design.
*   **Base Conditions:** The system provides built-in conditions for common logical operations:
    *   **Logical Operators:** `AND`, `OR`, `NOT` conditions for combining multiple conditions into complex Boolean expressions.
    *   **Comparison Conditions:** For comparing various numerical or categorical values (e.g., checking entity health against a threshold, comparing distances to targets).
*   Conditions can be nested to create sophisticated evaluation trees.

## State Evaluators and State Machines:

*   **State Evaluators:** Classes found in `com.hypixel.hytale.server.npc.decisionmaker.stateevaluator`. They are responsible for managing an NPC's state machine, determining which state is active and when transitions should occur.
*   **`StateSupport`:** This class manages an NPC's current state, handles transitions between states, and maintains a history of past states, which is vital for complex, context-aware AI.
*   **`StateTransitionController`:** Configures complex state machines using a builder pattern. It defines the rules and conditions under which an NPC moves from one state to another.
*   **`RoleStateChange` Interface:** Provides lifecycle callbacks for custom logic to be executed when an NPC's state or role changes.

## Integration Flow:

The Decision Makers system integrates seamlessly with other AI components:

*   **Sensors:** Sensors (which often populate the Blackboard) gather information from the world. Decision Makers then query this information (via the Blackboard) through conditions.
*   **Instruction System:** When conditions are met, Decision Makers trigger the execution of `Instructions`, which are the actual behavioral building blocks of the NPC.
*   **Dynamic Evaluation:** State changes within the NPC (driven by instructions or external events) trigger new evaluations by Decision Makers, leading to adaptive behaviors.

## Built-in Condition Types:

The system includes various categories of conditions, enabling modders to create diverse AI:

*   `Entity Conditions`
*   `Combat Conditions`
*   `World Conditions`
*   `Timer Conditions`
*   `Flock Conditions`
*   Modders can also register custom condition types.

## Best Practices and Performance:

*   **Simple Conditions:** Keep individual conditions atomic and focused to improve clarity and reduce bugs.
*   **Prioritize:** Use condition priorities to manage conflicts when multiple conditions could potentially be met.
*   **Avoid Rapid State Changes ("Thrashing"):** Design states and transitions carefully to prevent NPCs from rapidly switching between behaviors.
*   **Caching:** Cache frequently evaluated conditions or Blackboard data to optimize performance.
*   **Thorough Testing:** Rigorously test AI logic, especially state machines and complex conditions, to ensure robust behavior.
*   **Lightweight Evaluations:** Design conditions and their queries to the Blackboard to be as lightweight as possible to minimize performance impact.

### Code examples are explicitly mentioned as being present on the source page (simple/complex condition checks, state machine definition) and will be extracted separately.