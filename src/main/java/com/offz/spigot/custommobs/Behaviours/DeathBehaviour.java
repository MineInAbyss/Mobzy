package com.offz.spigot.custommobs.Behaviours;

import com.offz.spigot.custommobs.Mobs.Behaviours.MobBehaviour;
import com.offz.spigot.custommobs.Mobs.Behaviours.MobDrops;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface DeathBehaviour extends MobBehaviour {
    Map<MobType, ArrayList<MobDrops>> itemDrops = new HashMap<>();
    void onDeath(EntityDeathEvent e);

    static ArrayList<ItemStack> getDroppedItemStacks(MobType type){
        return MobDrops.toItemStacks(itemDrops.get(type));
    }

    static ArrayList<MobDrops> getMobDrops(MobType type){
        return itemDrops.get(type);
    }

    static void setItemDrops(MobType type, ArrayList<MobDrops> drops){
        itemDrops.put(type, drops);
    }
}
