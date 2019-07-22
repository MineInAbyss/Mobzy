package com.offz.spigot.custommobs.Mobs.Types;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.CustomType;
import com.offz.spigot.custommobs.Mobs.Behaviours.CustomMobBase;
import com.offz.spigot.custommobs.Mobs.Behaviours.Deathable;
import com.offz.spigot.custommobs.Mobs.Behaviours.Disguiseable;
import com.offz.spigot.custommobs.Mobs.CustomMob;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalLookAtPlayerPitchLock;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalMeleeAttackPitchLock;
import net.minecraft.server.v1_13_R2.*;

import javax.annotation.Nullable;

/**
 * Lots of code taken from EntityZombie
 */
public abstract class HostileMob extends EntityMonster implements CustomMob {
    protected MobBuilder builder;
    private Deathable deathable = new Deathable(this);
    private Disguiseable disguiseable = new Disguiseable(this);

    public HostileMob(World world, MobBuilder builder) {
        super(CustomType.getType(builder), world);
        this.builder = builder;
        CustomMobBase base = new CustomMobBase(this);
        base.apply();
        addScoreboardTag("hostileMob");
    }

    @Override
    public EntityLiving getEntity() {
        return this;
    }

    @Override
    protected void n() {
        createPathfinders();
    }

    @Override
    public void createPathfinders() {
        goalSelector.a(1, new PathfinderGoalFloat(this));
        goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        goalSelector.a(8, new PathfinderGoalLookAtPlayerPitchLock(this, EntityHuman.class, 8.0F));
        goalSelector.a(8, new PathfinderGoalRandomLookaround(this));

        goalSelector.a(2, new PathfinderGoalMeleeAttackPitchLock(this, 1.0D, false));
        goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));
        targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
    }

    /**
     * Removes if not in peaceful mode
     */
    public void tick() {
        super.tick();
        if (!world.isClientSide && world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            die();
        }
    }

    @Override
    public MobBuilder getBuilder() {
        return builder;
    }

    //TODO check what this does
    @Nullable
    @Override
    public Entity bO() {
        return bP().isEmpty() ? null : bP().get(0);
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

    @Override
    protected SoundEffect D() {
        return soundAmbient();
    }

    @Override
    protected SoundEffect d(DamageSource damagesource) {
        return soundHurt();
    }

    @Override
    protected SoundEffect cs() {
        return soundDeath();
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        a(soundStep(), 0.15F, 1.0F);
    }

    @Override
    public void die(DamageSource damagesource) {
        deathable.die(damagesource);
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

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        return new ChatMessage(getBuilder().getName());
    }
}
