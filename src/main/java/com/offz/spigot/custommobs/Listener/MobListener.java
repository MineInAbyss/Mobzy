package com.offz.spigot.custommobs.Listener;

import com.offz.spigot.custommobs.CustomMobs;
import com.offz.spigot.custommobs.MobContext;
import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Mobs.CustomMob;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.EntityLiving;
import net.minecraft.server.v1_13_R2.EnumItemSlot;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

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

            //TODO One time I got a NoClassDefFoundError when trying to run the task, but I haven't been able to
            // replicate it since. Reloading the plugin fixed it.
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!entity.getBukkitEntity().isDead()) {
                    is.setDamage(modelID);
                    entity.setEquipment(EnumItemSlot.HEAD, is);
                }
            }, 5);
        }
    }

    /**
     * Remove all old entities, which still contain the "customMob" tag (now "customMob2")
     *
     * @param e the event
     */
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        for (org.bukkit.entity.Entity entity : e.getChunk().getEntities()) {
            if (entity.getScoreboardTags().contains("customMob"))
                entity.remove();
        }
    }

    /**
     * The magic method that lets you hit entities in their server side hitboxes
     * TODO this doesn't work in adventure mode, but the alternative is a lot worse to deal with. Decide what to do.
     *
     * @param e
     */
    @EventHandler
    public void onLeftClick(PlayerInteractEvent e) {
        //TODO I'd like some way to ignore hits onto the disguised entity. This could be done by using a marker
        // armorstand as a disguise, but the disguise plugin seems to crash clients whenever we do that :yeeko:
        Player p = e.getPlayer();
        if (leftClicked(e) || rightClicked(e)) {
            RayTraceResult trace = p.getWorld().rayTrace(p.getLocation().add(new Vector(0, p.getEyeHeight(), 0)), p.getLocation().getDirection(), 3, FluidCollisionMode.ALWAYS, true, 0, entity -> !entity.equals(p));
            if (trace != null && trace.getHitEntity() != null) {
                Entity hit = ((CraftEntity) trace.getHitEntity()).getHandle();
                if (!(hit instanceof CustomMob))
                    return;

                if (leftClicked(e)) {
                    e.setCancelled(true);
                    ((CraftPlayer) p).getHandle().attack(hit);
                } else {
                    EntityHuman nmsPlayer = ((CraftPlayer) p).getHandle();
                    ((CustomMob) hit).onRightClick(nmsPlayer);
                }
            }
        }
    }


    public boolean leftClicked(PlayerInteractEvent e) {
        return e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK;
    }

    //TODO this event doesn't send out a packet when right clicking air with an empty hand
    public boolean rightClicked(PlayerInteractEvent e) {
        return e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK;
    }
}