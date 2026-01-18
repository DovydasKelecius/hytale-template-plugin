# com.hypixel.fastutil

This package contains utility classes for primitive types, based on the fastutil library. It provides specialized and efficient a new ConcurrentHashMap and functional interfaces for byte, char, double, float, int, long, and short types.

## Overview

The `com.hypixel.fastutil` package is organized into sub-packages for each primitive type:

*   `bytes`: Contains classes and interfaces for byte primitives.
*   `chars`: Contains classes and interfaces for char primitives.
*   `doubles`: Contains classes and interfaces for double primitives.
*   `floats`: Contains classes and interfaces for float primitives.
*   `ints`: Contains classes and interfaces for int primitives.
*   `longs`: Contains classes and interfaces for long primitives.
*   `shorts`: Contains classes and interfaces for short primitives.

### FastCollection

The `FastCollection` interface extends `java.util.Collection` and provides additional `forEach` methods that accept primitive types as arguments. This avoids boxing and unboxing of primitive types, which can improve performance.

### ConcurrentHashMaps

Each sub-package contains a specialized `ConcurrentHashMap` for that primitive type (e.g., `Byte2ObjectConcurrentHashMap`, `Char2ObjectConcurrentHashMap`, etc.). These maps are highly concurrent and optimized for primitive keys.

### Functional Interfaces

Each sub-package also contains a set of functional interfaces that are similar to the standard Java functional interfaces (e.g., `Function`, `Predicate`, `Consumer`), but are specialized for primitive types. For example, the `bytes` package contains the following functional interfaces:

*   `Byte2ByteOperator`: Represents an operation that takes two byte arguments and returns a byte result.
*   `Byte2CharOperator`: Represents an operation that takes a byte and a char argument and returns a char result.
*   `Byte2DoubleOperator`: Represents an operation that takes a byte and a double argument and returns a double result.
*   `Byte2FloatOperator`: Represents an operation that takes a byte and a float argument and returns a float result.
*   `Byte2IntOperator`: Represents an operation that takes a byte and an int argument and returns an int result.
*   `Byte2LongOperator`: Represents an operation that takes a byte and a long argument and returns a long result.
*   `Byte2ObjectOperator`: Represents an operation that takes a byte and an object and returns an object result.
*   `Byte2ShortOperator`: Represents an operation that takes a byte and a short argument and returns a short result.
'
These interfaces can be used to write more efficient and expressive code when working with primitive types.
