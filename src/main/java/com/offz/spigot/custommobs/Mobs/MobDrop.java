package com.offz.spigot.custommobs.Mobs;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MobDrop {
    private Material material;
    private double dropChance;
    private int minAmount;
    private int maxAmount;


    public MobDrop(Material material, double dropChance) {
        this(material, dropChance, 1, 1);
    }

    public MobDrop(Material material, double dropChance, int maxAmount) {
        this(material, dropChance, 0, maxAmount);
    }

    public MobDrop(Material material, double dropChance, int minAmount, int maxAmount) {
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
