package com.offz.spigot.custommobs.Mobs.Hostile;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Mobs.MobDrop;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalWalkingAnimation;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.v1_13_R2.GenericAttributes;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Material;

public class Tamaugachi extends HostileMob implements HitBehaviour {
    static MobBuilder builder = new MobBuilder("Tamaugachi", 17)
            .setDisguiseAs(DisguiseType.ZOMBIE)
            .setDrops(new MobDrop(Material.NETHER_WART, 4), new MobDrop(Material.QUARTZ, 5, 10));

    public Tamaugachi(World world) {
        super(world, builder);
        this.setSize(2.1F, 3F);

    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        this.goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, builder.getModelID()));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(100.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.5D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(7.0);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(64.0);
    }
}