# Hytale Modding: Time System

Hytale's Time System is a core plugin module responsible for managing the in-game clock, encompassing day/night cycles, moon phases, and overall time flow. Modders can interact with this system to read current time, modify it, or implement time-dependent game mechanics.

## Core Time Models and Resources:

*   **`WorldTimeResource`:** This is the primary model for representing and storing world-specific time information. It contains:
    *   `gameTime`: The current absolute game time.
    *   `currentHour`: The current hour of the day.
    *   Configuration for `daytimeDuration` and `nighttimeDuration`, allowing customization of cycle lengths.
*   **`TimeResource`:** A more general resource for tracking time, likely used for various time-related operations. It specifically tracks an `Instant now` and a `timeDilationModifier`.

## Time Control and Synchronization:

*   **Time Pausing:** The system allows for the pausing of in-game time.
*   **Moon Phases:** Moon phases are dynamically calculated based on the day of the year and the total number of moon phases in the game's configuration.
*   **`TimePacketSystem`:** This system is responsible for periodically broadcasting time updates to connected clients, ensuring that all players have a synchronized view of the in-game time.
*   **`World.setTimeDilation()`:** This API allows modders to adjust the rate at which time passes in a specific `World`. It includes validation to prevent invalid inputs and broadcasts updates to clients to maintain synchronization.

## Code Examples (as mentioned in the summary):

The documentation specifically points to examples for:

*   **Reading World Time:** Accessing the current `gameTime` from the `WorldTimeResource`.
*   **Setting Day Time:** Programmatically changing the time of day using `WorldTimeResource`.

## `TimeCommand`:

The system also provides a built-in `TimeCommand`, offering in-game commands for players or administrators to:

*   View current time information.
*   Set the time of day.
*   Pause the time flow.
*   Adjust the time dilation rate.
