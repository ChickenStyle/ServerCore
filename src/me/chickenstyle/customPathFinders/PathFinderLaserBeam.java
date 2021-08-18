package me.chickenstyle.customPathFinders;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.chickenstyle.Main;
import net.minecraft.server.v1_15_R1.EntityInsentient;
import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PathfinderGoal;
import xyz.xenondevs.particle.ParticleEffect;

public class PathFinderLaserBeam extends PathfinderGoal{

	private final EntityInsentient a; // mob
	public EntityLiving b;  // target
	
	private final int f; // delay between attacks (in ticks)
	
	private int delay;
	
	
	
	public PathFinderLaserBeam(EntityInsentient a,int f) {
		this.a = a;
		this.f = f;
		delay = 0;
	}
	
	@Override
	public boolean a() {
		this.b = a.getGoalTarget();
		if (this.b == null) {
			return false;
		}


		return this.b instanceof EntityPlayer;
	}
	
	
	
	@Override
	public void c() {
		Bukkit.getScheduler().runTaskTimer(Main.getInstance(),(task) -> {
			EntityLiving target = a.getGoalTarget();
			
			
			if (target instanceof EntityPlayer && a.isAlive()) {
				if(delay >= f) {
					delay = 0;
					double space = 0.4;
					
					Location point1 = a.getBukkitEntity().getLocation().clone().add(0,1.5,0);
					Location point2 = target.getBukkitEntity().getLocation().clone().add(0,1.2,0);
					
					

				    Vector p1 = point1.toVector();
				    Vector p2 = point2.toVector();
				    
				    
				    
				    new BukkitRunnable() {
				    	double length = 0;
				    	double distance = point1.distance(point2);
				    	Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
						@Override
						public void run() {
							if (length < distance) {
								p1.add(vector);
						    	ParticleEffect.DRIP_LAVA.display(new Location (point1.getWorld()  ,p1.getX(),p1.getY(),p1.getZ()));
						    	point1.getWorld().playSound(point1,Sound.BLOCK_LAVA_POP,1,1);
						        length += space;
								
								for (Entity e:p1.toLocation(point1.getWorld()).getWorld().getNearbyEntities(p1.toLocation(point1.getWorld()), 1, 1, 1)) {
									if (!e.equals(a.getBukkitEntity())) {
										if (e instanceof LivingEntity) {
											LivingEntity ent = (LivingEntity) e;
											ent.damage(5);
											e.setFireTicks(5*20);
										}
									}		
								}
						        
							} else {
								this.cancel();
							}
							
						}
					}.runTaskTimer(Main.getInstance(), 0, 1);

					
				} else {
					delay++;
				}
			} else {
				task.cancel();
			}
			
		},0,1);
	}
	
}
