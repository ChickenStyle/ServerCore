package me.chickenstyle;

import me.chickenstyle.dimensions.mine.entities.MinersHandle;
import me.chickenstyle.listeners.MobsEvents;
import me.chickenstyle.utils.PacketReader;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	private static Main instance;
	private PacketReader packetReader;

	@Override
	public void onEnable() {
		this.getConfig().options().copyDefaults();
	    saveDefaultConfig();
		instance = this;
		packetReader = new PacketReader();

		getCommand("move").setExecutor(new MoveCommand());
		getCommand("load").setExecutor(new LoadCommand());
		registerListeners();

		validatePlayers();

		for (ChunkGenerators generator:ChunkGenerators.values()) {
			boolean contains = false;
			for (World world:Bukkit.getWorlds()) {
				contains = world.getName().equalsIgnoreCase(generator.getName());
			}

			if (!contains) {
				System.out.println(" ");
				System.out.println("--------Generating " + generator.getName() + "--------");
				WorldCreator creator = new WorldCreator(generator.getName());
				creator.generator(generator.getChunkGenerator());
				getServer().createWorld(creator);
				System.out.println("--------Finished Generating " + generator.getName() + "--------");
				System.out.println(" ");
			}
		}
	}
	
	@Override
	public void onDisable() {
		invalidatePlayers();
		for (World world:getServer().getWorlds()) {
			for (Entity entity:world.getEntities()) {
				net.minecraft.server.v1_15_R1.Entity ent = ((CraftEntity) entity).getHandle();
				if (ent instanceof CustomMob) {
					entity.remove();
					
				}
				
			}
		}

		for (EntityPlayer npc: MinersHandle.getNPCS().values()) {
			MinersHandle.removeNPCPacket(npc);
		}

	}
	/*
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
	    return new AetherChunkGenerator();
	}*/

	public void validatePlayers() {
		if(!Bukkit.getOnlinePlayers().isEmpty()) {
			for(Player p : Bukkit.getOnlinePlayers()) {
				packetReader.inject(p);
			}
		}
	}

	public void invalidatePlayers() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			packetReader.uninject(p);
		}
	}
	
	
	private void registerListeners() {
		getServer().getPluginManager().registerEvents(new MobsEvents(), this);
	}
	
	public static Main getInstance() {
		return instance;
	}
	
}
