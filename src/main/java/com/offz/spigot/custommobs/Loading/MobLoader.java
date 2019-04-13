package com.offz.spigot.custommobs.Loading;

import com.offz.spigot.custommobs.MobContext;
import com.offz.spigot.custommobs.Mobs.Type.FlyingMobType;
import com.offz.spigot.custommobs.Mobs.Type.GroundMobType;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import com.offz.spigot.custommobs.Mobs.Type.NPCMobType;

public class MobLoader {


    public static void loadAllMobs(MobContext context) {
        Class[] mobTypes = {GroundMobType.class, FlyingMobType.class, NPCMobType.class};

        for (Class c : mobTypes)
            for (Object o : c.getEnumConstants())
                MobType.registerMobType((MobType) o);
        CustomType.registerAllMobs();
        SpawnRegistry.registerMobSpawns();
    }
    public static void unloadAllMobs() {
        // Unregister
        MobType.unregisterAllMobs();
    }

}

