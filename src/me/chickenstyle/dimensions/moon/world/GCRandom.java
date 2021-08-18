package me.chickenstyle.dimensions.moon.world;

import org.bukkit.Chunk;
import org.bukkit.util.noise.NoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

public class GCRandom {
    public Chunk chunk;

    // Note: Smaller frequencies yield slower change (more stretched out)
    //   Larger amplitudes yield greater influence on final void
    // Frequency
    private final double f1xz;
    private final double f1y;
    // Density
    private final int amplitude1 = 100;
    private final double subtractForLessThanCutoff;
    // Second pass - small noise
    private final double f2xz = 0.25;
    private final double f2y = 0.05;
    private final int amplitude2 = 2;
    // Third pass - vertical noise
    private final double f3xz = 0.025;
    private final double f3y = 0.005;
    private final int amplitude3 = 20;
    // Position
    private final int caveBandBuffer;

    // Noise
    private final NoiseGenerator noiseGen1;
    private final NoiseGenerator noiseGen2;
    private final NoiseGenerator noiseGen3;

    public GCRandom(Chunk chunk) {
        this.chunk = chunk;
        subtractForLessThanCutoff = amplitude1 - 62;
        f1xz = 1.0 / 200;
        f1y = 1.0 / 100;
       
        caveBandBuffer = 16;
        
        noiseGen1 = new SimplexNoiseGenerator(chunk.getWorld());
        noiseGen2 = new SimplexNoiseGenerator((long) noiseGen1.noise(chunk.getX(), chunk.getZ()));
        noiseGen3 = new SimplexNoiseGenerator((long) noiseGen1.noise(chunk.getX(), chunk.getZ()));
    }

    public boolean isInGiantCave(int x, int y, int z) {
        double xx = (chunk.getX() << 4) | (x & 0xF);
        double yy = y;
        double zz = (chunk.getZ() << 4) | (z & 0xF);

        double n1 = (noiseGen1.noise(xx * f1xz, yy * f1y, zz * f1xz) * amplitude1);
        double n2 = (noiseGen2.noise(xx * f2xz, yy * f2y, zz * f2xz) * amplitude2);
        double n3 = (noiseGen3.noise(xx * f3xz, yy * f3y, zz * f3xz) * amplitude3);
        double lc = linearCutoffCoefficient(y);

        boolean isInCave = n1 + n2 - n3 - lc > 62;
        return isInCave;
    }

    private double linearCutoffCoefficient(int y) {
        // Out of bounds
        if (y < 6 || y > 50) {
            return subtractForLessThanCutoff;
            // Bottom layer distortion
        } else if (y >= 6 && y <= 6 + caveBandBuffer) {
            double yy = y - 50;
            return (-subtractForLessThanCutoff / (double) caveBandBuffer) * yy + subtractForLessThanCutoff;
            // Top layer distortion
        } else if (y <= 50 && y >= 50 - caveBandBuffer) {
            double yy = y - 50 + caveBandBuffer;
            return (subtractForLessThanCutoff / (double) caveBandBuffer) * yy;
            // In bounds, no distortion
        } else {
            return 0;
        }
    }
}
