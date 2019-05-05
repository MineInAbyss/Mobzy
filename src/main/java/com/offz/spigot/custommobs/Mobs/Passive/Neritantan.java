package com.offz.spigot.custommobs.Mobs.Passive;

import com.offz.spigot.custommobs.Builders.MobBuilder;
import com.offz.spigot.custommobs.Loading.CustomType;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalTemptPitchLock;
import com.offz.spigot.custommobs.Pathfinders.PathfinderGoalWalkingAnimation;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;

import java.util.function.Function;

public class Neritantan extends PassiveMob {
    static MobBuilder builder = new MobBuilder("Neritantan", 2)
            .setTemptItems(new Item[]{Items.CARROT, Items.POTATO, Items.BEETROOT});

    public Neritantan(World world) {
        super(world, builder);
        Function<? super World, ? extends Entity> a = Neritantan::new;
        Bukkit.broadcastMessage(CustomType.getBuilder(this).getName() + "'s constructor has been called");
    }

    @Override
    protected void createPathfinders() {
        super.createPathfinders();
        this.goalSelector.a(0, new PathfinderGoalWalkingAnimation(this, builder.getModelID()));
        this.goalSelector.a(4, new PathfinderGoalTemptPitchLock(this, 1.2D, false, builder.getTemptItems()));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    @Override
    public MobBuilder getBuilder() {
        return builder;
    }

    @Override
    protected MinecraftKey getDefaultLootTable() {
        return new MinecraftKey("entities/zombie");
    }

    public PassiveMob createChild(EntityAgeable entityageable) {
        return new Neritantan(this.world);
    }
}
