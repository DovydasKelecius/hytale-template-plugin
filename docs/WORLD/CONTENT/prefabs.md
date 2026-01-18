# Hytale Modding: Prefab System

Hytale's Prefab System is a powerful tool for defining, managing, and placing pre-built structures within the game world. These prefabs can contain a variety of game elements, including blocks, fluids, entities, and even other nested prefabs. The system provides extensive APIs for programmatic manipulation and integration with world generation.

## Architecture:

The prefab system is primarily composed of:

*   **`PrefabStore`:** A central singleton service responsible for:
    *   Loading prefabs from various sources (server, asset packs, world generation directories).
    *   Saving prefabs to designated locations.
    *   Caching `BlockSelection` objects for efficient retrieval.
    *   Handling path resolution for different prefab categories.
*   **`BlockSelection`:** This class represents the actual content of a prefab. It stores the detailed data for blocks, fluids, and entities within the defined structure. `BlockSelection` objects are designed to be thread-safe and offer methods for various placement scenarios.

## `PrefabStore` API:

The `PrefabStore` offers a range of methods for interacting with prefabs:

*   **Loading:**
    *   `getServerPrefab(path)`, `getAssetPrefab(path)`, `getWorldGenPrefab(path)`: Methods to load prefabs from server-managed, asset-pack-specific, or world-generation-specific directories, respectively. These are `@Nonnull` and throw `PrefabLoadException` if a prefab is not found.
    *   `getAssetPrefabFromAnyPack(path)`: A more lenient loading method that searches across all asset packs and returns `null` if the prefab isn't found.
    *   `getServerPrefabDir(path)`, `getAssetPrefabDir(path)`, `getWorldGenPrefabDir(path)`: Load all prefabs from a given directory.
    *   `getPrefab(absolutePath)`: Loads a prefab from an absolute path with caching.
*   **Saving:**
    *   `saveServerPrefab(key, selection, overwrite)`, `saveAssetPrefab(key, selection, overwrite)`, `saveWorldGenPrefab(key, selection, overwrite)`: Save a `BlockSelection` to the respective prefab directories.
    *   `savePrefab(absolutePath, selection, overwrite)`: Saves to a custom absolute path.
    *   Saving operations can invalidate cache entries and may throw `PrefabSaveException` on errors (e.g., file already exists and `overwrite` is `false`, or I/O issues).

## `BlockSelection` API:

`BlockSelection` provides comprehensive functionality for defining and manipulating prefab content:

*   **Creation:**
    *   `new BlockSelection()`: Create an empty prefab definition.
    *   `new BlockSelection(blockCapacity, entityCapacity)`: Create with initial capacity hints for performance.
    *   `new BlockSelection(existingSelection)`: Clone an existing prefab.
    *   Methods to set its world position, anchor point, and bounding box.
*   **Adding Content:**
    *   `addBlockAtWorldPos()`, `addBlockAtLocalPos()`: Add blocks with specific IDs, rotations, and block entity data.
    *   `addEmptyAtWorldPos()`: Place an air block.
    *   `addFluidAtWorldPos()`, `addFluidAtLocalPos()`: Add fluid blocks (e.g., water, lava) with specified levels.
    *   `addEntityFromWorld()`, `addEntityHolderRaw()`: Include entities within the prefab definition.
    *   `copyFromAtWorld()`: Copy existing blocks and fluids from a part of the game world into the selection.
*   **Querying Content:**
    *   `hasBlockAtWorldPos()`, `hasBlockAtLocalPos()`: Check for block existence.
    *   `getBlockAtWorldPos()`, `getFluidAtWorldPos()`, `getFluidLevelAtWorldPos()`, `getSupportValueAtWorldPos()`, `getStateAtWorldPos()`: Retrieve block/fluid properties.
    *   `getBlockCount()`, `getFluidCount()`, `getEntityCount()`, `getSelectionVolume()`: Get aggregate information.
*   **Iteration:**
    *   `forEachBlock()`, `forEachFluid()`, `forEachEntity()`: Iterate over the individual components of the prefab.
*   **Comparison:**
    *   `compare()`: For comparing prefab content based on conditions.

## Placing Prefabs:

The primary function of `BlockSelection` is to be placed into the world:

*   **`place()` and `placeNoReturn()`:** Methods to insert the prefab's content into a `World` at a specified `Vector3i` position.
*   These methods support:
    *   `FeedbackConsumer`: A callback for reporting placement progress (e.g., to a player).
    *   `BlockMask`: A filter to control which existing blocks in the world are replaced by the prefab.
    *   **Undo Functionality:** The `place()` method can return a `BlockSelection` representing the blocks that were replaced, allowing for undo actions.
    *   Entity callbacks for custom logic upon spawning entities within the prefab.
    *   `World` matching to check if a prefab can be placed in a specific world context or if it matches existing world features.

## `PrefabRotation`:

An enum that handles rotation of prefabs around the Y-axis (0, 90, 180, 270 degrees). It provides utilities to:

*   Rotate `Vector3i`, `Vector3d`, `Vector3l` coordinates.
*   Get rotated yaw angles.
*   Combine rotations.
*   Transform selections by rotating around axes or flipping along them.

## `PrefabWeights`:

A class used for managing weighted random selection of prefabs. This is especially useful for world generation, allowing certain prefabs to appear more frequently than others.

## `PrefabCopyableComponent`:

A component that marks entities as eligible for inclusion when saving a prefab. Entities without this component might be excluded from saved prefab data.

## Related Classes:

*   `PrefabLoadException`: Thrown when a prefab asset fails to load.
*   `PrefabSaveException`: Thrown when an attempt to save a prefab fails (e.g., file exists, I/O error).
*   `BlockMask`: An interface for custom filtering logic during block placement.
*   `FeedbackConsumer`: An interface for receiving progress updates during potentially long-running operations like prefab placement.

### Code examples are explicitly mentioned as being present on the source page and will be extracted separately.
