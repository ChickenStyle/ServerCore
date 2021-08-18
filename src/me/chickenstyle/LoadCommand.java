package me.chickenstyle;

import me.chickenstyle.dimensions.mine.entities.Miner;
import me.chickenstyle.dimensions.mine.entities.MinersHandle;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.*;

import java.io.File;

public class LoadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location loc = player.getLocation();

            Miner miner = new Miner(loc.getWorld());
            miner.spawn(loc);

            MinersHandle.createNPC(miner);


            Zombie zombie = (Zombie) loc.getWorld().spawnEntity(loc, EntityType.ZOMBIE);

            player.sendMessage(zombie.getHealth() + "");

            ((CraftPlayer)player).getHandle().attack(((CraftEntity)zombie).getHandle());

            player.sendMessage(zombie.getHealth() + "");
            /*
            for (int x = loc.getBlockX() - 20; x < loc.getBlockX() + 20;x++) {
                for (int y = loc.getBlockY() - 20; y < loc.getBlockY() + 20;y++) {
                    for (int z = loc.getBlockZ() - 20; z < loc.getBlockZ() + 20;z++) {
                        if (loc.getWorld().getBlockAt(x,y,z).getType() == Material.STONE) {
                            loc.getWorld().getBlockAt(x,y,z).setType(Material.AIR);
                        }
                    }
                }
            }*/

        }

        return false;
    }

}
