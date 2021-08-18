package me.chickenstyle.dimensions.mine.entities;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.chickenstyle.Main;
import me.chickenstyle.utils.Utils;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

public class MinersHandle {

    private static String[] skin = getSkin("Friyo");

    protected static HashMap<Miner, EntityPlayer> npcs = new HashMap<>();

    private static BukkitTask entityMoveRunnable = new BukkitRunnable() {
        @Override
        public void run() {
            Set<Miner> keys = npcs.keySet();
            if (keys != null) {
                for (Miner miner:keys) {
                    if (miner.isAlive()) {

                        EntityPlayer npc = npcs.get(miner);

                        update(npc,miner);

                        double mobX = miner.locX();
                        double mobY = miner.locY();
                        double mobZ = miner.locZ();

                        double lastX = miner.lastX;
                        double lastY = miner.lastY;
                        double lastZ = miner.lastZ;

                        if (mobX != lastX || mobY != lastY || mobZ != lastZ) {
                            double getX = mobX - lastX;
                            double getY = mobY - lastY;
                            double getZ = mobZ - lastZ;

                            move(npc,getX,getY,getZ,miner.yaw, miner.pitch);

                            miner.lastX = mobX;
                            miner.lastY = mobY;
                            miner.lastZ = mobZ;

                        }

                    } else {

                    /*
                    for (Player player: Bukkit.getOnlinePlayers()) {
                        PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
                        connection.sendPacket(new PacketPlayOutCombatEvent(npcs.get(miner).getCombatTracker(), PacketPlayOutCombatEvent.EnumCombatEventType.ENTITY_DIED));
                        connection.sendPacket(new PacketPlayOutEntityStatus(npcs.get(miner),(byte)3));
                        connection.sendPacket(new PacketPlayOutAnimation(npcs.get(miner),(byte)0));
                    }*/
                        npcs.get(miner).damageEntity(DamageSource.GENERIC,npcs.get(miner).getMaxHealth());
                        removeNPCPacket(npcs.get(miner));
                        npcs.remove(miner);
                    }
                }
            }
        }
    }.runTaskTimer(Main.getInstance(),0,0);

    public static void createNPC(Miner miner) {
        miner.setSilent(true);
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = miner.getWorld().getWorld().getHandle();
        GameProfile profile = new GameProfile(UUID.randomUUID(), Utils.color("&7Miner " + (int) miner.getHealth() + "/" + (int) miner.getMaxHealth() + "❤"));

        PlayerInteractManager manager = new PlayerInteractManager(world);

        try {
            Field gField = PlayerInteractManager.class.getDeclaredField("gamemode");
            gField.setAccessible(true);
            gField.set(manager,EnumGamemode.SURVIVAL);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        EntityPlayer npc = new EntityPlayer(server,world,profile,manager);


        double x = miner.locX();
        double y = miner.locY();
        double z = miner.locZ();

        float yaw = miner.yaw;

        float pitch = miner.pitch;

        npc.setLocation(x,y,z,yaw,pitch);


        profile.getProperties().put("textures",new Property("textures",skin[0],skin[1]));


        addNPCPacket(npc,miner);

        //sendSetNPCSkinPacket(npc,"Friyo");

        npcs.put(miner,npc);



    }

    protected static void addNPCPacket(EntityPlayer npc,Miner miner) {
        for (Player player : Bukkit.getOnlinePlayers()) {

            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc,(byte)(npc.yaw * 256/360)));
            connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(),EnumItemSlot.MAINHAND,new net.minecraft.server.v1_15_R1.ItemStack(Items.DIAMOND_PICKAXE)));

            org.bukkit.inventory.ItemStack torch = new org.bukkit.inventory.ItemStack(Material.TORCH);
            connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(),EnumItemSlot.OFFHAND,CraftItemStack.asNMSCopy(torch)));




            String[] rarity = {"LEATHER","IRON","GOLDEN","DIAMOND"};
            Random rnd = new Random();
            org.bukkit.inventory.ItemStack helmet = new org.bukkit.inventory.ItemStack(Material.valueOf(rarity[rnd.nextInt(rarity.length)] + "_HELMET"));

            helmet = rnd.nextDouble() < 0.05 ? new org.bukkit.inventory.ItemStack(Material.ANVIL) : helmet;

            connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(),EnumItemSlot.HEAD,CraftItemStack.asNMSCopy(helmet)));



            org.bukkit.inventory.ItemStack boots = new org.bukkit.inventory.ItemStack(Material.valueOf(rarity[rnd.nextInt(rarity.length)] + "_BOOTS"));
            connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(),EnumItemSlot.FEET,CraftItemStack.asNMSCopy(boots)));


            connection.sendPacket(new PacketPlayOutEntityDestroy(miner.getId()));
        }

        new BukkitRunnable(){

            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                    connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,npc));
                }
            }
        }.runTaskLater(Main.getInstance(),5);

    }

    public static void removeNPCPacket(EntityPlayer npc) {
        for (Player player:Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
        }
    }

    public static void addNPCJoin(Player player) {
        for (EntityPlayer npc : npcs.values()) {
            PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,npc));
            connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc,(byte)(npc.yaw * 256/360)));
            connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(),EnumItemSlot.MAINHAND,new net.minecraft.server.v1_15_R1.ItemStack(Items.DIAMOND_PICKAXE)));

            org.bukkit.inventory.ItemStack torch = new org.bukkit.inventory.ItemStack(Material.TORCH);
            connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(),EnumItemSlot.OFFHAND,CraftItemStack.asNMSCopy(torch)));

            org.bukkit.inventory.ItemStack helmet = new org.bukkit.inventory.ItemStack(Material.GOLDEN_HELMET);
            connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(),EnumItemSlot.HEAD,CraftItemStack.asNMSCopy(helmet)));

            org.bukkit.inventory.ItemStack boots = new org.bukkit.inventory.ItemStack(Material.IRON_BOOTS);
            connection.sendPacket(new PacketPlayOutEntityEquipment(npc.getId(),EnumItemSlot.FEET,CraftItemStack.asNMSCopy(boots)));

            connection.sendPacket(new PacketPlayOutEntityDestroy(npcs.get(npc).getId()));
        }

        new BukkitRunnable(){

            @Override
            public void run() {
                for (EntityPlayer npc : npcs.values()) {
                    PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
                    connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,npc));
                }
            }
        }.runTaskLater(Main.getInstance(),5);
    }

    private static void update(EntityPlayer npc,Miner miner) {

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntityHeadRotation(npc,(byte)(miner.yaw *256/360)));
            connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(npc.getId(),(byte)(miner.yaw*256/360),(byte)(miner.pitch*256/360),true));

        }
    }

    private static void move(EntityPlayer npc,double x,double y,double z,float yaw,float pitch) {
        for (Player player: Bukkit.getOnlinePlayers()) {
            PlayerConnection connection = ((CraftPlayer)player).getHandle().playerConnection;
            connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(npc.getId(),(short)(x*4096),(short)(y*4096),(short)(z*4096),(byte)(yaw*256/360),(byte)(pitch*256/360),true));
        }
    }

    public static HashMap<Miner,EntityPlayer> getNPCS() {
        return npcs;
    }

    private static String[] getSkin(String name) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader = new InputStreamReader(url.openStream());
            String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid
                    + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());
            JsonObject property = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

            String texture = property.get("value").getAsString();
            String signature = property.get("signature").getAsString();
            return new String[] {texture,signature};
        } catch (Exception e) {
            return new String[] {"",""};
        }
    }

}
