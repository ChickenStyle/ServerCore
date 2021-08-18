package me.chickenstyle.dimensions.moon.world.populators;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import me.chickenstyle.utils.Utils;

public class HugeMeteor extends BlockPopulator{

	@Override
	public void populate(World world, Random random, Chunk chunk) {
		
		if (new Random().nextDouble() < 0.00024414062) {
			Material[] ores = {
					Material.OBSIDIAN,
					Material.OBSIDIAN,
					Material.OBSIDIAN,
					Material.OBSIDIAN,
					Material.OBSIDIAN,
					Material.BLACK_CONCRETE_POWDER,
					Material.BLACK_CONCRETE_POWDER,
					Material.BLACK_CONCRETE_POWDER,
					Material.BLACK_CONCRETE_POWDER,
					Material.BLACK_CONCRETE_POWDER,
					Material.IRON_ORE,
					Material.GOLD_ORE,
					Material.GOLD_ORE,
					Material.GOLD_ORE,
					Material.GOLD_ORE,
					Material.GOLD_ORE,
					Material.REDSTONE_ORE,
					Material.DIAMOND_ORE};

			Material[] air = {Material.AIR};
			
		    int x = random.nextInt(15);
		    int y = 0;
		    int z = random.nextInt(15);
		    for (int i = world.getMaxHeight() - 1;i > 0;i--) {
		    	if (chunk.getBlock(x, i, z).getType() == Material.AIR && chunk.getBlock(x, i - 1, z).getType() != Material.AIR) {
		    		y = i;
		    		break;
		    	}
		    }
		   
		    Location centerLoc = chunk.getBlock(x,y,z).getLocation();
		    Utils.generateMeteor(centerLoc, 9, air);
		    Utils.generateMeteor(centerLoc.subtract(0, 2, 0), 7, ores);
			return;
		}
	}
	
}
