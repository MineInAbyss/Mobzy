package com.offz.spigot.mobzy.Mobs.Types;

import com.offz.spigot.mobzy.Builders.MobBuilder;
import com.offz.spigot.mobzy.CustomType;
import com.offz.spigot.mobzy.Mobs.Behaviours.*;
import com.offz.spigot.mobzy.Mobs.CustomMob;
import com.offz.spigot.mobzy.Pathfinders.PathfinderGoalLookAtPlayerPitchLock;
import com.offz.spigot.mobzy.Pathfinders.PathfinderGoalMeleeAttackPitchLock;
import net.minecraft.server.v1_13_R2.*;

import javax.annotation.Nullable;

/**
 * Lots of code taken from EntityZombie
 */
public abstract class HostileMob extends EntityMonster implements CustomMob {
    protected MobBuilder builder;
    private Deathable deathable = new Deathable(this);
    private Disguiseable disguiseable = new Disguiseable(this);

    public HostileMob(World world, String name) {
        this(world, CustomType.getBuilder(name));
    }

    public HostileMob(World world, MobBuilder builder) {
        super(CustomType.getType(builder), world);
        this.builder = builder;
        CustomMobBase base = new CustomMobBase(this);
        moveController = new ControllerMove(this);
        base.apply();
        addScoreboardTag("hostileMob");
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        Integer toDrop = new ExpDroppable(this).getExpToDrop();
        return toDrop == null ? super.getExpValue(entityhuman) : toDrop;
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        InitAttributeable initAttributeable = new InitAttributeable(this);
        initAttributeable.setConfiguredAttributes();
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
        goalSelector.a(8, new PathfinderGoalLookAtPlayerPitchLock(this, EntityTypes.PLAYER, 8.0F));
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

    @Override
    public MobBuilder getStaticBuilder() {
        return CustomType.getBuilder(P().c().getSimpleName());
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
        saveMobNBT(nbttagcompound);
    }

    @Override
    public void saveMobNBT(NBTTagCompound nbttagcompound) {
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
        this.getBukkitEntity().getWorld().playSound(this.getLocation(), soundAmbient(), org.bukkit.SoundCategory.HOSTILE, 1, (float) (1 + Math.random() * 0.2));
        return null;
    }

    @Override
    protected SoundEffect d(DamageSource damagesource) {
        this.getBukkitEntity().getWorld().playSound(this.getLocation(), soundHurt(), org.bukkit.SoundCategory.HOSTILE, 1, (float) (1 + Math.random() * 0.2));
        return null;
    }

    @Override
    protected SoundEffect cs() {
        this.getBukkitEntity().getWorld().playSound(this.getLocation(), soundDeath(), org.bukkit.SoundCategory.HOSTILE, 1, (float) (1 + Math.random() * 0.2));
        return null;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.getBukkitEntity().getWorld().playSound(this.getLocation(), soundStep(), org.bukkit.SoundCategory.HOSTILE, 1, (float) (1 + Math.random() * 0.2));
    }

    @Override
    public void die(DamageSource damagesource) {
        deathable.die(damagesource);
    }

    @Override
    public void die() {
        super.die();
        disguiseable.undisguise();
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
