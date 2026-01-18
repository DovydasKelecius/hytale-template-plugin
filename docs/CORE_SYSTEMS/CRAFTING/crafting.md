# com.hypixel.hytale.builtin.crafting

This package contains features related to crafting.

## Overview

The `crafting` package provides a system for players to craft items. It includes a plugin class for the feature and classes for defining crafting recipes, managing crafting stations, and handling crafting interactions.

### Key Classes

*   `CraftingPlugin`: The main plugin class for the crafting feature.
*   `CraftingManager`: A component that manages crafting for a player.
*   `RecipeCommand`: A command that allows players to manage crafting recipes.
*   `BenchRecipeRegistry`: A registry for crafting recipes that can be performed at a bench.
*   `CraftingWindow`: A UI window for crafting.

### Sub-packages

*   `commands`: Contains the `RecipeCommand` class.
*   `component`: Contains the `CraftingManager` component.
*   `interaction`: Contains interactions related to crafting.
*   `state`: Contains block states related to crafting stations.
*   `system`: Contains systems that manage crafting.
*   `window`: Contains UI windows for crafting.
