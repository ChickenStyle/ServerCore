package me.chickenstyle.dimensions.moon.world.populators;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.plugin.Plugin;

import me.chickenstyle.dimensions.moon.world.BlockToucher;
import me.chickenstyle.dimensions.moon.world.GCRandom;

public class GiantCavePopulator extends BlockPopulator {

    private final BlockFace[] faces = { BlockFace.UP, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };


    // Material
    private final Material material;
    private final BlockToucher toucher;

    public GiantCavePopulator(Plugin plugin) {
        material = Material.AIR;
        toucher = new BlockToucher(plugin);
    }

    @Override
    public void populate(final World world, final Random random, final Chunk source) {
        GCRandom gcRandom = new GCRandom(source);
        
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 50; y > 6; y--) {
                    if (gcRandom.isInGiantCave(x, y, z)) {
                        Block block = source.getBlock(x, y, z);
                        Block blockUp1 = block.getRelative(BlockFace.UP);
                        Block blockUp2 = blockUp1.getRelative(BlockFace.UP);
                        Block blockUp3 = blockUp2.getRelative(BlockFace.UP);
                        if (isHoldingBackOcean(block) || isHoldingBackOcean(blockUp1)) {
                            continue;
                        } else if (isHoldingBackOcean(blockUp2) || isHoldingBackOcean(blockUp3)) {
                            // Support the ocean with stone to keep the bottom from falling out
                            if (block.getType().hasGravity()) { // sand or gravel
                                block.setType(Material.STONE, false);
                                blockUp1.setType(Material.STONE, false);
                            }
                        } else {
                            block.setType(material, false);
                            toucher.touch(block);

                        }
                    }
                }
            }
        }
    }

    private boolean isHoldingBackOcean(Block block) {
        return isSurfaceWater(block) || isNextToSurfaceWater(block);
    }

    private boolean isNextToSurfaceWater(Block block) {
        for (BlockFace face : faces) {
            Block adjacent = block.getRelative(face);
            // Don't look at neighboring chunks to prevent runaway chunk generation
            // Use block coordinates to compute chunk coordinates to prevent loading chunks
            if (block.getX() >> 4 == adjacent.getX() >> 4 && block.getZ() >> 4 == adjacent.getZ() >> 4) {
                if (isSurfaceWater(adjacent)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSurfaceWater(Block block) {
        // Walk the column of blocks above block looking sea level
        while (isWater(block)) {
            if (block.getY() >= block.getWorld().getSeaLevel() - 1) {
                return true;
            } else {
                block = block.getRelative(BlockFace.UP);
            }
        }
        return false;
    }

    private boolean isWater(Block block) {
        Material material = block.getType();
        return material == Material.WATER;
    }

}
