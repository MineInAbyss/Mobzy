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
        layerSpawns.put("Orth", Arrays.asList(
                new MobSpawn.MobSpawnBuilder().withMobID("FUWAGI").withMinLightLevel(7).withMinAmount(2).withMaxAmount(6).withRadius(6).withWhitelist(Arrays.asList(Material.GRASS_BLOCK)).build(),
                new MobSpawn.MobSpawnBuilder().withMobID("ROHANA").withMaxLightLevel(7).withMinAmount(5).withRadius(6).withWhitelist(Arrays.asList(Material.GRASS_BLOCK)).build()
        ));

        layerSpawns.put("Edge of the Abyss", Arrays.asList(
                new MobSpawn.MobSpawnBuilder().withMobID("FUWAGI").build())
        );

        layerSpawns.put("Forest of Temptation", Arrays.asList(
                new MobSpawn.MobSpawnBuilder().withMobID("INBYO").build(),
                new MobSpawn.MobSpawnBuilder().withMobID("CORPSE_WEEPER").build())
        );

        layerSpawns.put("Great Fault", Arrays.asList(
                new MobSpawn.MobSpawnBuilder().withMobID("NERITANTAN").build())
        );

        layerSpawns.put("The Goblet of Giants", Arrays.asList(
                new MobSpawn.MobSpawnBuilder().withMobID("ROHANA").build())
        );

        layerSpawns.put("Sea of Corpses", Arrays.asList()
        );

        layerSpawns.put("The Capital of the Unreturned", Arrays.asList()
        );

        layerSpawns.put("The Final Maelstrom", Arrays.asList()
        );
    }

    public static Map<String, List<MobSpawn>> getLayerSpawns() {
        return layerSpawns;
    }
}
