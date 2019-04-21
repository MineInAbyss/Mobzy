package com.offz.spigot.custommobs.Mobs.Passive;

import com.offz.spigot.custommobs.Loading.CustomType;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalLookAtPlayerPitchLock;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Animals;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nullable;

public abstract class PassiveMob extends EntityAnimal {
    private static final DataWatcherObject<Boolean> bC;
    private static final DataWatcherObject<Integer> bD;

    static {
        bC = DataWatcher.a(PassiveMob.class, DataWatcherRegistry.i);
        bD = DataWatcher.a(PassiveMob.class, DataWatcherRegistry.b);
    }

    private boolean bG;
    private int bH;
    private int bI;

    //TODO eventually have a builder class for passing parameters here
    public PassiveMob(World world, String name, int modelID) {
        super(CustomType.getType(name), world);

        this.setSize(0.5F, 0.5F);

        this.addScoreboardTag("customMob");
        this.setCustomNameVisible(true);

        Animals asAnimal = ((Animals) this.getBukkitEntity());
        asAnimal.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false));

        this.addScoreboardTag(name);
        org.bukkit.inventory.ItemStack is = new org.bukkit.inventory.ItemStack(org.bukkit.Material.DIAMOND_SWORD);
        is.setDurability((short) modelID);

        ItemMeta meta = is.getItemMeta();
        meta.setUnbreakable(true);
        is.setItemMeta(meta);
        asAnimal.getEquipment().setHelmet(is);

        DisguiseAPI.disguiseEntity(this.getBukkitEntity(), new MobDisguise(DisguiseType.ZOMBIE/*getDisguiseType()*/));
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

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    @Nullable
    public Entity bO() {
        return this.bP().isEmpty() ? null : this.bP().get(0);
    }

    public boolean dh() {
        Entity entity = this.bO();
        if (!(entity instanceof EntityHuman)) {
            return false;
        } else {
            EntityHuman entityhuman = (EntityHuman) entity;
            return entityhuman.getItemInMainHand().getItem() == Items.CARROT_ON_A_STICK || entityhuman.getItemInOffHand().getItem() == Items.CARROT_ON_A_STICK;
        }
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (bD.equals(datawatcherobject) && this.world.isClientSide) {
            this.bG = true;
            this.bH = 0;
            this.bI = this.datawatcher.get(bD);
        }

        super.a(datawatcherobject);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        Bukkit.broadcastMessage("Neritantan saved");
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        Bukkit.broadcastMessage("Neritantan loaded");

        //we actually want to reload this even if it's already disguised to fix visibility for players
//        if (!DisguiseAPI.isDisguised(this.getBukkitEntity())) { //if not disguised
        Bukkit.broadcastMessage("Disguised entity");
        //TODO do we need to undisguise?
        DisguiseAPI.disguiseEntity(this.getBukkitEntity(), new MobDisguise(getDisguiseType()));
//        }
    }

    abstract DisguiseType getDisguiseType();

    protected SoundEffect D() {
        return soundAmbient();
    }

    //TODO have a builder for sounds?
    protected SoundEffect soundAmbient() {
        return SoundEffects.ENTITY_PIG_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return soundHurt();
    }

    protected SoundEffect soundHurt() {
        return SoundEffects.ENTITY_PIG_HURT;
    }

    protected SoundEffect cs() {
        return soundDeath();
    }

    protected SoundEffect soundDeath() {
        return SoundEffects.ENTITY_PIG_DEATH;
    }

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(soundStep(), 0.15F, 1.0F);
    }

    protected SoundEffect soundStep() {
        return SoundEffects.ENTITY_PIG_STEP;
    }


    public void die(DamageSource damagesource) {
        super.die(damagesource);
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.L;
    }

    public void a(float f, float f1, float f2) {
        Entity entity = this.bP().isEmpty() ? null : this.bP().get(0);
        if (this.isVehicle() && this.dh()) {
            this.yaw = entity.yaw;
            this.lastYaw = this.yaw;
            this.pitch = 0F;
            this.setYawPitch(this.yaw, this.pitch);
            this.aQ = this.yaw;
            this.aS = this.yaw;
            this.Q = 1.0F;
            this.aU = this.cK() * 0.1F;
            if (this.bG && this.bH++ > this.bI) {
                this.bG = false;
            }

            if (this.bT()) {
                float f3 = (float) this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue() * 0.225F;
                if (this.bG) {
                    f3 += f3 * 1.15F * MathHelper.sin((float) this.bH / (float) this.bI * 3.1415927F);
                }

                this.o(f3);
                super.a(0.0F, 0.0F, 1.0F);
            } else {
                this.motX = 0.0D;
                this.motY = 0.0D;
                this.motZ = 0.0D;
            }

            this.aI = this.aJ;
            double d0 = this.locX - this.lastX;
            double d1 = this.locZ - this.lastZ;
            float f4 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;
            if (f4 > 1.0F) {
                f4 = 1.0F;
            }

            this.aJ += (f4 - this.aJ) * 0.4F;
            this.aK += this.aJ;
        } else {
            this.Q = 0.5F;
            this.aU = 0.02F;
            super.a(f, f1, f2);
        }

    }

    public boolean dz() {
        if (this.bG) {
            return false;
        } else {
            this.bG = true;
            this.bH = 0;
            this.bI = this.getRandom().nextInt(841) + 140;
            this.getDataWatcher().set(bD, this.bI);
            return true;
        }
    }

    public abstract EntityAgeable createChild(EntityAgeable entityageable);
//        public boolean f(ItemStack itemstack) {
//        return bE.a(itemstack);
//    }
}
