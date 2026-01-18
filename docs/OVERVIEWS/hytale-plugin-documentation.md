# Hytale Plugin Documentation

## 1. Plugin System

Hytale's server-side functionality can be extended through plugins, which are Java JAR files designed to add custom features and logic.

### `manifest.json` Structure

Each plugin requires a `manifest.json` file at the root of its JAR. This file defines essential metadata:

*   **`Group`**: The plugin's group identifier.
*   **`Name`**: The unique name of the plugin.
*   **`Version`**: The plugin's version string.
*   **`Description`**: A brief description of the plugin's purpose.
*   **`Main`**: The fully qualified class name of the plugin's main entry point (must extend `JavaPlugin`).
*   **`Authors`**: A list of the plugin's authors.
*   **`Website`**: An optional website or project URL.
*   **`ServerVersion`**: The compatible Hytale server version.
*   **`Dependencies`**: A list of other plugins this plugin depends on.

### Plugin Lifecycle

A plugin's main class extends `JavaPlugin` and typically implements three key lifecycle methods:

*   **`setup()`**: Called during server initialization. This is the primary stage for registering components, commands, and events.
*   **`start()`**: Executed after all plugins have completed their `setup()` phase. Ideal for starting background tasks, initializing complex states, or performing actions that rely on other plugins being fully set up.
*   **`shutdown()`**: Invoked when the server stops or the plugin is unloaded. Used for cleaning up resources, saving data, and ensuring a graceful exit.

### Access to Registries

Plugins have access to various registries provided by the Hytale server to manage different aspects of gameplay and server functionality. These can include registries for commands, events, tasks, entities, assets, and more, allowing plugins to interact with and extend core game systems.

### Hot Reloading Considerations

Hytale supports hot reloading of plugins. To ensure proper functionality and prevent resource leaks during hot reloads, it's crucial to:

*   Perform thorough cleanup in the `shutdown()` method.
*   Avoid using static state that persists across reloads; instead, use the plugin instance to manage state.

### Building and Deployment

Plugins are typically built using a tool like Gradle. A common Gradle configuration will specify the Java version (Java 21 or higher recommended) and include a `compileOnly` dependency on `HytaleServer.jar`. The build process should ensure that the `manifest.json` is correctly packaged at the root of the generated JAR.

To deploy a plugin, the compiled JAR file is placed in the `mods/` directory of the Hytale server installation. Plugins are loaded in a specific order: core plugins, plugins in the `builtin/` directory, plugins on the classpath, plugins in the `mods/` directory, and finally, plugins from additional directories specified via the `--mods-directories` server option.

---

## 2. Event System

The Hytale event system is a powerful mechanism for plugins to react to game occurrences, communicate with each other, and manage asynchronous operations.

### Architecture

The event system is built around several core components:

*   **`EventBus`**: The central dispatcher for all events.
*   **`SyncEventBusRegistry`**: Manages synchronous events (`IEvent`), where handlers execute immediately in priority order.
*   **`AsyncEventBusRegistry`**: Manages asynchronous events (`IAsyncEvent`), where handlers execute using `CompletableFuture` chaining.
*   **`EventRegistry`**: A per-plugin registry that manages the lifecycle of event subscriptions, ensuring proper cleanup when a plugin is unloaded.

### Event Types

Events in Hytale can be categorized by their execution model and capabilities:

*   **Synchronous Events (`IEvent`)**: Handlers for these events execute sequentially and immediately on the thread that dispatches the event.
*   **Asynchronous Events (`IAsyncEvent`)**: Handlers for these events execute in a non-blocking manner, typically involving `CompletableFuture` for chaining operations.
*   **Cancellable Events (`ICancellable`)**: These events implement the `ICancellable` interface, allowing handlers to prevent the default action from occurring (e.g., `PlaceBlockEvent`, `BreakBlockEvent`).

### Dispatch Priority

Event dispatch is prioritized, allowing plugins to influence the order in which their handlers are called. Priorities range from `FIRST` (-21844) to `LAST` (21844), with `NORMAL` (0) being the default.

### Subscribing to Events

Plugins can subscribe to events using their `EventRegistry`:

*   **Basic Registration**: `getEventRegistry().register(EventType.class, this::eventHandlerMethod);`
*   **Keyed Events**: For events with specific contexts (e.g., world-specific events), plugins can register for a particular key.
*   **Global Registration**: `registerGlobal()` allows listening to all instances of a keyed event, regardless of its specific key.

### Custom Events

Developers can create custom events by implementing `IEvent` or `IAsyncEvent` (and `ICancellable` if the event should be cancellable). This allows for custom communication within and between plugins.

### Dispatching Events

Events are dispatched using an `IEventDispatcher`, obtained from `HytaleServer.get().getEventBus().dispatchFor(eventInstance)`. Asynchronous dispatch is handled with `dispatchForAsync()` and `whenComplete()` callbacks for result processing.

### Built-in Events

The Hytale server provides a wide range of built-in events covering various game aspects:

*   **Server Lifecycle**: `BootEvent`, `ShutdownEvent`, `PluginSetupEvent`, `PrepareUniverseEvent`.
*   **World Events**: `AddWorldEvent`, `RemoveWorldEvent`, `AllWorldsLoadedEvent`, `StartWorldEvent`.
*   **Player Events**: `PlayerConnectEvent`, `PlayerDisconnectEvent`, `PlayerChatEvent`, `PlayerReadyEvent`.
*   **Entity Events**: `EntityRemoveEvent`, `LivingEntityInventoryChangeEvent`, `LivingEntityUseBlockEvent`.
*   **Block Events (ECS Events)**: `PlaceBlockEvent`, `BreakBlockEvent`, `DamageBlockEvent`, `UseBlockEvent`.
*   **Other ECS Events**: `CraftRecipeEvent`, `DropItemEvent`, `SwitchActiveSlotEvent`, `ChangeGameModeEvent`.
*   **Asset Events**: `LoadedAssetsEvent`, `RemovedAssetsEvent`, `GenerateAssetsEvent`.
*   **Permission Events**: `GroupPermissionChangeEvent`, `PlayerPermissionChangeEvent`, `PlayerGroupEvent`.

### Unregistering Events

Event registrations can be manually closed using `registration.close()`. However, the `EventRegistry` automatically cleans up all registered handlers when a plugin is unloaded, simplifying resource management.

### Asynchronous Handlers

Handlers for asynchronous events can be registered using `registerAsync()` to process and transform `CompletableFuture` results. `registerUnhandled()` allows for registering handlers that only fire if no other keyed handler processed the event.

### Best Practices

*   **Appropriate Priorities**: Use event priorities to ensure handlers execute in the desired order.
*   **Pre-check for Listeners**: Check `hasListener()` before creating and dispatching events if they are expensive to create.
*   **Proper Async Handling**: Manage asynchronous operations to avoid blocking the main thread, especially for I/O.
*   **Respect Cancellation**: Check if events are cancelled to avoid unnecessary processing.
*   **Keyed Events for Scope**: Use keyed events to target specific contexts and improve efficiency.
*   **Async for I/O**: Prefer asynchronous events and handlers for I/O-bound operations to maintain server responsiveness.

---

## 3. Command System

The Hytale server's command system enables developers to create custom commands that players and the console can execute, extending server functionality beyond built-in commands.

### Architecture

Commands are managed centrally by the `CommandManager`. The system supports both built-in server commands and plugin-specific commands.

### Creating and Registering Commands

Custom commands are created by extending the `AbstractCommand` class and implementing its `execute` method, which contains the command's logic. Commands are typically registered within a plugin's `setup()` method.

### Command Arguments

The command system supports various argument types and parsing mechanisms:

*   **Required Arguments**: Positional arguments that a user must provide.
*   **Optional Arguments**: Identified by a `--name value` syntax and can be omitted by the user.
*   **Default Arguments**: If an argument is not provided by the user, a predefined default value is used.
*   **Flag Arguments**: Boolean switches (e.g., `--verbose`) that do not take a value; their presence indicates `true`.

### Subcommands

Related commands can be logically grouped using `AbstractCommandCollection`, allowing for hierarchical command structures (e.g., `/myplugin subcmd1` and `/myplugin subcmd2`).

### Command Sender

The `CommandSender` interface represents the entity that executes a command. Common implementations include:

*   **`Player`**: A player in the game.
*   **`ConsoleSender`**: The server console. The console sender typically bypasses all permission checks and implicitly has all permissions.

### Permissions

The command system integrates with the Hytale permission system:

*   **Auto-generated Permissions**: Permissions are often automatically generated based on the plugin and command names.
*   **Custom Permissions**: Developers can define custom permission nodes for specific commands or subcommands.
*   **Manual Checks**: Permissions can be manually checked within the command's `execute` method.
*   **Required Permissions**: Commands can explicitly require certain permissions in their constructor or definition.

### Aliases

Commands can be configured with multiple aliases, allowing users to execute them using different names (e.g., `/gm` for `/gamemode`).

### Confirmation Required

For potentially dangerous or irreversible operations, commands can be configured to require a `--confirm` flag from the user, adding an extra layer of safety.

### Programmatic Execution

Commands can be executed programmatically from server-side code using the `CommandManager`, allowing plugins to trigger commands internally without user input.

### Messages

Commands can send various types of messages to the sender or other players:

*   **Raw Messages**: Plain text messages.
*   **Translation Messages**: Messages that are dynamically translated based on the recipient's language settings, often with parameter substitution for dynamic content.

### Argument Types

The command system provides various argument types for parsing user input, including `String`, `Int`, `Double`, `Boolean`, and `List` of values.

### Built-in Commands

Hytale includes a suite of built-in commands for common server operations, such as:

*   `help`
*   `stop`
*   `kick`
*   `who`
*   `gamemode`
*   `give`
*   `tp`
*   `entity`
*   `chunk`
*   `worldgen`

### Base Command Classes

Several base command classes are available to simplify common command patterns:

*   **`AbstractAsyncCommand`**: For commands that perform asynchronous operations, typically returning a `CompletableFuture<Void>`.
*   **`AbstractPlayerCommand`**: For commands specifically designed to be executed by players.

### Best Practices

*   **Typed Arguments**: Use specific argument types to ensure robust parsing and validation of user input.
*   **Asynchronous Operations**: For long-running tasks, return `CompletableFuture` and avoid blocking the main thread.
*   **Descriptive Help**: Provide clear and concise help messages for all commands and their arguments.
*   **Input Validation**: Always validate user input to prevent errors and ensure data integrity.
*   **Error Handling**: Implement graceful error handling for invalid input or unexpected conditions.
*   **Permissions**: Secure commands by requiring appropriate permissions.
*   **Subcommands for Grouping**: Organize related functionality using subcommands to keep the command structure clean.

---

## 4. Permissions and Access Control

The Hytale permission system offers fine-grained control over access to server features, commands, and custom plugin functionality. It supports user-level permissions, group-based inheritance, and flexible wildcard matching.

### Architecture

The permission system's core components include:

*   **`PermissionsModule`**: The central module for permission management.
*   **`PermissionProvider`**: An interface (with `HytalePermissionsProvider` as the default implementation) that defines how permissions are managed (e.g., user permissions, group permissions, user-group membership).
*   **Permission Groups**: Categorize users (e.g., OP, Default, Custom groups).
*   **User Permissions**: Permissions assigned directly to individual users (identified by UUIDs).
*   **Virtual Groups**: Dynamically assigned groups based on specific conditions, such as the player's game mode.

### Core Interfaces

*   **`PermissionHolder`**: Implemented by entities (like `Player` and `CommandSender`) that can hold and be checked for permissions. Provides methods like `hasPermission(@Nonnull String id)` and `hasPermission(@Nonnull String id, boolean defaultValue)`.
*   **`PermissionProvider`**: Allows for custom permission backends to integrate with Hytale, managing user permissions, group permissions, and user-group memberships.

### Permission Syntax

Hytale permissions use a flexible, hierarchical syntax:

*   **Permission Nodes**: Dot-separated strings (e.g., `namespace.category.action`).
*   **Wildcards**: The `*` character can be used for broad access (e.g., `hytale.*` grants all permissions within the `hytale` namespace; `*` grants all permissions globally).
*   **Negation**: Permissions can be prefixed with `-` (e.g., `-hytale.command.stop`) to explicitly deny access. Negated permissions take precedence over inherited group permissions.

### Built-in Permissions

The `HytalePermissions` class defines constants for standard server permissions. Additionally, common command permissions are implicitly defined (e.g., `hytale.command.gamemode.self`, `hytale.command.kick`).

### Permission Groups

*   **Default Groups**:
    *   **"OP"**: Grants all permissions via the `*` wildcard.
    *   **"Default"**: Provides no special permissions by default.
*   **Virtual Groups**: Automatically assigned based on in-game conditions, such as the player's game mode. For instance, `hytale.editor.builderTools` might be granted automatically in Creative mode.

### `PermissionsModule` Usage

The `PermissionsModule` can be accessed via `PermissionsModule.get()` and provides methods for:

*   **Checking Permissions**: `hasPermission(UUID userId, String permissionNode)`.
*   **Managing User Permissions**: `addUserPermission(UUID userId, String permissionNode)`, `removeUserPermission(UUID userId, String permissionNode)`.
*   **Managing Group Memberships**: `addUserToGroup(UUID userId, String groupName)`, `removeUserFromGroup(UUID userId, String groupName)`, `getGroupsForUser(UUID userId)`.

### Checking Permissions in Commands

Commands can enforce permissions directly:

*   **Constructor Requirement**: `requirePermission("myplugin.command.mycommand")` in the command's constructor.
*   **Manual Check**: `context.sender().hasPermission("myplugin.action")` within the `executeSync()` method.

### Permission Events

The system dispatches events when permission-related changes occur:

*   **`PlayerPermissionChangeEvent`**: Fired when an individual player's permissions are modified.
*   **`PlayerGroupEvent`**: Fired when a player's group membership changes.
*   **`GroupPermissionChangeEvent`**: Fired when the permissions assigned to a group are modified.

### Configuration (`permissions.json`)

Permission data is stored in a `permissions.json` file in the server directory. This file typically contains sections for `users` (individual user permissions and group assignments) and `groups` (definitions of permission groups and their associated nodes).

### Built-in Permission Commands

Hytale provides commands for managing permissions:

*   **`/op` commands**: For granting or revoking OP (operator) status for players, which implies having all permissions.
*   **`/perm` commands**: For listing, adding, and removing permissions for users and groups, and managing user-group memberships.

### Best Practices

*   **Hierarchical Naming**: Use a consistent, dot-separated naming convention for permission nodes (e.g., `pluginname.feature.action`).
*   **Granular Permissions**: Design permissions to be as specific as possible, allowing administrators fine-tuned control.
*   **Groups for Roles**: Utilize permission groups to manage roles and simplify administration.
*   **React to Changes**: Listen for `Permission Events` to update plugin logic when permissions change.
*   **Restrictive by Default**: Default to denying access and explicitly grant permissions.
*   **Document Permissions**: Clearly document all custom permission nodes used by your plugin.
*   **Use Constants**: Define permission strings as constants in your code to prevent typos and improve maintainability.

---

## 5. Task Scheduling and Async System

The Hytale server provides a sophisticated task scheduling and asynchronous system, allowing plugins to manage time-based events, perform non-blocking operations, and interact safely with the game world across different execution threads.

### Architecture

The task and async system integrates several key components:

*   **`SCHEDULED_EXECUTOR`**: A global single-threaded `ScheduledExecutorService` (accessible via `HytaleServer.SCHEDULED_EXECUTOR`) dedicated to scheduling delayed and repeating tasks.
*   **`EventBus`**: Supports asynchronous event handling, as described in the Event System documentation.
*   **`World`**: Each `World` instance runs on its own dedicated thread, implementing `Executor` and managing a `taskQueue` for safe world interactions.
*   **`TickingThread`**: The base class for `World` instances, featuring a `tick()` method (default 30 TPS) and an `AtomicBoolean` (`acceptingTasks`) to control task submission.

### Task Registry

Each plugin has a `TaskRegistry` responsible for tracking and managing scheduled tasks. This ensures that all plugin-related tasks are properly cancelled and cleaned up when the plugin is disabled or the server shuts down.

*   **`registerTask()`**: Used to register `CompletableFuture<Void>` or `ScheduledFuture<Void>` instances.
*   **`TaskRegistration`**: A wrapper around `Future` that provides lifecycle management, automatically cancelling registered tasks.

### Global Scheduled Executor

`HytaleServer.SCHEDULED_EXECUTOR` is crucial for time-based operations:

*   **One-time Delayed Tasks**: `schedule(Runnable command, long delay, TimeUnit unit)`.
*   **Repeating Tasks (Fixed Delay)**: `scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)`. The delay is applied *after* the previous task completes.
*   **Repeating Tasks (Fixed Rate)**: `scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)`. The task runs at a fixed interval, regardless of how long the previous execution took.

It's vital to remember that this executor runs on a separate thread. Any interactions with world state or entities must be carefully dispatched to the appropriate world thread using `world.execute()`.

### World Thread Execution

Each `World` instance operates on its own dedicated thread and acts as an `Executor`.

*   **`world.execute(Runnable command)`**: This method ensures that the provided `Runnable` is executed safely on the world's thread. It's the only safe way to modify entities, blocks, and other world-specific data.
*   **`thenAcceptAsync(result -> { /* process result */ }, world)`**: When handling asynchronous operations, this `CompletableFuture` method allows you to process results back on the world thread.
*   **`world.isAlive()`**: It's recommended to check `world.isAlive()` before submitting tasks to avoid `SkipSentryException` if the world is not accepting tasks (e.g., during shutdown).

### Tick Rate and Timing

*   Hytale worlds typically tick at 30 Ticks Per Second (TPS), though this can be configured.
*   Constants like `NANOS_IN_ONE_MILLI`, `NANOS_IN_ONE_SECOND`, and `TPS` are available for timing calculations.
*   `world.isInThread()` can be used to verify if the current code execution is on the world's dedicated thread.

### `CompletableFuture` Patterns

The `CompletableFutureUtil` class provides helpful utilities for working with `CompletableFuture`:

*   **Cancellation Check**: Checking if a throwable represents a cancellation.
*   **Cancelled Futures**: Creating already-cancelled futures.
*   **Unhandled Exceptions**: Catching and logging unhandled exceptions.
*   **Completion Transfer**: Transferring completion state between futures.
*   **Chaining Operations**: `thenCompose` (for sequential async operations), `thenApply` (for transforming results).
*   **Error Handling**: `exceptionally()` for recovering from errors in the chain.

### Async Commands

For commands that involve long-running or asynchronous operations, plugins can extend `AbstractAsyncCommand`.

*   **`executeAsync`**: This method should be overridden to perform the asynchronous logic and return a `CompletableFuture<Void>`.
*   **`runAsync`**: A utility method that wraps a `Runnable` in a try-catch block for exception logging, ensuring that errors in asynchronous tasks are reported.

### Common Scheduling Patterns

*   **Periodic Tasks**: Use `HytaleServer.SCHEDULED_EXECUTOR.scheduleWithFixedDelay()` for tasks that repeat after a fixed delay.
*   **Delayed Player Actions**: Schedule one-time tasks with `HytaleServer.SCHEDULED_EXECUTOR.schedule()` that can be cancelled if a player performs another action before the delay expires.
*   **Batched Processing**: Process large datasets in chunks across multiple ticks by scheduling subsequent batches on the world thread to avoid lag spikes.
*   **Cross-Thread Data Transfer**: Asynchronously load data (e.g., from a database) and then safely apply it to the world on the world thread using `CompletableFuture.supplyAsync()` followed by `thenAcceptAsync(..., world)`.

### Task Cancellation and Cleanup

*   **Manual Cancellation**: Tasks can be explicitly cancelled using `myTask.cancel(false)`.
*   **Automatic Cleanup**: The `TaskRegistry` automatically cancels all registered tasks when the plugin that created them is disabled, preventing orphaned tasks and resource leaks.

### Best Practices

1.  **Register All Tasks**: Always register long-running or repeating tasks with `getTaskRegistry()` to ensure automatic cleanup.
2.  **Use Appropriate Executor**:
    *   `HytaleServer.SCHEDULED_EXECUTOR` for timed (delayed/repeating) tasks.
    *   `world.execute()` for all modifications to world state or entities.
    *   `CompletableFuture.supplyAsync()` for general background computations that don't involve direct world interaction.
3.  **Handle Exceptions**: Always implement robust exception handling in asynchronous code to prevent silent failures and aid debugging.
4.  **Respect Thread Safety**: Only modify world state or entities on the world's dedicated thread.
5.  **Avoid Blocking**: Never use `future.join()` or `Thread.sleep()` on the world thread. Instead, use asynchronous chaining (`thenApply`, `thenCompose`) and callbacks.
6.  **Cancel Unused Tasks**: Explicitly cancel tasks that are no longer needed to free up resources.
7.  **Timeouts**: Implement appropriate timeouts for `CompletableFuture` instances that might otherwise hang indefinitely.
8.  **Batch Operations**: Break down large operations into smaller, manageable batches to prevent lag spikes, especially for tasks running on the world thread.

