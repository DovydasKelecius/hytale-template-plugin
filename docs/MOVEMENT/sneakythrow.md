# com.hypixel.hytale.sneakythrow

This package contains a utility class for throwing checked exceptions without declaring them.

## Overview

The `sneakythrow` package provides a utility to "sneakily" throw checked exceptions in Java without needing to declare them in method signatures or catch them. This can be useful in specific scenarios where checked exceptions are cumbersome, though it should be used with caution as it bypasses Java's exception handling mechanisms.

### Key Classes

*   `SneakyThrow`: The main utility class that provides methods to throw checked exceptions as unchecked ones.
*   `ThrowableRunnable`: A functional interface for a runnable that can throw an exception.

### Functional Interfaces

The package also defines several functional interfaces that extend standard Java functional interfaces to allow for throwing checked exceptions:

*   **Consumers:**
    *   `ThrowableBiConsumer`
    *   `ThrowableConsumer`
    *   `ThrowableIntConsumer`
    *   `ThrowableTriConsumer`
*   **Functions:**
    *   `ThrowableBiFunction`
    *   `ThrowableFunction`
*   **Suppliers:**
    *   `ThrowableIntSupplier`
    *   `ThrowableSupplier`

### Sub-packages

*   `consumer`: Contains functional interfaces for consumers that can throw exceptions.
*   `function`: Contains functional interfaces for functions that can throw exceptions.
*   `supplier`: Contains functional interfaces for suppliers that can throw exceptions.
