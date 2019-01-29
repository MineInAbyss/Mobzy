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
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        AbyssWorldManager manager = context.getWorldManager();
        if (MobType.getRegisteredMobType(e.getEntity()) == null && e.getEntity().getType() != null && !e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM) && manager.isAbyssWorld(e.getEntity().getWorld())) {
            Entity original = e.getEntity();

            if(e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)) {
                if(original.getName().equals("Mitty"))
                    spawnEntity(CustomType.npc.get("MITTY"), e.getLocation(), original);
                if(original.getName().equals("Nanachi"))
                    spawnEntity(CustomType.npc.get("NANACHI"), e.getLocation(), original);
                if(original.getName().equals("Marulk"))
                    spawnEntity(CustomType.npc.get("MARULK"), e.getLocation(), original);
                if(original.getName().equals("Ozen"))
                    spawnEntity(CustomType.npc.get("OZEN"), e.getLocation(), original);
            }

            if (manager.getLayerForSection(worldManager.getSectionFor(e.getLocation())).getName().equals("Orth"))
                switch (original.getType()) {
                }


            else if (manager.getLayerForSection(worldManager.getSectionFor(e.getLocation())).getName().equals("Edge of the Abyss"))
                switch (original.getType()) {
                    case PIG:
                    case COW:
                    case SHEEP:
                        spawnEntity(CustomType.ground.get("FUWAGI"), e.getLocation(), original);
                        break;
                    case ZOMBIE:
                        spawnEntity(CustomType.ground.get("ROHANA"), e.getLocation(), original);
                }


            else if (manager.getLayerForSection(worldManager.getSectionFor(e.getLocation())).getName().equals("Forest of Temptation"))
                switch (original.getType()) {
                    case WITCH:
                        if (Math.random() >= 0.5)
                            spawnEntity(CustomType.flying.get("CORPSE_WEEPER"), e.getLocation(), original);
                        break;
                    case ZOMBIE:
                        if (e.getEntity().getLocation().getY() > 130)
                            spawnEntity(CustomType.ground.get("INBYO"), e.getLocation(), original);
                        break;
                }

            else if (manager.getLayerForSection(worldManager.getSectionFor(e.getLocation())).getName().equals("Great Fault"))
                switch (original.getType()) {
                    case ZOMBIE:
                        spawnEntity(CustomType.ground.get("NERITANTAN"), e.getLocation(), original);
                        break;
                }

            else if (manager.getLayerForSection(worldManager.getSectionFor(e.getLocation())).getName().equals("The Goblets of Giants"))
                switch (original.getType()) {
                    case ZOMBIE:
                        spawnEntity(CustomType.ground.get("ROHANA"), e.getLocation(), original);
                        spawnEntity(CustomType.ground.get("ROHANA"), e.getLocation(), original);
                        spawnEntity(CustomType.ground.get("ROHANA"), e.getLocation(), original);
                        break;
                }

            else if (manager.getLayerForSection(worldManager.getSectionFor(e.getLocation())).getName().equals("Sea of Corpses"))
                switch (original.getType()) {

                }

            else if (manager.getLayerForSection(worldManager.getSectionFor(e.getLocation())).getName().equals("The Capital of the Unreturned"))
                switch (original.getType()) {

                }

            else if (manager.getLayerForSection(worldManager.getSectionFor(e.getLocation())).getName().equals("The Final Maelstrom"))
                switch (original.getType()) {

                }
        }
    }

    public static Entity spawnEntity(EntityTypes entityTypes, Location loc, Entity remove){
        remove.remove();
        return spawnEntity(entityTypes, loc);
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
