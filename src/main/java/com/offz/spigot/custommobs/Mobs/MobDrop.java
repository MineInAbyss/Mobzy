package com.offz.spigot.custommobs.Mobs;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MobDrop {
    private Material material;
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
        this.material = material;
        this.dropChance = dropChance;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public ItemStack chooseDrop() {
        if (Math.random() < dropChance)
            return new ItemStack(material, (int) ((Math.random() * (maxAmount - minAmount)) + minAmount));
        return null;
    }
}
