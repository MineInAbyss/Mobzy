package com.offz.spigot.custommobs.Loading;

import com.offz.spigot.custommobs.Spawning.MobSpawn;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnRegistry {
    private static Map<String, List<MobSpawn>> layerSpawns = new HashMap<>();

    static {
        addSpawn("Orth",
                new MobSpawn.MobSpawnBuilder().withMobID(CustomType.FUWAGI).withMinLightLevel(7).withMinAmount(2).withMaxAmount(3).withRadius(6).withWhitelist(Material.GRASS_BLOCK).build(),
                new MobSpawn.MobSpawnBuilder().withMobID(CustomType.ROHANA).withMaxLightLevel(7).withMinAmount(4).withMaxAmount(5).withRadius(6).withWhitelist(Material.GRASS_BLOCK).build(),
                new MobSpawn.MobSpawnBuilder().withMobID(CustomType.MOUNT).withMinAmount(1).withWhitelist(Material.GRASS_BLOCK, Material.STONE, Material.STONE_BRICKS, Material.COBBLESTONE).withBasePriority(0.2).build()
        );

        addSpawn("Edge of the Abyss",
                new MobSpawn.MobSpawnBuilder().withMobID(CustomType.FUWAGI).build(),
                new MobSpawn.MobSpawnBuilder().withMobID(CustomType.HAMMERBEAK).withBasePriority(0.4).withSpawnPos(MobSpawn.SpawnPosition.AIR).build()
        );

        addSpawn("Forest of Temptation",
                new MobSpawn.MobSpawnBuilder().withMobID(CustomType.INBYO).build(),
                new MobSpawn.MobSpawnBuilder().withMobID(CustomType.CORPSE_WEEPER).build()
        );

        addSpawn("Great Fault",
                new MobSpawn.MobSpawnBuilder().withMobID(CustomType.NERITANTAN).build()
        );

        addSpawn("The Goblet of Giants",
                new MobSpawn.MobSpawnBuilder().withMobID(CustomType.ROHANA).build()
        );

        addSpawn("Sea of Corpses");

        addSpawn("The Capital of the Unreturned");

        addSpawn("The Final Maelstrom");
    }

    private static void addSpawn(String name, MobSpawn... spawns) {
        layerSpawns.put(name, Arrays.asList(spawns));
    }

    public static Map<String, List<MobSpawn>> getLayerSpawns() {
        return layerSpawns;
    }
}
