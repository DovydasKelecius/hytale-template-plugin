# Hytale Gameplay Systems: Damage System (Example)

This example conceptually demonstrates Hytale's Damage System, focusing on dispatching `Damage` events and how a system might process them through the "Gather," "Filter," and "Inspect" pipeline stages, as described in the official documentation (https://hytale-docs.pages.dev/modding/systems/damage/).

The documentation highlights `DamageDataComponent`, `KnockbackComponent`, the `Damage` class extending `CancellableEcsEvent`, and the three-stage processing pipeline.

## 1. Defining Damage-Related Components and Events

```java
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// Conceptual Hytale Component interface (from ecs_overview example)
// public interface Component extends Cloneable, Serializable {}

// Conceptual Hytale Entity (from ecs_overview example)
// public class Entity { public final long id; ... }

// A basic Health Component (from previous example, adapted)
public class HealthComponent implements Component {
    public float currentHealth;
    public float maxHealth;

    public HealthComponent(float currentHealth, float maxHealth) {
        this.currentHealth = currentHealth;
        this.maxHealth = maxHealth;
    }

    public void takeDamage(float amount) {
        this.currentHealth = Math.max(0, this.currentHealth - amount);
    }

    @Override
    public HealthComponent clone() {
        try {
            return (HealthComponent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("HealthComponent not cloneable!");
        }
    }

    @Override
    public String toString() {
        return "HealthComponent{" + "current=" + currentHealth + ", max=" + maxHealth + '}' ;
    }
}

// Component for tracking combat-related data
public class DamageDataComponent implements Component {
    public long lastHitTimestamp;
    public long lastDamagerEntityId;
    public float totalDamageTaken;

    public DamageDataComponent() {
        this.lastHitTimestamp = 0;
        this.lastDamagerEntityId = -1;
        this.totalDamageTaken = 0;
    }

    @Override
    public DamageDataComponent clone() {
        try {
            return (DamageDataComponent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("DamageDataComponent not cloneable!");
        }
    }

    @Override
    public String toString() {
        return "DamageDataComponent{lastDamager=" + lastDamagerEntityId + ", totalTaken=" + totalDamageTaken + '}' ;
    }
}

// Component for knockback properties (e.g., resistance, strength)
public class KnockbackComponent implements Component {
    public float knockbackResistance; // 0.0 to 1.0, 1.0 means immune
    public float knockbackMultiplier; // How much knockback this entity deals

    public KnockbackComponent(float resistance, float multiplier) {
        this.knockbackResistance = resistance;
        this.knockbackMultiplier = multiplier;
    }

    @Override
    public KnockbackComponent clone() {
        try {
            return (KnockbackComponent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("KnockbackComponent not cloneable!");
        }
    }

    @Override
    public String toString() {
        return "KnockbackComponent{resistance=" + knockbackResistance + ", multiplier=" + knockbackMultiplier + '}' ;
    }
}

// Component marking an entity as dead
public class DeathComponent implements Component {
    public long deathTimestamp;
    public String causeOfDeath;

    public DeathComponent(String cause) {
        this.deathTimestamp = System.currentTimeMillis();
        this.causeOfDeath = cause;
    }

    @Override
    public DeathComponent clone() {
        try {
            return (DeathComponent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("DeathComponent not cloneable!");
        }
    }

    @Override
    public String toString() {
        return "DeathComponent{cause='" + causeOfDeath + "', time=" + deathTimestamp + '}' ;
    }
}

// Conceptual Hytale CancellableEcsEvent interface
public interface CancellableEcsEvent {
    boolean isCancelled();
    void cancel();
}

// The Damage Event class as described
public class Damage implements CancellableEcsEvent {
    public final Entity target;
    public final Entity source; // Can be null for environmental damage
    public final String damageCause; // e.g., "MELEE", "FIRE", "FALLING"
    public float amount; // This amount can be modified by systems
    private boolean cancelled;

    public Damage(Entity target, Entity source, String damageCause, float amount) {
        this.target = target;
        this.source = source;
        this.damageCause = damageCause;
        this.amount = amount;
        this.cancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }

    @Override
    public String toString() {
        return String.format("Damage{target=%d, source=%s, cause='%s', amount=%.2f, cancelled=%b}",
                target.getId(), (source != null ? source.getId() : "null"), damageCause, amount, cancelled);
    }
}
```

## 2. Conceptual Damage Processing System

```java
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

// Conceptual Hytale EventBus (from previous examples, for publishing/subscribing to events)
class EventBus {
    private final Map<Class<?>, List<java.util.function.Consumer<Object>>> listeners = new HashMap<>();

    public <T> void subscribe(Class<T> eventType, java.util.function.Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>()) 
                 .add(event -> listener.accept(eventType.cast(event)));
    }

    public void publish(Object event) {
        List<java.util.function.Consumer<Object>> eventListeners = listeners.getOrDefault(event.getClass(), Collections.emptyList());
        for (java.util.function.Consumer<Object> listener : eventListeners) {
            listener.accept(event);
        }
    }
}

// Conceptual Hytale ECS World (from ecs_overview example)
// Provides access to entities and their components
// (Assuming methods like addComponent, getComponent, hasComponent, addSystem, tick)
// For this example, we need to extend it to allow removing components for death.
class EcsWorldWithDeath extends EcsWorld {
    public EcsWorldWithDeath() {
        super();
    }
    public void removeComponent(long entityId, Class<? extends Component> componentType) {
        Map<Class<? extends Component>, Component> components = entityComponents.get(entityId);
        if (components != null) {
            components.remove(componentType);
        }
    }
}


public class DamageSystem implements System {
    private final EventBus eventBus;
    private final EcsWorldWithDeath ecsWorld; // Use extended EcsWorld for component removal

    public DamageSystem(EventBus eventBus, EcsWorldWithDeath ecsWorld) {
        this.eventBus = eventBus;
        this.ecsWorld = ecsWorld;
        // Subscribe to DamageEvent for processing
        eventBus.subscribe(Damage.class, this::processDamageEvent);
    }

    @Override
    public void run(float deltaTime) {
        // Damage system typically reacts to events, so its `run` method might be empty
        // or used for ticking effects like damage over time, which aren't covered here.
    }

    private void processDamageEvent(Damage event) {
        if (event.isCancelled()) {
            System.out.println("  [DamageSystem] Damage event to Entity " + event.target.getId() + " was cancelled.");
            return;
        }

        System.out.println("  [DamageSystem] Processing Damage Event: " + event.damageCause + " for " + event.target.getId() + " (Initial: " + event.amount + ")");

        // --- Stage 1: Gather Damage Group ---
        // The event itself represents the "gathered" damage.
        // In a real system, this might involve checking for critical hits, bonus damage sources etc.

        // --- Stage 2: Filter Damage Group ---
        // Apply damage reduction, invulnerability, etc.
        if (ecsWorld.hasComponent(event.target.getId(), DamageReductionComponent.class)) {
            DamageReductionComponent dr = ecsWorld.getComponent(event.target.getId(), DamageReductionComponent.class);
            event.amount *= (1.0f - dr.reductionPercentage); // Reduce damage amount
            System.out.printf("    [Filter] Applied %.0f%% damage reduction. New amount: %.2f\n", dr.reductionPercentage * 100, event.amount);
        }

        // Apply actual damage to HealthComponent
        if (ecsWorld.hasComponent(event.target.getId(), HealthComponent.class)) {
            HealthComponent health = ecsWorld.getComponent(event.target.getId(), HealthComponent.class);
            health.takeDamage(event.amount);
            System.out.println("    [Filter] Entity " + event.target.getId() + " Health: " + health);

            // --- Stage 3: Inspect Damage Group ---
            // Handle post-damage effects like visual feedback, sound, knockback, and death.
            if (health.currentHealth <= 0 && !ecsWorld.hasComponent(event.target.getId(), DeathComponent.class)) {
                System.out.println("    [Inspect] Entity " + event.target.getId() + " died!");
                ecsWorld.addComponent(event.target.getId(), new DeathComponent(event.damageCause));
                // Trigger an event for DeathSystem to handle animations, item drops, etc.
                eventBus.publish(new EntityDeathEvent(event.target, event.source, event.damageCause));
            }

            // Apply Knockback (conceptual)
            if (event.source != null && ecsWorld.hasComponent(event.target.getId(), KnockbackComponent.class) &&
                ecsWorld.hasComponent(event.source.getId(), KnockbackComponent.class)) {
                KnockbackComponent targetKnockback = ecsWorld.getComponent(event.target.getId(), KnockbackComponent.class);
                KnockbackComponent sourceKnockback = ecsWorld.getComponent(event.source.getId(), KnockbackComponent.class);
                float effectiveKnockback = (event.amount * sourceKnockback.knockbackMultiplier) * (1.0f - targetKnockback.knockbackResistance);
                System.out.printf("    [Inspect] Applying %.2f effective knockback to Entity %d (from %d)\n", effectiveKnockback, event.target.getId(), event.source.getId());
                // This would typically modify PhysicsBodyState.velX/Y/Z
            }
        } else {
            System.out.println("    [Filter] Entity " + event.target.getId() + " has no HealthComponent. Damage ignored.");
        }
    }
}

// Conceptual Event for when an entity dies
class EntityDeathEvent {
    public final Entity deadEntity;
    public final Entity killerEntity;
    public final String cause;

    public EntityDeathEvent(Entity deadEntity, Entity killerEntity, String cause) {
        this.deadEntity = deadEntity;
        this.killerEntity = killerEntity;
        this.cause = cause;
    }

    @Override
    public String toString() {
        return "EntityDeathEvent{dead=" + deadEntity.getId() + ", killer=" + (killerEntity != null ? killerEntity.getId() : "null") + ", cause='" + cause + "'}";
    }
}

// Conceptual System to react to death events (e.g., despawn, drop items)
class DeathSystem implements System {
    private final EventBus eventBus;
    private final EcsWorldWithDeath ecsWorld;

    public DeathSystem(EventBus eventBus, EcsWorldWithDeath ecsWorld) {
        this.eventBus = eventBus;
        this.ecsWorld = ecsWorld;
        eventBus.subscribe(EntityDeathEvent.class, this::processDeathEvent);
    }

    @Override
    public void run(float deltaTime) {
        // This system primarily reacts to events
    }

    private void processDeathEvent(EntityDeathEvent event) {
        System.out.println("  [DeathSystem] Handling death of Entity " + event.deadEntity.getId() + ". Cause: " + event.cause);
        // Remove active components, play death animation, drop items, etc.
        ecsWorld.removeComponent(event.deadEntity.getId(), HealthComponent.class);
        ecsWorld.removeComponent(event.deadEntity.getId(), DamageDataComponent.class);
        ecsWorld.removeComponent(event.deadEntity.getId(), KnockbackComponent.class);
        // In a real system, the entity might be marked for removal or despawned.
        System.out.println("  [DeathSystem] Entity " + event.deadEntity.getId() + " components removed.");
    }
}
```

## 3. Conceptual Usage Flow

```java
import java.util.*;

// Assume Entity (from ecs_overview example), EcsWorldWithDeath (extended),
// Component, HealthComponent, DamageDataComponent, KnockbackComponent, DeathComponent,
// CancellableEcsEvent, Damage, EventBus, DamageSystem, EntityDeathEvent, DeathSystem are all defined above.

public class HytaleGameplayDamageExample {
    public static void main(String[] args) {
        EcsWorldWithDeath hytaleWorld = new EcsWorldWithDeath();
        EventBus eventBus = new EventBus();

        // Register systems
        hytaleWorld.addSystem(new DamageSystem(eventBus, hytaleWorld));
        hytaleWorld.addSystem(new DeathSystem(eventBus, hytaleWorld));

        // Create Entities
        Entity player = hytaleWorld.createEntity();
        hytaleWorld.addComponent(player.getId(), new HealthComponent(100.0f, 100.0f));
        hytaleWorld.addComponent(player.getId(), new DamageDataComponent());
        hytaleWorld.addComponent(player.getId(), new KnockbackComponent(0.2f, 1.0f)); // 20% res, 1x deals

        Entity enemyGoblin = hytaleWorld.createEntity();
        hytaleWorld.addComponent(enemyGoblin.getId(), new HealthComponent(50.0f, 50.0f));
        hytaleWorld.addComponent(enemyGoblin.getId(), new DamageDataComponent());
        hytaleWorld.addComponent(enemyGoblin.getId(), new KnockbackComponent(0.0f, 0.5f)); // No res, 0.5x deals

        Entity environmentalFire = hytaleWorld.createEntity(); // Source of environmental damage
        hytaleWorld.addComponent(environmentalFire.getId(), new NameComponent("Environmental Fire"));


        System.out.println("--- Initial State ---");
        System.out.println("Player: " + hytaleWorld.getComponent(player.getId(), HealthComponent.class));
        System.out.println("Enemy Goblin: " + hytaleWorld.getComponent(enemyGoblin.getId(), HealthComponent.class));
        System.out.println("---------------------\\n");

        // --- Scenario 1: Goblin attacks Player ---
        System.out.println("--- Goblin attacks Player ---");
        // Player has 20% damage reduction
        Damage playerDamageEvent = new Damage(player, enemyGoblin, "MELEE", 30.0f);
        eventBus.publish(playerDamageEvent); // DamageSystem will process this
        System.out.println("Player health after hit: " + hytaleWorld.getComponent(player.getId(), HealthComponent.class));
        System.out.println("---------------------\\n");

        // --- Scenario 2: Player attacks Goblin, Goblin dies ---
        System.out.println("--- Player attacks Goblin (Lethal) ---");
        Damage goblinDamageEvent = new Damage(enemyGoblin, player, "SWORD_SLASH", 60.0f);
        eventBus.publish(goblinDamageEvent); // DamageSystem processes, Goblin should die
        System.out.println("Goblin health after hit: " + hytaleWorld.getComponent(enemyGoblin.getId(), HealthComponent.class));
        System.out.println("Goblin has DeathComponent? " + hytaleWorld.hasComponent(enemyGoblin.getId(), DeathComponent.class));
        System.out.println("---------------------\\n");

        // --- Scenario 3: Player steps on environmental fire, damage is cancelled by a shield ---
        System.out.println("--- Player steps on environmental fire ---");
        Damage fireDamageEvent = new Damage(player, environmentalFire, "ENVIRONMENTAL_FIRE", 15.0f);
        // Simulate an external system (e.g., a temporary shield effect) cancelling the damage
        System.out.println("Simulating shield cancelling fire damage...");
        fireDamageEvent.cancel();
        eventBus.publish(fireDamageEvent);
        System.out.println("Player health after fire: " + hytaleWorld.getComponent(player.getId(), HealthComponent.class));
        System.out.println("---------------------\\n");
    }
}
```

### Conceptual Output:

```
--- Initial State ---
Player: HealthComponent{current=100.0, max=100.0}
Enemy Goblin: HealthComponent{current=50.0, max=50.0}
---------------------

--- Goblin attacks Player ---
  [DamageSystem] Processing Damage Event: MELEE for 1 (Initial: 30.00)
    [Filter] Applied 20% damage reduction. New amount: 24.00
    [Filter] Entity 1 Health: HealthComponent{current=76.0, max=100.0}
    [Inspect] Applying 24.00 effective knockback to Entity 1 (from 2)
Player health after hit: HealthComponent{current=76.0, max=100.0}
---------------------

--- Player attacks Goblin (Lethal) ---
  [DamageSystem] Processing Damage Event: SWORD_SLASH for 2 (Initial: 60.00)
    [Filter] Entity 2 Health: HealthComponent{current=0.0, max=50.0}
    [Inspect] Entity 2 died!
  [DeathSystem] Handling death of Entity 2. Cause: SWORD_SLASH
  [DeathSystem] Entity 2 components removed.
    [Inspect] Applying 30.00 effective knockback to Entity 2 (from 1)
Goblin health after hit: HealthComponent{current=0.0, max=50.0}
Goblin has DeathComponent? true
---------------------

--- Player steps on environmental fire ---
Simulating shield cancelling fire damage...
  [DamageSystem] Damage event to Entity 1 was cancelled.
Player health after fire: HealthComponent{current=76.0, max=100.0}
---------------------
```