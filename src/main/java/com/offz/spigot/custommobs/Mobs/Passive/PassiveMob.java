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
import org.bukkit.entity.Animals;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class PassiveMob extends EntityAnimal implements CustomMob {

    protected MobBuilder builder;
    private boolean bG;
    private int bH;
    private int bI;

    //TODO eventually have a builder class for passing parameters here
    public PassiveMob(World world, MobBuilder builder) {
        super(CustomType.getType(builder), world);
        this.builder = builder;

        this.setSize(0.5F, 0.5F);
        this.addScoreboardTag("customMob");
        this.setCustomNameVisible(true);

        Animals asAnimal = ((Animals) this.getBukkitEntity());
        asAnimal.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));
        asAnimal.setCustomName(builder.getName());

        this.addScoreboardTag(builder.getName());

        //create an item based on model ID in head slot
        ItemStack is = new ItemStack(builder.getModelMaterial());
        is.setDurability((short) builder.getModelID());
        ItemMeta meta = is.getItemMeta();
        meta.setUnbreakable(true);
        is.setItemMeta(meta);
        asAnimal.getEquipment().setHelmet(is);

//        asAnimal.setRemoveWhenFarAway(true);
        Disguise disguise = new MobDisguise(builder.getDisguiseAs(), builder.isAdult());
        DisguiseAPI.disguiseEntity(this.getBukkitEntity(), disguise);
    }

    protected void n() {
        createPathfinders();
    }

    protected void createPathfinders() {
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalPanic(this, 1.25D));
        this.goalSelector.a(3, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(5, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayerPitchLock(this, EntityHuman.class, 6.0F));
    }

    @Override
    public MobBuilder getBuilder() {
        return builder;
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);

        CraftEntity asEntity = this.getBukkitEntity();
        if (!DisguiseAPI.isDisguised(asEntity)) { //if not disguised
            DisguiseAPI.disguiseEntity(asEntity, new MobDisguise(builder.getDisguiseAs()));
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
        this.a(soundStep(), 0.15F, 1.0F);
    }

    //TODO: Make a clean way of sharing methods like this between Hostile and PassiveMob
    public void die(DamageSource damagesource) {
        if (!this.killed) {
            CustomMobsAPI.debug(ChatColor.RED + "Died at coords " + (int) this.locX + " " + (int) this.locY + " " + (int) this.locZ);
            EntityLiving entityliving = this.cv();
            if (this.be >= 0 && entityliving != null)
                entityliving.a(this, this.be, damagesource);

            //TODO: This causes the entity to send a statistics update on death (we don't want this),
            // make sure it doesn't do anything else
            /*if (entity != null)
                entity.b(this);*/

            this.killed = true;
            this.getCombatTracker().g();
            if (!this.world.isClientSide) {
                if (this.isDropExperience() && this.world.getGameRules().getBoolean("doMobLoot")) {
                    boolean flag = this.lastDamageByPlayerTime > 0;
                    this.a(flag, 0, damagesource);
                    CraftEventFactory.callEntityDeathEvent(this, builder.getDrops());
                } else {
                    CraftEventFactory.callEntityDeathEvent(this);
                }
            }

            this.world.broadcastEntityEffect(this, (byte) 3);
        }
    }

    @Override
    public abstract EntityAgeable createChild(EntityAgeable entityageable);
}
