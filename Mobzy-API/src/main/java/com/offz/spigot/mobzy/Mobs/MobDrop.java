package com.offz.spigot.mobzy.Mobs;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class MobDrop implements ConfigurationSerializable {
    private ItemStack item;
    //TODO calculate looting
    private double dropChance;
    private int minAmount;
    private int maxAmount;

    public MobDrop(Material material, int amount) {
        this(material, amount, amount, 1);
    }

    public MobDrop(Material material, int amount, double dropChance) {
        this(material, amount, amount, dropChance);
    }

    public MobDrop(Material material, int minAmount, int maxAmount) {
        this(material, minAmount, maxAmount, 1);
    }

    public MobDrop(Material material, int minAmount, int maxAmount, double dropChance) {
        this(new ItemStack(material), minAmount, maxAmount, dropChance);
    }

    public MobDrop(ItemStack item, int minAmount, int maxAmount, double dropChance) {
        this.item = item;
        this.dropChance = dropChance;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
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
            return new MobDrop(item, amount, amount, dropChance);
        }

        if (!(args.containsKey("min-amount") && args.containsKey("max-amount")))
            return new MobDrop(item, 1, 1, dropChance);

        minAmount = (Integer) args.get("min-amount");
        maxAmount = (Integer) args.get("max-amount");

        return new MobDrop(item, minAmount, maxAmount, dropChance);
    }

    public ItemStack chooseDrop() {
        if (Math.random() < dropChance) {
            ItemStack drop = item.clone();
            drop.setAmount((int) ((Math.random() * (maxAmount - minAmount)) + minAmount));
            return drop;
        }
        return null;
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }
}
