package com.offz.spigot.custommobs.Mobs.Flying;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.CustomMobsAPI;
import com.offz.spigot.custommobs.Loading.CustomType;
import com.offz.spigot.custommobs.Mobs.CustomMob;
import com.offz.spigot.custommobs.Pathfinders.Flying.PathfinderGoalDiveOnTargetAttack;
import com.offz.spigot.custommobs.Pathfinders.Flying.PathfinderGoalIdleFly;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.event.CraftEventFactory;

import javax.annotation.Nullable;

/**
 * Lots of code taken from the EntityGhast class for flying mobs
 */
public abstract class FlyingMob extends EntityFlying implements CustomMob, IMonster {
    //    private static final DataWatcherObject<Boolean> a = DataWatcher.a(EntityGhast.class, DataWatcherRegistry.i);
    protected MobBuilder builder;
    private int b = 1;

    public FlyingMob(World world, MobBuilder builder) {
        super(CustomType.getType(builder), world);

        this.setSize(4.0F, 4.0F);
        this.builder = builder;
        this.moveController = new ControllerGhast(this);
//        ((LivingEntity) this.getBukkitEntity()).setRemoveWhenFarAway(false);

        createCustomMob(world, builder, this);
    }

//    public void a(boolean flag) {
//        this.datawatcher.set(a, flag);
//    }

    public int getPower() {
        return this.b;
    }

    public void tick() {
        super.tick();
        if (!this.world.isClientSide && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.die();
        }

    }

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

//    protected void x_() {
//        super.x_();
//        this.datawatcher.register(a, false);
//    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.aq;
    }

    protected float cD() {
        return 10.0F;
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        return this.random.nextInt(20) == 0 && super.a(generatoraccess, flag) && generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    public int dg() {
        return 1;
    }

    public float getHeadHeight() {
        return 2.6F;
    }

    protected void n() {
        createPathfinders();
    }

    protected void createPathfinders() {
        this.goalSelector.a(5, new PathfinderGoalIdleFly(this));
        this.goalSelector.a(1, new PathfinderGoalDiveOnTargetAttack(this));
//        this.goalSelector.a(7, new PathfinderGoalGhastAttackTarget(this));
        this.targetSelector.a(1, new PathfinderGoalTargetNearestPlayer(this));
//        this.goalSelector.a(7, new PathfinderGoalLookAtPlayerPitchLock(this, EntityHuman.class, 20.0F, 1F));
    }

    @Override
    public MobBuilder getBuilder() {
        return builder;
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(100.0D);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);

        CraftEntity asEntity = getBukkitEntity();
        if (!DisguiseAPI.isDisguised(asEntity)) { //if not disguised
            Disguise disguise = new MobDisguise(builder.getDisguiseAs(), builder.isAdult());
            DisguiseAPI.disguiseEntity(asEntity, disguise);
            disguise.getWatcher().setInvisible(true);
        }
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
        if (!killed) {
            CustomMobsAPI.debug(ChatColor.RED + builder.getName() + " died at coords " + (int) locX + " " + (int) locY + " " + (int) locZ);
            EntityLiving entityliving = cv();
            if (be >= 0 && entityliving != null)
                entityliving.a(this, be, damagesource);

            killed = true;
            getCombatTracker().g();
            if (!world.isClientSide) {
                if (isDropExperience() && world.getGameRules().getBoolean("doMobLoot")) {
                    boolean flag = lastDamageByPlayerTime > 0;
                    a(flag, 0, damagesource);
                    CraftEventFactory.callEntityDeathEvent(this, builder.getDrops());
                } else {
                    CraftEventFactory.callEntityDeathEvent(this);
                }
            }

            world.broadcastEntityEffect(this, (byte) 3);
        }
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
