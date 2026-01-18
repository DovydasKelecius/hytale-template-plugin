# com.hypixel.hytale.builtin.asseteditor

This package contains the in-game asset editor.

## Overview

The `asseteditor` package provides a system for editing game assets in real-time. It includes a plugin class for the feature and classes for managing the asset editor UI and functionality.

### Key Classes

*   `AssetEditorPlugin`: The main plugin class for the asset editor feature.
*   `AssetEditorPacketHandler`: A packet handler for the asset editor.
*   `AssetEditorGamePacketHandler`: A game packet handler for the asset editor.
*   `EditorClient`: Represents a client that is using the asset editor.
*   `AssetTree`: A UI component that displays the asset hierarchy.

### Sub-packages

*   `assettypehandler`: Contains classes for handling different types of assets.
*   `data`: Contains data classes related to the asset editor.
*   `datasource`: Contains classes for providing data to the asset editor.
*   `event`: Contains events related to the asset editor.
*   `util`: Contains utility classes for the asset editor.
