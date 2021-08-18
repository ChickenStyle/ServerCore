package me.chickenstyle.dimensions;

import java.util.HashMap;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import me.chickenstyle.CustomMob;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;

public interface IDimension {
	HashMap<ItemStack,Integer> getChestLoot();
	String getDimensionType();
	HashMap<EntityType, CustomMob> getCustomMobs();
	ChunkGenerator getChunkGenerator();
}
