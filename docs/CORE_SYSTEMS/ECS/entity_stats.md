# Hytale ECS: Entity Stats System

Hytale's Entity Stats System is an integral part of its Entity-Component-System (ECS) architecture, designed to manage various attributes of entities such as Health, Stamina, and Mana.

## Core Concepts:

*   **`EntityStatMap` Component:** Each entity possesses an `EntityStatMap` component. This component acts as a container for all the individual statistics (`EntityStatValue` instances) associated with that entity.
*   **`EntityStatValue`:** These instances represent individual stats. They store the current value of a stat, along with its minimum and maximum bounds.
*   **Modifiers:** `EntityStatValue` objects can have active `Modifier`s applied to them. Modifiers are mechanisms that dynamically adjust a stat's properties.

## Modifiers:

Modifiers play a crucial role in altering entity stats.
*   **Types of Modifiers:** An example is `StaticModifier`, which can adjust a stat's bounds (minimum and maximum values).
*   **Calculation:** Modifiers can apply changes using either additive or multiplicative calculations, offering flexibility in how stats are modified.
*   **Application and Removal:** Modifiers are applied and removed using unique keys. It's a best practice to prefix these keys with the plugin's name to prevent naming conflicts across different mods.
*   **Dynamic Adjustment:** Modifiers allow for dynamic changes to an entity's capabilities, for instance, temporarily boosting health or reducing movement speed.

## Client-Side Prediction and Custom Stats:

*   **Predictable Updates:** The system supports predictable updates, which is essential for client-side prediction, ensuring a smoother player experience by reducing perceived latency.
*   **Custom Stat Types:** Developers can define custom stat types using JSON asset files. These custom stats can include regeneration rules with built-in conditions, allowing for complex and nuanced stat behaviors (e.g., health regeneration that only occurs when out of combat).

By leveraging the Entity Stats System, Hytale allows for a robust and flexible way to manage and modify entity attributes, crucial for diverse gameplay mechanics and character progression.
