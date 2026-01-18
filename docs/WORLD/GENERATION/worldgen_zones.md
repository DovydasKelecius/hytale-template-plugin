# Hytale Modding: Zone System (World Generation)

Hytale's Zone System provides a high-level organizational structure for world generation, dividing the game world into large-scale regions. Each zone is a self-contained definition that influences the biomes, caves, unique structures, and even the player's discovery experience within its boundaries.

## Core Classes and API:

*   **`Zone` Record:** This is the primary record (similar to a data class) that encapsulates a complete zone definition. It holds:
    *   `id` (int): A unique identifier for the zone.
    *   `name` (String): The internal programmatic name of the zone.
    *   `discoveryConfig` (`ZoneDiscoveryConfig`): How the zone's discovery is presented to the player.
    *   `caveGenerator` (`CaveGenerator`): The generator responsible for caves within this zone.
    *   `biomePatternGenerator` (`BiomePatternGenerator`): Defines the biome distribution within the zone.
    *   `uniquePrefabContainer` (`UniquePrefabContainer`): A container for unique structures (prefabs) exclusive to this zone.
*   **`ZoneDiscoveryConfig`:** Configures the player notification system when a new zone is discovered. Properties include:
    *   `display` (boolean): Whether a notification is shown.
    *   `zone` (String): The display name of the zone.
    *   `soundEventId` (String): The sound played on discovery.
    *   `icon` (String): The icon displayed in the notification.
    *   `major` (boolean): If `true`, the zone is considered "major" for UI purposes.
    *   `duration`, `fadeInDuration`, `fadeOutDuration` (floats): Control the timing of the discovery notification animation.
*   **`ZonePatternGenerator`:** The top-level generator used to determine which `Zone` applies to a given world position. Key aspects:
    *   Uses a `MaskProvider` to transform coordinates and sample a mask image.
    *   Employs `ZoneColorMapping` to translate RGB colors from the mask image into `Zone` objects.
    *   Can calculate `borderDistance` to the nearest zone boundary.
*   **`ZoneGeneratorResult`:** A result object returned by `ZonePatternGenerator`, containing the identified `Zone` and its calculated `borderDistance`.
*   **`ZoneColorMapping`:** A crucial class that maps RGB color values (often from a mask image) to one or more `Zone` objects. It supports mapping a single color to a single zone or to an array of zones for random selection at that color point.
*   **`MaskProvider`:** An interface for transforming world coordinates to mask coordinates and providing mask values. This allows for complex transformations like rotation, scaling, and distortion of zone patterns.
*   **Unique Zones (`Zone.Unique`, `Zone.UniqueEntry`, `Zone.UniqueCandidate`):** These classes manage the placement of special zones that are designed to appear only once in the world.
    *   `UniqueEntry` defines placement rules (e.g., parent zone color, valid parent zone IDs, search radius, padding).
    *   `Unique` combines the unique `Zone` with its asynchronously determined world position.

## Zone Integration:

Zones form the backbone of world generation, integrating with other systems:

*   **Biomes:** Each zone has its own `BiomePatternGenerator` to manage biome distribution.
*   **Caves:** Zones can have custom `CaveGenerator` configurations.
*   **Unique Prefabs:** Zones can contain specific, unique structures managed by a `UniquePrefabContainer` (e.g., dungeons, cities).

## Key Concepts:

*   **Border Distance:** The `borderDistance` calculated by `ZoneGeneratorResult` is vital for creating smooth transitions between zones, facilitating biome fading, feature exclusion, heightmap blending, and environmental gradients.
*   **Multi-Zone Colors:** When a mask image has a single color mapped to multiple zones, a `PointGenerator` is used to deterministically select one of the zones, ensuring smooth transitions.
*   **Unique Zone Placement:** Unique zones are placed asynchronously based on rules defined in `Zone.UniqueEntry`, often within specific parent zones.

### Code examples are explicitly provided on the source page and will be extracted separately.
