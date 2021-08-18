package me.chickenstyle.dimensions.aether.entities;

import me.chickenstyle.CustomMob;
import me.chickenstyle.customPathFinders.PathFinderLaserBeam;
import me.chickenstyle.utils.Utils;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Random;

public class AetherSkeleton extends EntitySkeleton implements CustomMob,IRangedEntity {
	private static final Random rnd = new Random();
	public AetherSkeleton(World world) {
		super(EntityTypes.SKELETON, ((CraftWorld) world).getHandle());
		
		this.setCustomNameVisible(true);
		getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(70);
		
		setHealth(70);
		
		this.setCustomName(new ChatComponentText(Utils.color("&fAether Skeleton &c" + (int) getHealth() + "&f/&c" + (int) this.getMaxHealth() + " ❤")));
		
		
		ItemStack head = new ItemStack(Material.END_ROD);
		
		this.setSlot(EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(head));
		this.setSlot(EnumItemSlot.MAINHAND, new net.minecraft.server.v1_15_R1.ItemStack(Items.BOW));
		
	}
	
	@Override
	protected void initPathfinder() {
		this.goalSelector.a(0,new PathfinderGoalFloat(this));
		this.goalSelector.a(2, new PathfinderGoalRestrictSun(this));
		this.goalSelector.a(3, new PathfinderGoalFleeSun(this, 1.0D));
	    this.goalSelector.a(3, new PathfinderGoalAvoidTarget<>(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
	    this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
	    this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
	    this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
	    this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, new Class[0]));
	    this.targetSelector.a(4, new PathFinderLaserBeam(this, 20*5));
	    
	}
	

	@Override
    public void spawn(Location loc) {
        this.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        this.world.addEntity(this, CreatureSpawnEvent.SpawnReason.CUSTOM);

    }

    @Override
    public void updateName() {
    	this.setCustomNameVisible(true);
    	
    	
    	this.setCustomName(new ChatComponentText(Utils.color("&fAether Skeleton &c" + (int) getHealth() + "&f/&c" + (int) this.getMaxHealth() + " ❤")));
    }
	@Override
	public HashMap<ItemStack, Double> getDrop(int fortuneLevel) {
		HashMap<ItemStack, Double> drop = new HashMap<>();
		
		drop.put(new ItemStack(Material.GOLD_INGOT,rnd.nextInt(2) + 1), 0.8 + (fortuneLevel) * 0.5);
		drop.put(new ItemStack(Material.GOLD_NUGGET,rnd.nextInt(9) + 2), 0.8 +  (fortuneLevel) * 0.5);
		
		return drop;
	}

	@Override
	public double getSpawnChance() {
		return 100;
	}

}

