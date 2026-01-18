# Hytale Gameplay Systems: Damage System

Hytale's Damage System is a fundamental part of its gameplay, built upon the Entity-Component-System (ECS) architecture. It comprehensively manages all aspects of damage dealt to and taken by entities, primarily through an event-driven model.

## Core Components and Concepts:

*   **`DamageDataComponent`:** This component is attached to entities to track combat-related data, such as recent damage taken, damage dealt, and other combat statistics.
*   **`KnockbackComponent`:** Handles the physical knockback effects applied to an entity when it takes damage.
*   **`Damage` Class:** Damage events are encapsulated within the `Damage` class, which extends `CancellableEcsEvent`. This allows for damage events to be intercepted and modified or even canceled before they are fully processed. The `Damage` object contains crucial details, including:
    *   **Initial Amount:** The base amount of damage.
    *   **Cause:** The reason for the damage, often an asset-defined type.
    *   **Source:** The origin of the damage (e.g., an attacking entity, an environmental hazard).
*   **Damage Sources:** Damage can originate from various sources, providing flexibility in gameplay design:
    *   Entities (e.g., another player, a monster)
    *   Projectiles (e.g., arrows, fireballs)
    *   The environment (e.g., falling, lava, suffocation)
    *   Commands (e.g., administrator commands)
*   **Damage Causes (Assets):** Damage causes are defined as assets (likely JSON files), allowing modders to customize their properties. These properties can include:
    *   Durability loss for items.
    *   Stamina loss for entities.
    *   Whether the damage bypasses certain resistances.

## Damage Processing Pipeline:

The damage system processes damage through a well-defined three-stage pipeline:

1.  **"Gather Damage Group":** This is the initial stage where raw damage is applied and grouped.
2.  **"Filter Damage Group":** In this crucial stage, the damage can be modified or even canceled. This is where mechanics like armor-based damage reduction, invulnerability frames, or damage type resistances would be applied.
3.  **"Inspect Damage Group":** After filtering, this stage handles post-modification effects. This includes triggering visual feedback (particle effects, hit animations), auditory feedback (sound effects), and applying knockback.

## Knockback System:

Processed after the damage calculations, the knockback system applies physical displacement to entities. It supports different knockback types, and its effectiveness can be mitigated by certain items or armor.

## Death Processing:

When an entity's health reaches zero, a `DeathComponent` is added to it. This triggers a separate death processing pipeline, which orchestrates various death-related effects, such as:

*   Playing death animations.
*   Dropping items from the entity's inventory.
*   Respawn mechanics for players.
*   Other custom effects defined by modders.

This comprehensive damage system provides a powerful and flexible framework for creating diverse combat scenarios and environmental hazards in Hytale.
