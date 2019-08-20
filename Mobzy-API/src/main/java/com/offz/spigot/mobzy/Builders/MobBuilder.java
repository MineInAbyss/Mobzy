package com.offz.spigot.mobzy.Builders;

import com.offz.spigot.mobzy.Mobs.MobDrop;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class MobBuilder implements ConfigurationSerializable {
    private String name;
    private int modelID;
    private Material modelMaterial = Material.DIAMOND_SWORD;
    private DisguiseType disguiseAs = DisguiseType.ZOMBIE;
    private List<Material> temptItems;
    private Double maxHealth;
    private Double movementSpeed;
    private Double followRange;
    private Double attackDamage;
    private Integer minExp;
    private Integer maxExp;
    private boolean isAdult = true;
    private List<MobDrop> drops = new ArrayList<>();
    public MobBuilder(String name, int modelID) {
        this.name = name;
        this.modelID = modelID;
    }

    public static MobBuilder deserialize(Map<String, Object> args, String name) {
        if (args.containsKey("name"))
            name = (String) args.get("name");

        MobBuilder builder = new MobBuilder(name, (int) args.get("model"));

        if (args.containsKey("adult"))
            builder.setAdult((boolean) args.get("adult"));
        if (args.containsKey("disguise-as"))
            builder.setDisguiseAs(DisguiseType.valueOf((String) args.get("disguise-as")));
        if (args.containsKey("drops"))
            builder.setDrops(((List<Map<String, Object>>) args.get("drops")).stream()
                    .map(MobDrop::deserialize)
                    .collect(Collectors.toList()));
        if (args.containsKey("model-material"))
            builder.setModelMaterial(Material.getMaterial((String) args.get("model-material")));
        if (args.containsKey("tempt-items"))
            builder.setTemptItems(((List<Object>) args.get("tempt-items")).stream()
                    .map(item -> Material.getMaterial((String) item))
                    .collect(Collectors.toList()));
        if (args.containsKey("max-health"))
            builder.setMaxHealth(((Number) args.get("max-health")).doubleValue());
        if (args.containsKey("movement-speed"))
            builder.setMovementSpeed(((Number) args.get("movement-speed")).doubleValue());
        if (args.containsKey("attack-damage"))
            builder.setAttackDamage(((Number) args.get("attack-damage")).doubleValue());
        if (args.containsKey("follow-range"))
            builder.setFollowRange(((Number) args.get("follow-range")).doubleValue());
        if (args.containsKey("min-exp"))
            builder.setMinExp(((Number) args.get("min-exp")).intValue());
        if (args.containsKey("max-exp"))
            builder.setMaxExp(((Number) args.get("max-exp")).intValue());
        return builder;
    }

    public Integer getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(Integer maxExp) {
        this.maxExp = maxExp;
    }

    public Integer getMinExp() {
        return minExp;
    }

    public void setMinExp(Integer minExp) {
        this.minExp = minExp;
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

    public MobBuilder setModelMaterial(Material modelMaterial) {
        this.modelMaterial = modelMaterial;
        return this;
    }

    public ItemStack getModelItemStack() {
        ItemStack is = new ItemStack(getModelMaterial(), 1, (short) getModelID());
        ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(getName());
        meta.setUnbreakable(true);
        is.setItemMeta(meta);
        return is;
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

    /**
     * Does nothing yet
     *
     * @return a serialized version of the mob builder
     */
    @Override
    public Map<String, Object> serialize() {
        return null;
    }
}
