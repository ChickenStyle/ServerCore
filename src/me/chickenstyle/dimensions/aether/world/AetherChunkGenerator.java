package me.chickenstyle.dimensions.aether.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import me.chickenstyle.CustomMob;
import me.chickenstyle.dimensions.aether.entities.AetherSkeleton;
import me.chickenstyle.dimensions.aether.entities.AetherZombie;
import me.chickenstyle.dimensions.IDimension;
import me.chickenstyle.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import me.chickenstyle.dimensions.aether.world.populators.AetherBuildingsPopulator;
import me.chickenstyle.dimensions.aether.world.populators.TotemPopulator;
import me.chickenstyle.dimensions.aether.world.populators.TreePopulator;

public class AetherChunkGenerator extends ChunkGenerator implements IDimension {

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
    	ArrayList<BlockPopulator> populator = new ArrayList<>();
    	populator.add(new TreePopulator());
    	populator.add(new AetherBuildingsPopulator());
    	populator.add(new TotemPopulator());
    	return populator;
       
    }
	 

	@Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
    	SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(world.getSeed()),8);
    	ChunkData chunk = createChunkData(world);
        generator.setScale(0.02D);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
            	double generatedNoise = generator.noise(chunkX*16+x, chunkZ*16+z, 0.6D,0.8D);
            	int currentHeight = (int) ((generatedNoise * 7D)+70D);

            	double minY = (currentHeight - ((generatedNoise*generatedNoise*0.8) *9D) + 2);
            	
            	if (currentHeight > 73) {
            		chunk.setBlock(x, currentHeight, z, Material.GRASS_BLOCK);
                	for (int i = currentHeight - 1; i > minY;i--) {

						Material[] ores = {Material.GOLD_ORE,Material.IRON_ORE,Material.GOLD_ORE,Material.IRON_ORE,Material.GOLD_BLOCK};

                		if (random.nextDouble() > 0.05) {
                			chunk.setBlock(x, i, z, random.nextBoolean() ? Material.STONE : Material.MOSSY_COBBLESTONE);
                		} else {
                			chunk.setBlock(x, i, z, ores[random.nextInt(ores.length)]);
                		}
                		
                		
                	}
            	}

				for (int i = 0; i < world.getMaxHeight();i++) {
					biome.setBiome(chunkX + x,i,chunkZ + z,Biome.FOREST);
				}
            	
            }
        }
        return chunk;
    }
	
	@Override
	public HashMap<ItemStack,Integer> getChestLoot(){
		HashMap<ItemStack,Integer> loot = new HashMap<>();
		Random rnd = new Random();
		loot.put(new ItemStack(Material.STRING,2 + rnd.nextInt(7)), 100);
		loot.put(new ItemStack(Material.STRING,1 + rnd.nextInt(10)), 100);
		loot.put(new ItemStack(Material.IRON_INGOT,4 + rnd.nextInt(13)), 80);
		loot.put(new ItemStack(Material.DIAMOND,1 + rnd.nextInt(6)), 50);
		return loot;
		
	}


	@Override
	public String getDimensionType() {
		return "Aether";
	}

	@Override
	public HashMap<EntityType, CustomMob> getCustomMobs() {
		return new HashMap<EntityType, CustomMob>(){{
			put(EntityType.ZOMBIE, new AetherZombie(Bukkit.getWorld(getDimensionType())));
			put(EntityType.SKELETON, new AetherSkeleton(Bukkit.getWorld(getDimensionType())));
		}};
	}

	@Override
	public ChunkGenerator getChunkGenerator() {
		return this;
	}


	public static void loadSchematic(Location loc, Clipboard clipboard, boolean ignoreAir) {
		int maxArea = Utils.loadSchematic(loc,clipboard,ignoreAir);
		Utils.replaceChests(new AetherChunkGenerator(),loc,maxArea);

	}

}
