# com.hypixel.hytale.procedurallib

This package contains classes for procedural generation.

## Overview

The `procedurallib` package provides a library of tools and algorithms for procedural content generation. This is a core component for generating various aspects of the Hytale world, including terrain, biomes, and other environmental features.

### Key Concepts

*   **Noise Functions:** Various implementations of noise functions (Perlin, Simplex, Cellular) used to generate natural-looking patterns.
*   **Conditions:** Logic for evaluating specific criteria, often used to determine where certain generated features can exist.
*   **Properties:** Modifiers that can be applied to noise functions to alter their behavior (e.g., blend, fractal, gradient).
*   **Suppliers:** Functional interfaces for providing generated values.
*   **Randomization:** Tools for introducing controlled randomness into the generation process.

### Key Classes

*   `NoiseFunction`: Base interface for all noise functions.
*   `NoiseFunction2d`, `NoiseFunction3d`: Specialized noise functions for 2D and 3D generation.
*   `Condition`: Base interface for conditions.
*   `NoiseProperty`: Base interface for noise properties.
*   `CoordinateRandomizer`: Utility for randomizing coordinates.

### Sub-packages

*   `condition`: Contains classes for defining various conditions.
*   `json`: Contains JSON loaders for procedural generation assets.
*   `logic`: Contains the core logic and implementations of noise functions and other generation algorithms.
*   `property`: Contains classes for defining properties that modify noise functions.
*   `random`: Contains utilities for randomization in procedural generation.
*   `supplier`: Contains functional interfaces for supplying procedural values.
*   `util`: Contains general utility methods for procedural generation.
