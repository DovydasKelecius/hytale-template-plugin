# Hytale Gameplay: Entity Effects System (Example)

This example conceptually demonstrates Hytale's Entity Effects System, illustrating how status effects, buffs, and debuffs are managed through components, with application, ticking, and removal mechanisms, based on the official documentation (https://hytale-docs.pages.dev/modding/systems/entity-effects/).

The documentation is high-level, stating effects are component-managed, have durations, and can be removed via expiration or game logic. No specific API names or code examples are provided on the page, so this example uses common ECS patterns to fulfill the description.

## 1. Defining Effect-Related Components

```java
import java.io.Serializable;
import java.util.*;

// Conceptual Hytale Component interface (from ecs_overview example)
// public interface Component extends Cloneable, Serializable {}

// Conceptual Hytale Entity (from ecs_overview example)
// public class Entity { public final long id; ... }

// Component: Represents an active effect on an entity
public class ActiveEffectComponent implements Component {
    public final String effectTypeId; // ID referencing an EffectConfig asset (e.g., "POISON", "SPEED_BOOST")
    public long startTimeMillis;
    public long durationMillis; // How long the effect lasts (0 for permanent until removed)
    public float potency;       // Strength of the effect

    public ActiveEffectComponent(String effectTypeId, long durationMillis, float potency) {
        this.effectTypeId = effectTypeId;
        this.startTimeMillis = System.currentTimeMillis();
        this.durationMillis = durationMillis;
        this.potency = potency;
    }

    public boolean isExpired() {
        return durationMillis > 0 && (System.currentTimeMillis() - startTimeMillis >= durationMillis);
    }

    @Override
    public ActiveEffectComponent clone() {
        try {
            return (ActiveEffectComponent) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("ActiveEffectComponent not cloneable!");
        }
    }

    @Override
    public String toString() {
        long timeLeft = durationMillis > 0 ? (durationMillis - (System.currentTimeMillis() - startTimeMillis)) / 1000 : -1;
        return "ActiveEffectComponent{type='" + effectTypeId + "', potency=" + potency + ", timeLeftSec=" + (timeLeft == -1 ? "Permanent" : timeLeft) + '}'";
    }
}

// Component: Indicates an entity is affected by something, for easier system queries
// This component would hold all ActiveEffectComponents on this entity.
public class EffectsContainerComponent implements Component {
    public final List<ActiveEffectComponent> effects; // Using List to allow multiple effects of same type

    public EffectsContainerComponent() {
        this.effects = new ArrayList<>();
    }

    // Adds an effect to the container.
    public void addEffect(ActiveEffectComponent effect) {
        effects.add(effect);
    }

    // Finds and returns the first effect matching the type ID.
    public Optional<ActiveEffectComponent> getEffect(String effectTypeId) {
        return effects.stream().filter(e -> e.effectTypeId.equals(effectTypeId)).findFirst();
    }

    // Removes all effects matching the type ID.
    public boolean removeEffects(String effectTypeId) {
        return effects.removeIf(e -> e.effectTypeId.equals(effectTypeId));
    }

    @Override
    public EffectsContainerComponent clone() {
        try {
            EffectsContainerComponent cloned = (EffectsContainerComponent) super.clone();
            cloned.effects.addAll(this.effects); // Shallow copy; could be deep for effects themselves
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("EffectsContainerComponent not cloneable!");
        }
    }

    @Override
    public String toString() {
        return "EffectsContainerComponent{effects=" + effects + '}'";
    }
}
```

## 2. Conceptual Entity Effects System

This system handles the application, ticking, and removal of effects.

```java
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

// Conceptual Hytale System interface (from ecs_overview example)
// public interface System { void run(float deltaTime); }

// Conceptual Hytale ECS World (from ecs_overview example)
// public class EcsWorld { ... }
// EcsWorld needs a method to remove components
class EcsWorldForEffects extends EcsWorld {
    public void removeComponent(long entityId, Class<? extends Component> componentType) {
        Map<Class<? extends Component>, Component> components = entityComponents.get(entityId);
        if (components != null) {
            components.remove(componentType);
        }
    }
}

public class EntityEffectsSystem implements System {
    private final EcsWorldForEffects ecsWorld;

    public EntityEffectsSystem(EcsWorldForEffects ecsWorld) {
        this.ecsWorld = ecsWorld;
    }

    /**
     * Applies an effect to a target entity.
     */
    public void applyEffect(long targetEntityId, String effectTypeId, long durationMillis, float potency) {
        if (!ecsWorld.hasEntity(targetEntityId)) {
            System.out.println("  [EffectsSystem] Cannot apply effect to non-existent entity " + targetEntityId);
            return;
        }

        EffectsContainerComponent container = ecsWorld.getComponent(targetEntityId, EffectsContainerComponent.class);
        if (container == null) {
            container = new EffectsContainerComponent();
            ecsWorld.addComponent(targetEntityId, container);
        }
        ActiveEffectComponent newEffect = new ActiveEffectComponent(effectTypeId, durationMillis, potency);
        container.addEffect(newEffect);
        System.out.println("  [EffectsSystem] Applied effect " + newEffect + " to Entity " + targetEntityId);
    }

    /**
     * Explicitly removes all effects of a specific type from an entity.
     * @return true if any effect was removed, false otherwise.
     */
    public boolean removeEffectByType(long targetEntityId, String effectTypeId) {
        EffectsContainerComponent container = ecsWorld.getComponent(targetEntityId, EffectsContainerComponent.class);
        if (container != null) {
            boolean removed = container.removeEffects(effectTypeId);
            if (removed) {
                System.out.println("  [EffectsSystem] Explicitly removed effect " + effectTypeId + " from Entity " + targetEntityId);
                // If no more effects, remove the container component itself
                if (container.effects.isEmpty()) {
                    ecsWorld.removeComponent(targetEntityId, EffectsContainerComponent.class);
                    System.out.println("  [EffectsSystem] EffectsContainerComponent removed from Entity " + targetEntityId + " as it's empty.");
                }
            }
            return removed;
        }
        return false;
    }

    @Override
    public void run(float deltaTime) {
        System.out.println("  [EffectsSystem] Ticking active effects...");
        for (Entity entity : ecsWorld.getAllEntities()) {
            EffectsContainerComponent container = ecsWorld.getComponent(entity.getId(), EffectsContainerComponent.class);
            if (container != null) {
                Iterator<ActiveEffectComponent> iterator = container.effects.iterator();
                while (iterator.hasNext()) {
                    ActiveEffectComponent effect = iterator.next();
                    if (effect.isExpired()) {
                        System.out.println("    [EffectsSystem] Effect " + effect.effectTypeId + " expired on Entity " + entity.getId());
                        iterator.remove(); // Remove expired effect
                    } else {
                        // Apply effect logic (e.g., modify stats, deal damage over time)
                        // This would typically interact with other components (e.g., HealthComponent, MovementComponent)
                        if (effect.effectTypeId.equals("POISON")) {
                            if (ecsWorld.hasComponent(entity.getId(), HealthComponent.class)) {
                                HealthComponent health = ecsWorld.getComponent(entity.getId(), HealthComponent.class);
                                float damagePerTick = 1.0f * effect.potency * deltaTime; // Example damage over time
                                health.currentHealth = Math.max(0, health.currentHealth - damagePerTick);
                                System.out.printf("      Entity %d poisoned (%.2f damage). Health: %.1f\n", entity.getId(), damagePerTick, health.currentHealth);
                            }
                        } else if (effect.effectTypeId.equals("SPEED_BOOST")) {
                            // Conceptual: Modify MovementSpeed Stat in EntityStatMapComponent
                            // System.out.println("      Entity " + entity.getId() + " is moving faster due to SPEED_BOOST (Potency: " + effect.potency + ")");
                        }
                    }
                }
                // If no more effects, remove the container component itself
                if (container.effects.isEmpty()) {
                    ecsWorld.removeComponent(entity.getId(), EffectsContainerComponent.class);
                    System.out.println("    [EffectsSystem] EffectsContainerComponent removed from Entity " + entity.getId() + " as it's empty.");
                }
            }
        }
    }
}
```

## 3. Conceptual Usage Flow

```java
import java.util.concurrent.TimeUnit;
import java.util.*;

// Assume Entity (from ecs_overview example), EcsWorldForEffects (extended), Component,
// HealthComponent (from damage example), ActiveEffectComponent, EffectsContainerComponent,
// EntityEffectsSystem are all defined above.

public class HytaleGameplayEntityEffectsExample {
    public static void main(String[] args) throws InterruptedException {
        // Conceptual Hytale ECS World setup
        EcsWorldForEffects hytaleWorld = new EcsWorldForEffects();

        // Add the EntityEffectsSystem
        EntityEffectsSystem effectsSystem = new EntityEffectsSystem(hytaleWorld);
        hytaleWorld.addSystem(effectsSystem);

        // --- Create an Entity (e.g., a player) ---
        Entity player = hytaleWorld.createEntity();
        hytaleWorld.addComponent(player.getId(), new NameComponent("PlayerHero"));
        hytaleWorld.addComponent(player.getId(), new HealthComponent(100.0f, 100.0f));

        System.out.println("--- Initial State ---");
        System.out.println("Player " + player.getId() + " Health: " + hytaleWorld.getComponent(player.getId(), HealthComponent.class));
        System.out.println("Player " + player.getId() + " EffectsContainer: " + hytaleWorld.getComponent(player.getId(), EffectsContainerComponent.class));
        hytaleWorld.tick(0.05f); // Simulate one tick to run systems
        System.out.println("---------------------\\n");
        TimeUnit.MILLISECONDS.sleep(100);

        // --- Scenario 1: Apply a POISON effect (duration: 5 seconds, potency: 1.0) ---
        System.out.println("--- Scenario 1: Apply POISON effect ---");
        effectsSystem.applyEffect(player.getId(), "POISON", 5000L, 1.0f);
        hytaleWorld.tick(0.05f); // Tick to process new effect
        System.out.println("Player " + player.getId() + " Health: " + hytaleWorld.getComponent(player.getId(), HealthComponent.class));
        System.out.println("Player " + player.getId() + " EffectsContainer: " + hytaleWorld.getComponent(player.getId(), EffectsContainerComponent.class));
        System.out.println("---------------------\\n");
        TimeUnit.MILLISECONDS.sleep(100);

        // --- Scenario 2: Apply a SPEED_BOOST effect (duration: 3 seconds, potency: 1.5) ---
        System.out.println("--- Scenario 2: Apply SPEED_BOOST effect ---");
        effectsSystem.applyEffect(player.getId(), "SPEED_BOOST", 3000L, 1.5f);
        hytaleWorld.tick(0.05f);
        System.out.println("Player " + player.getId() + " EffectsContainer: " + hytaleWorld.getComponent(player.getId(), EffectsContainerComponent.class));
        System.out.println("---------------------\\n");
        TimeUnit.MILLISECONDS.sleep(100);

        // --- Simulate time passing for effects to tick and expire ---
        System.out.println("--- Simulating 4 seconds of game time ---");
        for (int i = 0; i < 40; i++) { // 40 ticks * 0.1s = 4 seconds
            hytaleWorld.tick(0.1f); // Use a larger delta time for quicker simulation
            // Print state periodically
            if (i % 10 == 0) {
                System.out.println("  Time: " + (i * 0.1f) + "s. Health: " +
                                   (hytaleWorld.hasComponent(player.getId(), HealthComponent.class) ?
                                   hytaleWorld.getComponent(player.getId(), HealthComponent.class).currentHealth : "N/A"));
                System.out.println("  Effects: " + hytaleWorld.getComponent(player.getId(), EffectsContainerComponent.class));
            }
            TimeUnit.MILLISECONDS.sleep(100); // Small pause for readability
        }
        System.out.println("---------------------\\n");

        // --- Scenario 3: Explicitly remove remaining effects (e.g., antidote) ---
        System.out.println("--- Scenario 3: Explicitly remove remaining effects ---");
        effectsSystem.removeEffectByType(player.getId(), "POISON"); // Poison should be gone or expired already
        effectsSystem.removeEffectByType(player.getId(), "SPEED_BOOST"); // Remove remaining speed boost if any
        hytaleWorld.tick(0.05f);
        System.out.println("Player " + player.getId() + " EffectsContainer: " + hytaleWorld.getComponent(player.getId(), EffectsContainerComponent.class));
        System.out.println("---------------------\\n");
    }
}
```

### Conceptual Output:

```
--- Initial State ---
Player 1 Health: HealthComponent{current=100.0, max=100.0}
Player 1 EffectsContainer: null
  [EffectsSystem] Ticking active effects...
---------------------

--- Scenario 1: Apply POISON effect ---
  [EffectsSystem] Applied effect ActiveEffectComponent{type='POISON', potency=1.0, timeLeftSec=5} to Entity 1
  [EffectsSystem] Ticking active effects...
Player 1 Health: HealthComponent{current=100.0, max=100.0}
Player 1 EffectsContainer: EffectsContainerComponent{effects=[ActiveEffectComponent{type='POISON', potency=1.0, timeLeftSec=5}]}
---------------------

--- Scenario 2: Apply SPEED_BOOST effect ---
  [EffectsSystem] Applied effect ActiveEffectComponent{type='SPEED_BOOST', potency=1.5, timeLeftSec=3} to Entity 1
  [EffectsSystem] Ticking active effects...
Player 1 EffectsContainer: EffectsContainerComponent{effects=[ActiveEffectComponent{type='POISON', potency=1.0, timeLeftSec=5}, ActiveEffectComponent{type='SPEED_BOOST', potency=1.5, timeLeftSec=3}]}
---------------------

--- Simulating 4 seconds of game time ---
  [EffectsSystem] Ticking active effects...
      Entity 1 poisoned (0.10 damage). Health: 99.9
  Time: 0.0s. Health: 99.9
  Effects: EffectsContainerComponent{effects=[ActiveEffectComponent{type='POISON', potency=1.0, timeLeftSec=4}, ActiveEffectComponent{type='SPEED_BOOST', potency=1.5, timeLeftSec=2}]}
  [EffectsSystem] Ticking active effects...
      Entity 1 poisoned (0.10 damage). Health: 99.8
...
  [EffectsSystem] Ticking active effects...
      Entity 1 poisoned (0.10 damage). Health: 99.5
    [EffectsSystem] Effect SPEED_BOOST expired on Entity 1
  Time: 1.0s. Health: 99.5
  Effects: EffectsContainerComponent{effects=[ActiveEffectComponent{type='POISON', potency=1.0, timeLeftSec=0}]}
  [EffectsSystem] Ticking active effects...
    [EffectsSystem] Effect POISON expired on Entity 1
    [EffectsSystem] EffectsContainerComponent removed from Entity 1 as it's empty.
  Time: 2.0s. Health: 99.5
  Effects: null
---------------------

--- Scenario 3: Explicitly remove remaining effects ---
  [EffectsSystem] Explicitly removed effect POISON from Entity 1 (no effect, already gone)
  [EffectsSystem] Explicitly removed effect SPEED_BOOST from Entity 1 (no effect, already gone)
  [EffectsSystem] Ticking active effects...
Player 1 EffectsContainer: null
---------------------
```

```