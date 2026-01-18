# Hytale Modding: Lighting System

Hytale's lighting system is responsible for simulating and rendering light within the game world, encompassing both natural and artificial light sources. It's designed to be efficient and allows for programmatic interaction by modders.

## Core Architecture:

*   **`ChunkLightingManager`:** This is the central manager for lighting within a chunk. It orchestrates the lighting calculations and updates.
*   **`LightCalculation`:** An interface that defines the algorithm used for calculating light. The system uses a pluggable design, allowing different implementations.
    *   **`FloodLightCalculation`:** The default implementation for `LightCalculation`. This algorithm is responsible for computing light levels.
*   **`ChunkLightData`:** Lighting data is stored on a per-block section basis within this component. It holds light levels across **4 channels**:
    *   Red
    *   Green
    *   Blue
    *   Sky (for ambient and sunlight)

## `FloodLightCalculation` Details:

The `FloodLightCalculation` algorithm efficiently computes light in two main phases:

*   **Local Light:** Calculates the direct light originating from blocks (e.g., torches, glowstone) and fluids within a specific chunk section.
*   **Global Light:** Propagates light from neighboring chunk sections, ensuring seamless light transitions and interactions across chunk boundaries.

## Modder Interaction:

Modders can interact with the lighting system through various APIs:

*   **Querying Light Levels:**
    *   `blockSection.getLocalLight()`: Retrieves the local (block/fluid) light level at a specific position within a block section.
    *   `blockSection.getGlobalLight()`: Retrieves the global (propagated) light level.
*   **Forcing Relight:** Modders can force the lighting system to recalculate and update light levels. This is typically done by invalidating light data at a specific granularity:
    *   `world.getChunkLighting().invalidateLight(chunk/section)`: Invalidates light for an entire chunk or a specific section within it, triggering a relight operation. This is necessary when custom blocks that emit light are placed or removed, or when existing blocks change their light properties.

### No direct code examples were explicitly provided on the source page for "Lighting System".
The content describes the concepts, components, and API methods but does not offer Java code snippets demonstrating their specific implementation or usage.
