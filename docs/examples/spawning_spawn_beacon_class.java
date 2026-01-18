// Here are the explicit code snippets you requested from the provided URL:

// **Spawn Beacons:**
// ```java
// public class SpawnBeacon extends Entity {
//     private BeaconSpawnWrapper spawnWrapper;
//     private String spawnConfigId;
//     private IntSet unspawnableRoles;
//     private SpawningContext spawningContext;
// }
// ```

// **InitialBeaconDelay component:**
// ```java
// public class InitialBeaconDelay implements Component<EntityStore> {
//     private double delay;
// }
// ```

// **SpawnMarkerEntity component:**
// ```java
// public class SpawnMarkerEntity implements Component<EntityStore> {
//     private String spawnMarkerId;
//     private SpawnMarker cachedMarker;
//     private double respawnCounter;
//     private Duration gameTimeRespawn;
//     private Instant spawnAfter;
//     private int spawnCount;
//     private Set<UUID> suppressedBy;
//     private InvalidatablePersistentRef[] npcReferences;
//     private StoredFlock storedFlock;
// }
// ```

// **SpawnController interface:**
// ```java
// public interface SpawnController {
//     boolean canSpawn(SpawningContext context);
//     void onSpawn(SpawningContext context);
//     void onDeath(SpawningContext context);
// }
// ```

// **SpawningContext class:**
// ```java
// public class SpawningContext {
//     private World world;
//     private Vector3d position;
//     private Random random;
//     private ComponentAccessor accessor;
//     // ... spawn-specific data
// }
// ```

// **Spawn Configuration JSON:**
// ```json
// {
//   "id": "forest_wolf_spawn",
//   "npcType": "wolf",
//   "minCount": 2,
//   "maxCount": 5,
//   "spawnRadius": 20.0,
//   "respawnTime": 300.0
// }
// ```

// **SpawnSuppressionComponent:**
// ```java
// public class SpawnSuppressionComponent implements Component<EntityStore> {
//     private Set<String> suppressedSpawnTypes;
//     private double radius;
// }
// ```

// **ISpawnable interface:**
// ```java
// public interface ISpawnable {
//     void onSpawned(SpawningContext context);
// }
// ```

// **ISpawnableWithModel interface:**
// ```java
// public interface ISpawnableWithModel extends ISpawnable {
//     Model getModel();
//     void setModel(Model model);
// }
// ```