package com.offz.spigot.custommobs.Listener;

import com.offz.spigot.custommobs.CustomMobs;
import com.offz.spigot.custommobs.MobContext;
import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Mobs.CustomMob;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.EnumItemSlot;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class MobListener implements Listener {
    CustomMobs plugin;
    private MobContext context;

    public MobListener(MobContext context) {
        this.context = context;
        plugin = (CustomMobs) context.getPlugin();
    }

    /**
     * We use this method to prevent any entity related statistics if they are from our custom mobs, since it causes
     * server crashes. This is mainly used to prevent entity killing players statistic changes, since player killing
     * entity statistics are already handled by the custom mobs' die() methods (if those are handled here, they seem to
     * still cause errors sometimes).
     *
     * @param e the event
     */
    @EventHandler
    public void onStatisticIncrement(PlayerStatisticIncrementEvent e) {
        //if the statistic is entity related and the entity is null, it must be custom, therefore we cancel the event
        if (e.getStatistic().getType().equals(Statistic.Type.ENTITY) && e.getEntityType() == null) {
            e.setCancelled(true);
//            CustomMobsAPI.debug(ChatColor.RED + "Overrode statistic");
        }
    }

    /**
     * Switch to the hit model of the entity, then shortly after, back to the normal one to create a hit effect
     *
     * @param e the event
     */
    @EventHandler
    public void onHit(EntityDamageEvent e) {
        Entity entity = (((CraftEntity) e.getEntity()).getHandle());
        if (entity instanceof HitBehaviour) {
            int modelID = ((CustomMob) entity).getBuilder().getModelID();
            net.minecraft.server.v1_13_R2.ItemStack is = ((EntityLiving) entity).getEquipment(EnumItemSlot.HEAD);
            is.setDamage(modelID + 2);
            entity.setEquipment(EnumItemSlot.HEAD, is);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!entity.getBukkitEntity().isDead()) {
                        is.setDamage(modelID + 1);
                        entity.setEquipment(EnumItemSlot.HEAD, is);
                    }
                }
            }.runTaskLater(plugin, 5);
        }
    }

}