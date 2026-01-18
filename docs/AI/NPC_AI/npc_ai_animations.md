# Hytale Modding: NPC Animation System

Hytale's NPC Animation System provides comprehensive control over an NPC's visual movements and expressions. It allows modders to trigger animations, react to animation events, and organize animations into groups to create realistic and responsive character behaviors.

## Core Concepts:

*   **Animation Assets:** Animations are typically defined as separate assets (e.g., `.hym` files for models with animations).
*   **Animation Controllers:** These define how animations are played, blended, and transitioned.
*   **Animation States:** NPCs can exist in different animation states (e.g., idle, walking, attacking), with transitions between them.

## Key Hytale APIs/Classes:

*   **`AnimationComponent`:** An ECS component attached to an NPC that manages its current animation state, active animations, and playback properties.
*   **`AnimationPlayer`:** An object within the `AnimationComponent` that controls the playback of individual animations.
*   **`AnimationGroup`:** A configuration that groups related animations (e.g., all walking animations, all attack animations). This facilitates managing and selecting animations programmatically.
*   **`AnimationEvent`:** Events that are triggered at specific points during an animation's playback (e.g., a footstep sound event, a hit event during an attack animation).

## Playing Animations:

Modders can trigger animations on an NPC:

*   **`animationComponent.playAnimation(animationId, options)`:** Plays a specific animation by its ID. `options` might include loop settings, blend times, or playback speed.
*   **`animationComponent.stopAnimation(animationId)`:** Stops a currently playing animation.
*   **`animationComponent.setAnimationState(stateId)`:** Sets the NPC to a predefined animation state, which might automatically handle transitions and play default animations for that state.

## Animation Events:

*   Modders can register listeners for `AnimationEvent`s. These events are configured within the animation asset itself and can signal specific moments during playback.
*   This allows for synchronized game logic (e.g., dealing damage exactly when a sword swing animation connects, playing sound effects at specific animation frames).

## Animation Groups:

*   `AnimationGroup`s allow for organizing animations into logical collections.
*   Modders can then use APIs to select a random animation from a group, or play a specific animation within a group, simplifying animation management.

## Code Examples:

The document explicitly mentions code examples for:

*   **Playing an animation:** Demonstrating how to trigger animations.
*   **Animation events:** Illustrating how to react to events triggered by animations.
*   **Animation groups:** Showing how to define and use animation groups.
