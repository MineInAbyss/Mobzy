package com.offz.spigot.mobzy.Mobs;

import com.google.auto.value.AutoValue;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@AutoValue
public abstract class MobDrop implements ConfigurationSerializable {
    public static MobDrop create(Material material, int amount, double dropChance) {
        return create(material, amount, amount, dropChance);
    }

    public static MobDrop create(Material material, int minAmount, int maxAmount) {
        return create(material, minAmount, maxAmount, 1);
    }

    public static MobDrop create(Material material, int minAmount, int maxAmount, double dropChance) {
        return create(new ItemStack(material), minAmount, maxAmount, dropChance);
    }

    public static MobDrop create(ItemStack item, int minAmount, int maxAmount, double dropChance) {
        return new AutoValue_MobDrop(item, minAmount, maxAmount, dropChance);
    }

    /**
     * Required method for configuration serialization
     *
     * @param args map to deserialize
     * @return deserialized item stack
     * @see ConfigurationSerializable
     */
    public static MobDrop deserialize(Map<String, Object> args) {
        ItemStack item;
        int minAmount;
        int maxAmount;
        double dropChance = 1;

        if (args.containsKey("item"))
            item = ((ItemStack) args.get("item"));
        else if (args.containsKey("material"))
            try {
                item = new ItemStack(Material.getMaterial((String) args.get("material")));
            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("Deserializing " + args);
                e.printStackTrace();
                return null;
            }
        else
            return null;

        if (args.containsKey("drop-chance"))
            dropChance = (Double) args.get("drop-chance");

        if (args.containsKey("amount")) {
            int amount = (Integer) args.get("amount");
            return create(item, amount, amount, dropChance);
        }

        if (!(args.containsKey("min-amount") && args.containsKey("max-amount")))
            return create(item, 1, 1, dropChance);

        minAmount = (Integer) args.get("min-amount");
        maxAmount = (Integer) args.get("max-amount");

        return create(item, minAmount, maxAmount, dropChance);
    }

    public ItemStack chooseDrop() {
        if (Math.random() < dropChance()) {
            ItemStack drop = item().clone();
            drop.setAmount((int) ((Math.random() * (maxAmount() - minAmount())) + minAmount()));
            return drop;
        }
        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }

    abstract ItemStack item();

    abstract int minAmount();

    abstract int maxAmount();

    //TODO calculate looting
    abstract double dropChance();
}
