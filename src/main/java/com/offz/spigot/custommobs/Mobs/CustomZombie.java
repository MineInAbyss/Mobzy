package com.offz.spigot.custommobs.Mobs;

import net.minecraft.server.v1_13_R2.EntityZombie;
import net.minecraft.server.v1_13_R2.World;

public class CustomZombie extends EntityZombie implements CustomMob {

    //TODO rework aggressive mobs
    public CustomZombie(World world) {
        super(world);
        createCustomMob(this, new String[]{"MOB"});
    }

    /*@Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        ((Neritantan) this).a(nbttagcompound);
    }*/

    /*@Override
    public void setCustomName(IChatBaseComponent iCBC) {
        super.setCustomName(iCBC);
        if (firstSetName) {
            firstSetName = false;
            GroundMobType type = (GroundMobType) MobType.getRegisteredMobType(iCBC.getString());
            MobBehaviour behaviour = type.getBehaviour();
            Zombie asZombie = (Zombie) registerBehaviours(this, type, behaviour);

            asZombie.setBaby(type.isBaby());
            asZombie.getEquipment().clear();

            if (behaviour instanceof SpawnModelBehaviour) {
                asZombie.getEquipment().setHelmet(new org.bukkit.inventory.ItemStack(org.bukkit.Material.STONE_BUTTON)); //stop the mobs from burning (don't know of entity better way yet)
            } else {
                org.bukkit.inventory.ItemStack is = new ItemStack(org.bukkit.Material.DIAMOND_SWORD, 1, type.getModelID());
//                is.setDurability(type.getModelID()); //this might be redundant since we can just pass the damage in the constructor
                ItemMeta meta = is.getItemMeta();
                meta.setUnbreakable(true);
                is.setItemMeta(meta);

                asZombie.getEquipment().setHelmet(is);
            }

        }

    }*/

    //TODO: This does nothing; figure out different way of cancelling burning in daytime
//    @Override
//    public void k() {
//        super.k();
//        this.getBukkitEntity().setFireTicks(0);
//    }
}
