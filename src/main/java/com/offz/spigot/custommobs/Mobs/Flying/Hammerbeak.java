package com.offz.spigot.custommobs.Mobs.Flying;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Mobs.MobDrop;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Material;

public class Hammerbeak extends FlyingMob implements HitBehaviour {
    static MobBuilder builder = new MobBuilder("Hammerbeak", 23)
            .setDrops(new MobDrop(Material.BEEF, 1));

    public Hammerbeak(World world) {
        super(world, builder);
        setSize(3F, 3F);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
    }
}