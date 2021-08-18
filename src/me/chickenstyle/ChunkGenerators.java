package me.chickenstyle;

import me.chickenstyle.dimensions.IDimension;
import me.chickenstyle.dimensions.aether.world.AetherChunkGenerator;
import me.chickenstyle.dimensions.mine.world.MineChunkGenerator;
import me.chickenstyle.dimensions.moon.world.MoonChunkGenerator;
import org.bukkit.generator.ChunkGenerator;

public enum ChunkGenerators {

    AETHER(new AetherChunkGenerator()),
    MOON(new MoonChunkGenerator()),
    MINE(new MineChunkGenerator());


    private final IDimension dimension;


    ChunkGenerators(IDimension dimension) {
        this.dimension = dimension;
    }

    public String getName() {
        return dimension.getDimensionType();
    }

    public ChunkGenerator getChunkGenerator() {
        return dimension.getChunkGenerator();
    }

    public IDimension getDimension() {
        return dimension;
    }
}
