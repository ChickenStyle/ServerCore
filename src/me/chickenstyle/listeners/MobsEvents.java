package me.chickenstyle.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import me.chickenstyle.ChunkGenerators;
import me.chickenstyle.dimensions.mine.entities.MinersHandle;
import net.minecraft.server.v1_15_R1.EntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.chickenstyle.Main;
import me.chickenstyle.CustomMob;
import net.minecraft.server.v1_15_R1.Entity;

public class MobsEvents implements Listener{
	@EventHandler
	public void onEntitySpawn(CreatureSpawnEvent e) {
		if (e.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
			try {
				ChunkGenerators generator = ChunkGenerators.valueOf(e.getLocation().getWorld().getName().toUpperCase());
				if (generator.getDimension().getCustomMobs().containsKey(e.getEntityType())) {
					Location loc = e.getLocation();
					CustomMob mob = generator.getDimension().getCustomMobs().get(e.getEntityType());
					if (mob.getSpawnChance() > ThreadLocalRandom.current().nextDouble(0,99)) {
						mob.spawn(loc);
					}

				}
				e.setCancelled(true);
			} catch (Exception ignored) {}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		Entity ent = ((CraftEntity) e.getEntity()).getHandle();
		if (ent instanceof CustomMob) {
			CustomMob mob = (CustomMob) ent;
			new BukkitRunnable() {
				
				@Override
				public void run() {
					mob.updateName();
				}

			}.runTaskLater(Main.getInstance(), 1);
			
			
		}
	}
	
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		Entity ent = ((CraftEntity) e.getEntity()).getHandle();
		if (ent instanceof CustomMob && e.getEntity().getKiller() instanceof LivingEntity) {
			CustomMob mob = (CustomMob) ent;

			
			e.getDrops().clear();


			Player killer = e.getEntity().getKiller();
			int fortuneLevel = killer.getItemInHand() != null ? killer.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) : 0;

			Random rnd = new Random();
			HashMap<ItemStack,Double> drop = mob.getDrop(fortuneLevel);

			for (ItemStack item:drop.keySet()) {
				if (rnd.nextDouble() <= drop.get(item)) {
					e.getDrops().add(item);
				}
			}
			
			
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (MinersHandle.getNPCS() != null && !MinersHandle.getNPCS().isEmpty()) {
			MinersHandle.addNPCJoin(e.getPlayer());
		}
	}
}
