# com.hypixel.hytale.event

This package contains the event bus and event classes.

## Overview

The `event` package provides a flexible and extensible event bus system for broadcasting and handling events within the Hytale server. This allows for a decoupled architecture where different parts of the server can communicate without direct dependencies.

### Key Concepts

*   **Event Bus:** A central mechanism for dispatching events to registered listeners.
*   **Event:** An object that represents something that has happened in the game.
*   **Event Listener:** A class that listens for and handles specific types of events.
*   **Event Priority:** Allows listeners to specify the order in which they handle events.

### Key Classes

*   `EventBus`: The core event bus interface, responsible for dispatching events.
*   `EventRegistry`: Manages event listeners and their registrations.
*   `IEvent`: The base interface for all events.
*   `ICancellable`: An interface for events that can be cancelled.
*   `EventPriority`: An enum defining the priority levels for event listeners.
*   `AsyncEventBusRegistry`: An asynchronous event bus registry.
*   `SyncEventBusRegistry`: A synchronous event bus registry.

### Sub-packages

This package currently does not contain any further sub-packages.
