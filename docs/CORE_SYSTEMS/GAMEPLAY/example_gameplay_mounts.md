# Hytale Gameplay: Mounts System (Example)

This example conceptually demonstrates Hytale's Mounts System, illustrating how entities are mounted and dismounted, based on the official documentation (https://hytale-docs.pages.dev/modding/systems/mounts/).

The documentation highlights `MountedComponent` (for the rider), `MountedByComponent` (for the mount), and specific mount type components like `BlockMountComponent`, `NPCMountComponent`, and `MinecartComponent`.

## 1. Defining Mount-Related Components

```java
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

// Conceptual Hytale Component interface (from ecs_overview example)
// public interface Component extends Cloneable, Serializable {}

// Conceptual Hytale Entity (from ecs_overview example)
// public class Entity { public final long id; ... }


// Component attached to the RIDER entity when it is mounted
public class MountedComponent implements Component {
    public final long mountEntityId; // The ID of the entity being ridden
    public final String mountPointId; // Identifier for the specific point on the mount

    public MountedComponent(long mountEntityId, String mountPointId) {
        this.mountEntityId = mountEntityId;
        this.mountPointId = mountPointId;
    }

    @Override
    public MountedComponent clone() {
        try {
            return (MountedComponent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("MountedComponent not cloneable!");
        }
    }

    @Override
    public String toString() {
        return "MountedComponent{mountId=" + mountEntityId + ", mountPoint='" + mountPointId + "'}";
    }
}

// Component attached to the MOUNT entity when it is being ridden
public class MountedByComponent implements Component {
    public final Set<Long> riderEntityIds; // IDs of entities currently riding this mount

    public MountedByComponent() {
        this.riderEntityIds = new HashSet<>();
    }

    public void addRider(long riderId) {
        riderEntityIds.add(riderId);
    }

    public void removeRider(long riderId) {
        riderEntityIds.remove(riderId);
    }

    public boolean isBeingRidden() {
        return !riderEntityIds.isEmpty();
    }

    @Override
    public MountedByComponent clone() {
        try {
            MountedByComponent cloned = (MountedByComponent) super.clone();
            cloned.riderEntityIds.addAll(this.riderEntityIds); // Deep copy set
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("MountedByComponent not cloneable!");
        }
    }

    @Override
    public String toString() {
        return "MountedByComponent{riders=" + riderEntityIds + '}";
    }
}

// Marker component indicating an entity can be mounted
public class MountableComponent implements Component {
    public final Set<String> availableMountPoints; // e.g., "saddle", "seat_01"

    public MountableComponent(String... mountPoints) {
        this.availableMountPoints = new HashSet<>(Arrays.asList(mountPoints));
    }

    public Optional<String> findAvailableMountPoint() {
        // In a real system, this would check if a point is free/occupied
        return availableMountPoints.stream().findFirst(); // Just return first for simplicity
    }

    @Override
    public MountableComponent clone() {
        try {
            MountableComponent cloned = (MountableComponent) super.clone();
            cloned.availableMountPoints.addAll(this.availableMountPoints);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("MountableComponent not cloneable!");
        }
    }

    @Override
    public String toString() {
        return "MountableComponent{points=" + availableMountPoints + '}";
    }
}

// Specific mount type marker components (as mentioned in docs)
public class BlockMountComponent implements Component { /* No specific fields for example */ @Override public BlockMountComponent clone() { try { return (BlockMountComponent) super.clone(); } catch (CloneNotSupportedException e) { throw new AssertionError(); } } }
public class NPCMountComponent implements Component { /* No specific fields for example */ @Override public NPCMountComponent clone() { try { return (NPCMountComponent) super.clone(); } catch (CloneNotSupportedException e) { throw new AssertionError(); } } }
public class MinecartComponent implements Component { /* No specific fields for example */ @Override public MinecartComponent clone() { try { return (MinecartComponent) super.clone(); } catch (CloneNotSupportedException e) { throw new AssertionError(); } } }
```

## 2. Conceptual Mount System

```java
import java.util.Optional;
import java.util.Collection;

// Conceptual Hytale System interface (from ecs_overview example)
// public interface System { void run(float deltaTime); }

// Conceptual Hytale ECS World (from ecs_overview example)
// public class EcsWorld { ... }
// EcsWorld needs methods for adding/removing components by entity ID
class EcsWorldForMounts extends EcsWorld {
    public void removeComponent(long entityId, Class<? extends Component> componentType) {
        Map<Class<? extends Component>, Component> components = entityComponents.get(entityId);
        if (components != null) {
            components.remove(componentType);
        }
    }
}

public class MountSystem implements System {
    private final EcsWorldForMounts ecsWorld;

    public MountSystem(EcsWorldForMounts ecsWorld) {
        this.ecsWorld = ecsWorld;
    }

    /**
     * Simulates the mounting process.
     * @param riderId The ID of the entity attempting to mount.
     * @param mountId The ID of the entity/block to be mounted.
     * @return true if mounting was successful, false otherwise.
     */
    public boolean attemptMount(long riderId, long mountId) {
        System.out.println("  [MountSystem] Attempting to mount Entity " + riderId + " onto Entity " + mountId);

        // 1. Check if rider is already mounted
        if (ecsWorld.hasComponent(riderId, MountedComponent.class)) {
            System.out.println("    Rider " + riderId + " is already mounted.");
            return false;
        }

        // 2. Validate target is mountable and has an available mount point
        if (!ecsWorld.hasComponent(mountId, MountableComponent.class)) {
            System.out.println("    Mount " + mountId + " is not mountable.");
            return false;
        }
        MountableComponent mountable = ecsWorld.getComponent(mountId, MountableComponent.class);
        Optional<String> mountPointOpt = mountable.findAvailableMountPoint();
        if (mountPointOpt.isEmpty()) {
            System.out.println("    Mount " + mountId + " has no available mount points.");
            return false;
        }
        String mountPoint = mountPointOpt.get();

        // 3. Add MountedComponent to rider
        MountedComponent mounted = new MountedComponent(mountId, mountPoint);
        ecsWorld.addComponent(riderId, mounted);

        // 4. Update or add MountedByComponent on mount
        MountedByComponent mountedBy = ecsWorld.getComponent(mountId, MountedByComponent.class);
        if (mountedBy == null) {
            mountedBy = new MountedByComponent();
            ecsWorld.addComponent(mountId, mountedBy);
        }
        mountedBy.addRider(riderId);

        System.out.println("    Successfully mounted Entity " + riderId + " onto Entity " + mountId + " at point '" + mountPoint + "'.");
        return true;
    }

    /**
     * Simulates the dismounting process.
     * @param riderId The ID of the entity attempting to dismount.
     * @return true if dismounting was successful, false otherwise.
     */
    public boolean attemptDismount(long riderId) {
        System.out.println("  [MountSystem] Attempting to dismount Entity " + riderId);

        MountedComponent mounted = ecsWorld.getComponent(riderId, MountedComponent.class);
        if (mounted == null) {
            System.out.println("    Entity " + riderId + " is not mounted.");
            return false;
        }

        long mountId = mounted.mountEntityId;

        // 1. Remove MountedComponent from rider
        ecsWorld.removeComponent(riderId, MountedComponent.class);

        // 2. Update MountedByComponent on mount
        MountedByComponent mountedBy = ecsWorld.getComponent(mountId, MountedByComponent.class);
        if (mountedBy != null) {
            mountedBy.removeRider(riderId);
            if (!mountedBy.isBeingRidden()) {
                // If no more riders, remove MountedByComponent from mount
                ecsWorld.removeComponent(mountId, MountedByComponent.class);
            }
        }
        System.out.println("    Successfully dismounted Entity " + riderId + " from Entity " + mountId + ".");
        return true;
    }

    @Override
    public void run(float deltaTime) {
        // This system's run method would handle:
        // - Synchronizing rider's position/orientation with mount's.
        // - Forwarding player input from rider to mount controller.
        // - Updating rider animations based on mount's movement states.
        // - Handling death scenarios for rider/mount (dismounting automatically).
    }
}
```

## 3. Conceptual Usage Flow

```java
import java.util.concurrent.TimeUnit;
import java.util.*;

// Assume Entity (from ecs_overview example), EcsWorldForMounts (extended), Component,
// MountedComponent, MountedByComponent, MountableComponent,
// BlockMountComponent, NPCMountComponent, MinecartComponent, MountSystem are all defined above.

public class HytaleGameplayMountsExample {
    public static void main(String[] args) throws InterruptedException {
        EcsWorldForMounts hytaleWorld = new EcsWorldForMounts();

        // Add MountSystem to the world
        MountSystem mountSystem = new MountSystem(hytaleWorld);
        hytaleWorld.addSystem(mountSystem);

        // --- Create Entities ---
        Entity player = hytaleWorld.createEntity(); // Rider
        hytaleWorld.addComponent(player.getId(), new NameComponent("PlayerOne"));

        Entity horse = hytaleWorld.createEntity(); // Mount
        hytaleWorld.addComponent(horse.getId(), new NameComponent("NobleSteed"));
        hytaleWorld.addComponent(horse.getId(), new MountableComponent("saddle"));
        hytaleWorld.addComponent(horse.getId(), new NPCMountComponent()); // Marker for NPC mount

        Entity minecart = hytaleWorld.createEntity(); // Another Mount
        hytaleWorld.addComponent(minecart.getId(), new NameComponent("OldMinecart"));
        hytaleWorld.addComponent(minecart.getId(), new MountableComponent("seat_01", "seat_02"));
        hytaleWorld.addComponent(minecart.getId(), new MinecartComponent()); // Marker for Minecart mount

        System.out.println("--- Initial State ---");
        System.out.println("Player (" + player.getId() + ") mounted? " + hytaleWorld.hasComponent(player.getId(), MountedComponent.class));
        System.out.println("Horse (" + horse.getId() + ") mounted by anyone? " + hytaleWorld.hasComponent(horse.getId(), MountedByComponent.class));
        System.out.println("---------------------\\n");

        // --- Scenario 1: Player mounts the Horse ---
        System.out.println("--- Scenario 1: Player mounts Horse ---");
        mountSystem.attemptMount(player.getId(), horse.getId());
        hytaleWorld.tick(0.05f); // Simulate game tick
        System.out.println("Player (" + player.getId() + ") mounted? " + hytaleWorld.hasComponent(player.getId(), MountedComponent.class));
        System.out.println("  Player's MountedComponent: " + hytaleWorld.getComponent(player.getId(), MountedComponent.class));
        System.out.println("Horse (" + horse.getId() + ") MountedByComponent: " + hytaleWorld.getComponent(horse.getId(), MountedByComponent.class));
        System.out.println("---------------------\\n");
        TimeUnit.MILLISECONDS.sleep(100);


        // --- Scenario 2: Player attempts to mount Minecart while already mounted ---
        System.out.println("--- Scenario 2: Player attempts to mount Minecart ---");
        mountSystem.attemptMount(player.getId(), minecart.getId());
        hytaleWorld.tick(0.05f);
        System.out.println("Player (" + player.getId() + ") still mounted to horse? " + hytaleWorld.hasComponent(player.getId(), MountedComponent.class));
        System.out.println("Minecart (" + minecart.getId() + ") MountedByComponent: " + hytaleWorld.getComponent(minecart.getId(), MountedByComponent.class));
        System.out.println("---------------------\\n");
        TimeUnit.MILLISECONDS.sleep(100);

        // --- Scenario 3: Player dismounts from Horse ---
        System.out.println("--- Scenario 3: Player dismounts ---");
        mountSystem.attemptDismount(player.getId());
        hytaleWorld.tick(0.05f);
        System.out.println("Player (" + player.getId() + ") mounted? " + hytaleWorld.hasComponent(player.getId(), MountedComponent.class));
        System.out.println("Horse (" + horse.getId() + ") mounted by anyone? " + hytaleWorld.hasComponent(horse.getId(), MountedByComponent.class));
        System.out.println("---------------------\\n");
        TimeUnit.MILLISECONDS.sleep(100);
    }
}
```

### Conceptual Output:

```
--- Initial State ---
Player (1) mounted? false
Horse (2) mounted by anyone? false
---------------------

--- Scenario 1: Player mounts Horse ---
  [MountSystem] Attempting to mount Entity 1 onto Entity 2
    Successfully mounted Entity 1 onto Entity 2 at point 'saddle'.
Player (1) mounted? true
  Player's MountedComponent: MountedComponent{mountId=2, mountPoint='saddle'}
Horse (2) MountedByComponent: MountedByComponent{riders=[1]}
---------------------

--- Scenario 2: Player attempts to mount Minecart ---
  [MountSystem] Attempting to mount Entity 1 onto Entity 3
    Rider 1 is already mounted.
Player (1) still mounted to horse? true
Minecart (3) MountedByComponent: null
---------------------

--- Scenario 3: Player dismounts ---
  [MountSystem] Attempting to dismount Entity 1
    Successfully dismounted Entity 1 from Entity 2.
Player (1) mounted? false
Horse (2) mounted by anyone? false
---------------------

```