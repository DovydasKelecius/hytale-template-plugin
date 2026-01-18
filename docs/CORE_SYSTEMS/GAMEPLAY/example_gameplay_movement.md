# Hytale Gameplay: Movement System and State Changes (Example)

This example conceptually demonstrates Hytale's Movement & Locomotion System, focusing on how `MovementStatesComponent` manages an entity's movement states and how systems would react to these changes, based on the official documentation (https://hytale-docs.pages.dev/modding/systems/movement/).

The documentation mentions `MovementStatesComponent`, `MovementStates` (with flags like `idle`, `walking`, `sprinting`, etc.), and dedicated systems for specific mechanics.

## 1. Defining Movement State Component

```java
import java.io.Serializable;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

// Conceptual Hytale Component interface (from ecs_overview example)
// public interface Component extends Cloneable, Serializable {}

// Conceptual Hytale Entity (from ecs_overview example)
// public class Entity { public final long id; ... }

// Enum representing various movement states as described in the documentation
public enum MovementState {
    IDLE,
    WALKING,
    SPRINTING,
    JUMPING,
    CROUCHING,
    GLIDING,
    SWIMMING,
    MANTLING, // Mentioned as a specific mechanic
    FLYING    // Can be inferred as a possible state
}

// Component to hold an entity's current set of active movement states
public class MovementStatesComponent implements Component {
    private final Set<MovementState> activeStates;

    public MovementStatesComponent() {
        this.activeStates = EnumSet.of(MovementState.IDLE); // Default initial state
    }

    /**
     * Adds a movement state to the entity.
     * @param state The state to add.
     * @return true if the state was added, false if already present.
     */
    public boolean addState(MovementState state) {
        if (!activeStates.contains(state)) {
            activeStates.add(state);
            return true;
        }
        return false;
    }

    /**
     * Removes a movement state from the entity.
     * If all specific movement states are removed, IDLE is automatically set.
     * @param state The state to remove.
     * @return true if the state was removed, false if not present.
     */
    public boolean removeState(MovementState state) {
        if (activeStates.remove(state)) {
            // Ensure entity is always in at least IDLE if no other states are active
            if (activeStates.isEmpty()) {
                activeStates.add(MovementState.IDLE);
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if the entity is currently in a specific movement state.
     */
    public boolean hasState(MovementState state) {
        return activeStates.contains(state);
    }

    /**
     * Returns an unmodifiable set of the currently active movement states.
     */
    public Set<MovementState> getActiveStates() {
        return Collections.unmodifiableSet(activeStates);
    }

    @Override
    public MovementStatesComponent clone() {
        try {
            MovementStatesComponent cloned = (MovementStatesComponent) super.clone();
            cloned.activeStates.addAll(this.activeStates); // Deep copy EnumSet
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("MovementStatesComponent not cloneable!");
        }
    }

    @Override
    public String toString() {
        return "MovementStatesComponent{activeStates=" + activeStates + '}' ;
    }
}
```

## 2. Conceptual Systems Reacting to Movement States

These conceptual systems illustrate how Hytale's dedicated systems would interact with the `MovementStatesComponent`.

```java
import java.util.Collection;

// Conceptual Hytale System interface (from ecs_overview example)
// public interface System { void run(float deltaTime); }

// Conceptual Animation System that reacts to movement states
public class AnimationUpdateSystem implements System {
    private final EcsWorld ecsWorld; // Access to entity components

    public AnimationUpdateSystem(EcsWorld ecsWorld) {
        this.ecsWorld = ecsWorld;
    }

    @Override
    public void run(float deltaTime) {
        // System iterates over entities that have MovementStatesComponent
        for (Entity entity : ecsWorld.getAllEntities()) {
            if (ecsWorld.hasComponent(entity.getId(), MovementStatesComponent.class)) {
                MovementStatesComponent movementStates = ecsWorld.getComponent(entity.getId(), MovementStatesComponent.class);

                // Logic to select and play animations based on active states
                if (movementStates.hasState(MovementState.SPRINTING)) {
                    System.out.println("  [AnimSystem] Entity " + entity.getId() + ": Playing SPRINT animation.");
                } else if (movementStates.hasState(MovementState.WALKING)) {
                    System.out.println("  [AnimSystem] Entity " + entity.getId() + ": Playing WALK animation.");
                } else if (movementStates.hasState(MovementState.JUMPING)) {
                    System.out.println("  [AnimSystem] Entity " + entity.getId() + ": Playing JUMP_UP animation.");
                } else if (movementStates.hasState(MovementState.GLIDING)) {
                    System.out.println("  [AnimSystem] Entity " + entity.getId() + ": Playing GLIDE animation.");
                } else if (movementStates.hasState(MovementState.SWIMMING)) {
                    System.out.println("  [AnimSystem] Entity " + entity.getId() + ": Playing SWIM animation.");
                } else if (movementStates.hasState(MovementState.CROUCHING)) {
                    System.out.println("  [AnimSystem] Entity " + entity.getId() + ": Playing CROUCH animation.");
                } else if (movementStates.hasState(MovementState.MANTLING)) {
                    System.out.println("  [AnimSystem] Entity " + entity.getId() + ": Playing MANTLE animation.");
                } else if (movementStates.hasState(MovementState.FLYING)) {
                    System.out.println("  [AnimSystem] Entity " + entity.getId() + ": Playing FLY animation.");
                } else {
                    System.out.println("  [AnimSystem] Entity " + entity.getId() + ": Playing IDLE animation.");
                }
            }
        }
    }
}

// Conceptual Player Input System that changes movement states based on input
public class PlayerInputSystem implements System {
    private final EcsWorld ecsWorld;

    public PlayerInputSystem(EcsWorld ecsWorld) {
        this.ecsWorld = ecsWorld;
    }

    // This method simulates player input
    public void simulateInput(long playerId, String action) {
        Entity player = new Entity(playerId); // Conceptual wrapper
        if (ecsWorld.hasComponent(player.getId(), MovementStatesComponent.class)) {
            MovementStatesComponent movementStates = ecsWorld.getComponent(player.getId(), MovementStatesComponent.class);

            switch (action.toUpperCase()) {
                case "MOVE_FORWARD":
                    // Remove IDLE if present, add WALKING
                    if (movementStates.hasState(MovementState.IDLE)) movementStates.removeState(MovementState.IDLE);
                    movementStates.addState(MovementState.WALKING);
                    System.out.println("  [InputSystem] Player " + playerId + ": Initiated WALKING.");
                    break;
                case "SPRINT":
                    // Ensure walking is active, then add sprinting
                    movementStates.removeState(MovementState.IDLE);
                    movementStates.removeState(MovementState.WALKING); // Sprinting typically replaces walking
                    movementStates.addState(MovementState.SPRINTING);
                    System.out.println("  [InputSystem] Player " + playerId + ": Initiated SPRINTING.");
                    break;
                case "JUMP":
                    // Remove current ground states, add jumping
                    movementStates.removeState(MovementState.WALKING);
                    movementStates.removeState(MovementState.SPRINTING);
                    movementStates.removeState(MovementState.CROUCHING);
                    movementStates.addState(MovementState.JUMPING);
                    System.out.println("  [InputSystem] Player " + playerId + ": Initiated JUMPING.");
                    break;
                case "STOP_MOVING":
                    // Clear all specific movement states, leaving IDLE
                    for (MovementState state : EnumSet.allOf(MovementState.class)) {
                        if (state != MovementState.IDLE) movementStates.removeState(state);
                    }
                    movementStates.addState(MovementState.IDLE); // Ensure IDLE
                    System.out.println("  [InputSystem] Player " + playerId + ": Stopped moving (now IDLE).");
                    break;
                case "CROUCH":
                    movementStates.removeState(MovementState.WALKING);
                    movementStates.removeState(MovementState.SPRINTING);
                    movementStates.removeState(MovementState.JUMPING);
                    movementStates.addState(MovementState.CROUCHING);
                    System.out.println("  [InputSystem] Player " + playerId + ": Initiated CROUCHING.");
                    break;
                case "GLIDE":
                    // Assume player must be airborne to glide
                    if (movementStates.hasState(MovementState.JUMPING)) { // Or falling
                        movementStates.removeState(MovementState.JUMPING);
                        movementStates.addState(MovementState.GLIDING);
                        System.out.println("  [InputSystem] Player " + playerId + ": Initiated GLIDING.");
                    } else {
                        System.out.println("  [InputSystem] Player " + playerId + ": Cannot glide from current state.");
                    }
                    break;
                case "SWIM":
                    // Assume player enters water
                    movementStates.clearStates(); // Clear all current states
                    movementStates.addState(MovementState.SWIMMING);
                    System.out.println("  [InputSystem] Player " + playerId + ": Initiated SWIMMING.");
                    break;
                case "MANTLE":
                    // Mantling is typically a transient state
                    movementStates.addState(MovementState.MANTLING);
                    System.out.println("  [InputSystem] Player " + playerId + ": Initiated MANTLING.");
                    // After a short duration, mantling state would be removed and player would be WALKING/IDLE
                    break;
                default:
                    System.out.println("  [InputSystem] Player " + playerId + ": Unrecognized action: " + action);
            }
        }
    }
}

// Extension to MovementStatesComponent to clear all states for simplification in example
class MovementStatesComponentExtended extends MovementStatesComponent {
    public void clearStates() {
        this.activeStates.clear();
    }
}
```

## 3. Conceptual Usage Flow

```java
import java.util.*;

// Assume Entity (from ecs_overview example), EcsWorld (from ecs_overview example),
// Component, MovementState, MovementStatesComponentExtended, AnimationUpdateSystem, 
// PlayerInputSystem are all defined above.

public class HytaleGameplayMovementExample {
    public static void main(String[] args) throws InterruptedException {
        // Conceptual ECS World setup
        EcsWorld hytaleWorld = new EcsWorld();

        // Add systems
        AnimationUpdateSystem animationSystem = new AnimationUpdateSystem(hytaleWorld);
        PlayerInputSystem playerInputSystem = new PlayerInputSystem(hytaleWorld);
        hytaleWorld.addSystem(animationSystem);
        // hytaleWorld.addSystem(playerInputSystem); // Input system is called directly for input simulation

        // Create a Player Entity
        Entity player = hytaleWorld.createEntity();
        hytaleWorld.addComponent(player.getId(), new MovementStatesComponentExtended());

        System.out.println("--- Initial State (Game Start) ---");
        System.out.println("Player " + player.getId() + " States: " + hytaleWorld.getComponent(player.getId(), MovementStatesComponentExtended.class));
        hytaleWorld.tick(0.05f); // Run systems for one tick
        Thread.sleep(500);
        System.out.println("-----------------------------------\\n");

        // --- Simulate Player Actions ---
        System.out.println("--- Simulating Player Actions ---");

        playerInputSystem.simulateInput(player.getId(), "MOVE_FORWARD"); // Player starts walking
        hytaleWorld.tick(0.05f);
        System.out.println("Player " + player.getId() + " States: " + hytaleWorld.getComponent(player.getId(), MovementStatesComponentExtended.class));
        Thread.sleep(500);

        playerInputSystem.simulateInput(player.getId(), "SPRINT"); // Player starts sprinting
        hytaleWorld.tick(0.05f);
        System.out.println("Player " + player.getId() + " States: " + hytaleWorld.getComponent(player.getId(), MovementStatesComponentExtended.class));
        Thread.sleep(500);

        playerInputSystem.simulateInput(player.getId(), "JUMP"); // Player jumps
        hytaleWorld.tick(0.05f);
        System.out.println("Player " + player.getId() + " States: " + hytaleWorld.getComponent(player.getId(), MovementStatesComponentExtended.class));
        Thread.sleep(500);

        playerInputSystem.simulateInput(player.getId(), "GLIDE"); // Player glides after jumping
        hytaleWorld.tick(0.05f);
        System.out.println("Player " + player.getId() + " States: " + hytaleWorld.getComponent(player.getId(), MovementStatesComponentExtended.class));
        Thread.sleep(500);

        playerInputSystem.simulateInput(player.getId(), "STOP_MOVING"); // Player lands and stops
        hytaleWorld.tick(0.05f);
        System.out.println("Player " + player.getId() + " States: " + hytaleWorld.getComponent(player.getId(), MovementStatesComponentExtended.class));
        Thread.sleep(500);

        playerInputSystem.simulateInput(player.getId(), "SWIM"); // Player enters water
        hytaleWorld.tick(0.05f);
        System.out.println("Player " + player.getId() + " States: " + hytaleWorld.getComponent(player.getId(), MovementStatesComponentExtended.class));
        Thread.sleep(500);

        playerInputSystem.simulateInput(player.getId(), "MANTLE"); // Player performs a mantle
        hytaleWorld.tick(0.05f);
        System.out.println("Player " + player.getId() + " States: " + hytaleWorld.getComponent(player.getId(), MovementStatesComponentExtended.class));
        // Mantling is often a temporary state, so it would be removed by another system after an animation
        hytaleWorld.getComponent(player.getId(), MovementStatesComponentExtended.class).removeState(MovementState.MANTLING);
        hytaleWorld.tick(0.05f);
        System.out.println("Player " + player.getId() + " States (after mantle completes): " + hytaleWorld.getComponent(player.getId(), MovementStatesComponentExtended.class));
        System.out.println("-----------------------------------\\n");
    }
}
```

### Conceptual Output:

```
--- Initial State (Game Start) ---
Player 1 States: MovementStatesComponent{activeStates=[IDLE]}
  [AnimSystem] Entity 1: Playing IDLE animation.
-----------------------------------

--- Simulating Player Actions ---
  [InputSystem] Player 1: Initiated WALKING.
  [AnimSystem] Entity 1: Playing WALK animation.
Player 1 States: MovementStatesComponent{activeStates=[WALKING]}
  [InputSystem] Player 1: Initiated SPRINTING.
  [AnimSystem] Entity 1: Playing SPRINT animation.
Player 1 States: MovementStatesComponent{activeStates=[SPRINTING]}
  [InputSystem] Player 1: Initiated JUMPING.
  [AnimSystem] Entity 1: Playing JUMP_UP animation.
Player 1 States: MovementStatesComponent{activeStates=[JUMPING]}
  [InputSystem] Player 1: Initiated GLIDING.
  [AnimSystem] Entity 1: Playing GLIDE animation.
Player 1 States: MovementStatesComponent{activeStates=[GLIDING]}
  [InputSystem] Player 1: Stopped moving (now IDLE).
  [AnimSystem] Entity 1: Playing IDLE animation.
Player 1 States: MovementStatesComponent{activeStates=[IDLE]}
  [InputSystem] Player 1: Initiated SWIMMING.
  [AnimSystem] Entity 1: Playing SWIM animation.
Player 1 States: MovementStatesComponent{activeStates=[SWIMMING]}
  [InputSystem] Player 1: Initiated MANTLING.
  [AnimSystem] Entity 1: Playing MANTLE animation.
Player 1 States: MovementStatesComponent{activeStates=[SWIMMING, MANTLING]}
  State Removed: MANTLING
  [AnimSystem] Entity 1: Playing SWIM animation.
Player 1 States (after mantle completes): MovementStatesComponent{activeStates=[SWIMMING]}
-----------------------------------

```
