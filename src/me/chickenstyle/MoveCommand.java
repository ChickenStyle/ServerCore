package me.chickenstyle;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String str, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (Bukkit.getWorld(args[0]) != null) {
                Location pLoc = player.getLocation();
                Location loc = new Location(Bukkit.getWorld(args[0]),pLoc.getX(),69,pLoc.getZ(),pLoc.getYaw(),pLoc.getPitch());

                player.teleport(loc);
                player.sendMessage("Teleported you to " + args[0]);
            } else {
                player.sendMessage("&cInvalid World!");
            }
        }

        return false;
    }
}
