package com.offz.spigot.mobzy.GUI;

import com.derongan.minecraft.guiy.gui.*;
import com.offz.spigot.mobzy.Builders.MobBuilder;
import com.offz.spigot.mobzy.GUI.Layouts.MobConfigLayout;
import com.offz.spigot.mobzy.Mobzy;
import com.offz.spigot.mobzy.MobzyAPI;
import com.offz.spigot.mobzy.Spawning.SpawnRegistry;
import de.erethon.headlib.HeadLib;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MobzyGUI extends GuiHolder {
    private Player player;
    //    private Layout spawns;
    private Mobzy plugin;
    private List<Layout> history = new ArrayList<>();
    private ClickableElement back;
    private List<ClickableElement> mobConfigs = new ArrayList<>();
    private List<ClickableElement> spawnList = new ArrayList<>();
    private FileConfiguration config;

    public MobzyGUI(Player player, Mobzy plugin) {
        super(6, "Mobzy", plugin);
        this.plugin = plugin;
        this.player = player;

        //create back button
        Element cell = Cell.forItemStack(HeadLib.RED_X.toItemStack("Back"));
        back = new ClickableElement(cell);
        back.setClickAction(clickEvent -> backInHistory());

//        spawns = buildSpawns();
//        regionLayout = buildRegions();
        setElement(buildMobConfigLayout());
    }

    public void addBackButton(Layout layout) {
        history.add(layout);
        layout.addElement(8, 5, back);
    }

    public void backInHistory() {
        if (history.size() <= 1) {
            player.closeInventory();
            return;
        }
        setElement(history.get(history.size() - 2));
        history.remove(history.size() - 1);
    }

    private Layout buildMobConfigLayout() {
        Layout layout = new Layout();

        FillableElement grid = new FillableElement(4, 8);
        layout.addElement(0, 0, grid);

        player.sendMessage(plugin.getMobzyConfig().getMobCfgs().values() + "");
        Collection<FileConfiguration> values = plugin.getMobzyConfig().getSpawnCfgs().values();
        if (values.size() == 1)
            return buildRegions(values.iterator().next());
        else
            values.forEach(config -> {
                String name = "Unnamed configuration";
                Material icon = Material.BEDROCK;

                if (config.contains("config.name"))
                    name = config.getString("config.name");
                if (config.contains("config.icon"))
                    icon = Material.getMaterial(config.getString("config.icon"));

                Element cell = Cell.forMaterial(icon, name);
                ClickableElement mobConfig = new ClickableElement(cell);
                mobConfig.setClickAction(clickEvent -> setElement(buildRegions(config)));

                grid.addElement(mobConfig);
            });

        addBackButton(layout);
        return layout;
    }

    private Layout buildRegions(FileConfiguration config) {
        this.config = config;
        Layout layout = new Layout();

        FillableElement grid = new FillableElement(4, 8);
        layout.addElement(0, 0, grid);

        config.getMapList("regions").forEach(region -> {
            String regionName = (String) region.get("name");
            player.sendMessage(regionName);

            Element cell = Cell.forMaterial(Material.DIAMOND, regionName);
            ClickableElement mobConfig = new ClickableElement(cell);
            mobConfig.setClickAction(clickEvent -> setElement(buildSpawns((ArrayList<Map<String, Object>>) region.get("spawns"), (String) region.get("name"))));
            grid.addElement(mobConfig);
        });

        addBackButton(layout);
        return layout;
    }

    private Layout buildSpawns(ArrayList<Map<String, Object>> spawns, String regionName) {
        Layout layout = new Layout();

        FillableElement grid = new FillableElement(4, 8);
        layout.addElement(0, 0, grid);

        spawns.forEach(spawn -> {
            MobBuilder spawnBuilder;
            if (spawn.containsKey("reuse"))
                spawnBuilder = MobzyAPI.getBuilder(SpawnRegistry.getReusedBuilder((String) spawn.get("reuse")).getEntityType().c().getSimpleName());
            else {
                String mobName = (String) spawn.get("mob");
                player.sendMessage(mobName);
                spawnBuilder = MobzyAPI.getBuilder(mobName);
            }
            Element cell = Cell.forItemStack(spawnBuilder.getModelItemStack());
            ClickableElement mobConfig = new ClickableElement(cell);
            //open up the config layout with its menu options
            mobConfig.setClickAction(clickEvent -> setElement(new MobConfigLayout(this, spawn, regionName)));
            grid.addElement(mobConfig);
        });

        addBackButton(layout);
        return layout;
    }

    public void saveConfigValues(Map<String, Object> spawn, List<MobzyPropertyElement> mobProperties) {
        spawn.clear();
        mobProperties.forEach(property -> spawn.put(property.getKey(), property.getValue()));
        plugin.getMobzyConfig().saveSpawnCfg(config);
        player.sendMessage(ChatColor.GREEN + "Successfully saved mob's configuartion");
    }
}
