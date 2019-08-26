package com.offz.spigot.mobzy.Mobs.Types;

import com.offz.spigot.mobzy.Builders.MobBuilder;
import com.offz.spigot.mobzy.CustomType;
import com.offz.spigot.mobzy.Mobs.Behaviours.*;
import com.offz.spigot.mobzy.Mobs.CustomMob;
import com.offz.spigot.mobzy.Pathfinders.Controllers.MZControllerMoveFlying;
import com.offz.spigot.mobzy.Pathfinders.Flying.PathfinderGoalFlyDamageTarget;
import com.offz.spigot.mobzy.Pathfinders.Flying.PathfinderGoalIdleFly;
import net.minecraft.server.v1_13_R2.*;

/**
 * Lots of code taken from the EntityGhast class for flying mobs
 */
public abstract class FlyingMob extends EntityFlying implements CustomMob, IMonster {
    //    private static final DataWatcherObject<Boolean> a = DataWatcher.a(EntityGhast.class, DataWatcherRegistry.i);
    protected MobBuilder builder;
    private int power = 1;
    private Deathable deathable = new Deathable(this);
    private Disguiseable disguiseable = new Disguiseable(this);

    public FlyingMob(World world, String name) {
        this(world, CustomType.getBuilder(name));
    }

    public FlyingMob(World world, MobBuilder builder) {
        super(CustomType.getType(builder), world);
        setSize(4.0F, 4.0F);
        this.builder = builder;
        moveController = new MZControllerMoveFlying(this);
        CustomMobBase base = new CustomMobBase(this);
        base.apply();
        addScoreboardTag("flying");
    }

    @Override
    protected boolean isDropExperience() {
        return true;
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        Integer toDrop = new ExpDroppable(this).getExpToDrop();
        return toDrop == null ? 1 + this.world.random.nextInt(3) : toDrop;
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
        goalSelector.a(1, new PathfinderGoalFlyDamageTarget(this));
        goalSelector.a(5, new PathfinderGoalIdleFly(this));
        targetSelector.a(1, new PathfinderGoalTargetNearestPlayer(this));
    }

//    public void a(boolean flag) {
//        this.datawatcher.set(a, flag);
//    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        InitAttributeable initAttributeable = new InitAttributeable(this);
        initAttributeable.setConfiguredAttributes();
    }

    //TODO Only used by ghast's pathfinder, remove once we change it
    public int getPower() {
        return power;
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

    //TODO don't know what this does, seems to be data registry with NMS of a variable?
//    protected void x_() {
//        super.x_();
//        this.datawatcher.register(a, false);
//    }

    //TODO no idea what this is, but seems to be a larger number with bigger mobs
    protected float cD() {
        return 10.0F;
    }

    //TODO aaaaaaaaaaaaaaaaaaaaa (don't know what it does)
    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        return this.random.nextInt(20) == 0 && super.a(generatoraccess, flag) && generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    //TODO no idea what dg() is or whether it's important
    public int dg() {
        return 1;
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

    public void die(DamageSource damagesource) {
        deathable.die(damagesource);
    }

    @Override
    public void die() {
        super.die();
        disguiseable.undisguise();
    }

    @Override
    public IChatBaseComponent getScoreboardDisplayName() {
        return new ChatMessage(getBuilder().getName());
    }

    //The Ghast's pathfinder, eventually this will be our own custom one!
    static class PathfinderGoalGhastAttackTarget extends PathfinderGoal {
        private final FlyingMob ghast;
        public int a;

        public PathfinderGoalGhastAttackTarget(FlyingMob entityghast) {
            this.ghast = entityghast;
        }

        public boolean a() {
            return this.ghast.getGoalTarget() != null;
        }

        public void c() {
            this.a = 0;
        }

        public void d() {
//            this.ghast.a(false);
        }

        public void e() {
            EntityLiving entityliving = this.ghast.getGoalTarget();
//            double d0 = 64.0D;
            if (entityliving.h(this.ghast) < 4096.0D && this.ghast.hasLineOfSight(entityliving)) {
                World world = this.ghast.world;
                ++this.a;
                if (this.a == 10) {
                    world.a(null, 1015, new BlockPosition(this.ghast), 0);
                }

                if (this.a == 20) {
//                    double d1 = 4.0D;
                    Vec3D vec3d = this.ghast.f(1.0F);
                    double d2 = entityliving.locX - (this.ghast.locX + vec3d.x * 4.0D);
                    double d3 = entityliving.getBoundingBox().minY + (double) (entityliving.length / 2.0F) - (0.5D + this.ghast.locY + (double) (this.ghast.length / 2.0F));
                    double d4 = entityliving.locZ - (this.ghast.locZ + vec3d.z * 4.0D);
                    world.a(null, 1016, new BlockPosition(this.ghast), 0);
                    EntityLargeFireball entitylargefireball = new EntityLargeFireball(world, this.ghast, d2, d3, d4);
                    entitylargefireball.bukkitYield = (float) (entitylargefireball.yield = this.ghast.getPower());
                    entitylargefireball.locX = this.ghast.locX + vec3d.x * 4.0D;
                    entitylargefireball.locY = this.ghast.locY + (double) (this.ghast.length / 2.0F) + 0.5D;
                    entitylargefireball.locZ = this.ghast.locZ + vec3d.z * 4.0D;
                    world.addEntity(entitylargefireball);
                    this.a = -40;
                }
            } else if (this.a > 0) {
                --this.a;
            }

//            this.ghast.a(this.a > 10);
        }
    }
}
