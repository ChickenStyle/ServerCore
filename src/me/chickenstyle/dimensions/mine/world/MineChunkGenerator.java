package me.chickenstyle.dimensions.mine.world;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import me.chickenstyle.CustomMob;
import me.chickenstyle.dimensions.IDimension;
import me.chickenstyle.dimensions.mine.entities.Miner;
import me.chickenstyle.dimensions.mine.world.populators.MineBuildingsPopulator;
import me.chickenstyle.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MineChunkGenerator extends ChunkGenerator implements IDimension {

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return new ArrayList<BlockPopulator>() {{
            add(new MineBuildingsPopulator());
        }};

    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        // Floor and ceiling generators
        SimplexOctaveGenerator floorGenerator = new SimplexOctaveGenerator(world.getSeed(),8);
        SimplexOctaveGenerator ceilingGenerator = new SimplexOctaveGenerator(world.getSeed()/2,8);

        //Cave Generator
        SimplexOctaveGenerator cheeseGenerator = new SimplexOctaveGenerator(world.getSeed()/4,8);

        cheeseGenerator.setScale(0.025D);


        ChunkData chunk = createChunkData(world);
        floorGenerator.setScale(0.05D);
        ceilingGenerator.setScale(0.2D);

        HashMap<Material,Integer> ores = new HashMap<Material,Integer>(){{
            put(Material.EMERALD_ORE,3);
            put(Material.DIAMOND_ORE,4);
            put(Material.IRON_ORE,10);
            put(Material.GOLD_ORE,8);
            put(Material.COAL_ORE,16);
            put(Material.LAPIS_ORE,16);
            put(Material.REDSTONE_ORE,16);
        }};


        Material[] keys = {Material.IRON_ORE,Material.IRON_ORE,
                            Material.GOLD_ORE,Material.GOLD_ORE,
                            Material.COAL_ORE,Material.COAL_ORE,
                            Material.LAPIS_ORE,Material.LAPIS_ORE,
                            Material.REDSTONE_ORE,Material.REDSTONE_ORE,
                            Material.DIAMOND_ORE,Material.EMERALD_ORE};


        HashMap<Material,Section> oresHeight = new HashMap<Material,Section>() {{
           // Spawns everywhere
           put(Material.COAL_ORE, new Section(1, world.getMaxHeight() - 2));
           put(Material.IRON_ORE, new Section(1, world.getMaxHeight() - 2));
           put(Material.GOLD_ORE, new Section(1, world.getMaxHeight() - 2));
           // Spawns in the lower part
           put(Material.DIAMOND_ORE, new Section(1, 30));
           put(Material.EMERALD_ORE, new Section(1,30));
           // Spawn in the upper part
           put(Material.REDSTONE_ORE, new Section(100, world.getMaxHeight() - 2));
           put(Material.LAPIS_ORE, new Section(100, world.getMaxHeight() - 2));
        }};

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {

                // Puts the Bedrock on top and the bottom

                chunk.setBlock(x,0,z,Material.BEDROCK);
                chunk.setBlock(x,world.getMaxHeight() - 1,z,Material.BEDROCK);


                //Spawns the floor
                double floorNoise = floorGenerator.noise(chunkX*16+x, chunkZ*16+z, 0.2D,0.3D);

                int floorHeight = (int)((floorNoise * 8) + 70);
                double cheeseNoise = cheeseGenerator.noise(chunkX*16 + x,chunkZ*16+z,0.4,0.4);
                int cheeseHeight = (int) (58 - (cheeseNoise * 3));


                for (int y = 1; y < floorHeight;y++) {
                    int realX = chunkX * 16 + x;
                    int realZ = chunkZ * 16 + z;


                    chunk.setBlock(x,y,z,Material.STONE);

                    if (y < cheeseHeight) {
                        if (cheeseGenerator.noise(realX, y, realZ,0.6,0.5) >= 0.8) {
                            chunk.setBlock(x,y,z,Material.AIR);
                        }
                    }

                    if (y == 1 && chunk.getType(x,y,z) == Material.AIR) {
                        chunk.setBlock(x,y,z,Material.LAVA);
                    }


                }



                // Spawns the ceiling
                double ceilingNoise = ceilingGenerator.noise(chunkX*16+x, chunkZ*16+z, 0.6D,1D);
                int ceilingHeight = (int) (140-(ceilingNoise*9));
                for (int y = world.getMaxHeight() - 2; y > ceilingHeight;y--) {
                    if (chunk.getType(x,y,z) == Material.AIR) {
                        chunk.setBlock(x,y,z,Material.STONE);
                    }
                }
                //Spawns the ores
                for (int i = 0; i < 20; i ++) {
                    int y = random.nextInt(world.getMaxHeight() - 2);
                    Material ore = keys[random.nextInt(keys.length)];
                    if (chunk.getType(x,y,z) == Material.STONE && oresHeight.get(ore).isInSection(y)) {
                        if (random.nextDouble() < 0.1) {
                            int loopX = x;
                            int loopZ = z;
                            for (int k = 0; k < ores.get(ore);k++) {
                                if (chunk.getType(loopX,y,loopZ) == Material.STONE) {
                                    chunk.setBlock(loopX,y,loopZ,ore);
                                }
                                switch (random.nextInt(6)) {  // The direction chooser
                                    case 0: loopX++; break;
                                    case 1: y++; break;
                                    case 2: loopZ++; break;
                                    case 3: loopX--; break;
                                    case 4: y--; break;
                                    case 5: loopZ--; break;
                                }
                            }

                        }
                    }

                }

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
        return "Mine";
    }

    @Override
    public HashMap<EntityType, CustomMob> getCustomMobs() {
        return new HashMap<EntityType, CustomMob>() {{
            //put(EntityType.ZOMBIE,new Miner(Bukkit.getWorld(getDimensionType())));
        }};
    }

    @Override
    public ChunkGenerator getChunkGenerator() {
        return this;
    }


    public static void loadSchematic(Location loc, Clipboard clipboard, boolean ignoreAir) {
        int diameter = Utils.loadSchematic(loc, clipboard, ignoreAir);
        Random rnd = new Random();
        for (int x = loc.getBlockX() - (diameter / 2); x <= loc.getBlockX() + (diameter / 2); x++) {
            for (int y = loc.getBlockY(); y <= loc.getBlockY() + (diameter - (diameter * 0.3)); y++) {
                for (int z = loc.getBlockZ() - (diameter / 2); z <= loc.getBlockZ() + (diameter / 2); z++) {
                    String matType = loc.getWorld().getBlockAt(x, y, z).getType().toString();
                    if (matType.contains("STONE_BRICK")) {
                        System.out.println("REPLACING!");
                        Material mat = loc.getWorld().getBlockAt(x, y, z).getType();
                        if (matType.contains("STAIRS")) {
                            mat = rnd.nextBoolean() ? mat : Material.valueOf("MOSSY_" + mat);
                        } else if (matType.contains("WALL")) {
                            mat = rnd.nextBoolean() ? mat : Material.valueOf("MOSSY_" + mat);
                        } else {
                            mat = rnd.nextBoolean() ? mat : Material.valueOf("MOSSY_" + mat);
                            mat = rnd.nextDouble() < 0.2 ? Material.valueOf("CRACKED_" + mat) : mat;
                        }
                        loc.getWorld().getBlockAt(x, y, z).setType(mat);

                    }
                }
            }
        }
    }


    private static class Section {

        private final int minY;
        private final int maxY;
        public Section(int minY,int maxY) {
            this.minY = minY;
            this.maxY = maxY;
        }

        public boolean isInSection(int y) {
            return y >= minY && y <= maxY;
        }
    }



}


