# com.hypixel.hytale.builtin.adventure.objectives

This package contains the core quest and objective system.

## Overview

The `objectives` package provides a powerful and flexible system for creating and managing quests and objectives. It includes a wide range of features, such as:

*   **Objective and Task Management:** The package provides a framework for defining objectives and tasks, and for tracking the player's progress.
*   **Objective Commands:** A set of commands are provided for managing objectives, such as starting, completing, and resetting them.
*   **Objective Triggers:** Objectives can be triggered by a variety of events, such as entering a certain area, talking to an NPC, or collecting a certain item.
*   **Objective Rewards:** Players can be rewarded for completing objectives with items, reputation, or other a variety of other things.
*   **Objective UI:** The package includes a UI for displaying objectives to the player.

### Key Classes

*   `ObjectivePlugin`: The main plugin class for the objective system.
*   `Objective`: Represents a single objective.
*   `ObjectiveTask`: Represents a single task within an objective.
*   `ObjectiveCommand`: A command that allows players to manage their objectives.
*   `ObjectiveAdminPanelPage`: A UI page that allows administrators to manage objectives.

### Sub-packages

*   `admin`: Contains the `ObjectiveAdminPanelPage` class.
*   `blockstates`: Contains block states related to objectives.
*   `commands`: Contains the `ObjectiveCommand` class and other objective-related commands.
*   `completion`: Contains classes related to objective completion.
*   `components`: Contains components related to objectives.
*   `config`: Contains asset definitions for objectives.
*   `events`: Contains events related to objectives.
*   `historydata`: Contains classes for storing historical data about objectives.
*   `interactions`: Contains interactions related to objectives.
*   `markers`: Contains classes related to objective markers.
*   `systems`: Contains systems that manage objectives.
*   `task`: Contains classes that define the different types of tasks that a player can have.
*   `transaction`: Contains classes related to objective transactions.
