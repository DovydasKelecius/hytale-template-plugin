# com.hypixel.hytale.function

This package contains functional interfaces.

## Overview

The `function` package provides a collection of functional interfaces similar to those found in `java.util.function`, but extended or specialized for the Hytale server's needs, particularly with primitive types and multiple arguments. These interfaces are used to enable more expressive and efficient lambda-based programming.

### Key Concepts

*   **Primitive Specialization:** Many interfaces provide `int`, `long`, `float`, `double`, `byte`, `char`, and `short` variations to avoid boxing/unboxing overhead.
*   **Arity Extension:** Interfaces for functions, consumers, and predicates with higher arity (more arguments) than standard Java interfaces.

### Key Interfaces

*   **Consumers:**
    *   `BooleanConsumer`
    *   `DoubleQuadObjectConsumer`
    *   `FloatConsumer`
    *   `IntBiObjectConsumer`
    *   `IntObjectConsumer`
    *   `IntTriObjectConsumer`
    *   `QuadConsumer`
    *   `ShortObjectConsumer`
    *   `TriConsumer`
    *   `TriIntConsumer`
*   **Functions:**
    *   `BiDoubleToDoubleFunction`
    *   `BiIntToDoubleFunction`
    *   `BiLongToDoubleFunction`
    *   `BiToFloatFunction`
    *   `QuadBoolFunction`
    *   `ToFloatFunction`
    *   `TriBoolFunction`
    *   `TriFunction`
    *   `TriIntObjectDoubleToByteFunction`
    *   `TriToIntFunction`
*   **Predicates:**
    *   `BiFloatPredicate`
    *   `BiIntPredicate`
    *   `LongTriIntBiObjPredicate`
    *   `ObjectPositionBlockFunction`
    *   `QuadObjectDoublePredicate`
    *   `QuadPredicate`
    *   `TriIntObjPredicate`
    *   `TriIntPredicate`
    *   `TriObjectDoublePredicate`
    *   `TriPredicate`
    *   `UnaryBiPredicate`
*   **Suppliers:**
    *   `CachedSupplier`
    *   `SupplierUtil`

### Sub-packages

*   `consumer`: Contains consumer functional interfaces.
*   `function`: Contains function functional interfaces.
*   `predicate`: Contains predicate functional interfaces.
*   `supplier`: Contains supplier functional interfaces.
