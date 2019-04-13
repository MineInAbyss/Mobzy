package com.offz.spigot.custommobs.Spawning;

import com.derongan.minecraft.deeperworld.world.WorldManager;
import com.derongan.minecraft.mineinabyss.AbyssContext;
import com.derongan.minecraft.mineinabyss.world.AbyssWorldManager;
import com.offz.spigot.custommobs.Loading.CustomType;
import com.offz.spigot.custommobs.Loading.SpawnRegistry;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.ChatMessage;
import net.minecraft.server.v1_13_R2.EntityTypes;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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

    public static boolean spawnEntity(String name, Location loc, Entity remove) {
        remove.remove();
        return spawnEntity(name, loc);
    }

    /**
     * Spawns entity at specified Location
     *
     * @param name name of entity to spawn
     * @param loc  Location to spawn at
     * @return Reference to the spawned bukkit Entity
     */
    public static boolean spawnEntity(String name, Location loc) {
        EntityTypes entityTypes = CustomType.types.get(MobType.toEntityTypeName(name));
        if (entityTypes == null)
            return false;
        net.minecraft.server.v1_13_R2.Entity nmsEntity = entityTypes.a( // NMS method to spawn an entity from an EntityTypes
                ((CraftWorld) loc.getWorld()).getHandle(), // reference to the NMS world
                null, // EntityTag NBT compound
                new ChatMessage(name), // custom name of entity
                null, // player reference. used to know if player is OP to apply EntityTag NBT compound
                new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), // the BlockPosition to spawn at
                true, // center entity on BlockPosition and correct Y position for Entity's height
                false); // not sure. alters the Y position. this is only ever true when using spawn egg and clicked face is UP
        // feel free to further modify your entity here if wanted
        // it's already been added to the world at this point
        return true;
//        return nmsEntity == null ? null : nmsEntity.getBukkitEntity(); // convert to a Bukkit entity (THIS ALWAYS RETURNS NULL)
    }
}