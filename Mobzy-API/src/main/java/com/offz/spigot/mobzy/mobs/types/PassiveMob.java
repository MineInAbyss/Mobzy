package com.offz.spigot.mobzy.mobs.types;

import com.offz.spigot.mobzy.mobs.MobTemplate;
import com.offz.spigot.mobzy.CustomType;
import com.offz.spigot.mobzy.mobs.behaviours.*;
import com.offz.spigot.mobzy.mobs.CustomMob;
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalLookAtPlayerPitchLock;
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalLookWhereHeaded;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.entity.LivingEntity;

/**
 * Lots of code taken from EntityPig
 */
public abstract class PassiveMob extends EntityAnimal implements CustomMob {
    protected MobTemplate builder;
    private Deathable deathable = new Deathable(this);
    private Disguiseable disguiseable = new Disguiseable(this);

    public PassiveMob(World world, String name) {
        this(world, CustomType.getBuilder(name));
    }

    public PassiveMob(World world, MobTemplate builder) {
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
    public MobTemplate getBuilder() {
        return builder;
    }

    @Override
    public MobTemplate getStaticBuilder() {
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
        saveMobNBT(nbttagcompound);
    }

    @Override
    public void saveMobNBT(NBTTagCompound nbttagcompound) {
        //TODO make the mobs undisguise when unloaded, not just on death. It should ONLY happen when the chunk the mob is in gets unloaded,
        // and this method gets called randomly sometimes. Perhaps a ChunkUnloadEvent or something similar is a good way to do this
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
        this.getBukkitEntity().getWorld().playSound(this.getLocation(), soundAmbient(), org.bukkit.SoundCategory.NEUTRAL, 1, (float) (1 + Math.random() * 0.2));
        return null;
    }

    @Override
    protected SoundEffect d(DamageSource damagesource) {
        this.getBukkitEntity().getWorld().playSound(this.getLocation(), soundHurt(), org.bukkit.SoundCategory.NEUTRAL, 1, (float) (1 + Math.random() * 0.2));
        return null;
    }

    @Override
    protected SoundEffect cs() {
        this.getBukkitEntity().getWorld().playSound(this.getLocation(), soundDeath(), org.bukkit.SoundCategory.NEUTRAL, 1, (float) (1 + Math.random() * 0.2));
        return null;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.getBukkitEntity().getWorld().playSound(this.getLocation(), soundStep(), org.bukkit.SoundCategory.NEUTRAL, 1, (float) (1 + Math.random() * 0.2));
    }

    public void die(DamageSource damagesource) {
        deathable.die(damagesource);
    }

    @Override
    public void die() {
        super.die();
        disguiseable.undisguise();
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
