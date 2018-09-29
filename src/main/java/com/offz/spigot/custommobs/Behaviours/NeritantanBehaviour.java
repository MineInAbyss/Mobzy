package com.offz.spigot.custommobs.Behaviours;

import com.offz.spigot.custommobs.MobType.MobType;
import com.offz.spigot.custommobs.Mobs.MobBehaviour;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class NeritantanBehaviour implements HitBehaviour, DeathBehaviour, MobBehaviour {
    MobType type;

    @Override
    public void onHit(EntityDamageByEntityEvent e) {
        if(e.getEntity().getCustomName() == type.getName())
            getHit((Player) e.getDamager(), e.getEntity());
    }

    @Override
    public void onDeath(EntityDeathEvent e){
        for(Entity passanger: e.getEntity().getPassengers())
            passanger.remove();
    }

    private void getHit(Player player, Entity e) {
        player.getWorld().spawnParticle(Particle.EXPLOSION_LARGE, e.getLocation(), 3);
        player.getWorld().playSound(e.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2f, .7f);
    }

    @Override
    public void setMobType(MobType type) {
        this.type = type;
    }
}
