package me.chickenstyle.dimensions.mine.entities;

import com.mojang.authlib.GameProfile;
import me.chickenstyle.CustomMob;
import me.chickenstyle.Main;
import me.chickenstyle.utils.Utils;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class Miner extends EntityZombie implements CustomMob {

    public double prevX,prevY,prevZ;

    public Miner(World world) {
        super(EntityTypes.ZOMBIE, ((CraftWorld) world).getHandle());
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue((0.23000000417232513D*2));
        this.getAttributeInstance(GenericAttributes.ATTACK_KNOCKBACK).setValue(69);
        this.getAttributeInstance(GenericAttributes.ARMOR).setValue(0);
        //setSlot(EnumItemSlot.MAINHAND,new net.minecraft.server.v1_15_R1.ItemStack(Items.DIAMOND_PICKAXE));
        //setSlot(EnumItemSlot.OFFHAND,new net.minecraft.server.v1_15_R1.ItemStack(Items.DIAMOND_SWORD));
        updateName();

    }

    @Override
    public void spawn(Location loc) {
        this.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        this.world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
        prevX = loc.getX();
        prevY = loc.getY();
        prevZ = loc.getZ();
    }

    @Override
    public void updateName() {

    }

    @Override
    public HashMap<org.bukkit.inventory.ItemStack, Double> getDrop(int fortuneLevel) {
        return new HashMap<>();
    }

    @Override
    public double getSpawnChance() {
        return 0.5;
    }

    @Override
    protected boolean en() {
        return false;
    }
}
