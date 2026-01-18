# Hytale Modding: Fluid System

Hytale's Fluid System manages all aspects of liquids and other fluid-like behaviors within the game world. It's an integrated system that handles fluid properties, simulation, and interaction with blocks and entities.

## Core Architecture and Components:

*   **`FluidSection` Component:** Fluid data is stored efficiently per chunk section within a `FluidSection` component. For each block in a chunk section, this component tracks:
    *   **Fluid ID:** The type of fluid (e.g., water, lava, custom fluid).
    *   **Fluid Level:** The fill level of the fluid in that block space.
*   **`FluidPlugin`:** A built-in plugin (or core system) responsible for the overarching management of fluids. Its responsibilities include:
    *   Managing `FluidSection` components.
    *   Handling fluid data migration between versions.
    *   Loading and replicating fluid data across the network.
    *   Ticking fluid simulations.
*   **Fluid Simulation:** Fluid simulation is tightly integrated with the block ticking system.
    *   **`FluidTicker`:** Defines the specific behavior for each fluid type. This includes properties like:
        *   Flow rate.
        *   "Demotion" (how quickly a fluid spreads or lowers its level).
        *   Interaction with other fluids or blocks.

## Client-Side Replication and Assets:

*   **Network Replication:** Changes in fluid state are replicated to clients via network updates, ensuring that all players see a consistent fluid simulation.
*   **Lighting Invalidation:** Any change in fluid levels or types invalidates the local lighting calculations, triggering updates to ensure correct visual rendering.
*   **`Fluid` Assets:** Custom fluids can be defined as `Fluid` assets. These assets likely specify properties such as:
    *   Visual appearance.
    *   Physical properties (density, viscosity).
    *   Behavior rules (e.g., whether it burns entities, applies effects).
*   **`FluidFX` Assets:** Defines specific visual and auditory effects associated with fluids (e.g., splashing sounds, particle effects). Both `Fluid` and `FluidFX` assets are replicated to clients.

## Modder Interaction:

Modders can programmatically interact with the fluid system to place or modify fluids in the world:

*   **Asynchronous Operations:** Modifying fluids requires asynchronous operations, specifically for resolving chunk sections. This is critical for maintaining server performance.
*   **Setting Fluids:** The general process for modders to set fluids in the world involves:
    1.  Asynchronously resolving the relevant `ChunkSection`.
    2.  Calling `fluidSection.setFluid()` to define the fluid ID and level for a specific block.
    3.  Marking the `WorldChunk` as dirty to trigger network updates and saving.
    4.  Setting the block as "ticking" to ensure that the fluid simulation for that block begins or continues.

### No direct code examples were explicitly provided on the source page for "Fluid System".
The content describes the concepts, components, and general process for interacting with the fluid system but does not offer Java code snippets demonstrating their specific implementation or usage.
