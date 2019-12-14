package com.offz.spigot.mobzy.spawning.regions;

import com.offz.spigot.mobzy.Mobzy;
import com.offz.spigot.mobzy.MobzyConfig;
import com.offz.spigot.mobzy.spawning.MobSpawn;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityTypes;

import java.util.*;

/**
 * A region with determined hostile, passive, flying, etc... spawns. Currently only layers are treated as regions.
 * In the future, this will integrate with WorldGuard regions to allow for more specific region setting
 */
public class SpawnRegion {
    //TODO maybe mob caps should be determined per region?
    private Map<Class<? extends Entity>, List<MobSpawn>> spawns = new HashMap<>();
    private MobzyConfig config = (Mobzy.getInstance()).getMobzyConfig();
    private String name;

    public SpawnRegion(String name, MobSpawn... spawns) {
        this.name = name;

        for (MobSpawn spawn : spawns) {
            addSpawn(spawn);
        }
    }

    public String getName() {
        return name;
    }

    public List<MobSpawn> getSpawnsFor(Class<? extends Entity> mobType) {
        if(!spawns.containsKey(mobType))
            return Collections.emptyList();
        return spawns.get(mobType);
    }

    public void addSpawn(MobSpawn spawn) {
        Class entityClass = spawn.getEntityType().c();
        //add to a different spawn list depending on what kind of entity type it is (since we have separate mob caps per list)
        for (Class<? extends Entity> type : config.getRegisteredMobTypes().values()) {
            if (type.isAssignableFrom(entityClass)) {
                if (spawns.get(type) == null)
                    spawns.put(type, new ArrayList<>(Collections.singletonList(spawn)));
                else
                    spawns.get(type).add(spawn);
                return;
            }
        }
    }

    public MobSpawn getSpawnOfType(EntityTypes type) {
        return spawns.values().stream()
                .flatMap(List::stream)
                .filter(spawn -> spawn.getEntityType().equals(type))
                .findFirst()
                .orElse(null);
    }
}
