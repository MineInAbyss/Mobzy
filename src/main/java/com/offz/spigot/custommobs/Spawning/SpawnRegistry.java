package com.offz.spigot.custommobs.Spawning;

import com.offz.spigot.custommobs.CustomType;
import com.offz.spigot.custommobs.Spawning.Regions.SpawnRegion;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class SpawnRegistry {
    private static Map<String, SpawnRegion> layerSpawns = new HashMap<>();

    static {
        addSpawn("Orth",
                new MobSpawn.MobSpawnBuilder().setEntityType(CustomType.FUWAGI).setMinLightLevel(7).setMinAmount(2).setMaxAmount(3).setRadius(6).setWhitelist(Material.GRASS_BLOCK).build(),
                new MobSpawn.MobSpawnBuilder().setEntityType(CustomType.HAMMERBEAK).setBasePriority(0.1).setSpawnPos(MobSpawn.SpawnPosition.AIR).build(),
                new MobSpawn.MobSpawnBuilder().setEntityType(CustomType.MOUNT).setSections("city").setMinAmount(1).setWhitelist(Material.GRASS_BLOCK, Material.STONE_BRICKS).setBasePriority(0.2).build()
        );

        addSpawn("Edge of the Abyss",
                new MobSpawn.MobSpawnBuilder().setEntityType(CustomType.FUWAGI).setMinLightLevel(7).setMinAmount(2).setMaxAmount(3).setRadius(6).setWhitelist(Material.GRASS_BLOCK).build(),
                new MobSpawn.MobSpawnBuilder().setEntityType(CustomType.HAMMERBEAK).setBasePriority(0.1).setSpawnPos(MobSpawn.SpawnPosition.AIR).build(),
                new MobSpawn.MobSpawnBuilder().setEntityType(CustomType.SILKFANG).setRadius(4).setWhitelist(Material.STONE, Material.COBBLESTONE).build()
        );

        addSpawn("Forest of Temptation",
                new MobSpawn.MobSpawnBuilder().setEntityType(CustomType.INBYO).setSections("l2s5").setMinAmount(4).setMaxAmount(8).setMaxLightLevel(7).setMinY(140).build(),
                new MobSpawn.MobSpawnBuilder().setEntityType(CustomType.CORPSE_WEEPER).setBasePriority(0.4).setSpawnPos(MobSpawn.SpawnPosition.AIR).build()
        );

        addSpawn("Great Fault",
                new MobSpawn.MobSpawnBuilder().setEntityType(CustomType.NERITANTAN).setMinAmount(3).setMaxAmount(5).setWhitelist(Material.ANDESITE, Material.WHITE_CONCRETE, Material.GRASS_BLOCK, Material.DIRT).build(),
                new MobSpawn.MobSpawnBuilder().setEntityType(CustomType.MADOKAJACK).setMinAmount(2).setMaxAmount(4).setBasePriority(0.4).setSpawnPos(MobSpawn.SpawnPosition.AIR).build()
        );

        addSpawn("The Goblet of Giants",
                new MobSpawn.MobSpawnBuilder().setEntityType(CustomType.ROHANA).setMaxLightLevel(7).setMinAmount(4).setMaxAmount(5).setRadius(6).setWhitelist(Material.GRASS_BLOCK).build(),
                new MobSpawn.MobSpawnBuilder().setEntityType(CustomType.KAZURA).setMaxLightLevel(7).setWhitelist(Material.WATER).build(),
                new MobSpawn.MobSpawnBuilder().setEntityType(CustomType.KUONGATARI).setMinAmount(2).setMaxAmount(4).setRadius(6).setMaxLightLevel(7).setWhitelist(Material.GRASS_BLOCK).build()
        );

        addSpawn("Sea of Corpses");

        addSpawn("The Capital of the Unreturned");

        addSpawn("The Final Maelstrom");
    }

    private static void addSpawn(String name, MobSpawn... spawns) {
        layerSpawns.put(name, new SpawnRegion(name, spawns));
    }

    public static Map<String, SpawnRegion> getLayerSpawns() {
        return layerSpawns;
    }
}
