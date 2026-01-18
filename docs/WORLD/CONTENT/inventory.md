# Hytale Modding: Inventory and Items System

Hytale's Inventory and Items System is a sophisticated framework designed for managing all aspects of in-game items, player inventories, and various types of item storage. It provides a rich API for modders to create custom items, define their properties, and control how they interact within the game world.

## Core Architecture:

The system is built upon several key components:

*   **`Inventory`:** This class serves as the primary manager for a player's entire inventory. It provides high-level access to different sections of a player's storage.
*   **`ItemContainer`:** Represents various types of storage locations for items. This is a versatile abstraction that can represent:
    *   The player's **Hotbar**.
    *   **Armor slots**.
    *   The main **Backpack/Inventory** space.
    *   External containers like chests or crafting tables.
*   **`ItemStack`:** An immutable instance of an item. It represents a specific quantity of a particular `Item` asset, along with its current durability, metadata, and other unique properties. Being immutable means any modification (e.g., changing quantity) returns a *new* `ItemStack` instance.
*   **`Item`:** Represents the definition or asset of an item (e.g., "Wooden Sword", "Health Potion"). These are typically defined in asset files and describe the item's base properties.
    *   **Subtypes:** The documentation mentions `ItemWeapon` and `ItemArmor`, indicating specialized `Item` types with properties relevant to combat or protection.
    *   **`ItemCategory`:** Used for UI organization and filtering, allowing items to be categorized (e.g., "Tools", "Weapons", "Consumables").

## Key Functionalities:

*   **Accessing Inventory Sections:** Modders can access specific `ItemContainer` instances (e.g., hotbar, armor slots) within a player's `Inventory` using predefined IDs.
*   **Item Movement:**
    *   `inventory.moveItem()`: For basic item transfers between slots or containers.
    *   `inventory.smartMoveItem()`: Offers intelligent item movement, such as automatically equipping armor or stacking items.
*   **`ItemStack` Manipulation:** Create `ItemStack` objects with specified quantities, durability, and custom metadata. Due to immutability, operations like changing quantity will yield a new `ItemStack`.
*   **`ItemContainer` Operations:** Perform fundamental operations on `ItemContainer`s, including:
    *   `get/setItem()`: Accessing or placing items in specific slots.
    *   `add/removeItem()`: Adding or removing items, handling partial additions/removals.
    *   `findItem()`: Searching for items within a container.
    *   Moving items between different `ItemContainer` instances.

## Events and Best Practices:

*   **Item-Related Events:** The system dispatches various events that modders can listen to, such as:
    *   `DropItemEvent`: When an item is dropped.
    *   `InteractivelyPickupItemEvent`: When a player picks up an item.
    *   `LivingEntityInventoryChangeEvent`: When an entity's inventory changes.
    *   These events allow for custom logic to be triggered or for item behavior to be modified.
*   **Best Practices for Modders:**
    *   Use predefined section constants for inventory access.
    *   Validate slot indices to prevent errors.
    *   Handle item "remainders" when partial stacks are moved.
    *   Always clone `ItemStack` objects if you intend to modify them (since they are immutable).
    *   Utilize `smartMoveItem()` for common intelligent inventory operations.

### No direct code examples were explicitly provided on the source page for "Inventory and Items System".
The content describes the concepts and API methods but does not offer Java code snippets demonstrating their implementation or usage.
