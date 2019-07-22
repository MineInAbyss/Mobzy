package com.offz.spigot.custommobs.Mobs.Hostile;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Mobs.Behaviours.HitBehaviour;
import com.offz.spigot.custommobs.Mobs.MobDrop;
import com.offz.spigot.custommobs.Mobs.Types.HostileMob;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalWalkingAnimation;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import net.minecraft.server.v1_13_R2.World;
import org.bukkit.Material;

public class Ottobas extends HostileMob implements HitBehaviour {
    static MobBuilder builder = new MobBuilder("Ottobas", 47)
            .setDisguiseAs(DisguiseType.ZOMBIE)
            .setDrops(new MobDrop(Material.PORKCHOP, 2, 4));

    public Ottobas(World world) {
        super(world, builder);
        this.setSize(2F, 3F);

    }

    @Override
    public void createPathfinders() {
        super.createPathfinders();
        this.goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, builder.getModelID()));
    }
}