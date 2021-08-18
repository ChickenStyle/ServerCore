package me.chickenstyle.dimensions.aether.world.populators;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;

import me.chickenstyle.Main;
import me.chickenstyle.utils.Utils;
import me.chickenstyle.dimensions.aether.world.AetherChunkGenerator;

public class AetherBuildingsPopulator extends BlockPopulator{
	
	@Override
	public void populate(World world, Random random, Chunk chunk) {
		
		int tries = 5;
		for (int i = 0;i < tries;i++) {
	        int X = random.nextInt(15);
	        int Z = random.nextInt(15);
	        int Y;
	        for (Y = world.getMaxHeight()-1; chunk.getBlock(X, Y, Z).getType() == Material.AIR; Y--);
			
	        if (chunk.getBlock(X, Y, Z).getType() == Material.GRASS_BLOCK) {
				File directoryPath = new File(Main.getInstance().getDataFolder() + "/Schematics/Aether/");
				File[] schematics = directoryPath.listFiles();
				File schem = schematics[new Random().nextInt(schematics.length)];
				
				
	        	Clipboard clipboard = null;
	        	ClipboardFormat format = ClipboardFormats.findByFile(schem);
	        	ClipboardReader reader;
	        	
	    		try {
	    			reader = format.getReader(new FileInputStream(schem));
	    	    	clipboard = reader.read();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
				
				Location loc = chunk.getBlock(X, Y+1, Z).getLocation();
	    		int area = Math.max(clipboard.getRegion().getLength(), clipboard.getRegion().getWidth()); 
	        	area++;
	        	if (Utils.emptyArea(loc, area,Material.GRASS_BLOCK)) {
					if (random.nextDouble() < 0.15) {

						AetherChunkGenerator.loadSchematic(loc,clipboard,false);
						break;
					}
	        	}
	        }
		}
	}
}
