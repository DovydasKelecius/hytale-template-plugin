# com.hypixel.hytale.plugin

This package contains the plugin management system.

## Overview

The `plugin` package provides a framework for loading, managing, and interacting with plugins. It defines the core interfaces and classes for plugins, allowing for modular extension of server functionality.

### Key Concepts

*   **Plugin:** A modular extension that adds functionality to the Hytale server.
*   **Plugin ClassLoader:** A specialized class loader for isolating plugin code.
*   **Early Plugin Loading:** A mechanism for loading certain plugins before the main server startup.

### Key Classes

*   `EarlyPluginLoader`: Handles the early loading of plugins.
*   `TransformingClassLoader`: A class loader that can transform bytecode.
*   `ClassTransformer`: An interface for transforming bytecode.

### Sub-packages

*   `early`: Contains classes related to early plugin loading.
