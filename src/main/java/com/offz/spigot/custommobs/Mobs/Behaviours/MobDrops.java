package com.offz.spigot.custommobs.Mobs.Behaviours;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MobDrops {
    private ItemStack itemStack;
    private double percent;

    public MobDrops(ItemStack is, double p){
        //TODO: add some way of returning between x and y items on drop
        itemStack = is;
        percent = p;
    }

    public MobDrops(Material m, double p) {
        this(new ItemStack(m), p);
    }

    public MobDrops(Material m, int amount, double p) {
        this(new ItemStack(m, amount), p);
    }

    public MobDrops(Material m, String name, double p) {
        this(m, p);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + name);
        itemStack.setItemMeta(meta);
    }

    public MobDrops(Material m, int amount, String name, double p) {
        this(m, name, p);
        itemStack.setAmount(amount);
    }

    public MobDrops(Material m, String name, List<String> lore, double p) {
        this(m, name, p);
        itemStack.setLore(lore);
    }

    public MobDrops(Material m, int amount, String name, List<String> lore, double p) {
        this(m, amount, name, p);
        itemStack.setLore(lore);
    }

    public static ArrayList<ItemStack> toItemStacks(ArrayList<MobDrops> mobDrops){
        if(mobDrops == null)
            return null;
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        for(MobDrops md: mobDrops){
            if(md.percent < Math.random() * 100)
                itemStacks.add(md.itemStack);
        }
        return itemStacks;
    }
}
