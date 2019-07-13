package com.offz.spigot.custommobs.Mobs.Behaviours;

import com.offz.spigot.custommobs.Mobs.CustomMob;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;

public class Disguiseable extends MobBehaviour {
    public Disguiseable(CustomMob mob) {
        super(mob);
    }

    public void disguise() {
        CraftEntity asEntity = mob.getEntity().getBukkitEntity();

        if (!DisguiseAPI.isDisguised(asEntity)) { //if not disguised
            Disguise disguise = new MobDisguise(mob.getBuilder().getDisguiseAs(), mob.getBuilder().isAdult());
            DisguiseAPI.disguiseEntity(asEntity, disguise);
            disguise.getWatcher().setInvisible(true);
        }
    }
}
