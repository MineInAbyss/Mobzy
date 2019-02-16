package com.offz.spigot.custommobs.Mobs;

import com.offz.spigot.custommobs.Behaviours.AnimationBehaviour;
import com.offz.spigot.custommobs.Behaviours.SpawnModelBehaviour;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import com.offz.spigot.custommobs.Mobs.Type.NPCMobType;
import net.minecraft.server.v1_13_R2.EntityVillager;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.entity.Villager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NPC extends EntityVillager {
    private boolean firstSetName = true;

    public NPC(World world) {
        super(world);
        Villager npc = (Villager) this.getBukkitEntity();

        this.addScoreboardTag("customMob");
        this.addScoreboardTag("NPC");
        this.setCustomNameVisible(true);
        this.setSilent(true);
        this.setInvulnerable(true);
        npc.setRemoveWhenFarAway(false);

        npc.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, true));

        this.getWorld().addEntity(this);
    }

    @Override
    public void setCustomName(IChatBaseComponent iChatBaseComponent) {
        super.setCustomName(iChatBaseComponent);
        if (firstSetName) {
            firstSetName = false;
            Villager npc = (Villager) this.getBukkitEntity();
            NPCMobType type = (NPCMobType) MobType.getRegisteredMobType(iChatBaseComponent.getString());

            this.addScoreboardTag(type.getEntityTypeName());
            npc.setCustomName(type.getName());

            if (type.isBaby())
                npc.setBaby();
            else
                npc.setAdult();
            npc.setAgeLock(true);

            AnimationBehaviour.registerMob(npc, type, type.getModelID());
            SpawnModelBehaviour.spawnModel(npc, type);

            this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);
        }
    }
}
