package me.chickenstyle;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public interface CustomMob {
	void spawn(Location loc);
	void updateName();
	HashMap<ItemStack,Double> getDrop(int fortuneLevel);
	double getSpawnChance();
	
}
