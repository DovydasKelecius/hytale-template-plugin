# com.hypixel.hytale.math

This package contains math-related utility classes.

## Overview

The `math` package provides a collection of utility classes and data structures for mathematical operations, particularly those relevant to 3D game development. It includes classes for vectors, matrices, quaternions, and various geometric calculations.

### Key Concepts

*   **Vectors:** Classes for 2D, 3D, and 4D vectors (e.g., `Vec2f`, `Vec3f`, `Vec3d`, `Vec4f`).
*   **Matrices:** Classes for 4x4 matrices (e.g., `Mat4f`).
*   **Quaternions:** Classes for representing rotations (e.g., `Quatf`).
*   **Bounding Boxes and Shapes:** Classes for defining and manipulating 2D and 3D shapes (e.g., `Box2D`, `Box`, `Sphere`, `Cylinder`).
*   **Raycasting:** Utilities for performing ray-intersection tests.
*   **Iterators:** Specialized iterators for traversing blocks in various patterns (e.g., `BlockIterator`, `CircleIterator`).

### Key Classes

*   `Axis`: An enum representing the X, Y, and Z axes.
*   `Mat4f`: A 4x4 matrix for 3D transformations.
*   `Quatf`: A quaternion for representing rotations.
*   `Vec2f`, `Vec3f`, `Vec3d`, `Vec4f`: Vector classes for different dimensions and precision.
*   `BlockUtil`: Utility methods for block-related calculations.
*   `RaycastAABB`: Performs ray-axis-aligned bounding box intersection tests.

### Sub-packages

*   `block`: Contains utility classes for block-related mathematical operations.
*   `codec`: Contains codecs for math-related data types.
*   `data`: Contains data classes related to math.
*   `hitdetection`: Contains classes for hit detection.
*   `iterator`: Contains specialized iterators for mathematical structures.
*   `matrix`: Contains matrix-related classes.
*   `random`: Contains utilities for random number generation.
*   `range`: Contains classes for defining ranges.
*   `raycast`: Contains classes for raycasting.
*   `shape`: Contains classes for defining and manipulating 2D and 3D shapes.
*   `util`: Contains general math utility methods.
*   `vector`: Contains vector-related classes.
