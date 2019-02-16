package com.offz.spigot.custommobs.Mobs;

import com.offz.spigot.custommobs.Behaviours.AnimationBehaviour;
import com.offz.spigot.custommobs.Behaviours.SpawnModelBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.GroundMobType;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;

public class CustomZombie extends EntityZombie {

    private boolean firstSetName = true;

    public CustomZombie(World world) {
        super(world);
        Zombie passiveMob = (Zombie) this.getBukkitEntity();

        this.addScoreboardTag("customMob");
        this.setCustomNameVisible(false);
        this.setSilent(true);
        passiveMob.setRemoveWhenFarAway(true);

        passiveMob.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true));

        this.getWorld().addEntity(this);
    }

    @Override
    public void setCustomName(IChatBaseComponent iChatBaseComponent) {
        super.setCustomName(iChatBaseComponent);
        if (firstSetName) {
            firstSetName = false;
            Zombie mob = (Zombie) this.getBukkitEntity();
            GroundMobType type = (GroundMobType) MobType.getRegisteredMobType(iChatBaseComponent.getString());

            mob.addScoreboardTag(type.getEntityTypeName());
            mob.setCustomName(type.getName());
            mob.setBaby(type.isBaby());
            mob.getEquipment().clear();

            AnimationBehaviour.registerMob(mob, type, type.getModelID());
            if (type.getBehaviour() instanceof SpawnModelBehaviour) {
                SpawnModelBehaviour.spawnModel(mob, type);
                mob.getEquipment().setHelmet(new ItemStack(org.bukkit.Material.STONE_BUTTON)); //stop the mobs from burning (don't know of a better way yet)
            } else {
                ItemStack is = new ItemStack(org.bukkit.Material.DIAMOND_SWORD);
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

    //TODO: This does nothing; figure out different way of cancelling burning in daytime
//    @Override
//    public void k() {
//        super.k();
//        this.getBukkitEntity().setFireTicks(0);
//    }
}
