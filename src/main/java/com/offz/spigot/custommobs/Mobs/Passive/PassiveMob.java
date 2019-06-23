package com.offz.spigot.custommobs.Mobs.Passive;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.CustomMobsAPI;
import com.offz.spigot.custommobs.Loading.CustomType;
import com.offz.spigot.custommobs.Mobs.CustomMob;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalLookAtPlayerPitchLock;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R2.event.CraftEventFactory;

/**
 * Lots of code taken from EntityPig
 */
public abstract class PassiveMob extends EntityAnimal implements CustomMob {
    protected MobBuilder builder;

    public PassiveMob(World world, MobBuilder builder) {
        super(CustomType.getType(builder), world);
        this.builder = builder;

        createCustomMob(world, builder, this);
    }

    protected void n() {
        createPathfinders();
    }

    protected void createPathfinders() {
        goalSelector.a(1, new PathfinderGoalFloat(this));
        goalSelector.a(2, new PathfinderGoalPanic(this, 1.25D));
        goalSelector.a(3, new PathfinderGoalBreed(this, 1.0D));
        goalSelector.a(5, new PathfinderGoalFollowParent(this, 1.1D));
        goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 1.0D));
        goalSelector.a(7, new PathfinderGoalLookAtPlayerPitchLock(this, EntityHuman.class, 6.0F));
    }

    @Override
    public MobBuilder getBuilder() {
        return builder;
    }

    protected void initAttributes() {
        super.initAttributes();
        getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
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

    //TODO: Make a clean way of sharing methods like this between Hostile and PassiveMob
    public void die(DamageSource damagesource) {
        if (!killed) {
            CustomMobsAPI.debug(ChatColor.RED + builder.getName() + " died at coords " + (int) locX + " " + (int) locY + " " + (int) locZ);
            EntityLiving entityliving = cv();
            if (be >= 0 && entityliving != null)
                entityliving.a(this, be, damagesource);

            //TODO: This causes the entity to send a statistics update on death (we don't want this),
            // make sure it doesn't do anything else
            /*if (entity != null)
                entity.b(this);*/

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

    @Override
    public abstract EntityAgeable createChild(EntityAgeable entityageable);
}
