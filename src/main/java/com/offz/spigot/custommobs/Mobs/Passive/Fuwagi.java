package com.offz.spigot.custommobs.Mobs.Passive;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Mobs.MobDrop;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalTemptPitchLock;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Material;

public class Fuwagi extends PassiveMob implements HitBehaviour {
    static MobBuilder builder = new MobBuilder("Fuwagi", 5)
            .setTemptItems(new Item[]{Items.CARROT, Items.APPLE})
            .setDrops(new MobDrop(Material.PORKCHOP, 1));

    public Fuwagi(World world) {
        super(world, builder);
    }

    @Override
    protected void createPathfinders() {
        super.createPathfinders();
        this.goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, builder.getModelID()));
        this.goalSelector.a(4, new PathfinderGoalTemptPitchLock(this, 1.2D, false, builder.getTemptItems()));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.2D);
    }

    @Override
    public SoundEffect soundHurt() {
        return SoundEffects.ENTITY_RABBIT_ATTACK;
    }

    @Override
    public SoundEffect soundAmbient() {
        return SoundEffects.ENTITY_RABBIT_AMBIENT;
    }

    @Override
    public SoundEffect soundStep() {
        return SoundEffects.ENTITY_RABBIT_JUMP;
    }

    @Override
    public SoundEffect soundDeath() {
        return SoundEffects.ENTITY_RABBIT_DEATH;
    }

    public PassiveMob createChild(EntityAgeable entityageable) {
        return new Neritantan(this.world);
    }
}
