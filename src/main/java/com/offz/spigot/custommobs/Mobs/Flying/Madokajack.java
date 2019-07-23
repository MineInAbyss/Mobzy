package com.offz.spigot.custommobs.Mobs.Flying;

import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Mobs.Types.FlyingMob;
import net.minecraft.server.v1_13_R2.World;

public class Madokajack extends FlyingMob implements HitBehaviour {
    public Madokajack(World world) {
        super(world, "Madokajack");
        setSize(3f, 3f);
    }
}