package me.chickenstyle.dimensions.aether.entities;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import me.chickenstyle.CustomMob;
import me.chickenstyle.utils.Utils;
import net.minecraft.server.v1_15_R1.ChatComponentText;
import net.minecraft.server.v1_15_R1.EntityHuman;
import net.minecraft.server.v1_15_R1.EntityPigZombie;
import net.minecraft.server.v1_15_R1.EntityTurtle;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.EntityVillagerAbstract;
import net.minecraft.server.v1_15_R1.EntityZombie;
import net.minecraft.server.v1_15_R1.EnumItemSlot;
import net.minecraft.server.v1_15_R1.GenericAttributes;
import net.minecraft.server.v1_15_R1.Items;
import net.minecraft.server.v1_15_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_15_R1.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_15_R1.PathfinderGoalLeapAtTarget;
import net.minecraft.server.v1_15_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_15_R1.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_15_R1.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_15_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_15_R1.PathfinderGoalRandomStrollLand;
import net.minecraft.server.v1_15_R1.PathfinderGoalZombieAttack;

public class AetherZombie extends EntityZombie implements CustomMob{

	public AetherZombie(World world) {
		super(EntityTypes.ZOMBIE, ((CraftWorld) world).getHandle());
		
		this.setCustomNameVisible(true);
		getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(50);
		getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(10);
		getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(15);
		this.setHealth(50);
		this.setCustomName(new ChatComponentText(Utils.color("&fAether Zombie &c" + (int) getHealth() + "&f/&c" + (int) this.getMaxHealth() + " ❤")));

		ItemStack head = new ItemStack(Material.END_ROD);
		
		this.setSlot(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(head));
		this.setSlot(EnumItemSlot.MAINHAND, new net.minecraft.server.v1_15_R1.ItemStack(Items.DIAMOND_SWORD));
		

		
		
	}
	@Override
	protected void initPathfinder() {
		this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
		this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
		l();
	}
	@Override
	protected void l() {
		this.goalSelector.a(0,new PathfinderGoalFloat(this));
		this.goalSelector.a(1, new PathfinderGoalZombieAttack(this, 1.5D, true)); 
		this.goalSelector.a(6, new PathfinderGoalMoveThroughVillage(this, 1.0D, true, 4, this::ey)); 
		this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.1D));
		this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a(new Class[] { EntityPigZombie.class }));
		this.goalSelector.a(2, new PathfinderGoalLeapAtTarget(this, 0.4F));
		if (this.world.spigotConfig.zombieAggressiveTowardsVillager)
			this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false)); 
		
		this.targetSelector.a(5, new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.bw));
	}
	
	@Override
    public void spawn(Location loc) {
        this.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        this.world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);
    }
    
    @Override
    public void updateName() {
    	this.setCustomNameVisible(true);
    	
    	this.setCustomName(new ChatComponentText(Utils.color("&fAether Zombie &c" + (int) getHealth() + "&f/&c" + (int) this.getMaxHealth() + " ❤")));
    }

	
	@Override
	public HashMap<ItemStack, Double> getDrop(int fortuneLevel) {
		HashMap<ItemStack, Double> drop = new HashMap<>();
		
		return drop;
	}

	@Override
	public double getSpawnChance() {
		return 100;
	}

}
