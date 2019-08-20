package com.offz.spigot.mobzy.Mobs.Types;

import com.offz.spigot.mobzy.Builders.MobBuilder;
import com.offz.spigot.mobzy.CustomType;
import com.offz.spigot.mobzy.Mobs.Behaviours.*;
import com.offz.spigot.mobzy.Mobs.CustomMob;
import com.offz.spigot.mobzy.Pathfinders.PathfinderGoalLookAtPlayerPitchLock;
import com.offz.spigot.mobzy.Pathfinders.PathfinderGoalLookWhereHeaded;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.entity.LivingEntity;

/**
 * Lots of code taken from EntityPig
 */
public abstract class PassiveMob extends EntityAnimal implements CustomMob {
    protected MobBuilder builder;
    private Deathable deathable = new Deathable(this);
    private Disguiseable disguiseable = new Disguiseable(this);

    public PassiveMob(World world, String name) {
        this(world, CustomType.getBuilder(name));
    }

    public PassiveMob(World world, MobBuilder builder) {
        super(CustomType.getType(builder), world);
        this.builder = builder;

        CustomMobBase base = new CustomMobBase(this);
        base.apply();
        addScoreboardTag("passiveMob");
        //TODO this is a temporary fix to see if it affects performance
        ((LivingEntity) this.getBukkitEntity()).setRemoveWhenFarAway(true);
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        Integer toDrop = new ExpDroppable(this).getExpToDrop();
        return toDrop == null ? super.getExpValue(entityhuman) : toDrop;
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
        goalSelector.a(7, new PathfinderGoalLookAtPlayerPitchLock(this, EntityTypes.PLAYER, 6.0F));
        goalSelector.a(8, new PathfinderGoalLookWhereHeaded(this));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        InitAttributeable initAttributeable = new InitAttributeable(this);
        initAttributeable.setConfiguredAttributes();
    }

    @Override
    public MobBuilder getBuilder() {
        return builder;
    }

    @Override
    public MobBuilder getStaticBuilder() {
        return CustomType.getBuilder(P().c().getSimpleName());
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
        this.getBukkitEntity().getWorld().playSound(this.getLocation(), soundAmbient(), 1, 1);
        return null;
    }

    @Override
    protected SoundEffect d(DamageSource damagesource) {
        this.getBukkitEntity().getWorld().playSound(this.getLocation(), soundHurt(), 1, 1);
        return null;
    }

    @Override
    protected SoundEffect cs() {
        this.getBukkitEntity().getWorld().playSound(this.getLocation(), soundDeath(), 1, 1);
        return null;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.getBukkitEntity().getWorld().playSound(this.getLocation(), soundStep(), 1, 1);
    }

    public void die(DamageSource damagesource) {
        deathable.die(damagesource);
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return null;
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        return new ChatMessage(getBuilder().getName());
    }
}
