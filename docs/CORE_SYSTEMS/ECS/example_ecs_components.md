# Hytale ECS Components: Definition and Management (Example)

This example illustrates how to define custom components and conceptually manage them within Hytale's ECS, drawing directly from the descriptions on the official documentation page (https://hytale-docs.pages.dev/modding/ecs/components/).

The documentation emphasizes that components are data containers, must implement `Cloneable` and be `Serializable`, and are managed via a `Store` and `CommandBuffer` for thread safety.

## 1. Defining Custom Components

Hytale components are typically plain Java classes that implement the `Component` interface.

```java
import java.io.Serializable;

// Conceptual Hytale Component interface
// public interface Component extends Cloneable, Serializable {}

// Example 1: ManaComponent - stores numerical data
public class ManaComponent implements Component {
    public float currentMana;
    public float maxMana;
    public float manaRegenRate; // Example of more detailed data

    public ManaComponent(float currentMana, float maxMana, float manaRegenRate) {
        this.currentMana = currentMana;
        this.maxMana = maxMana;
        this.manaRegenRate = manaRegenRate;
    }

    @Override
    public ManaComponent clone() {
        try {
            return (ManaComponent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("ManaComponent is not cloneable!"); // Should not happen
        }
    }

    @Override
    public String toString() {
        return "ManaComponent{current=" + currentMana + ", max=" + maxMana + ", regen=" + manaRegenRate + "}";
    }
}

// Example 2: FlyingMarker - a simple flag component (no data, presence signifies a state)
public class FlyingMarker implements Component {
    // This component's existence indicates the entity can fly.
    // No fields are necessary for a marker component.

    @Override
    public FlyingMarker clone() {
        try {
            return (FlyingMarker) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("FlyingMarker is not cloneable!"); // Should not happen
        }
    }

    @Override
    public String toString() {
        return "FlyingMarker{}";
    }
}
```

## 2. Conceptual ECS Framework Interactions

The documentation mentions `Store` and `CommandBuffer` for managing components. This suggests an API where you don't directly manipulate components on an `Entity` object, but rather interact with a central store.

```java
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Conceptual Hytale Entity ID (from ecs_overview example)
// public class Entity { public final long id; ... }

// Represents a conceptual Hytale Component Store (thread-safe operations)
// In a real system, this would be part of the ECS framework
class ComponentStore {
    // Map: Entity ID -> Map: ComponentType -> Component Instance
    private final Map<Long, Map<Class<? extends Component>, Component>> entityComponents = new ConcurrentHashMap<>();

    public void addComponent(long entityId, Component component) {
        entityComponents.computeIfAbsent(entityId, k -> new HashMap<>()) 
                        .put(component.getClass(), component);
        System.out.println("  [Store] Added component " + component.getClass().getSimpleName() + " to Entity " + entityId);
    }

    public <T extends Component> T getComponent(long entityId, Class<T> componentType) {
        Map<Class<? extends Component>, Component> components = entityComponents.get(entityId);
        if (components != null) {
            return componentType.cast(components.get(componentType));
        }
        return null;
    }

    public boolean hasComponent(long entityId, Class<? extends Component> componentType) {
        Map<Class<? extends Component>, Component> components = entityComponents.get(entityId);
        return components != null && components.containsKey(componentType);
    }

    public <T extends Component> T removeComponent(long entityId, Class<T> componentType) {
        Map<Class<? extends Component>, Component> components = entityComponents.get(entityId);
        if (components != null) {
            T removed = componentType.cast(components.remove(componentType));
            if (removed != null) {
                System.out.println("  [Store] Removed component " + componentType.getSimpleName() + " from Entity " + entityId);
                return removed;
            }
        }
        return null;
    }
}

// Conceptual Hytale CommandBuffer for deferred, thread-safe operations
// In a real system, operations added here would be applied at a safe point (e.g., end of tick)
class CommandBuffer {
    private final ComponentStore store; // Reference to the store to apply commands
    private final Map<Long, Map<Class<? extends Component>, Component>> pendingAdds = new ConcurrentHashMap<>();
    private final Map<Long, Set<Class<? extends Component>>> pendingRemoves = new ConcurrentHashMap<>();

    public CommandBuffer(ComponentStore store) {
        this.store = store;
    }

    public void addComponent(long entityId, Component component) {
        pendingAdds.computeIfAbsent(entityId, k -> new HashMap<>()) 
                   .put(component.getClass(), component);
        System.out.println("  [CommandBuffer] Scheduled add: " + component.getClass().getSimpleName() + " to Entity " + entityId);
    }

    public void removeComponent(long entityId, Class<? extends Component> componentType) {
        pendingRemoves.computeIfAbsent(entityId, k -> ConcurrentHashMap.newKeySet())
                      .add(componentType);
        System.out.println("  [CommandBuffer] Scheduled remove: " + componentType.getSimpleName() + " from Entity " + entityId);
    }

    public void flush() {
        System.out.println("[CommandBuffer] Flushing pending commands...");
        // Process removes first to handle potential overlaps with adds
        for (Map.Entry<Long, Set<Class<? extends Component>>> entry : pendingRemoves.entrySet()) {
            long entityId = entry.getKey();
            for (Class<? extends Component> componentType : entry.getValue()) {
                store.removeComponent(entityId, componentType);
            }
        }
        pendingRemoves.clear();

        for (Map.Entry<Long, Map<Class<? extends Component>, Component>> entityEntry : pendingAdds.entrySet()) {
            long entityId = entityEntry.getKey();
            for (Component component : entityEntry.getValue().values()) {
                store.addComponent(entityId, component);
            }
        }
        pendingAdds.clear();
        System.out.println("[CommandBuffer] Commands flushed.");
    }
}
```

## 3. Usage Example

This example demonstrates conceptual usage of these components and the `Store`/`CommandBuffer`.

```java
import java.io.Serializable; // For Component interface
import java.util.*;

// Assume Entity (from ecs_overview example), Component interface,
// ManaComponent, FlyingMarker, ComponentStore, CommandBuffer are defined above

public class HytaleEcsComponentsExample {
    public static void main(String[] args) {
        // Conceptual ECS setup
        ComponentStore store = new ComponentStore();
        CommandBuffer commandBuffer = new CommandBuffer(store);

        // Create an entity (e.g., a player)
        Entity player = new Entity(101);

        System.out.println("--- Initial State ---");
        // Check if player has ManaComponent or FlyingMarker (should be false)
        System.out.println("Player has ManaComponent? " + store.hasComponent(player.getId(), ManaComponent.class));
        System.out.println("Player has FlyingMarker? " + store.hasComponent(player.getId(), FlyingMarker.class));
        System.out.println("---------------------
");

        // --- Scenario 1: Adding components ---
        System.out.println("--- Scenario 1: Adding Components ---");
        commandBuffer.addComponent(player.getId(), new ManaComponent(50.0f, 100.0f, 1.0f));
        commandBuffer.addComponent(player.getId(), new FlyingMarker());
        commandBuffer.flush(); // Apply pending changes to the store

        System.out.println("\n--- After Adding Components ---");
        ManaComponent playerMana = store.getComponent(player.getId(), ManaComponent.class);
        System.out.println("Player Mana: " + (playerMana != null ? playerMana : "None"));
        System.out.println("Player has FlyingMarker? " + store.hasComponent(player.getId(), FlyingMarker.class));
        System.out.println("---------------------------------
");

        // --- Scenario 2: Modifying a component (directly after flush for simplicity) ---
        System.out.println("--- Scenario 2: Modifying a Component ---");
        if (playerMana != null) {
            playerMana.currentMana = 75.0f; // Imagine a spell restored some mana
            System.out.println("Player Mana after modification: " + playerMana);
        }
        System.out.println("-----------------------------------------
");


        // --- Scenario 3: Removing a component ---
        System.out.println("--- Scenario 3: Removing a Component ---");
        commandBuffer.removeComponent(player.getId(), FlyingMarker.class);
        commandBuffer.flush(); // Apply pending changes to the store

        System.out.println("\n--- After Removing FlyingMarker ---");
        System.out.println("Player has FlyingMarker? " + store.hasComponent(player.getId(), FlyingMarker.class));
        System.out.println("Player Mana: " + store.getComponent(player.getId(), ManaComponent.class)); // ManaComponent should still be there
        System.out.println("-------------------------------------
");
    }
}
```

### Conceptual Output:

```
--- Initial State ---
Player has ManaComponent? false
Player has FlyingMarker? false
---------------------

--- Scenario 1: Adding Components ---
  [CommandBuffer] Scheduled add: ManaComponent to Entity 101
  [CommandBuffer] Scheduled add: FlyingMarker to Entity 101
[CommandBuffer] Flushing pending commands...
  [Store] Added component ManaComponent to Entity 101
  [Store] Added component FlyingMarker to Entity 101
[CommandBuffer] Commands flushed.

--- After Adding Components ---
Player Mana: ManaComponent{current=50.0, max=100.0, regen=1.0}
Player has FlyingMarker? true
---------------------------------

--- Scenario 2: Modifying a Component ---
Player Mana after modification: ManaComponent{current=75.0, max=100.0, regen=1.0}
-----------------------------------------


--- Scenario 3: Removing a Component ---
  [CommandBuffer] Scheduled remove: FlyingMarker from Entity 101
[CommandBuffer] Flushing pending commands...
  [Store] Removed component FlyingMarker from Entity 101
[CommandBuffer] Commands flushed.

--- After Removing FlyingMarker ---
Player has FlyingMarker? false
Player Mana: ManaComponent{current=75.0, max=100.0, regen=1.0}
-------------------------------------
```