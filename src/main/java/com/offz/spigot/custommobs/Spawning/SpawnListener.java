package com.offz.spigot.custommobs.Spawning;

import com.derongan.minecraft.deeperworld.world.WorldManager;
import com.derongan.minecraft.mineinabyss.AbyssContext;
import com.derongan.minecraft.mineinabyss.world.AbyssWorldManager;
import com.offz.spigot.custommobs.Loading.CustomType;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import net.minecraft.server.v1_13_R2.BlockPosition;
import net.minecraft.server.v1_13_R2.EntityTypes;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
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

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent e) {
        if (MobType.getRegisteredMobType(e.getEntity()) == null && e.getEntity().getType() != null && !e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
            AbyssWorldManager manager = context.getWorldManager();
            switch (e.getEntity().getType()) {
                case ZOMBIE:
                    if (manager.getLayerForSection(worldManager.getSectionFor(e.getLocation())).getName().equals("Orth")) {
                        spawnEntity(CustomType.NERITANTAN, e.getLocation());
                        e.getEntity().remove();
                    }
                    break;
                case SKELETON:
                    if (manager.getLayerForSection(worldManager.getSectionFor(e.getLocation())).getName().equals("Orth")) {
                        spawnEntity(CustomType.FUWAGI, e.getLocation());
                        e.getEntity().remove();
                    }
                    break;

                default:
                    break;
            }
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
