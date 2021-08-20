package me.chickenstyle.utils;

import com.google.common.collect.Multimap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.chickenstyle.Main;
import me.chickenstyle.dimensions.mine.entities.Miner;
import me.chickenstyle.dimensions.mine.entities.MinersHandle;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import io.netty.channel.Channel;

import java.lang.reflect.Field;
import java.util.*;

public class PacketReader {

    Channel channel;
    public static Map<UUID, Channel> channels = new HashMap<>();

    public void inject(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        channel = craftPlayer.getHandle().playerConnection.networkManager.channel;
        channels.put(player.getUniqueId(), channel);

        if (channel.pipeline().get("PacketInjector") != null)
            return;

        channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<PacketPlayInUseEntity>() {

            @Override
            protected void decode(ChannelHandlerContext channel, PacketPlayInUseEntity packet, List<Object> arg) throws Exception {
                arg.add(packet);
                readPacket(player, packet);
            }

        });
    }

    public void uninject(Player player) {
        channel = channels.get(player.getUniqueId());
        if (channel == null || channel.pipeline() == null) return;


        if (channel.pipeline().get("PacketInjector") != null)
            channel.pipeline().remove("PacketInjector");
    }

    public void readPacket(Player player, Packet<?> packet) {
        if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
            if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT"))
                return;
            if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT"))
                return;
            if (getValue(packet, "action").toString().equalsIgnoreCase("OFF_HAND"))
                return;
            if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT_AT"))
                return;

            int id = (int) getValue(packet, "a");

            if (getValue(packet, "action").toString().equalsIgnoreCase("ATTACK")) {
                for (EntityPlayer npc : MinersHandle.getNPCS().values()) {
                    if (npc.getId() == id) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
                            Miner miner = null;


                            for (Miner key:MinersHandle.getNPCS().keySet()) {
                                if (MinersHandle.getNPCS().get(key).equals(npc)) {
                                    miner = key;
                                }
                            }

                            if (miner != null) {

                                player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(100D);
                                player.saveData();
                                ((CraftPlayer) player).getHandle().attack((miner));

                                player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(4D);
                                player.saveData();

                                player.sendMessage(miner.getHealth() + "");

                                for (Player online:npc.getWorld().getWorld().getPlayers()) {
                                    PlayerConnection connection = ((CraftPlayer)online).getHandle().playerConnection;
                                    connection.sendPacket(new PacketPlayOutAnimation(npc,1));
                                }
                                Location loc = new Location(miner.getWorld().getWorld(),miner.locX(),miner.locY(),miner.locZ());




                                miner.getWorld().getWorld().playSound(loc, Sound.ENTITY_PLAYER_HURT,1,1);
                                miner.updateName();
                            }

                            //Bukkit.getPluginManager().callEvent(new RightClickNPC(player, npc));
                        }, 0);
                    }
                }
            }
        }

    }

    private Object getValue(Object instance, String name) {
        Object result = null;

        try {
            Field field = instance.getClass().getDeclaredField(name);

            field.setAccessible(true);

            result = field.get(instance);

            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
