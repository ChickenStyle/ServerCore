package me.chickenstyle.dimensions.moon.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import me.chickenstyle.CustomMob;
import me.chickenstyle.dimensions.IDimension;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import me.chickenstyle.Main;
import me.chickenstyle.dimensions.moon.world.populators.CavePopulator;
import me.chickenstyle.dimensions.moon.world.populators.GiantCavePopulator;
import me.chickenstyle.dimensions.moon.world.populators.HugeMeteor;
import me.chickenstyle.dimensions.moon.world.populators.MediumMeteor;
import me.chickenstyle.dimensions.moon.world.populators.SmallMeteor;



public class MoonChunkGenerator extends ChunkGenerator implements IDimension {

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
    	ArrayList<BlockPopulator> populator = new ArrayList<>();
    	populator.add(new SmallMeteor());
    	populator.add(new MediumMeteor());
    	populator.add(new HugeMeteor());
    	populator.add(new CavePopulator());
    	populator.add(new GiantCavePopulator(Main.getPlugin(Main.class)));
    	return populator;
       
    }
    
    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
    	SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        ChunkData chunk = createChunkData(world);
        generator.setScale(0.01D);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int currentHeight = (int) (generator.noise(chunkX*16+x, chunkZ*16+z, 0.5D, 0.5D)*10D+50D);
                
                double chance = random.nextDouble();
                if (chance > 0.3) {
                	chunk.setBlock(x, currentHeight , z, Material.STONE);
                } else if (chance <= 0.3 && chance > 0.02) {
                	chunk.setBlock(x, currentHeight , z, Material.ANDESITE);
                } else {

                    chunk.setBlock(x,currentHeight,z, random.nextBoolean() ? Material.ANDESITE_SLAB : Material.STONE_SLAB);

                }
                
                for (int i = currentHeight - 1; i > 0; i--) {
                    chunk.setBlock(x,i,z, random.nextBoolean() ? Material.ANDESITE : Material.STONE);
                }
                
                chunk.setBlock(x, 0, z, Material.BEDROCK);
            }
        }
        return chunk;
    }


    @Override
    public HashMap<ItemStack, Integer> getChestLoot() {
        return new HashMap<>();
    }

    @Override
    public String getDimensionType() {
        return "Moon";
    }

    @Override
    public HashMap<EntityType, CustomMob> getCustomMobs() {
        return new HashMap<>();
    }

    @Override
    public ChunkGenerator getChunkGenerator() {
        return this;
    }


    public static void loadSchematic(Location loc, Clipboard clipboard, boolean ignoreAir) {

    }
}
