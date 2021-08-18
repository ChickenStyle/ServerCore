package me.chickenstyle.dimensions.mine.world.populators;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import me.chickenstyle.Main;
import me.chickenstyle.dimensions.IDimension;
import me.chickenstyle.dimensions.mine.world.MineChunkGenerator;
import me.chickenstyle.utils.Utils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

public class MineBuildingsPopulator extends BlockPopulator {


    @Override
    public void populate(World world, Random random, Chunk chunk) {
        File directoryPath = new File(Main.getInstance().getDataFolder() + "/Schematics/Mine/");
        File[] schematics = directoryPath.listFiles();
        File schem = schematics[random.nextInt(schematics.length)];
        if (random.nextInt(100) < MineBuildings.valueOf(schem.getName().replace(".schem","")).getChance()) {
            int X = random.nextInt(15);
            int Z = random.nextInt(15);
            int Y = 85;

            while (chunk.getBlock(X,Y,Z).getType() == Material.AIR) {Y--;}

            Clipboard clipboard = null;
            ClipboardFormat format = ClipboardFormats.findByFile(schem);
            ClipboardReader reader;

            try {
                reader = format.getReader(new FileInputStream(schem));
                clipboard = reader.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Location loc = chunk.getBlock(X, Y + 1, Z).getLocation();
            int area = Math.max(clipboard.getRegion().getLength(), clipboard.getRegion().getWidth());


            if (Utils.emptyArea(loc, area,Material.STONE)) {
                System.out.println(loc);
                MineChunkGenerator.loadSchematic(loc,clipboard,true);
            }


        }


    }
    private enum MineBuildings {

        Torch_Stand(5);

        private int chance;
        MineBuildings(int chance) {
            this.chance = chance;
        }

        public int getChance() {
            return chance;
        }

        public void setChance(int chance) {
            this.chance = chance;
        }
    }

}


