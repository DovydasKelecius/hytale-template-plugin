# Hytale ECS Concepts: Entity, Component, System (Conceptual Example)

This documentation outlines the core ECS concepts in Hytale: Entities as unique identifiers, Components as data containers, and Systems as logic processors. While the official documentation (https://hytale-docs.pages.dev/modding/ecs/) focuses on these roles, it doesn't provide direct code examples. This conceptual Java-like pseudo-code illustrates how these elements might interact within the Hytale framework, based on standard ECS patterns and the described roles.

## 1. Entity: A Unique Identifier

In Hytale, an `Entity` is primarily a lightweight reference, typically an ID, to a game object. It doesn't hold data directly.

```java
// Hytale's Entity class would likely be provided by the framework.
// It acts as a handle or ID to a collection of components.
public class Entity {
    private final long id; // Unique identifier for the entity

    // Entities are typically created and managed by the ECS framework
    public Entity(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    // In a real Hytale environment, methods to add/get/remove components
    // would be provided by an ECS World or EntityManager, taking the Entity ID.
    // For conceptual clarity, we show it here as if the Entity object itself
    // could manage components, but remember it's just an ID conceptually.
    // Hytale's API would be more like: `ecsWorld.addComponent(entity.getId(), new MyComponent());`
}
```

## 2. Component: A Data Container

`Components` are pure data structures that hold specific attributes for an `Entity`. They should implement `Cloneable` and be `Serializable` for persistence.

```java
// Hytale's base Component interface
public interface Component extends Cloneable, Serializable {
    // No methods, just a marker interface for data-only classes
}

// Example: A simple PositionComponent for an entity's 3D location
public class PositionComponent implements Component {
    public float x, y, z;

    public PositionComponent(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public PositionComponent clone() {
        // Hytale requires components to be Cloneable for entity copying
        try {
            return (PositionComponent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone not supported for PositionComponent");
        }
    }

    @Override
    public String toString() {
        return "PositionComponent{" + "x=" + x + ", y=" + y + ", z=" + z + '}' ;
    }
}

// Example: A component for an entity's display name
public class NameComponent implements Component {
    public String name;

    public NameComponent(String name) {
        this.name = name;
    }

    @Override
    public NameComponent clone() {
        try {
            return (NameComponent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clone not supported for NameComponent");
        }
    }

    @Override
    public String toString() {
        return "NameComponent{name='" + name + "'}" ;
    }
}
```

## 3. System: Logic Processor

`Systems` contain the game logic. They iterate over entities that possess a specific set of components and perform operations on their data.

```java
import java.util.Collection; // Represents a collection of entities from the ECS World

// Hytale's base System interface (conceptual)
public interface System {
    void run(float deltaTime); // Called every game tick
}

// Example: A system that logs the position and name of all entities that have both
public class EntityLoggerSystem implements System {

    // In a real Hytale system, this `ecsWorld` would be injected or accessible
    // and provide methods to query entities based on their components.
    private final EcsWorld ecsWorld;

    public EntityLoggerSystem(EcsWorld ecsWorld) {
        this.ecsWorld = ecsWorld;
    }

    @Override
    public void run(float deltaTime) {
        System.out.println("--- EntityLoggerSystem running (deltaTime: " + deltaTime + ") ---");
        // Imagine ecsWorld.queryEntities(PositionComponent.class, NameComponent.class)
        // returns a filtered list/view of entities.
        for (Entity entity : ecsWorld.getAllEntities()) { // Simplified: iterate all, then filter
            if (ecsWorld.hasComponent(entity.getId(), PositionComponent.class) &&
                ecsWorld.hasComponent(entity.getId(), NameComponent.class)) {

                PositionComponent pos = ecsWorld.getComponent(entity.getId(), PositionComponent.class);
                NameComponent name = ecsWorld.getComponent(entity.getId(), NameComponent.class);

                System.out.println("Entity ID: " + entity.getId() +
                                   ", Name: " + name.name +
                                   ", Position: (" + pos.x + ", " + pos.y + ", " + pos.z + ")");
            }
        }
    }
}

// Conceptual Hytale ECS World/Manager for demonstration purposes
// In reality, this would be a sophisticated framework
class EcsWorld {
    private long nextEntityId = 1;
    private final Map<Long, Map<Class<? extends Component>, Component>> entityComponents = new HashMap<>();
    private final List<System> systems = new ArrayList<>();

    public Entity createEntity() {
        long id = nextEntityId++;
        entityComponents.put(id, new HashMap<>());
        return new Entity(id);
    }

    public void addComponent(long entityId, Component component) {
        entityComponents.get(entityId).put(component.getClass(), component);
    }

    public <T extends Component> T getComponent(long entityId, Class<T> componentType) {
        return componentType.cast(entityComponents.get(entityId).get(componentType));
    }

    public boolean hasComponent(long entityId, Class<? extends Component> componentType) {
        return entityComponents.get(entityId).containsKey(componentType);
    }

    public Collection<Entity> getAllEntities() {
        List<Entity> entities = new ArrayList<>();
        for (Long id : entityComponents.keySet()) {
            entities.add(new Entity(id));
        }
        return entities;
    }

    public void addSystem(System system) {
        systems.add(system);
    }

    public void tick(float deltaTime) {
        for (System system : systems) {
            system.run(deltaTime);
        }
    }
}
```

## 4. Conceptual Usage

```java
import java.io.Serializable;
import java.util.*;

// Assume all classes above are defined

public class HytaleEcsOverviewExample {
    public static void main(String[] args) {
        // Initialize the conceptual ECS World
        EcsWorld hytaleWorld = new EcsWorld();

        // Create Entities
        Entity playerEntity = hytaleWorld.createEntity();
        hytaleWorld.addComponent(playerEntity.getId(), new PositionComponent(0, 5, 0));
        hytaleWorld.addComponent(playerEntity.getId(), new NameComponent("PlayerOne"));

        Entity npcEntity = hytaleWorld.createEntity();
        hytaleWorld.addComponent(npcEntity.getId(), new PositionComponent(10, 0, 20));
        hytaleWorld.addComponent(npcEntity.getId(), new NameComponent("FriendlyNPC"));

        // Add a System to the world
        hytaleWorld.addSystem(new EntityLoggerSystem(hytaleWorld));

        // Simulate game ticks
        System.out.println("\n--- Game Tick 1 ---");
        hytaleWorld.tick(0.5f); // Half a second passes

        System.out.println("\n--- Game Tick 2 ---");
        // Imagine a movement system (not shown here) updates positions
        // For this example, let's manually change a position for demonstration
        PositionComponent playerPos = hytaleWorld.getComponent(playerEntity.getId(), PositionComponent.class);
        if (playerPos != null) {
            playerPos.x += 1.0f; // Player moves
        }
        hytaleWorld.tick(0.5f);
    }
}
```

### Conceptual Output:

```
--- Game Tick 1 ---
--- EntityLoggerSystem running (deltaTime: 0.5) ---
Entity ID: 1, Name: PlayerOne, Position: (0.0, 5.0, 0.0)
Entity ID: 2, Name: FriendlyNPC, Position: (10.0, 0.0, 20.0)

--- Game Tick 2 ---
--- EntityLoggerSystem running (deltaTime: 0.5) ---
Entity ID: 1, Name: PlayerOne, Position: (1.0, 5.0, 0.0)
Entity ID: 2, Name: FriendlyNPC, Position: (10.0, 0.0, 20.0)
```
