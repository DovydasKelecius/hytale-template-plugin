# Hytale ECS Entity Stats System (Example)

This example demonstrates how Hytale's Entity Stats System might be implemented and used, based on the descriptions from the official documentation page (https://hytale-docs.pages.dev/modding/ecs/entity-stats/).

The documentation highlights `EntityStatMap`, `EntityStatValue`, and various `Modifier` types. It also notes that stat types can be defined in JSON assets.

## 1. Defining Stat-Related Classes and Interfaces

```java
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

// Conceptual Hytale Component interface
// public interface Component extends Cloneable, Serializable {}

// Interface for a generic stat modifier
public interface IStatModifier {
    String getKey();
    // Function to apply the modification to a value
    Function<Float, Float> getOperation();
}

// An additive modifier
public class AdditiveModifier implements IStatModifier, Serializable {
    private final String key;
    private final float value;

    public AdditiveModifier(String key, float value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Function<Float, Float> getOperation() {
        return baseValue -> baseValue + value;
    }

    @Override
    public String toString() {
        return "AddModifier(key='" + key + "', val=" + value + ")";
    }
}

// A multiplicative modifier (e.g., +20% -> factor 1.2f)
public class MultiplicativeModifier implements IStatModifier, Serializable {
    private final String key;
    private final float factor;

    public MultiplicativeModifier(String key, float factor) {
        this.key = key;
        this.factor = factor;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Function<Float, Float> getOperation() {
        return baseValue -> baseValue * factor;
    }

    @Override
    public String toString() {
        return "MultModifier(key='" + key + "', factor=" + factor + ")";
    }
}

// Represents an individual stat (e.g., Health, Strength)
public class EntityStatValue implements Serializable {
    private final String name;
    private float baseValue;
    private float minBound;
    private float maxBound;
    private final Map<String, IStatModifier> modifiers = new LinkedHashMap<>(); // Order matters for application

    public EntityStatValue(String name, float baseValue, float minBound, float maxBound) {
        this.name = name;
        this.baseValue = baseValue;
        this.minBound = minBound;
        this.maxBound = maxBound;
    }

    public String getName() {
        return name;
    }

    public float getBaseValue() {
        return baseValue;
    }

    public void setBaseValue(float baseValue) {
        this.baseValue = baseValue;
        // In a real system, changing base value would trigger a recalculation
    }

    public void addModifier(IStatModifier modifier) {
        modifiers.put(modifier.getKey(), modifier);
    }

    public void removeModifier(String key) {
        modifiers.remove(key);
    }

    /**
     * Recalculates the current value of the stat, applying modifiers in order.
     * Additive modifiers are typically applied before multiplicative ones.
     */
    public float getComputedValue() {
        float computed = baseValue;

        // Apply additive modifiers first
        for (IStatModifier mod : modifiers.values()) {
            if (mod instanceof AdditiveModifier) {
                computed = mod.getOperation().apply(computed);
            }
        }

        // Then apply multiplicative modifiers
        for (IStatModifier mod : modifiers.values()) {
            if (mod instanceof MultiplicativeModifier) {
                computed = mod.getOperation().apply(computed);
            }
        }

        return Math.max(minBound, Math.min(maxBound, computed));
    }

    @Override
    public String toString() {
        return "Stat[" + name + ": base=" + baseValue + ", current=" + String.format("%.2f", getComputedValue()) +
               ", mods=" + modifiers.values() + "]";
    }
}

// Component to hold all EntityStatValue instances for an entity
public class EntityStatMapComponent implements Component {
    private final Map<String, EntityStatValue> stats = new HashMap<>();

    public void addStat(EntityStatValue stat) {
        stats.put(stat.getName(), stat);
    }

    public EntityStatValue getStat(String name) {
        return stats.get(name);
    }

    public boolean hasStat(String name) {
        return stats.containsKey(name);
    }

    @Override
    public EntityStatMapComponent clone() {
        try {
            EntityStatMapComponent cloned = (EntityStatMapComponent) super.clone();
            // Deep copy stats if needed, or if stats are immutable
            cloned.stats.clear();
            for (Map.Entry<String, EntityStatValue> entry : this.stats.entrySet()) {
                // Assuming EntityStatValue needs to be cloned or is immutable
                // For this example, we'll just copy the reference, but deep copy is often needed
                cloned.stats.put(entry.getKey(), entry.getValue());
            }
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("EntityStatMapComponent is not cloneable!");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("EntityStatMapComponent{");
        for (EntityStatValue stat : stats.values()) {
            sb.append("\n  ").append(stat.toString());
        }
        sb.append("\n}");
        return sb.toString();
    }
}
```

## 2. Conceptual Usage with an Entity

```java
import java.util.*;

// Assume Entity (from ecs_overview example), Component, and all stat-related classes are defined above

public class HytaleEcsEntityStatsExample {
    public static void main(String[] args) {
        // Conceptual ECS World (from ecs_overview example)
        EcsWorld hytaleWorld = new EcsWorld();

        // Create a Player Entity
        Entity player = hytaleWorld.createEntity();
        player.addComponent(new NameComponent("HeroPlayer"));

        // Add the EntityStatMapComponent to the player
        EntityStatMapComponent playerStats = new EntityStatMapComponent();
        hytaleWorld.addComponent(player.getId(), playerStats);

        // Define and add various stats
        EntityStatValue healthStat = new EntityStatValue("Health", 100.0f, 0.0f, 200.0f);
        EntityStatValue strengthStat = new EntityStatValue("Strength", 10.0f, 1.0f, 50.0f);
        EntityStatValue moveSpeedStat = new EntityStatValue("MoveSpeed", 5.0f, 0.1f, 15.0f);
        EntityStatValue defenseStat = new EntityStatValue("Defense", 20.0f, 0.0f, 100.0f);

        playerStats.addStat(healthStat);
        playerStats.addStat(strengthStat);
        playerStats.addStat(moveSpeedStat);
        playerStats.addStat(defenseStat);

        System.out.println("--- Initial Player Stats for Entity " + player.getId() + " ---");
        System.out.println(playerStats);
        System.out.println("Computed Health: " + playerStats.getStat("Health").getComputedValue());
        System.out.println("----------------------------------------\n");

        // --- Apply Modifiers ---
        System.out.println("--- Applying Modifiers ---");

        // Temporary Strength Potion: +5 Strength
        strengthStat.addModifier(new AdditiveModifier("ModA:StrengthPotion", 5.0f));
        System.out.println("Strength after potion: " + strengthStat.getComputedValue());

        // Speed Buff: +20% Movement Speed
        moveSpeedStat.addModifier(new MultiplicativeModifier("ModB:SpeedBuff", 1.2f));
        System.out.println("MoveSpeed after buff: " + moveSpeedStat.getComputedValue());

        // Armor Bonus: +15 Defense
        defenseStat.addModifier(new AdditiveModifier("ModC:ArmorBonus", 15.0f));
        System.out.println("Defense after armor: " + defenseStat.getComputedValue());

        // Weakness Debuff: -10% Strength
        strengthStat.addModifier(new MultiplicativeModifier("ModD:Weakness", 0.9f));
        System.out.println("Strength after weakness: " + strengthStat.getComputedValue()); // Potion + Weakness

        System.out.println("\n--- Player Stats After Modifiers ---");
        System.out.println(playerStats);
        System.out.println("------------------------------------\n");

        // --- Remove Modifiers ---
        System.out.println("--- Removing Modifiers ---");
        strengthStat.removeModifier("ModA:StrengthPotion"); // Remove Strength Potion
        moveSpeedStat.removeModifier("ModB:SpeedBuff");     // Remove Speed Buff

        System.out.println("\n--- Player Stats After Removing Some Modifiers ---");
        System.out.println(playerStats);
        System.out.println("--------------------------------------------------\n");

        // Simulate a regeneration tick (conceptual, would be a system)
        System.out.println("--- Simulating Health Regeneration ---");
        healthStat.setBaseValue(healthStat.getBaseValue() + 5.0f); // Imagine a regen effect changes base value
        System.out.println("Health after regen: " + healthStat.getComputedValue());
        System.out.println("--------------------------------------\n");
    }
}
```

### Conceptual Output:

```
--- Initial Player Stats for Entity 1 ---
EntityStatMapComponent{
  Stat[Health: base=100.0, current=100.00, mods=[]]
  Stat[Strength: base=10.0, current=10.00, mods=[]]
  Stat[MoveSpeed: base=5.0, current=5.00, mods=[]]
  Stat[Defense: base=20.0, current=20.00, mods=[]]
}
Computed Health: 100.0
----------------------------------------

--- Applying Modifiers ---
Strength after potion: 15.0
MoveSpeed after buff: 6.0
Defense after armor: 35.0
Strength after weakness: 13.5 // (10 + 5) * 0.9 = 13.5

--- Player Stats After Modifiers ---
EntityStatMapComponent{
  Stat[Health: base=100.0, current=100.00, mods=[]]
  Stat[Strength: base=10.0, current=13.50, mods=[AddModifier(key='ModA:StrengthPotion', val=5.0), MultModifier(key='ModD:Weakness', factor=0.9)]]
  Stat[MoveSpeed: base=5.0, current=6.00, mods=[MultModifier(key='ModB:SpeedBuff', factor=1.2)]]
  Stat[Defense: base=20.0, current=35.00, mods=[AddModifier(key='ModC:ArmorBonus', val=15.0)]]
}
------------------------------------

--- Removing Modifiers ---

--- Player Stats After Removing Some Modifiers ---
EntityStatMapComponent{
  Stat[Health: base=100.0, current=100.00, mods=[]]
  Stat[Strength: base=10.0, current=9.00, mods=[MultModifier(key='ModD:Weakness', factor=0.9)]] // Only weakness remains
  Stat[MoveSpeed: base=5.0, current=5.00, mods=[]]
  Stat[Defense: base=20.0, current=35.00, mods=[AddModifier(key='ModC:ArmorBonus', val=15.0)]]
}
--------------------------------------------------

--- Simulating Health Regeneration ---
Health after regen: 105.0
--------------------------------------