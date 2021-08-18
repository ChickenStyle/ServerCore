package me.chickenstyle.dimensions.aether.world.populators;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import me.chickenstyle.utils.Utils;

public class TotemPopulator extends BlockPopulator{

	@Override
	public void populate(World world, Random random, Chunk chunk) {
		int X = random.nextInt(15);
        int Z = random.nextInt(15);
        int Y;
        for (Y = world.getMaxHeight()-1; chunk.getBlock(X, Y, Z).getType() == Material.AIR; Y--);
		
        if (chunk.getBlock(X, Y, Z).getType() == Material.GRASS_BLOCK) {
        	Location loc = chunk.getBlock(X, Y+1, Z).getLocation();
        	if (Utils.emptyArea(loc, 3,Material.GRASS_BLOCK)) {
        		chunk.getWorld().getBlockAt(loc).setType(Material.QUARTZ_PILLAR);
        		chunk.getWorld().getBlockAt(loc.clone().add(0,1,0)).setType(Material.QUARTZ_PILLAR);
        		chunk.getWorld().getBlockAt(loc.clone().add(0,2,0)).setType(Material.SEA_LANTERN);
        	}
        }
		
		
	}

}
