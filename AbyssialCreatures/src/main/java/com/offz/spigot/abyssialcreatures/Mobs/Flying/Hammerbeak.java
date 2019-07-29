package com.offz.spigot.abyssialcreatures.Mobs.Flying;

import com.offz.spigot.mobzy.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.mobzy.Mobs.Types.FlyingMob;
import net.minecraft.server.v1_13_R2.World;

public class Hammerbeak extends FlyingMob implements HitBehaviour {
    public Hammerbeak(World world) {
        super(world, "Hammerbeak");
        setSize(3F, 3F);
    }
}