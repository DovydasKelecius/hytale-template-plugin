# Hytale ECS: Components

In Hytale's Entity-Component-System (ECS) architecture, **Components** are fundamental as data containers attached to entities. They are responsible for defining an entity's attributes and characteristics.

## Key Characteristics of Hytale Components:

*   **Data Containers:** Components primarily hold data related to a specific aspect of an entity (e.g., health, position, inventory, visual appearance). They do not contain logic; logic is handled by Systems.
*   **`Cloneable` Interface:** Components must implement the `Cloneable` interface. This is crucial for scenarios where entities need to be copied, ensuring that their associated data (components) can also be duplicated correctly.
*   **Serialization:** Components are designed to be serializable, which allows for their persistence. This means component data can be saved and loaded, enabling features like saving game state or player data.
*   **Examples:**
    *   `HealthComponent`: Stores an entity's `maxHealth` and `currentHealth`.
    *   `FlyingMarker`: A simple component that acts as a boolean flag, indicating whether an entity possesses the ability to fly. The presence of this component signifies the capability, while its absence means the entity cannot fly.

## Component Management:

Components are registered within a plugin's setup phase, making them available for use throughout the game. Their management on entities (adding, removing, or modifying) is typically done through a `Store` and `CommandBuffer`. This approach ensures thread-safe operations, which is vital in a multi-threaded game environment.

By using components, developers can achieve flexible entity composition, allowing for dynamic and modular game object definitions.
