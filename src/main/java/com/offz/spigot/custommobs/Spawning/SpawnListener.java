package com.offz.spigot.custommobs.Spawning;

import com.derongan.minecraft.deeperworld.world.WorldManager;
import com.derongan.minecraft.mineinabyss.AbyssContext;
import com.derongan.minecraft.mineinabyss.world.AbyssWorldManager;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class SpawnListener implements Listener {
    private WorldManager worldManager;
    private AbyssContext context;

    public SpawnListener(AbyssContext context) {
        this.context = context;
        this.worldManager = context.getRealWorldManager();
    }

    //probably not going to be replacing vanilla entities anymore
    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        AbyssWorldManager manager = context.getWorldManager();
        Entity original = e.getEntity();
        /*if(original.getType().equals(EntityType.BAT)){
            return;
        }
        net.minecraft.server.v1_13_R2.Entity entity = (((CraftEntity) e.getEntity()).getHandle());
        if (!(entity instanceof CustomMob)) {
//            e.setCancelled(true);
            String layerName = manager.getLayerForSection(worldManager.getSectionFor(e.getLocation())).getName();

            RandomCollection<MobSpawn> validSpawns = new RandomCollection<>();

            for (MobSpawn spawn : SpawnRegistry.getLayerSpawns().get(layerName)) {
                double weight = spawn.getPriority(e.getLocation());
                validSpawns.add(weight, spawn);
            }

            if(validSpawns.isEmpty()) {
                e.setCancelled(true);
                CustomMobsAPI.debug(ChatColor.RED + "Spawn Failed");
                return;
            }

            //weighted random decision of valid spawn
//            MobSpawn spawn = validSpawns.next();
//            spawn.spawn(e.getLocation());
            CustomMobsAPI.debug(ChatColor.GREEN + "Spawned Fuwagi");
//            e.setCancelled(true);
            Fuwagi fuwagi = new Fuwagi(((CraftWorld) e.getLocation().getWorld()).getHandle());
            ((CraftWorld) e.getLocation().getWorld()).getHandle().addEntity(fuwagi, CreatureSpawnEvent.SpawnReason.NATURAL);
//            CustomType.spawnEntity("fuwagi", e.getLocation());
            e.getEntity().remove();
        } else
            CustomMobsAPI.debug(e.getSpawnReason() + " Spawn reason");*/
    }
}