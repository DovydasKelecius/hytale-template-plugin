# Hytale Modding: Cave Generation

Hytale's Cave Generation system is designed to create complex and visually rich underground networks through a highly procedural and node-based approach. This system offers extensive customization for modders to define unique cave behaviors and appearances.

## Core Architecture:

The cave system is built around several interconnected classes:

*   **`Cave`:** Represents an entire cave system or network.
*   **`CaveType`:** Defines the overall characteristics of a particular type of cave (e.g., "Mineshaft Cave," "Cavernous Cave"). It acts as a blueprint for the `CaveGenerator`.
*   **`CaveNodeType`:** Defines templates for individual nodes within a cave system. A cave node is a segment or feature within the larger cave.
*   **`CaveNode`:** Represents an actual instance of a node within a generated cave, holding its specific position, shape, and properties.

## Cave Shapes and Customization:

The API supports a variety of predefined geometric shapes for cave nodes, which can be further customized with procedural noise for organic, natural appearances:

*   `PIPE`
*   `CYLINDER`
*   `ELLIPSOID`
*   `PREFAB` (allowing for integration of pre-built structures into caves)
*   `EMPTY_LINE`
*   `DISTORTED`

## Cave Generation Algorithm (`CaveGenerator`):

The `CaveGenerator` class orchestrates the cave generation process, typically following these steps:

1.  **Starting a Cave:** Initiates the generation process from a designated starting point.
2.  **Continuing Nodes:** Extends the cave network by adding new nodes based on predefined child entries or rules.
3.  **Generating Prefabs:** Integrates `PREFAB` type nodes, placing pre-built structures within the cave.
4.  **Compiling Structure:** Assembles all the generated nodes and elements into a final, coherent cave structure.

## Modder Capabilities:

Modders can extensively customize the cave generation system by:

*   **Custom `CaveType` Implementations:** Defining new types of caves with unique properties.
*   **Custom `CaveNodeType` Implementations:** Creating new templates for individual cave segments or features.
*   **Custom `CaveNodeShape` Implementations:** Designing bespoke geometric shapes for cave nodes.

This allows for highly specialized cave behaviors and appearances, from simple tunnels to elaborate underground cities.

## Integrated Features:

The cave system integrates with other world generation aspects:

*   **Biome Masking:** Cave generation can respect biome boundaries or blend differently based on underlying biomes.
*   **Prefab Placement:** Seamlessly places prefabs (as `PREFAB` nodes) within the cave structure.
*   **Fluid Level Configurations:** Defines how fluids (e.g., underground lakes, lava flows) generate within caves.

## Best Practices:

*   **Balance Complexity:** While powerful, complex custom cave generation needs careful balancing to avoid excessive computation.
*   **Optimize Performance:** Implementations should prioritize efficiency, especially for large-scale generation.
*   **Thorough Debugging:** Due to the procedural nature, extensive debugging is often required to achieve desired results.

### Code examples are explicitly mentioned as being present on the source page and will be extracted separately.
