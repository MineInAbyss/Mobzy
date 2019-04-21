package com.offz.spigot.custommobs.Behaviours.Listener;

import com.offz.spigot.custommobs.CustomMobs;
import com.offz.spigot.custommobs.MobContext;
import com.offz.spigot.custommobs.Mobs.Type.MobType;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.TargetedDisguise;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class MobListener implements Listener {
    CustomMobs plugin;
    private MobContext context;

    public MobListener(MobContext context) {
        this.context = context;
        plugin = (CustomMobs) context.getPlugin();
    }

    @EventHandler
    public void onjoin(PlayerJoinEvent event) {
//        injectPlayer(event.getPlayer());

        disguiseSearch:
        //TODO maybe get server render distance and multiply 16 by it, since this essentially assumes it's 10
        for (Entity e : event.getPlayer().getNearbyEntities(160, 256, 160)) //try and load all nearby entities that would be in unloaded chunks
            if (MobType.getRegisteredMobType(e) != null/*DisguiseAPI.isDisguised(e)*/) { //TODO I think isDisguised returns false when this problem occurs
                for (Entity checkPlayer : event.getPlayer().getNearbyEntities(64, 128, 64)) //if player near entity, it's already loaded
                    if (e instanceof Player)
                        continue disguiseSearch;
                Bukkit.broadcastMessage("Can player see? " + ((TargetedDisguise) DisguiseAPI.getDisguise(e)).canSee(event.getPlayer()));
                if (!((TargetedDisguise) DisguiseAPI.getDisguise(e)).canSee(event.getPlayer())) {
                    DisguiseAPI.disguiseEntity(e, new MobDisguise(DisguiseType.ZOMBIE));
                    Bukkit.broadcastMessage("Redisguised");
                }
            }
    }


    //TODO remove later if we don't need it
    /*@EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        MobDisguise mobDisguise = new MobDisguise(DisguiseType.ZOMBIE);
        for(Entity entity: e.getChunk().getEntities()){
        }
        *//*Arrays.stream(e.getChunk().getEntities())
                .filter(entity -> entity instanceof CraftEntity)
                .map(entity -> ((CraftEntity) entity).getHandle())
                .filter(entity -> entity instanceof EntityLiving)
                .map(entity -> (EntityLiving) entity)
                .forEach(this::spawnNewEntity);*//*
    }*/

    /*public void spawnNewEntity(EntityLiving entity) {
        MobType type = MobType.getRegisteredMobType(entity.getScoreboardTags());
        if (type != null) {
            NBTTagCompound compound = new NBTTagCompound();
            entity.b(compound); //copy over nbt data
            try {
                EntityLiving newEntity = (EntityLiving) type.getEntityClass().getConstructor(net.minecraft.server.v1_13_R2.World.class).newInstance(entity.getWorld());
                newEntity.a(compound); //apply nbt data
                newEntity.setLocation(entity.locX, entity.locY, entity.locZ, entity.yaw, 0);
                entity.getWorld().addEntity(newEntity); //spawn new entity
                entity.die();
                Bukkit.broadcastMessage("Reloaded entity");
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }*/

    //TODO playing around with intercepting packets, doubt we'll need this, remove later
    /*@EventHandler
    public void onleave(PlayerQuitEvent event) {
        removePlayer(event.getPlayer());
    }

    private void removePlayer(Player player) {
        Channel channel = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            return null;
        });
    }*/

    /*private void injectPlayer(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {

            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                //Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "PACKET READ: " + ChatColor.RED + packet.toString());
                super.channelRead(channelHandlerContext, packet);
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
                //Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "PACKET WRITE: " + ChatColor.GREEN + packet.toString());
//                if(!(packet instanceof PacketPlayOutMapChunk || packet instanceof PacketPlayOutUnloadChunk || packet instanceof PacketPlayOutUpdateTime))
                if (packet instanceof PacketPlayOutSpawnEntityLiving) {
                    *//*PacketPlayOutSpawnEntityLiving packet1 = (PacketPlayOutSpawnEntityLiving) packet; //get packet
                    PacketDataSerializer data = new PacketDataSerializer(Unpooled.buffer()); //create a serializer to read data from packet
                    packet1.b(data); //apply packet's data to serializer
                    DataWatcher.b(data).toString();
                    for(Entity e: player.getNearbyEntities(10, 10 ,10))
                        Bukkit.broadcastMessage(e.getUniqueId().toString() + " vs Packet's " + data.i());
                    Entity entity = Bukkit.getEntity(data.i()); //get the Entity from UUID from data

                    if(entity != null && MobType.getRegisteredMobType(entity) != null) {
                        if (!DisguiseAPI.isDisguised(entity)) { //if not disguised
//                            Bukkit.broadcastMessage("Disguised entity");
//                            DisguiseAPI.disguiseEntity(entity, new MobDisguise(DisguiseType.ZOMBIE));
                        } else {
                            Bukkit.broadcastMessage("Redisguised entity");
//                            DisguiseAPI.getDisguise(entity).startDisguise();
                        }
                    }*//*

                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "PACKET SENT: " + ChatColor.GREEN + packet.toString());
                }
                super.write(channelHandlerContext, packet, channelPromise);
            }


        };

        ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().playerConnection.networkManager.channel.pipeline();
        pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);

    }*/

    /*@EventHandler
    public void onHit(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if(entity != null) {
            MobType type = MobType.getRegisteredMobType(entity);
            if (type != null) {
                if (type.getBehaviour() instanceof HitBehaviour) {
                    ((HitBehaviour) type.getBehaviour()).onHit(e);
                    EntityEquipment ee;
                    if(type.getBehaviour() instanceof SpawnModelBehaviour)
                        ee = ((ArmorStand) entity.getPassengers().get(0).getPassengers().get(0)).getEquipment();
                    else
                        ee = ((LivingEntity) entity).getEquipment();
                    ItemStack is = ee.getHelmet();
                    is.setDurability((short) (MobType.getRegisteredMobType(entity).getModelID() + 2));
                    ee.setHelmet(is);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!entity.isDead()) {
                                is.setDurability((short) (MobType.getRegisteredMobType(entity).getModelID() + 1));
                                ee.setHelmet(is);
                            }
                        }
                    }.runTaskLater(plugin, 5);

                }
            }
        }
    }*/

    /*@EventHandler
    public void onDeath(EntityDeathEvent e) {
        MobType type = MobType.getRegisteredMobType(e.getEntity());
        if (type != null) {
            Entity entity = e.getEntity();
            if (type.getBehaviour() instanceof AnimationBehaviour)
                ((AnimationBehaviour) type.getBehaviour()).onDeath(entity.getUniqueId());
            *//*if (type.getBehaviour() instanceof SpawnModelBehaviour) {
                entity.getPassengers().get(0).getPassengers().get(0).remove();
                entity.getPassengers().get(0).remove();
            }*//*
            if (type.getBehaviour() instanceof DeathBehaviour) {
//                e.setCancelled(true);
                ((DeathBehaviour) type.getBehaviour()).onDeath(e);
                Location loc = entity.getLocation();
                entity.getWorld().spawnParticle(Particle.CLOUD, loc.add(0, 0.75, 0), 10, 0.1, 0.25, 0.1, 0.025);

                e.getDrops().clear();
                ArrayList<ItemStack> drops = DeathBehaviour.getDroppedItemStacks(type);
                if (drops != null)
                    e.getDrops().addAll(drops);

                e.getEntity().remove();
            }
        }
    }*/

    /*@EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        MobType type = MobType.getRegisteredMobType(e.getEntity());
        if (type != null) {
            if (type.getBehaviour() instanceof SpawnBehaviour)
                ((SpawnBehaviour) type.getBehaviour()).onSpawn(e);
            if (type.getBehaviour() instanceof SpawnModelBehaviour) {*//*
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ArmorStand aec = (ArmorStand) e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.ARMOR_STAND);
                        aec.setGravity(false);
                        aec.setVisible(false);
                        aec.setArms(true);
                        aec.setSilent(true);
                        aec.setMarker(true);
                        aec.addScoreboardTag("customMob");

                        ArmorStand as = (ArmorStand) e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.ARMOR_STAND);
                        as.setGravity(false);
                        as.setVisible(false);
                        as.setArms(true);
                        as.setSilent(true);
                        as.setMarker(true);
                        as.addScoreboardTag("customMob");

                        as.setCustomNameVisible(false);
                        ItemStack is = new ItemStack(type.getMaterial());
                        is.setDurability(type.getModelID());

                        ItemMeta meta = is.getItemMeta();
                        meta.setUnbreakable(true);
                        is.setItemMeta(meta);

                        as.getEquipment().setHelmet(is);
                        aec.addPassenger(as);
                        e.getEntity().addPassenger(aec);
                    }
                }.runTaskLater(plugin, 1);*//*
            }
        }
    }*/

    /*@EventHandler
    public void onVeichleExit(EntityDismountEvent e) {
        Entity entity = e.getDismounted();
        if (entity.getScoreboardTags().contains("customMob")) {
            if (e.getDismounted().isDead()) { //if the entity despawns, ensure its additional parts are removed
                e.getEntity().remove();
                if (!e.getDismounted().getScoreboardTags().contains("additionalPart"))
                    Bukkit.broadcastMessage("Despawned " + MobType.getRegisteredMobType(e.getDismounted()).getName());
                return;
            }
            e.setCancelled(true);
        }
    }*/

    /*@EventHandler
    public void onTransform(EntityTransformEvent e){
        e.getEntity().remove();
        e.setCancelled(true);
    }*/

//    @EventHandler
//    public void playerJoin(PlayerAnimationEvent e){
//        e.getPlayer().playSound(e.getPlayer().getLocation(), "registerBehaviours", 1F, 5F);
//    }
}