package com.offz.spigot.custommobs.Mobs;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Location;

public interface CustomMob {
    EntityLiving getEntity();

    MobBuilder getBuilder();

    default double getX() {
        return getEntity().locX;
    }

    default double getY() {
        return getEntity().locY;
    }

    default double getZ() {
        return getEntity().locZ;
    }

    default World getWorld() {
        return getEntity().getWorld();
    }

    default Location getLocation() {
        return new Location(getWorld().getWorld(), getX(), getY(), getZ());
    }

    void createPathfinders();

    default SoundEffect soundAmbient() {
        return SoundEffects.ENTITY_PIG_AMBIENT;
    }

    default SoundEffect soundHurt() {
        return SoundEffects.ENTITY_PIG_HURT;
    }

    default SoundEffect soundDeath() {
        return SoundEffects.ENTITY_PIG_DEATH;
    }

    default SoundEffect soundStep() {
        return SoundEffects.ENTITY_PIG_STEP;
    }

    boolean getKilled();

    void setKilled(boolean killed);

    boolean dropsExperience();

    int lastDamageByPlayerTime();

    void unloadMobNBT(NBTTagCompound nbttagcompound);

    void loadMobNBT(NBTTagCompound nbttagcompound);

    /**
     * I suspect EntityLiving's "be" to be this, as Entity#die(DamageSource) calls Entity#a(Entity, int, DamageSource),
     * which gets overriden by EntityPlayer to call EntityHuman#addScore(int) with the passed int.
     *
     * @return the score with which a player should be rewarded with when the current entity is killed
     */
    int getKillScore();

    /**
     * TODO Not 100% confident about this one, but the way Entity#die(DamageSource) uses it, makes sense for cv() to return the killer
     *
     * @return the killer of the current entity if it has one.
     */
    default EntityLiving getKiller() {
        return getEntity().cv();
    }

    //TODO I'm not sure this is what EntityInsentient#a(boolean, int, DamageSource) actually does, change this method's name if I'm wrong
    void dropEquipment(boolean flag, int i, DamageSource damageSource);

    default void onRightClick(EntityHuman player) {
    }

    //TODO This might be useful for multi entity mobs later
    /*default Model spawnModel(World world, MobBuilder builder, EntityLiving entity) {
        if (!(entity instanceof SecondaryModelBehaviour))
            return null;

        Model model = new Model(world);
        model.setPosition(entity.locX, entity.locY, entity.locZ);

        //create an item based on model ID in head slot
        org.bukkit.inventory.ItemStack is = new ItemStack(builder.getModelMaterial());
        is.setDurability((short) builder.getModelID());
        ItemMeta meta = is.getItemMeta();
        meta.setUnbreakable(true);
        is.setItemMeta(meta);
        ((LivingEntity) model.getBukkitEntity()).getEquipment().setHelmet(is);
        model.setInvisible(true);
        model.setMarker(true);
        model.setSilent(true);
        model.addScoreboardTag("additionalPart");

//        EntityArmorStand empty = new EntityArmorStand(world);
//        empty.setPosition(entity.locX, entity.locY, entity.locZ);
//        empty.addScoreboardTag("additionalPart");

        entity.passengers.add(model);
//        empty.passengers.add(model);

//        world.addEntity(empty);
        world.addEntity(model);
        return model;
    }*/
}