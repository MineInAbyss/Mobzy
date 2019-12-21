/*
package com.offz.spigot.abyssalcreatures.mobs.hostile;

import com.offz.spigot.mobzy.mobs.behaviours.HitBehaviour;
import com.offz.spigot.mobzy.mobs.types.HostileMob;
import com.offz.spigot.mobzy.pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_15_R1.World;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Tamaugachi extends HostileMob implements HitBehaviour {
    public Tamaugachi(World world) {
        super(world, "Tamaugachi");
        this.setSize(2F, 2.5F);

        EntityEquipment ee = ((LivingEntity) getBukkitEntity()).getEquipment();
        ItemStack is = new ItemStack(Material.STONE);
        ItemMeta itemMeta = is.getItemMeta();
        itemMeta.setUnbreakable(true);
        itemMeta.addEnchant(Enchantment.DEPTH_STRIDER, 40, true);
        is.setItemMeta(itemMeta);
        ee.setBoots(is);
    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, getStaticBuilder().getModelID()));
    }
}*/