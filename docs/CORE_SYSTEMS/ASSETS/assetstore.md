# com.hypixel.hytale.assetstore

This package contains classes for managing and storing game assets.

## AssetStore

The `AssetStore` class is an abstract class that provides the basic functionality for storing and managing assets of a specific type. It is responsible for loading assets from disk, decoding them, and storing them in an `AssetMap`. It also handles asset dependencies, so that assets are loaded in the correct order.

### Key Features

*   **Asset Loading:** The `AssetStore` can load assets from a directory or from a list of paths.
*   **Asset Decoding:** The `AssetStore` uses an `AssetCodec` to decode assets from their raw format (e.g., JSON) into Java objects.
*   **Asset Storage:** The `AssetStore` stores assets in an `AssetMap`, which is a specialized map that is optimized for storing assets.
*   **Asset Dependencies:** The `AssetStore` can be configured with a list of other asset stores that it depends on. This ensures that assets are loaded in the correct order.
*   **Asset Validation:** The `AssetStore` can validate assets to ensure that they are well-formed and that they meet certain criteria.
*   **File Monitoring:** The `AssetStore` can monitor files for changes and automatically reload assets when they are modified.

## Other Classes

The `com.hypixel.hytale.assetstore` package also contains the following classes:

*   **`AssetMap`**: An interface for a map that stores assets.
*   **`AssetPack`**: Represents a pack of assets.
*   **`AssetReferences`**: A class that holds references to other assets.
*   **`AssetRegistry`**: A class that manages all the asset stores in the game.
*   **`JsonAsset`**: A base class for assets that are stored in JSON format.
*   **`RawAsset`**: A class that represents a raw, unprocessed asset.
*   **`codec`**: A sub-package that contains classes for encoding and decoding assets.
*   **`event`**: A sub-package that contains events related to asset loading and management.
*   **`iterator`**: A sub-package that contains classes for iterating over assets.
*   **`map`**: A sub-package that contains different implementations of the `AssetMap` interface.

The classes in this package work together to provide a robust and efficient system for managing game assets.
