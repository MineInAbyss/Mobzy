package com.offz.spigot.custommobs.Mobs;

import com.offz.spigot.custommobs.Behaviours.AnimationBehaviour;
import com.offz.spigot.custommobs.Behaviours.SpawnModelBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.GroundMobType;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.entity.Pig;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class PassiveMob extends EntityPig {

    private boolean firstSetName = true;

    public PassiveMob(World world) {
        super(world);
        Pig passiveMob = (Pig) this.getBukkitEntity();

        //TODO: Custom Pathfinders current have to be done by a class that extends this class.
        // Not a terrible way of doing things but we'll have to make a new method if we want text-based mob creation
//        this.targetSelector.a(0, new PathfinderGoalNearestAttackableTarget<Neritantan>(this, Neritantan.class, true));
//        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, RecipeItemStack.a(new IMaterial[]{Items.CARROT,Items.APPLE}), false));
        this.addScoreboardTag("customMob");
        this.setCustomNameVisible(false);
        this.setSilent(true);
        passiveMob.setRemoveWhenFarAway(true);

        passiveMob.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true));

        this.getWorld().addEntity(this);
    }

    //TODO: I'd hate having to copy this method every time, maybe have a class with this that we can extend from,
    // we just need a way to be able to choose what mob that class extends from too
    @Override
    public void setCustomName(IChatBaseComponent iChatBaseComponent) {
        super.setCustomName(iChatBaseComponent);
        if (firstSetName) {
            firstSetName = false;
            Pig mob = (Pig) this.getBukkitEntity();
            GroundMobType type = (GroundMobType) MobType.getRegisteredMobType(iChatBaseComponent.getString());

            mob.addScoreboardTag(type.getEntityTypeName());
            mob.setCustomName(type.getName());

            if (type.isBaby())
                mob.setBaby();
            else
                mob.setAdult();
            mob.setAgeLock(true);

            AnimationBehaviour.registerMob(mob, type, type.getModelID());
            if (type.getBehaviour() instanceof SpawnModelBehaviour)
                SpawnModelBehaviour.spawnModel(mob, type);
            else {
                org.bukkit.inventory.ItemStack is = new ItemStack(org.bukkit.Material.DIAMOND_SWORD);
                is.setDurability(type.getModelID());

                ItemMeta meta = is.getItemMeta();
                meta.setUnbreakable(true);
                is.setItemMeta(meta);

                mob.getEquipment().setHelmet(is);
            }

            Map<String, Double> initAttributes = type.getInitAttributes();

            for (String attributeName : type.getInitAttributes().keySet()) {
                try {
                    this.getAttributeInstance((IAttribute) GenericAttributes.class.getField(attributeName).get(this)).setValue(initAttributes.get(attributeName));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                }
            }
        }
    }
}
