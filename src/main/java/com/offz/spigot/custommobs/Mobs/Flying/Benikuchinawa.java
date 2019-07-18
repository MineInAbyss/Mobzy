package com.offz.spigot.custommobs.Mobs.Flying;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.World;

public class Benikuchinawa extends FlyingMob implements HitBehaviour {
    static MobBuilder builder = new MobBuilder("Benikuchinawa", 32);

    public Benikuchinawa(World world) {
        super(world, builder);
        setSize(5f, 2f);
        //An alternative method for setting boundaries which doesn't seem to work anymore
//        AxisAlignedBB boundingBox = new AxisAlignedBB(locX - 5, locY, locZ - 1, locX + 5, locY + 1, locZ + 1);
//        a(boundingBox);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(50.0D);
    }
}