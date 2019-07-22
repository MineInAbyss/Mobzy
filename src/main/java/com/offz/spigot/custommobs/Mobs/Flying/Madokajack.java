package com.offz.spigot.custommobs.Mobs.Flying;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Mobs.MobDrop;
import com.offz.spigot.custommobs.Mobs.Types.FlyingMob;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Material;

public class Madokajack extends FlyingMob implements HitBehaviour {
    static MobBuilder builder = new MobBuilder("Madokajack", 20)
            .setDrops(new MobDrop(Material.BEEF, 2));

    public Madokajack(World world) {
        super(world, builder);
        setSize(3f, 3f);
    }
}