# com.hypixel.hytale.builtin.hytalegenerator

This package contains the world generator.

## Overview

The `hytalegenerator` package is responsible for generating the world of Hytale. It is a very large and complex package, and it is not possible to provide a complete documentation of it here. However, this document will provide a high-level overview of the package and its sub-packages.

### Key Concepts

*   **Biomes:** The world is divided into biomes, which are different types of environments.
*   **Chunks:** The world is divided into chunks, which are 16x16x256 blocks in size.
*   **Density:** A 3D noise function that determines the shape of the terrain.
*   **Props:** Small, decorative objects that are placed in the world, such as trees and rocks.
*   **World Structures:** Large, complex structures that are placed in the world, such as dungeons and villages.

### Sub-packages

The `hytalegenerator` package is organized into the following sub-packages:

*   `assets`: Contains asset definitions for the world generator.
*   `biome`: Contains classes related to biomes.
*   `biomemap`: Contains classes for creating and managing biome maps.
*   `bounds`: Contains classes for representing and manipulating 3D bounds.
*   `cartas`: Contains classes for creating and managing noise cartas.
*   `chunkgenerator`: Contains the `ChunkGenerator` class, which is responsible for generating chunks.
*   `commands`: Contains commands for interacting with the world generator.
*   `conveyor`: Contains classes for managing the world generation pipeline.
*   `datastructures`: Contains data structures used by the world generator.
*   `delimiters`: Contains classes for defining delimiters.
*   `density`: Contains classes for creating and managing density functions.
*   `environmentproviders`: Contains classes for providing environment data to the world generator.
*   `fields`: Contains classes for creating and managing noise fields.
*   `framework`: Contains the core framework for the world generator.
*   `iterators`: Contains iterators for traversing the world.
*   `material`: Contains classes for representing materials.
*   `materialproviders`: Contains classes for providing material data to the world generator.
*   `newsystem`: Contains a new, experimental world generation system.
*   `patterns`: Contains classes for creating and managing patterns.
*   `plugin`: Contains the main plugin class for the world generator.
*   `positionproviders`: Contains classes for providing position data to the world generator.
*   `propdistributions`: Contains classes for distributing props in the world.
*   `props`: Contains classes for creating and managing props.
*   `rangemaps`: Contains classes for creating and managing range maps.
*   `referencebundle`: Contains classes for managing reference bundles.
*   `scanners`: Contains classes for scanning the world for suitable locations to place props and world structures.
*   `seed`: Contains classes for managing the world seed.
*   `threadindexer`: Contains classes for managing the thread indexer.
*   `tintproviders`: Contains classes for providing tint data to the world generator.
*   `vectorproviders`: Contains classes for providing vector data to the world generator.
