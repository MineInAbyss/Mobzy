package com.offz.spigot.custommobs.Loading;

import com.offz.spigot.custommobs.MobContext;
import com.offz.spigot.custommobs.Mobs.Type.GroundMobType;
import com.offz.spigot.custommobs.Mobs.Type.MobType;

public class MobLoader {


    public static void loadAllMobs(MobContext context) {
        for (GroundMobType groundMobType : GroundMobType.values()) {
            MobType.registerMobType(groundMobType);
        }
        CustomType.registerAllMobs();
    }
    public static void unloadAllMobs() {
        // Unregister
        MobType.unregisterAllMobs();
    }

}

