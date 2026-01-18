# Hytale Plugin Programming: GUI Documentation

## 1. Overview of Hytale GUI System

The Hytale server-side GUI system provides a robust framework for managing various user interfaces. It consists of three distinct subsystems: the Windows System, the Pages System, and the HUD System. These systems are managed on a per-player basis and are primarily accessed through the `Player` component.

### Architecture Overview

The `Player` component is central to UI interactions and includes several key managers:

*   **`WindowManager`**: Handles inventory-based UIs such as containers, crafting tables, and processing stations.
*   **`PageManager`**: Manages custom dialogs, menus, and full-screen overlays.
*   **`HudManager`**: Controls persistent on-screen elements like health bars, hotbars, and custom overlays.
*   **`HotbarManager`**: Specifically manages the player's hotbar slots.
*   **`WorldMapTracker`**: Manages the state and display of the world map UI.

These managers can be accessed from the `Player` component, for example:

```java
Player playerComponent = store.getComponent(ref, Player.getComponentType());
WindowManager windowManager = playerComponent.getWindowManager();
PageManager pageManager = playerComponent.getPageManager();
HudManager hudManager = playerComponent.getHudManager();
```

Additionally, `playerComponent.resetManagers()` can be used to reset various UI managers.

### UI File System

Hytale utilizes `.ui` files for defining client-side UI layouts. These are text-based assets that describe the structure, styles, and components of a user interface.

*   **`Common.ui`**: Contains global styles and variables applicable across different UI elements.
*   **`Pages/*.ui`**: Holds page-specific layouts and components.

For plugin development, custom `.ui` files should be placed in `src/main/resources/Common/UI/Custom/` within the plugin's asset pack. These files can then be referenced in Java code using paths like `Custom/MyPage.ui`. Textures within `.ui` files can be loaded using `PatchStyle(TexturePath: "image.png")`.

---

## 2. Windows System

The Windows System is primarily used for inventory-based graphical user interfaces, including chests, crafting tables, processing stations, and other interactive containers. It focuses on displaying item grids and managing player-item interactions.

### Architecture

The system is built around the `WindowManager`, which manages window operations for each player.

*   **`Window`**: The base class for all windows.
*   **`WindowType`**: An enum defining protocol-defined window types for client-side rendering.
*   **`WindowAction`**: Handles interactions sent from the client to the server.

### WindowManager Functionalities

The `WindowManager` is responsible for all window-related operations for a specific player and can be accessed via `Player.getWindowManager()`.

*   **Opening Windows**:
    *   Individual windows: `windowManager.openWindow(myWindow)`
    *   Multiple windows: `windowManager.openWindows(windows)`
    *   The `openWindow` method returns an `OpenWindow` packet containing the window ID, type, JSON data, inventory section, and extra resources.

*   **Updating Windows**:
    *   Manual update: `windowManager.updateWindow(window)`
    *   Mark as changed (update on next server tick): `windowManager.markWindowChanged(windowId)`
    *   The server automatically calls `updateWindows()` to send `UpdateWindow` packets for dirty windows and `validateWindows()` to close invalid `ValidatedWindow` implementations.

*   **Closing Windows**:
    *   Specific window: `windowManager.closeWindow(windowId)`
    *   All windows for a player: `windowManager.closeAllWindows()`
    *   Self-closing: `window.close()`

*   **Window ID Management**:
    *   Server-assigned IDs start from 1 and are auto-incremented.
    *   -1 is an invalid/unassigned ID.
    *   0 is reserved for client-initiated windows.

### Window Types

`WindowType` is an enum that dictates how the client renders a window. Examples include:

*   `Container` (generic item container)
*   `PocketCrafting`
*   `BasicCrafting`
*   `DiagramCrafting`
*   `StructuralCrafting`
*   `Processing`
*   `Memories`

### Creating Custom Windows

Developers can create custom windows by extending the `Window` class and overriding key methods:

*   **`getData()`**: Provides JSON data to the client.
*   **`onOpen0()`**: Logic executed when the window opens.
*   **`onClose0()`**: Cleanup logic when the window closes.
*   **`handleAction()`**: Processes client interactions (`WindowAction` subtypes).

### Window Lifecycle

A window's lifecycle involves:
1.  **Construction**
2.  **ID Assignment** by the `WindowManager`
3.  **Initialization**
4.  **Event Registration**
5.  Calling **`onOpen()`**
6.  **Packet Creation**
7.  **Active State** (handling actions and updates)
8.  **Validation**
9.  **Closing**, which dispatches a `WindowCloseEvent`.

### Window Data

Window data is sent to the client as JSON via the `getData()` method, included in `OpenWindow` and `UpdateWindow` packets. The data string has a maximum size of 4,096,000 bytes (UTF-8 length).

### WindowAction Types

Client interactions are sent as `WindowAction` subtypes, which are processed in the `handleAction()` method. Examples include:

*   `CraftRecipeAction`
*   `SelectSlotAction`
*   `SortItemsAction`

### Window Events

The `WindowCloseEvent` is dispatched when a window closes, allowing for custom event handling and registration.

### Built-in Window Classes

*   **`ContainerWindow`**: A basic implementation of an item container window.
*   **`BlockWindow`**: An abstract base class for windows tied to a specific block position. It implements `ValidatedWindow`, ensuring automatic closure if the player moves too far or the block changes.
*   **`ContainerBlockWindow`**: Extends `BlockWindow` and implements `ItemContainerWindow`, providing built-in handling for `SortItemsAction`.
*   **`ItemStackContainerWindow`**: Designed for containers whose contents are stored within an `ItemStack`.

### Client-Requestable Windows

Some windows can be initiated by client requests. These must be registered in the static `Window.CLIENT_REQUESTABLE_WINDOW_TYPES` map. Client-opened windows use ID 0 and will replace any existing window at that ID.

### Best Practices for Windows

*   **Validate Window State**: Ensure windows remain in a consistent and valid state.
*   **Graceful Closure**: Handle window closures smoothly and perform necessary cleanup.
*   **Use `invalidate()` for Updates**: Efficiently trigger updates for window content.
*   **Appropriate `WindowType`**: Choose the `WindowType` that best matches the window's purpose for correct client rendering.
*   **Limit Open Windows**: Avoid opening an excessive number of windows simultaneously.
*   **Implement `ValidatedWindow`**: For windows that should close under certain conditions (e.g., player movement away from a block).

---

## 3. Pages System (Custom Pages)

The Pages System is designed for creating custom dialogs, menus, and full-screen overlays. It allows developers to build interactive UIs for shops, complex dialogues, and custom interfaces with robust event handling.

### Architecture

The `PageManager` handles the management of custom pages for each player.

*   **`CustomUIPage`**: The base class for all custom pages.
*   **`BasicCustomUIPage`**: Simple pages that do not require explicit event handling.
*   **`InteractiveCustomUIPage`**: Pages designed to handle typed event data from client interactions.

### PageManager Functionalities

The `PageManager` is responsible for opening, updating, and closing pages.

*   **Opening Pages**: The `PageManager` facilitates the display of custom pages to players.
*   **Updating Pages**: Pages can be updated to reflect changes in server-side data or player state.
*   **Closing Pages**: Pages can be closed either programmatically from the server or by client actions, depending on their `CustomPageLifetime` configuration.

### Creating Custom Pages

To create a custom page, you typically extend `CustomUIPage` and implement methods such as:

*   **`build()`**: Defines the initial UI structure and content of the page, often using UI builders.
*   **`onOpen0()`**: Contains logic to be executed when the page is opened.
*   **`onClose0()`**: Contains cleanup logic to be executed when the page is closed.
*   **`handleEvent()`**: Processes events sent from the client, especially for `InteractiveCustomUIPage` instances.

### Page Lifecycle

The lifecycle of a custom page involves its creation, opening, active state (where it can receive and process events), and eventual closing.

### CustomPageLifetime Options

`CustomPageLifetime` defines how a page can be closed. This can include options like `DISMISS_ON_CLOSE` (closes on client-side close events), `NEVER_DISMISS` (requires server-side closure), or others depending on the specific implementation.

### Best Practices for Pages

*   **Modular Design**: Break down complex UIs into smaller, reusable page components.
*   **Efficient Updates**: Only send updates for elements that have changed to minimize network traffic.
*   **Clear Event Handling**: Design clear and robust event handlers for all interactive elements.
*   **Consider `CustomPageLifetime`**: Choose the appropriate lifetime setting based on the page's purpose and user interaction expectations.

---

## 4. HUD System

The HUD (Heads-Up Display) System manages persistent on-screen elements that provide information to the player during gameplay. This includes built-in components like health bars and hotbars, as well as custom overlays.

### Architecture

The `HudManager` is central to the HUD system, controlling HUD visibility and managing custom overlays on a per-player basis.

### HudManager Functionalities

The `HudManager` is accessed via `Player.getHudManager()` and provides control over HUD elements:

*   **Controlling Visibility**:
    *   `hudManager.setVisibleHudComponents()`: Sets which built-in HUD components are visible.
    *   `hudManager.showHudComponents()`: Makes specific built-in components visible without affecting others.
    *   `hudManager.hideHudComponents()`: Hides specific built-in components.
*   **Resetting HUD**:
    *   `hudManager.resetHud(playerRef)`: Resets the HUD to default components and clears any custom HUD overlays.
    *   `hudManager.resetUserInterface(playerRef)`: Resets the entire UI state for the player.
*   **Custom Overlays**: Manages custom HUD elements designed by developers.

### HudComponent Enum

The `HudComponent` Enum lists all built-in HUD components. Examples include:

*   `Hotbar`
*   `Health`
*   `Chat`
*   `Reticle`
*   And many others, each with a specific integer value and description.

### Custom HUD Overlays

Developers can create custom HUD overlays by extending the `CustomUIHud` abstract class and implementing the `build()` method. This method defines the UI structure and content for the custom overlay.

### Event Titles

Large announcement titles can be displayed using `EventTitleUtil`. This utility offers methods to show titles to individual players, all players in a world, or all players in the universe. Options include primary and secondary messages, icons, duration, and fade times.

### Toast Notifications

Small, temporary notifications (toasts) can be displayed using `NotificationUtil`. Notifications can be simple strings or include a style (e.g., `Default`, `Danger`, `Warning`, `Success`), an icon, and a secondary message. Like event titles, notifications can be sent to individual players, world-wide, or universe-wide.

### Kill Feed

The Kill Feed displays kill/death messages. These messages are constructed using `KillFeedMessage` objects, which can include formatted messages for the killer and decedent, along with an icon path.

### Practical Examples

The HUD system allows for a wide range of practical applications, such as:

*   **Minigame HUD**: Displaying game-specific information like scores, timers, or player statistics.
*   **Cinematic Mode**: Temporarily hiding or modifying HUD elements during cinematic sequences.
*   **Boss Fight Setup**: Presenting boss health bars, ability cooldowns, or phase indicators.

### Best Practices for HUD

*   **Minimize Updates**: Only update HUD elements when necessary to reduce performance overhead.
*   **Incremental Updates**: Send small, targeted updates rather than rebuilding the entire HUD.
*   **Cache HUD Instances**: Reuse custom HUD instances to avoid unnecessary object creation.
*   **Respect Player Preferences**: Allow players to customize HUD visibility or position where appropriate.
*   **Cleanup on Disconnect**: Ensure that HUD elements and managers are properly cleaned up when a player disconnects.

---

## 5. UI Building Tools

Hytale provides dedicated builder classes to facilitate the creation and manipulation of user interfaces from server-side code. The primary tools are `UICommandBuilder` and `UIEventBuilder`.

### UICommandBuilder

The `UICommandBuilder` is used to construct commands that modify the UI on the client-side. It offers a fluent API for various UI manipulation tasks.

*   **Purpose**: To build UI manipulation commands for setting values, appending elements, and other structural changes.
*   **Command Types**:
    *   `Append`: Adds new UI elements.
    *   `InsertBefore`: Inserts elements before a specified target.
    *   `Remove`: Deletes UI elements.
    *   `Clear`: Removes all child elements from a parent.
    *   `Set`: Sets properties or values of existing UI elements.
*   **Setting Values**: The builder can set various types of values:
    *   Text content
    *   Localized messages
    *   Numeric values (integers, floats)
    *   Boolean states
    *   Null values
    *   Complex objects, arrays, and lists
*   **Value Referencing**: The `Value` class allows referencing values from other UI documents or dynamic data.

### UIEventBuilder

The `UIEventBuilder` is used to bind client-side UI events to server-side callbacks, enabling interactivity.

*   **Purpose**: To bind UI events (e.g., clicks, value changes) to server-side handlers.
*   **Event Types**:
    *   `Activating` (e.g., click or tap)
    *   `RightClicking`
    *   `ValueChanged`
    *   `MouseEntered`
    *   `MouseExited`
    *   And others, allowing for diverse interactions.
*   **Locking vs. Non-locking Events**:
    *   **Locking Events**: The UI waits for a server response before allowing further interaction. Suitable for critical actions like purchasing items.
    *   **Non-locking Events**: The UI remains responsive while the server processes the event. Suitable for frequent updates or non-critical interactions.

### EventData

`EventData` is a record that wraps a `Map<String, String>` and is used for passing key-value parameters along with events from the client to the server. It provides methods to append string and enum values.

### Complete Example

The documentation provides a comprehensive example of an interactive shop page, showcasing how both `UICommandBuilder` and `UIEventBuilder` are used together to create a functional and dynamic UI. This example typically covers:

*   Defining the `.ui` layout.
*   Building the page on the server.
*   Handling client interactions (e.g., buying an item).
*   Updating the UI in response to server-side logic.

### Best Practices for UI Building

*   **Batch Updates**: Group multiple UI commands into a single update to reduce network overhead.
*   **Non-locking Events for Frequent Updates**: Use non-locking events for interactions that don't require immediate server confirmation to maintain UI responsiveness.
*   **Reference Styles**: Utilize `Common.ui` or other style definitions to ensure consistent styling.
*   **Clear Before Appending Dynamic Content**: When updating dynamic lists or sections, clear existing elements before appending new ones.
*   **Validate Event Data**: Always validate incoming `EventData` on the server-side to prevent malicious input or unexpected behavior.

