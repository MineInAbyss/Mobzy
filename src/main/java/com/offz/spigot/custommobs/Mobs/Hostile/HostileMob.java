package com.offz.spigot.custommobs.Mobs.Hostile;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.CustomMobsAPI;
import com.offz.spigot.custommobs.Loading.CustomType;
import com.offz.spigot.custommobs.Mobs.CustomMob;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalLookAtPlayerPitchLock;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalMeleeAttackPitchLock;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.event.CraftEventFactory;

import javax.annotation.Nullable;

/**
 * Lots of code taken from EntityZombie
 */
public abstract class HostileMob extends EntityMonster implements CustomMob {
    protected MobBuilder builder;

    public HostileMob(World world, MobBuilder builder) {
        super(CustomType.getType(builder), world);
        this.builder = builder;

        createCustomMob(world, builder, this);
    }

    protected void n() {
        createPathfinders();
    }

    protected void createPathfinders() {
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayerPitchLock(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));

        this.goalSelector.a(2, new PathfinderGoalMeleeAttackPitchLock(this, 1.0D, false));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
    }

    @Override
    public MobBuilder getBuilder() {
        return builder;
    }

    protected void initAttributes() {
        super.initAttributes();
        getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
        getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(35.0D);
    }

    @Nullable
    public Entity bO() {
        return bP().isEmpty() ? null : bP().get(0);
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
        CustomMobsAPI.debug(ChatColor.RED + builder.getName() + " died at coords " + (int) locX + " " + (int) locY + " " + (int) locZ);
        if (!killed) {
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
}
