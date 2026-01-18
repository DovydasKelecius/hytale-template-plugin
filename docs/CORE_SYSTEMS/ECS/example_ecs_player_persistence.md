# Hytale ECS Player Persistence with Custom Components (Example)

This example conceptually demonstrates player data persistence in Hytale's ECS, strictly adhering to the API and concepts mentioned in the official documentation (https://hytale-docs.pages.dev/modding/ecs/player-persistence/).

The documentation highlights `PlayerStorage`, `Holder<EntityStore>`, `Ref<EntityStore>`, `PlayerConfigData`, and the role of `BuilderCodec` for custom component serialization.

## 1. Defining a Custom Player Data Component

This component will hold player-specific, persistent data. It must be `Serializable`.

```java
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

// Conceptual Hytale Component interface (from ecs_overview example)
// public interface Component extends Cloneable, Serializable {}

// Custom component to store player's completed achievements
public class PlayerAchievementsComponent implements Component {
    private Set<String> completedAchievements;
    private long lastAchievementTimestamp;

    public PlayerAchievementsComponent() {
        this.completedAchievements = new HashSet<>();
        this.lastAchievementTimestamp = 0;
    }

    public void unlockAchievement(String achievementId) {
        if (completedAchievements.add(achievementId)) {
            lastAchievementTimestamp = System.currentTimeMillis();
            System.out.println("  Unlocked achievement: " + achievementId);
        }
    }

    public boolean hasAchievement(String achievementId) {
        return completedAchievements.contains(achievementId);
    }

    @Override
    public PlayerAchievementsComponent clone() {
        try {
            PlayerAchievementsComponent cloned = (PlayerAchievementsComponent) super.clone();
            cloned.completedAchievements = new HashSet<>(this.completedAchievements); // Deep copy set
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("PlayerAchievementsComponent not cloneable!");
        }
    }

    @Override
    public String toString() {
        return "PlayerAchievementsComponent{"
               + "completedAchievements=" + completedAchievements +
               ", lastAchievementTimestamp=" + lastAchievementTimestamp +
               '}' ;
    }
}

// PlayerConfigData - conceptual built-in component for global/per-world settings
public class PlayerConfigData implements Component {
    public boolean cheatsEnabled;
    public String preferredLanguage;

    public PlayerConfigData(boolean cheatsEnabled, String preferredLanguage) {
        this.cheatsEnabled = cheatsEnabled;
        this.preferredLanguage = preferredLanguage;
    }

    @Override
    public PlayerConfigData clone() {
        try {
            return (PlayerConfigData) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("PlayerConfigData not cloneable!");
        }
    }

    @Override
    public String toString() {
        return "PlayerConfigData{"
               + "cheatsEnabled=" + cheatsEnabled +
               ", preferredLanguage='" + preferredLanguage + '\'" +
               '}';
    }
}
```

## 2. Conceptual Hytale Persistence API

This section conceptually defines the API mentioned in the documentation: `PlayerStorage`, `BuilderCodec`, and `EntityStore` references.

```java
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

// Conceptual EntityStore (manages components for entities, within a World or Universe context)
// This is a simplified version of Hytale's internal component storage.
class EntityStore {
    private final Map<Long, Map<Class<? extends Component>, Component>> componentsByEntity = new HashMap<>();

    public void addComponent(long entityId, Component component) {
        componentsByEntity.computeIfAbsent(entityId, k -> new HashMap<>()) 
                          .put(component.getClass(), component);
    }

    public <T extends Component> T getComponent(long entityId, Class<T> componentType) {
        Map<Class<? extends Component>, Component> entityComps = componentsByEntity.get(entityId);
        return (entityComps != null) ? componentType.cast(entityComps.get(componentType)) : null;
    }

    public void removeEntity(long entityId) {
        componentsByEntity.remove(entityId);
    }

    // Returns a deep copy of all components for a given entity, ready for serialization
    public Map<Class<? extends Component>, Component> getAllComponentsForEntity(long entityId) {
        Map<Class<? extends Component>, Component> original = componentsByEntity.get(entityId);
        if (original == null) return Collections.emptyMap();

        Map<Class<? extends Component>, Component> copy = new HashMap<>();
        for (Map.Entry<Class<? extends Component>, Component> entry : original.entrySet()) {
            try {
                // Ensure components are cloneable as per Hytale docs
                copy.put(entry.getKey(), (Component) entry.getValue().clone());
            } catch (CloneNotSupportedException e) {
                // This shouldn't happen if components properly implement clone()
                System.err.println("Error cloning component for persistence: " + e.getMessage());
            }
        }
        return copy;
    }
}


// Conceptual Hytale BuilderCodec for custom component serialization/deserialization
// In a real system, this would handle actual JSON/binary serialization
class BuilderCodec<T extends Component> {
    private final Class<T> componentType;

    public BuilderCodec(Class<T> componentType) {
        this.componentType = componentType;
    }

    public String serialize(T component) {
        // Conceptual JSON serialization
        return "{ \"type\": \"" + componentType.getSimpleName() + "\", \"data\": \"" + component.toString() + "\" }";
    }

    public T deserialize(String jsonString) {
        // Conceptual deserialization (would parse JSON and reconstruct component)
        System.out.println("  [BuilderCodec] Deserializing: " + jsonString);
        // For this example, we'll return a new instance for known types
        if (componentType.equals(PlayerAchievementsComponent.class)) {
            // Simplified: return a default or partially reconstructed component
            return componentType.cast(new PlayerAchievementsComponent());
        }
        if (componentType.equals(PlayerConfigData.class)) {
            return componentType.cast(new PlayerConfigData(false, "en_US"));
        }
        return null;
    }
}


// Conceptual Hytale PlayerStorage service
class PlayerStorage {
    private final Map<Long, String> storedPlayerData = new HashMap<>(); // Simulates a database/file storage
    private final Map<Class<? extends Component>, BuilderCodec<? extends Component>> registeredCodecs = new HashMap<>();

    public PlayerStorage() {
        // Register default codecs or common ones
        registerCodec(new BuilderCodec<>(PlayerAchievementsComponent.class));
        registerCodec(new BuilderCodec<>(PlayerConfigData.class));
    }

    public void registerCodec(BuilderCodec<? extends Component> codec) {
        registeredCodecs.put(codec.componentType, codec);
    }

    public void savePlayerEntity(long playerId, EntityStore entityStore) {
        System.out.println("[PlayerStorage] Saving player " + playerId + "...");
        Map<Class<? extends Component>, Component> componentsToSave = entityStore.getAllComponentsForEntity(playerId);

        Map<String, String> serializedComponents = new HashMap<>();
        for (Map.Entry<Class<? extends Component>, Component> entry : componentsToSave.entrySet()) {
            BuilderCodec codec = registeredCodecs.get(entry.getKey());
            if (codec != null) {
                serializedComponents.put(entry.getKey().getName(), codec.serialize(entry.getValue()));
            } else {
                System.err.println("  No codec registered for component type: " + entry.getKey().getName());
            }
        }
        // In a real system, `serializedComponents` would be stored to disk/DB.
        // For this example, we'll store a JSON-like string representation.
        storedPlayerData.put(playerId, serializedComponents.toString());
        System.out.println("[PlayerStorage] Player " + playerId + " saved. Data: " + storedPlayerData.get(playerId));
    }

    public void loadPlayerEntity(long playerId, EntityStore entityStore) {
        System.out.println("[PlayerStorage] Loading player " + playerId + "...");
        String serialized = storedPlayerData.get(playerId);
        if (serialized != null) {
            // Simulate deserialization
            // In a real system, this would parse the JSON and use BuilderCodecs
            // to create actual component instances.
            System.out.println("  [PlayerStorage] Found stored data for " + playerId + ": " + serialized);
            // Reconstruct components (simplified)
            entityStore.addComponent(playerId, registeredCodecs.get(PlayerAchievementsComponent.class).deserialize("simulated json"));
            entityStore.addComponent(playerId, registeredCodecs.get(PlayerConfigData.class).deserialize("simulated json"));
            System.out.println("[PlayerStorage] Player " + playerId + " loaded into EntityStore.");
        } else {
            System.out.println("[PlayerStorage] No saved data found for player " + playerId + ".");
        }
    }
}
```

## 3. Conceptual Usage Flow

```java
import java.util.*;

// Assume Entity (from ecs_overview example), EcsWorld, Component, 
// PlayerAchievementsComponent, PlayerConfigData, 
// EntityStore, BuilderCodec, PlayerStorage are all defined above.

public class HytalePlayerPersistenceExample {
    public static void main(String[] args) {
        // Conceptual Hytale ECS World and Persistence Setup
        EcsWorld hytaleWorld = new EcsWorld(); // Manages entities and their components in memory
        PlayerStorage playerStorage = new PlayerStorage(); // Handles saving/loading to "disk"

        long playerId = 1001L;

        // --- Scenario 1: New Player Joins / Initial State ---
        System.out.println("--- Scenario 1: New Player ---");
        Entity newPlayer = hytaleWorld.createEntity(); // Create entity in ECS world
        // Assign the actual player ID to the entity
        // (In a real system, entity ID might be tied to player ID from login)
        newPlayer = new Entity(playerId); // Re-assign with specific ID for persistence

        // Add initial components to the new player
        PlayerAchievementsComponent achievements = new PlayerAchievementsComponent();
        PlayerConfigData config = new PlayerConfigData(false, "en_US");
        hytaleWorld.addComponent(newPlayer.getId(), achievements);
        hytaleWorld.addComponent(newPlayer.getId(), config);

        System.out.println("New Player (ID: " + newPlayer.getId() + ") in world:");
        System.out.println("  Achievements: " + hytaleWorld.getComponent(newPlayer.getId(), PlayerAchievementsComponent.class));
        System.out.println("  Config: " + hytaleWorld.getComponent(newPlayer.getId(), PlayerConfigData.class));
        System.out.println("----------------------------------------\n");

        // --- Player plays, unlocks achievement, changes settings ---
        System.out.println("--- Player progresses ---");
        PlayerAchievementsComponent playerAch = hytaleWorld.getComponent(newPlayer.getId(), PlayerAchievementsComponent.class);
        playerAch.unlockAchievement("FIRST_KILL");
        playerAch.unlockAchievement("FIRST_LOGIN");

        PlayerConfigData playerCfg = hytaleWorld.getComponent(newPlayer.getId(), PlayerConfigData.class);
        playerCfg.cheatsEnabled = true; // Oh no!

        System.out.println("Player state before save:");
        System.out.println("  Achievements: " + hytaleWorld.getComponent(newPlayer.getId(), PlayerAchievementsComponent.class));
        System.out.println("  Config: " + hytaleWorld.getComponent(newPlayer.getId(), PlayerConfigData.class));
        System.out.println("----------------------------------------\n");

        // --- Scenario 2: Saving Player Data ---
        System.out.println("--- Scenario 2: Saving Player Data ---");
        // PlayerStorage interacts with an EntityStore (which is part of the HytaleWorld in this conceptual example)
        playerStorage.savePlayerEntity(newPlayer.getId(), hytaleWorld.getEntityStore());
        System.out.println("----------------------------------------\n");

        // --- Scenario 3: Player disconnects / server restarts (clear in-memory state) ---
        System.out.println("--- Scenario 3: Player Disconnects (clear in-memory) ---");
        hytaleWorld.getEntityStore().removeEntity(newPlayer.getId());
        System.out.println("Player " + newPlayer.getId() + " removed from in-memory world.");
        System.out.println("Player has achievements component? " + hytaleWorld.hasComponent(newPlayer.getId(), PlayerAchievementsComponent.class));
        System.out.println("----------------------------------------\n");

        // --- Scenario 4: Player reconnects / server loads data ---
        System.out.println("--- Scenario 4: Player Reconnects (load data) ---");
        // Re-create player entity in the ECS world (with the same ID)
        Entity reconnectedPlayer = hytaleWorld.createEntityWithId(playerId); // Conceptual method to create entity with specific ID
        // Load data from PlayerStorage back into the ECS world's EntityStore
        playerStorage.loadPlayerEntity(reconnectedPlayer.getId(), hytaleWorld.getEntityStore());

        System.out.println("Reconnected Player (ID: " + reconnectedPlayer.getId() + ") in world:");
        PlayerAchievementsComponent loadedAch = hytaleWorld.getComponent(reconnectedPlayer.getId(), PlayerAchievementsComponent.class);
        PlayerConfigData loadedCfg = hytaleWorld.getComponent(reconnectedPlayer.getId(), PlayerConfigData.class);

        System.out.println("  Loaded Achievements: " + loadedAch);
        System.out.println("  Loaded Config: " + loadedCfg);
        System.out.println("Has 'FIRST_KILL' achievement? " + (loadedAch != null ? loadedAch.hasAchievement("FIRST_KILL") : "N/A"));
        System.out.println("Cheats enabled? " + (loadedCfg != null ? loadedCfg.cheatsEnabled : "N/A"));
        System.out.println("----------------------------------------\n");
    }
}
```

### Conceptual Output:

```
--- Scenario 1: New Player ---
New Player (ID: 1001) in world:
  Achievements: PlayerAchievementsComponent{completedAchievements=[], lastAchievementTimestamp=0}
  Config: PlayerConfigData{cheatsEnabled=false, preferredLanguage='en_US'}
----------------------------------------

--- Player progresses ---
  Unlocked achievement: FIRST_KILL
  Unlocked achievement: FIRST_LOGIN
Player state before save:
  Achievements: PlayerAchievementsComponent{completedAchievements=[FIRST_LOGIN, FIRST_KILL], lastAchievementTimestamp=<current_timestamp>}
  Config: PlayerConfigData{cheatsEnabled=true, preferredLanguage='en_US'}
----------------------------------------

--- Scenario 2: Saving Player Data ---
[PlayerStorage] Saving player 1001...
[PlayerStorage] Player 1001 saved. Data: {PlayerAchievementsComponent={ "type": "PlayerAchievementsComponent", "data": "PlayerAchievementsComponent{completedAchievements=[FIRST_LOGIN, FIRST_KILL], lastAchievementTimestamp=<current_timestamp>}" }, PlayerConfigData={ "type": "PlayerConfigData", "data": "PlayerConfigData{cheatsEnabled=true, preferredLanguage='en_US'}" }}
----------------------------------------

--- Scenario 3: Player Disconnects (clear in-memory) ---
Player 1001 removed from in-memory world.
Player has achievements component? false
----------------------------------------

--- Scenario 4: Player Reconnects (load data) ---
[PlayerStorage] Loading player 1001...
  [PlayerStorage] Found stored data for 1001: {PlayerAchievementsComponent={ "type": "PlayerAchievementsComponent", "data": "PlayerAchievementsComponent{completedAchievements=[FIRST_LOGIN, FIRST_KILL], lastAchievementTimestamp=<current_timestamp>}" }, PlayerConfigData={ "type": "PlayerConfigData", "data": "PlayerConfigData{cheatsEnabled=true, preferredLanguage='en_US'}" }}
  [BuilderCodec] Deserializing: simulated json
  [BuilderCodec] Deserializing: simulated json
[PlayerStorage] Player 1001 loaded into EntityStore.
Reconnected Player (ID: 1001) in world:
  Loaded Achievements: PlayerAchievementsComponent{completedAchievements=[], lastAchievementTimestamp=0} // Simplified deserialization
  Loaded Config: PlayerConfigData{cheatsEnabled=false, preferredLanguage='en_US'} // Simplified deserialization
Has 'FIRST_KILL' achievement? false // Due to simplified deserialization
Cheats enabled? false // Due to simplified deserialization
----------------------------------------
```
*(Note: The deserialization in this conceptual example is highly simplified and does not actually reconstruct the previous state from the JSON string. In a real Hytale system, the `BuilderCodec` would parse the serialized data and correctly re-populate the component's fields.)*