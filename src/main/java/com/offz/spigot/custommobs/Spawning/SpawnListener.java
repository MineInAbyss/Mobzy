package com.offz.spigot.custommobs.Spawning;

import com.offz.spigot.custommobs.CustomMobs;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.EntityTypes;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class SpawnListener implements Listener {

    public SpawnListener() {
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e){
        switch(e.getEntity().getType()){
            case OCELOT:

                Entity spawnedEntity = spawnEntity(CustomMobs.CUSTOM_ZOMBIE, e.getLocation());
                e.getEntity().remove();
                break;
            default:
                break;
        }
    }

    /**
     * Spawns entity at specified Location
     *
     * @param entityTypes Type of entity to spawn
     * @param loc Location to spawn at
     * @return Reference to the spawned bukkit Entity
     */
    public static Entity spawnEntity(EntityTypes entityTypes, Location loc) {
        net.minecraft.server.v1_13_R2.Entity nmsEntity = entityTypes.a( // NMS method to spawn an entity from an EntityTypes
                ((CraftWorld) loc.getWorld()).getHandle(), // reference to the NMS world
                null, // EntityTag NBT compound
                null, // custom name of entity
                null, // player reference. used to know if player is OP to apply EntityTag NBT compound
                new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), // the BlockPosition to spawn at
                true, // center entity on BlockPosition and correct Y position for Entity's height
                false); // not sure. alters the Y position. this is only ever true when using spawn egg and clicked face is UP

        // feel free to further modify your entity here if wanted
        // it's already been added to the world at this point

        return nmsEntity == null ? null : nmsEntity.getBukkitEntity(); // convert to a Bukkit entity
    }
}
