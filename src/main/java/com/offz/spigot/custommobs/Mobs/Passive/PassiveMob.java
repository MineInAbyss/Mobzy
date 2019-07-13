package com.offz.spigot.custommobs.Mobs.Passive;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.CustomType;
import com.offz.spigot.custommobs.Mobs.Behaviours.CustomMobBase;
import com.offz.spigot.custommobs.Mobs.Behaviours.Deathable;
import com.offz.spigot.custommobs.Mobs.Behaviours.Disguiseable;
import com.offz.spigot.custommobs.Mobs.CustomMob;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalLookAtPlayerPitchLock;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalLookWhereHeaded;
import net.minecraft.server.v1_13_R2.*;

/**
 * Lots of code taken from EntityPig
 */
public abstract class PassiveMob extends EntityAnimal implements CustomMob {
    protected MobBuilder builder;
    private Deathable deathable = new Deathable(this);
    private Disguiseable disguiseable = new Disguiseable(this);

    public PassiveMob(World world, MobBuilder builder) {
        super(CustomType.getType(builder), world);
        this.builder = builder;
//        ((LivingEntity) getBukkitEntity()).setRemoveWhenFarAway(true);

        CustomMobBase base = new CustomMobBase(this);
        base.apply();
        addScoreboardTag("passiveMob");
    }

    @Override
    public EntityLiving getEntity() {
        return this;
    }

    protected void n() {
        createPathfinders();
    }

    @Override
    public void createPathfinders() {
        goalSelector.a(1, new PathfinderGoalFloat(this));
        goalSelector.a(2, new PathfinderGoalPanic(this, 1.25D));
        goalSelector.a(3, new PathfinderGoalBreed(this, 1.0D));
        goalSelector.a(5, new PathfinderGoalFollowParent(this, 1.1D));
        goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 1.0D));
        goalSelector.a(7, new PathfinderGoalLookAtPlayerPitchLock(this, EntityHuman.class, 6.0F));
        goalSelector.a(8, new PathfinderGoalLookWhereHeaded(this));
    }

    @Override
    public MobBuilder getBuilder() {
        return builder;
    }

    @Override
    public boolean getKilled() {
        return killed;
    }

    @Override
    public void setKilled(boolean killed) {
        this.killed = killed;
    }

    @Override
    public boolean dropsExperience() {
        return isDropExperience();
    }

    @Override
    public int lastDamageByPlayerTime() {
        return lastDamageByPlayerTime;
    }

    @Override
    public int getKillScore() {
        return be;
    }

    @Override
    public void dropEquipment(boolean flag, int i, DamageSource damageSource) {
        a(flag, i, damageSource);
    }

    protected void initAttributes() {
        super.initAttributes();
        getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        unloadMobNBT(nbttagcompound);
    }

    @Override
    public void unloadMobNBT(NBTTagCompound nbttagcompound) {

    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        loadMobNBT(nbttagcompound);
    }

    @Override
    public void loadMobNBT(NBTTagCompound nbttagcompound) {
        disguiseable.disguise();
    }

    protected SoundEffect D() {
        return soundAmbient();
    }

    protected SoundEffect d(DamageSource damagesource) {
        return soundHurt();
    }

    protected SoundEffect cs() {
        return soundDeath();
    }

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        a(soundStep(), 0.15F, 1.0F);
    }

    public void die(DamageSource damagesource) {
        deathable.die(damagesource);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return null;
    }
}
