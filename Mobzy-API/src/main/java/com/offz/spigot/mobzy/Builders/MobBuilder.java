package com.offz.spigot.mobzy.Builders;

import com.offz.spigot.mobzy.Mobs.MobDrop;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class MobBuilder {
    private String name;
    private int modelID;
    private Material modelMaterial = Material.DIAMOND_SWORD;
    private DisguiseType disguiseAs = DisguiseType.ZOMBIE;
    private List<Material> temptItems;
    private Double maxHealth;
    private Double movementSpeed;
    private Double followRange;
    private Double attackDamage;

    private boolean isAdult = true;
    private List<MobDrop> drops = new ArrayList<>();

    public MobBuilder(String name, int modelID) {
        this.name = name;
        this.modelID = modelID;
    }

    public MobBuilder(MobBuilder copy) {
        this.name = copy.name;
        this.modelID = copy.modelID;
        this.modelMaterial = copy.modelMaterial;
        this.disguiseAs = copy.disguiseAs;
        this.temptItems = copy.temptItems;
        this.maxHealth = copy.maxHealth;
        this.movementSpeed = copy.movementSpeed;
        this.followRange = copy.followRange;
        this.attackDamage = copy.attackDamage;
    }

    public boolean isAdult() {
        return isAdult;
    }

    public MobBuilder setAdult(boolean adult) {
        isAdult = adult;
        return this;
    }

    public List<ItemStack> getDrops() {
        List<ItemStack> chosenDrops = new ArrayList<>();
        for (MobDrop drop : drops) {
            ItemStack chosenDrop = drop.chooseDrop();
            chosenDrops.add(chosenDrop);
        }
        return chosenDrops;
    }

    public MobBuilder setDrops(List<MobDrop> drops) {
        this.drops = drops;
        return this;
    }

    public Material getModelMaterial() {
        return modelMaterial;
    }

    public ItemStack getModelItemStack() {
        ItemStack is = new ItemStack(getModelMaterial(), 1, (short) getModelID());
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(getName());
        meta.setUnbreakable(true);
        is.setItemMeta(meta);
        return is;
    }

    public MobBuilder setModelMaterial(Material modelMaterial) {
        this.modelMaterial = modelMaterial;
        return this;
    }

    public String getName() {
        return name;
    }

    public MobBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public int getModelID() {
        return modelID;
    }

    public MobBuilder setModelID(int modelID) {
        this.modelID = modelID;
        return this;
    }

    public DisguiseType getDisguiseAs() {
        return disguiseAs;
    }

    public MobBuilder setDisguiseAs(DisguiseType disguiseAs) {
        this.disguiseAs = disguiseAs;
        return this;
    }

    public List<Material> getTemptItems() {
        return temptItems;
    }

    public MobBuilder setTemptItems(List<Material> temptItems) {
        this.temptItems = temptItems;
        return this;
    }

    public Double getMaxHealth() {
        return maxHealth;
    }

    public MobBuilder setMaxHealth(Double maxHealth) {
        this.maxHealth = maxHealth;
        return this;
    }

    public Double getMovementSpeed() {
        return movementSpeed;
    }

    public MobBuilder setMovementSpeed(Double movementSpeed) {
        this.movementSpeed = movementSpeed;
        return this;
    }

    public Double getFollowRange() {
        return followRange;
    }

    public MobBuilder setFollowRange(Double followRange) {
        this.followRange = followRange;
        return this;
    }

    public Double getAttackDamage() {
        return attackDamage;
    }

    public MobBuilder setAttackDamage(Double attackDamage) {
        this.attackDamage = attackDamage;
        return this;
    }
}
