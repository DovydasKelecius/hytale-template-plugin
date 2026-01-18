# Hytale Modding: Flock System

Hytale's Flock System is designed to simulate the collective behavior of groups of entities, such as birds flying in formation, schools of fish, or herds of animals. It provides a framework for creating realistic and dynamic group movements and interactions.

## Core Concepts:

*   **Boids-like Behavior:** The system likely implements principles similar to "boids" (bird-oid object), where individual entities follow simple rules (separation, alignment, cohesion) to produce complex emergent group behavior.
*   **Leader/Follower Dynamics:** Flocks often involve a leader entity and multiple follower entities, with rules governing how followers track the leader.
*   **Obstacle Avoidance:** Flocks need to navigate the environment while maintaining formation and avoiding collisions with world geometry and other entities.

## Key Hytale APIs/Classes:

*   **`Flock`:** The central class representing a group of entities moving together. It manages the rules, members, and overall state of the flock.
*   **`FlockMemberComponent`:** An ECS component attached to individual entities that are part of a flock. It links an entity to its `Flock` and contains flock-specific parameters.
*   **`FlockBehavior`:** Defines the rules and algorithms for how the flock members move and interact (e.g., cohesion, alignment, separation forces).
*   **`FlockProperties`:** A configuration object (likely data-driven) that defines parameters for the flock's behavior, such as maximum speed, turn rate, separation distance, and target weight.

## Functionality:

*   **Creating Flocks:** Modders can create `Flock` instances, defining their behavior and initial properties.
*   **Adding/Removing Entities:** Entities can be dynamically added to or removed from a flock.
*   **Leader Assignment:** Designating a leader entity that others follow.
*   **Behavior Configuration:** Customizing flocking rules and parameters (e.g., how strongly entities try to stay together, avoid each other, or match speed).
*   **Obstacle Avoidance:** Flocks implicitly or explicitly avoid obstacles in the environment.

## Code Examples:

The document explicitly mentions code examples for:

*   **Creating a flock:** Demonstrating how to instantiate a `Flock` object.
*   **Adding/removing entities:** Showing how to manage flock membership.
*   **Flock behavior:** Illustrating how `FlockBehavior` is configured.
*   **Flock properties:** Defining parameters for the flock.
