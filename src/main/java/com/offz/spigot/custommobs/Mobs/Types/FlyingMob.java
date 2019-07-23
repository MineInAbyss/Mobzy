package com.offz.spigot.custommobs.Mobs.Types;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.CustomType;
import com.offz.spigot.custommobs.Mobs.Behaviours.CustomMobBase;
import com.offz.spigot.custommobs.Mobs.Behaviours.Deathable;
import com.offz.spigot.custommobs.Mobs.Behaviours.Disguiseable;
import com.offz.spigot.custommobs.Mobs.CustomMob;
import com.offz.spigot.custommobs.Pathfinders.Flying.PathfinderGoalDiveOnTargetAttack;
import com.offz.spigot.custommobs.Pathfinders.Flying.PathfinderGoalIdleFly;
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
        moveController = new ControllerGhast(this);
        CustomMobBase base = new CustomMobBase(this);
        base.apply();
        addScoreboardTag("flying");
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
        goalSelector.a(5, new PathfinderGoalIdleFly(this));
        goalSelector.a(1, new PathfinderGoalDiveOnTargetAttack(this));
//        goalSelector.a(7, new PathfinderGoalGhastAttackTarget(this));
        targetSelector.a(1, new PathfinderGoalTargetNearestPlayer(this));
//        goalSelector.a(7, new PathfinderGoalLookAtPlayerPitchLock(this, EntityHuman.class, 20.0F, 1F));
    }

//    public void a(boolean flag) {
//        this.datawatcher.set(a, flag);
//    }

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

    /**
     * Makes fireballs hurt the entity a lot TODO don't think we'll need this
     */
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (damagesource.j() instanceof EntityLargeFireball && damagesource.getEntity() instanceof EntityHuman) {
            super.damageEntity(damagesource, 1000.0F);
            return true;
        } else {
            return super.damageEntity(damagesource, f);
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
    public IChatBaseComponent getScoreboardDisplayName() {
        return new ChatMessage(getBuilder().getName());
    }

    static class ControllerGhast extends ControllerMove {
        private final FlyingMob i;
        private int j;

        public ControllerGhast(FlyingMob entityghast) {
            super(entityghast);
            this.i = entityghast;
        }

        public void a() {
            if (this.h == Operation.MOVE_TO) {
                double d0 = this.b - this.i.locX;
                double d1 = this.c - this.i.locY;
                double d2 = this.d - this.i.locZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;
                if (this.j-- <= 0) {
                    this.j += this.i.getRandom().nextInt(5) + 2;
                    d3 = (double) MathHelper.sqrt(d3);
                    if (this.b(this.b, this.c, this.d, d3)) {
                        FlyingMob var10000 = this.i;
                        var10000.motX += d0 / d3 * 0.1D;
                        var10000 = this.i;
                        var10000.motY += d1 / d3 * 0.1D;
                        var10000 = this.i;
                        var10000.motZ += d2 / d3 * 0.1D;
                    } else {
                        this.h = Operation.WAIT;
                    }
                }
            }

        }

        private boolean b(double d0, double d1, double d2, double d3) {
            double d4 = (d0 - this.i.locX) / d3;
            double d5 = (d1 - this.i.locY) / d3;
            double d6 = (d2 - this.i.locZ) / d3;
            AxisAlignedBB axisalignedbb = this.i.getBoundingBox();

            for (int i = 1; (double) i < d3; ++i) {
                axisalignedbb = axisalignedbb.d(d4, d5, d6);
                if (!this.i.world.getCubes(this.i, axisalignedbb)) {
                    return false;
                }
            }

            return true;
        }
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
