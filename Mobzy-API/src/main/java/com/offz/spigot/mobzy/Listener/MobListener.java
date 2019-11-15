package com.offz.spigot.mobzy.Listener;

import com.offz.spigot.mobzy.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.mobzy.Mobs.CustomMob;
import com.offz.spigot.mobzy.Mobzy;
import com.offz.spigot.mobzy.MobzyAPI;
import com.offz.spigot.mobzy.MobzyContext;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityHuman;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Statistic;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;

public class MobListener implements Listener {
    Mobzy plugin;
    private MobzyContext context;

    public MobListener(MobzyContext context) {
        this.context = context;
        plugin = (Mobzy) context.getPlugin();
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
    @EventHandler(ignoreCancelled = true)
    public void onHit(EntityDamageEvent e) {

        Entity entity = (((CraftEntity) e.getEntity()).getHandle());
        if (entity instanceof HitBehaviour) {
            //change the model to its hit version
            int modelID = ((CustomMob) entity).getBuilder().getModelID();

            EntityEquipment ee = ((LivingEntity) entity.getBukkitEntity()).getEquipment();
            if (ee == null)
                return;
            ItemStack is = ee.getHelmet();
            ItemMeta itemMeta = is.getItemMeta();
            if (!(itemMeta instanceof Damageable))
                return;
            ((Damageable) itemMeta).setDamage(modelID + 2);
            is.setItemMeta(itemMeta);
            ee.setHelmet(is);

            //in 5 ticks change the model back to the non hit version
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!entity.getBukkitEntity().isDead()) {
                    //get the meta again in case it has changed within the task's delay period
                    ItemMeta newMeta = is.getItemMeta();
                    ((Damageable) newMeta).setDamage(modelID);
                    is.setItemMeta(newMeta);
                    ee.setHelmet(is);
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
     * @param e the event
     */
    @EventHandler
    public void onLeftClick(PlayerInteractEvent e) {
        //TODO I'd like some way to ignore hits onto the disguised entity. This could be done by using a marker
        // armorstand as a disguise, but the disguise plugin seems to crash clients whenever we do that :yeeko:
        Player p = e.getPlayer();
        if (leftClicked(e) || rightClicked(e)) {
            RayTraceResult trace = p.getWorld().rayTrace(p.getEyeLocation(), p.getLocation().getDirection(), 3, FluidCollisionMode.ALWAYS, true, 0, entity -> !entity.equals(p));
            if (trace != null && trace.getHitEntity() != null) {
                Entity hit = MobzyAPI.toNMS(trace.getHitEntity());
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