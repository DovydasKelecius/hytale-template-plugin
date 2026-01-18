# Hytale Gameplay: Interaction System (Example)

This example conceptually demonstrates Hytale's Interaction System, focusing on how interactions are triggered, chained, and processed, based on the official documentation (https://hytale-docs.pages.dev/modding/systems/interactions/).

The documentation highlights `InteractionModule`, `Interactions Component`, `InteractionManager Component`, various interaction types (input-based, client-side, server-side, control flow), "Root Interactions," chaining, selectors, and several processing systems.

## 1. Defining Interaction-Related Components, Actions, and Selectors

```java
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

// Conceptual Hytale Component interface (from ecs_overview example)
// public interface Component extends Cloneable, Serializable {}

// Conceptual Hytale Entity (from ecs_overview example)
// public class Entity { public final long id; ... }


// Enum for different types of interaction events/triggers
public enum InteractionTriggerType {
    PRIMARY_CLICK,    // Left-click, e.g., attack
    SECONDARY_CLICK,  // Right-click, e.g., use item
    USE_BLOCK,        // Interacting with a block
    USE_ENTITY,       // Interacting with an entity
    PROJECTILE_IMPACT, // When a projectile hits
    ABILITY_TRIGGER    // Triggering a special ability
}

// Conceptual base interface for an interaction action
public interface InteractionAction {
    /**
     * Executes the action logic.
     * @param ecsWorld The ECS world for accessing components and entities.
     * @param sourceEntityId The ID of the entity initiating the interaction.
     * @param targetEntityId The ID of the entity being targeted by the action.
     * @param context Additional context/properties for the action.
     */
    void execute(EcsWorld ecsWorld, long sourceEntityId, long targetEntityId, Map<String, Object> context);
}

// Example: Server-side action to damage an entity
public class DamageEntityAction implements InteractionAction {
    private final float damageAmount;
    private final String damageCause;

    public DamageEntityAction(float damageAmount, String damageCause) {
        this.damageAmount = damageAmount;
        this.damageCause = damageCause;
    }

    @Override
    public void execute(EcsWorld ecsWorld, long sourceEntityId, long targetEntityId, Map<String, Object> context) {
        System.out.println("    [Action: DamageEntity] Entity " + sourceEntityId + " deals " + damageAmount + " " + damageCause + " damage to Entity " + targetEntityId);
        // In a real system, this would publish a Damage event (as per Damage System example)
        // ecsWorld.getEventBus().publish(new Damage(new Entity(targetEntityId), new Entity(sourceEntityId), damageCause, damageAmount));
    }
}

// Example: Client-side action to place a block (needs server validation)
public class PlaceBlockAction implements InteractionAction {
    private final String blockType;

    public PlaceBlockAction(String blockType) {
        this.blockType = blockType;
    }

    @Override
    public void execute(EcsWorld ecsWorld, long sourceEntityId, long targetEntityId, Map<String, Object> context) {
        float targetX = (float) context.getOrDefault("targetX", 0f);
        float targetY = (float) context.getOrDefault("targetY", 0f);
        float targetZ = (float) context.getOrDefault("targetZ", 0f);
        System.out.println("    [Action: PlaceBlock] Entity " + sourceEntityId + " attempts to place " + blockType + " at (" + targetX + "," + targetY + "," + targetZ + ")");
        // This would typically send a packet to the server for validation and actual placement
    }
}

// Conceptual InteractionStep, which can be part of a chain
public class InteractionStep implements Serializable {
    public final InteractionAction action;
    public final InteractionSelector selector; // Optional: determines targets for this step
    public final Map<String, Object> properties; // Cooldowns, conditions, animation triggers etc.

    public InteractionStep(InteractionAction action, InteractionSelector selector, Map<String, Object> properties) {
        this.action = action;
        this.selector = selector;
        this.properties = properties;
    }

    public InteractionStep(InteractionAction action) {
        this(action, null, new HashMap<>());
    }
}

// Conceptual interface for an interaction target selector
public interface InteractionSelector {
    /**
     * Selects target entity IDs for an interaction based on source and context.
     * @param ecsWorld The ECS world.
     * @param sourceEntityId The entity initiating the selection.
     * @param context Additional context (e.g., raycast origin/direction, AOE center).
     * @return A list of entity IDs that are selected as targets.
     */
    List<Long> selectTargets(EcsWorld ecsWorld, long sourceEntityId, Map<String, Object> context);
}

// Example: Raycast Selector (targets a single entity in line of sight)
public class RaycastSelector implements InteractionSelector {
    @Override
    public List<Long> selectTargets(EcsWorld ecsWorld, long sourceEntityId, Map<String, Object> context) {
        System.out.println("    [Selector: Raycast] Entity " + sourceEntityId + " performing raycast.");
        // In a real game, this would query the physics engine for hits.
        // For this example, let's assume it always hits a fixed NPC (entity ID 2) if it exists.
        if (ecsWorld.getAllEntities().stream().anyMatch(e -> e.getId() == 2)) {
            return Collections.singletonList(2L); // Target NPC entity
        }
        return Collections.emptyList();
    }
}

// Example: AOECircle Selector (targets entities within an area of effect)
public class AOECircleSelector implements InteractionSelector {
    private final float radius;

    public AOECircleSelector(float radius) {
        this.radius = radius;
    }

    @Override
    public List<Long> selectTargets(EcsWorld ecsWorld, long sourceEntityId, Map<String, Object> context) {
        System.out.println("    [Selector: AOECircle] Entity " + sourceEntityId + " performing AOE search (radius=" + radius + ").");
        // In a real game, this would query entities within a radius from sourceEntityId's position.
        // For simplicity, let's say it targets entity 3 if it exists (another generic entity)
        if (ecsWorld.getAllEntities().stream().anyMatch(e -> e.getId() == 3)) {
            return Collections.singletonList(3L);
        }
        return Collections.emptyList();
    }
}


// Component: Attached to an entity, defining what interactions it has
public class InteractionsComponent implements Component {
    // Map of InteractionTriggerType to a list of root-level InteractionSteps
    public final Map<InteractionTriggerType, List<InteractionStep>> rootInteractions;

    public InteractionsComponent() {
        this.rootInteractions = new HashMap<>();
    }

    public void addRootInteraction(InteractionTriggerType trigger, InteractionStep step) {
        rootInteractions.computeIfAbsent(trigger, k -> new ArrayList<>()).add(step);
    }

    @Override
    public InteractionsComponent clone() {
        try {
            InteractionsComponent cloned = (InteractionsComponent) super.clone();
            // Deep copy interaction steps if they contain mutable state, for now shallow
            cloned.rootInteractions.putAll(this.rootInteractions);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("InteractionsComponent not cloneable!");
        }
    }

    @Override
    public String toString() {
        return "InteractionsComponent{triggers=" + rootInteractions.keySet() + '}
';
    }
}

// Component: Attached to an entity, managing currently active interaction chains
public class InteractionManagerComponent implements Component {
    private final Map<Long, InteractionStep> activeChains; // InteractionChain ID -> current step
    private static final AtomicLong interactionChainIdCounter = new AtomicLong();

    public InteractionManagerComponent() {
        this.activeChains = new HashMap<>();
    }

    public long startInteractionChain(InteractionStep initialStep) {
        long chainId = interactionChainIdCounter.incrementAndGet();
        activeChains.put(chainId, initialStep);
        System.out.println("    [InteractionManager] Started chain " + chainId + " with step: " + initialStep.action.getClass().getSimpleName());
        return chainId;
    }

    public InteractionStep getActiveStep(long chainId) {
        return activeChains.get(chainId);
    }

    public void completeInteractionChain(long chainId) {
        activeChains.remove(chainId);
        System.out.println("    [InteractionManager] Completed chain " + chainId);
    }

    @Override
    public InteractionManagerComponent clone() {
        try {
            InteractionManagerComponent cloned = (InteractionManagerComponent) super.clone();
            cloned.activeChains.putAll(this.activeChains);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("InteractionManagerComponent not cloneable!");
        }
    }

    @Override
    public String toString() {
        return "InteractionManagerComponent{activeChains=" + activeChains.keySet() + '}
';
    }
}
```

## 2. Conceptual Interaction Systems

```java
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;

// Conceptual Hytale System interface (from ecs_overview example)
// public interface System { void run(float deltaTime); }

// Conceptual Hytale ECS World (from ecs_overview example)
// public class EcsWorld { ... }
// Needs add/get/has component methods

// System to add InteractionManagerComponent to new player-like entities
public class PlayerAddInteractionManagerSystem implements System {
    private final EcsWorld ecsWorld;

    public PlayerAddInteractionManagerSystem(EcsWorld ecsWorld) {
        this.ecsWorld = ecsWorld;
    }

    @Override
    public void run(float deltaTime) {
        // Find entities that are players (conceptual) and don't have an InteractionManagerComponent
        for (Entity entity : ecsWorld.getAllEntities()) {
            if (ecsWorld.hasComponent(entity.getId(), NameComponent.class)) { // Example check for "Player"
                NameComponent name = ecsWorld.getComponent(entity.getId(), NameComponent.class);
                if (name.name.startsWith("Player") && !ecsWorld.hasComponent(entity.getId(), InteractionManagerComponent.class)) {
                    ecsWorld.addComponent(entity.getId(), new InteractionManagerComponent());
                    System.out.println("  [PlayerAddManagerSystem] Added InteractionManagerComponent to Player " + entity.getId());
                }
            }
        }
    }
}

// System that processes active interaction chains
public class TickInteractionManagerSystem implements System {
    private final EcsWorld ecsWorld;

    public TickInteractionManagerSystem(EcsWorld ecsWorld) {
        this.ecsWorld = ecsWorld;
    }

    @Override
    public void run(float deltaTime) {
        System.out.println("  [TickInteractionManagerSystem] Ticking active interaction chains...");
        for (Entity entity : ecsWorld.getAllEntities()) {
            if (ecsWorld.hasComponent(entity.getId(), InteractionManagerComponent.class)) {
                InteractionManagerComponent manager = ecsWorld.getComponent(entity.getId(), InteractionManagerComponent.class);

                // Process a copy to avoid concurrent modification issues if chains are completed
                List<Long> currentChainIds = new ArrayList<>(manager.activeChains.keySet());
                for (long chainId : currentChainIds) {
                    InteractionStep currentStep = manager.getActiveStep(chainId);
                    if (currentStep != null) {
                        System.out.println("    [TickManager] Processing step for chain " + chainId + " on Entity " + entity.getId());

                        List<Long> targets = Collections.emptyList();
                        // If there's a selector, use it to find targets
                        if (currentStep.selector != null) {
                            targets = currentStep.selector.selectTargets(ecsWorld, entity.getId(), currentStep.properties);
                        } else {
                            // If no selector, interaction applies to the source entity itself
                            targets = Collections.singletonList(entity.getId());
                        }

                        // Execute the action for each target
                        for (long targetId : targets) {
                            currentStep.action.execute(ecsWorld, entity.getId(), targetId, currentStep.properties);
                        }

                        // For this simple example, we complete the chain after one step.
                        // In reality, a chain might have multiple steps, conditions, delays, etc.
                        manager.completeInteractionChain(chainId);
                    }
                }
            }
        }
    }
}

// Other mentioned systems (conceptual, not implemented for brevity):
// CleanUpSystem: Responsible for removing completed/expired interaction data.
// TrackerTickSystem: Manages networked tracking of interactions for clients.
```

## 3. Conceptual Usage Flow

```java
import java.util.concurrent.TimeUnit;
import java.util.*;

// Assume Entity (from ecs_overview example), EcsWorld (from ecs_overview example), Component,
// InteractionTriggerType, InteractionAction, DamageEntityAction, PlaceBlockAction,
// InteractionSelector, RaycastSelector, AOECircleSelector, InteractionStep,
// InteractionsComponent, InteractionManagerComponent, PlayerAddInteractionManagerSystem,
// TickInteractionManagerSystem are all defined above.

public class HytaleGameplayInteractionsExample {
    public static void main(String[] args) throws InterruptedException {
        // Conceptual Hytale ECS World setup
        EcsWorld hytaleWorld = new EcsWorld();

        // Add systems
        hytaleWorld.addSystem(new PlayerAddInteractionManagerSystem(hytaleWorld));
        hytaleWorld.addSystem(new TickInteractionManagerSystem(hytaleWorld));

        // ---
 Create Entities ---
        Entity player = hytaleWorld.createEntity();
        hytaleWorld.addComponent(player.getId(), new NameComponent("PlayerOne"));
        // Player will get InteractionManagerComponent from PlayerAddInteractionManagerSystem

        Entity npc = hytaleWorld.createEntity();
        hytaleWorld.addComponent(npc.getId(), new NameComponent("FriendlyNPC"));
        hytaleWorld.addComponent(npc.getId(), new HealthComponent(50.0f, 50.0f));

        Entity anotherEntity = hytaleWorld.createEntity();
        hytaleWorld.addComponent(anotherEntity.getId(), new NameComponent("Tree"));


        // Define interactions for the player (attached as an InteractionsComponent)
        InteractionsComponent playerInteractions = new InteractionsComponent();
        playerInteractions.addRootInteraction(InteractionTriggerType.PRIMARY_CLICK,
                new InteractionStep(new DamageEntityAction(15.0f, "MELEE_ATTACK"), new RaycastSelector(), new HashMap<>()));
        playerInteractions.addRootInteraction(InteractionTriggerType.SECONDARY_CLICK,
                new InteractionStep(new PlaceBlockAction("DIRT_BLOCK"), new RaycastSelector(), Collections.singletonMap("targetX", 5f))); // Simplified target for placeblock
        hytaleWorld.addComponent(player.getId(), playerInteractions);

        System.out.println("--- Initial State ---");
        hytaleWorld.tick(0.05f); // Let PlayerAddInteractionManagerSystem run
        System.out.println("Player " + player.getId() + " Interactions: " + hytaleWorld.getComponent(player.getId(), InteractionsComponent.class));
        System.out.println("Player " + player.getId() + " InteractionManager: " + hytaleWorld.getComponent(player.getId(), InteractionManagerComponent.class));
        System.out.println("NPC " + npc.getId() + " Health: " + hytaleWorld.getComponent(npc.getId(), HealthComponent.class));
        System.out.println("---------------------\
");
        TimeUnit.MILLISECONDS.sleep(100);

        // ---
 Simulate Player Primary Click (attacking NPC) ---
        System.out.println("--- Scenario 1: Player Primary Click (attacks NPC) ---");
        InteractionManagerComponent playerManager = hytaleWorld.getComponent(player.getId(), InteractionManagerComponent.class);
        InteractionsComponent playerInts = hytaleWorld.getComponent(player.getId(), InteractionsComponent.class);

        // An external input system would convert player input into starting an interaction chain
        List<InteractionStep> primaryClickSteps = playerInts.rootInteractions.get(InteractionTriggerType.PRIMARY_CLICK);
        if (primaryClickSteps != null && !primaryClickSteps.isEmpty()) {
            playerManager.startInteractionChain(primaryClickSteps.get(0));
        }
        hytaleWorld.tick(0.05f); // Tick the world to process the started chain
        System.out.println("Player " + player.getId() + " InteractionManager: " + hytaleWorld.getComponent(player.getId(), InteractionManagerComponent.class));
        System.out.println("NPC " + npc.getId() + " Health: " + hytaleWorld.getComponent(npc.getId(), HealthComponent.class));
        System.out.println("---------------------\
");
        TimeUnit.MILLISECONDS.sleep(100);

        // ---
 Simulate Player Secondary Click (placing a block) ---
        System.out.println("--- Scenario 2: Player Secondary Click (places block) ---");
        List<InteractionStep> secondaryClickSteps = playerInts.rootInteractions.get(InteractionTriggerType.SECONDARY_CLICK);
        if (secondaryClickSteps != null && !secondaryClickSteps.isEmpty()) {
            playerManager.startInteractionChain(secondaryClickSteps.get(0));
        }
        hytaleWorld.tick(0.05f);
        System.out.println("Player " + player.getId() + " InteractionManager: " + hytaleWorld.getComponent(player.getId(), InteractionManagerComponent.class));
        System.out.println("---------------------\
");
    }
}
```

### Conceptual Output:

```
--- Initial State ---
  [PlayerAddManagerSystem] Added InteractionManagerComponent to Player 1
  [TickInteractionManagerSystem] Ticking active interaction chains...
Player 1 Interactions: InteractionsComponent{triggers=[PRIMARY_CLICK, SECONDARY_CLICK]}
Player 1 InteractionManager: InteractionManagerComponent{activeChains={}}
NPC 2 Health: HealthComponent{current=50.0, max=50.0}
---------------------

--- Scenario 1: Player Primary Click (attacks NPC) ---
    [InteractionManager] Started chain 1 with step: DamageEntityAction
  [TickInteractionManagerSystem] Ticking active interaction chains...
    [TickManager] Processing step for chain 1 on Entity 1
    [Selector: Raycast] Entity 1 performing raycast.
    [Action: DamageEntity] Entity 1 deals 15.0 MELEE_ATTACK damage to Entity 2
    [InteractionManager] Completed chain 1
Player 1 InteractionManager: InteractionManagerComponent{activeChains={}}
NPC 2 Health: HealthComponent{current=35.0, max=50.0}
---------------------

--- Scenario 2: Player Secondary Click (places block) ---
    [InteractionManager] Started chain 2 with step: PlaceBlockAction
  [TickInteractionManagerSystem] Ticking active interaction chains...
    [TickManager] Processing step for chain 2 on Entity 1
    [Selector: Raycast] Entity 1 performing raycast.
    [Action: PlaceBlock] Entity 1 attempts to place DIRT_BLOCK at (5.0,0.0,0.0)
    [InteractionManager] Completed chain 2
Player 1 InteractionManager: InteractionManagerComponent{activeChains={}}
---------------------
```