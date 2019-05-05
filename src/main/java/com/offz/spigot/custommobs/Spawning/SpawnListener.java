package com.offz.spigot.custommobs.Spawning;

import com.derongan.minecraft.deeperworld.world.WorldManager;
import com.derongan.minecraft.mineinabyss.AbyssContext;
import org.bukkit.event.Listener;

public class SpawnListener implements Listener {
    private WorldManager worldManager;
    private AbyssContext context;

    public SpawnListener(AbyssContext context) {
        this.context = context;
        this.worldManager = context.getRealWorldManager();
    }

    //probably not going to be replacing vanilla entities anymore
    /*@EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        AbyssWorldManager manager = context.getWorldManager();
        Entity original = e.getEntity();
        if(original.getType().equals(EntityType.BAT)){
            e.setCancelled(true);
            return;
        }
        if (MobType.getRegisteredMobType(e.getEntity()) == null && e.getEntity().getType() != null && !e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM) && !e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.DEFAULT) && manager.isAbyssWorld(e.getEntity().getWorld()) && original.getCustomName() == null) {
            String layerName = manager.getLayerForSection(worldManager.getSectionFor(e.getLocation())).getName();

            *//*RandomCollection<MobSpawn> validSpawns = new RandomCollection<>();

            for (MobSpawn spawn : SpawnRegistry.getLayerSpawns().get(layerName)) {
                double weight = spawn.getPriority(e);
                validSpawns.add(weight, spawn);
            }

            if(validSpawns.isEmpty()) {
                e.setCancelled(true);
                Bukkit.broadcastMessage("Spawn Failed");
                return;
            }*//*

            //weighted random decision of valid spawn
//            MobSpawn spawn = validSpawns.next();
//            spawn.spawn(e.getLocation());
            Bukkit.broadcastMessage("Replaced " + e.getEntity().getType() + " with " *//*+ spawn.getMobID()*//*);
//            e.setCancelled(true);

//            SpawnListener.spawnEntity("ROHANA", e.getLocation());
//            e.getEntity().remove();
            e.setCancelled(true);
        }
    }*/
}