package me.chickenstyle.customPathFinders;

import java.util.EnumSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import me.chickenstyle.Main;
import net.minecraft.server.v1_15_R1.EntityInsentient;
import net.minecraft.server.v1_15_R1.EntityLiving;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PathfinderGoal;
import net.minecraft.server.v1_15_R1.Vec3D;

public class PathfinderGoalLeapAtTarget extends PathfinderGoal {
	  private final EntityInsentient a;
	  
	  private EntityLiving b;
	  
	  private final float c;
	  
	  private int delay;
	  
	  private int jumpDelay;
	  
	  
	  public PathfinderGoalLeapAtTarget(EntityInsentient var0, float var1,int jumpDelay) {
	    this.a = var0;
	    this.c = var1;
	    a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
	    this.jumpDelay = jumpDelay;
	    
	    this.delay = 0;
	    
	    
	  }
	  
	  public boolean a() {
	    if (this.a.isVehicle())
	      return false; 
	    this.b = this.a.getGoalTarget();
	    if (this.b == null)
	      return false; 
	    double var0 = this.a.h(this.b);
	    if (var0 < 4.0D || var0 > 16.0D)
	      return false; 
	    if (!this.a.onGround)
	      return false; 
	    if (this.a.getRandom().nextInt(5) != 0)
	      return false; 
	    return true;
	  }
	  
	  //public boolean b() {
	   // return !this.a.onGround;
	 // }
	  
	  public void c() {
		  
		  EntityLiving target = a.getGoalTarget();
		  

		  
		  Bukkit.getScheduler().runTaskTimer(Main.getInstance(), (task) ->{
			  if (target == null || !(target instanceof EntityPlayer) || !a.isAlive()) {
				  task.cancel();
				  return;
			  }
			  
			  if (this.b == null || this.b.isAlive() == false) {
				  task.cancel();
				  return;
			  }
			  
			  Location entLoc = a.getBukkitEntity().getLocation();
			  
			  Location targetLoc = target.getBukkitEntity().getLocation();
			  
			  double distance = entLoc.distance(targetLoc);
			  
			  
			  this.delay = delay + 4;
			  
			  if (distance >= 1 && distance <= 4) {
				  if (this.delay > this.jumpDelay) {
					  Vec3D var0 = this.a.getMot();
					  Vec3D var1 = new Vec3D(this.b.locX() - this.a.locX(), 0.0D, this.b.locZ() - this.a.locZ());
					  if (var1.g() > 1.0E-7D) 
						  this.delay = 0;
						  var1 = var1.d().a(0.4D).e(var0.a(0.4D)); 
						  this.a.setMot(var1.x, this.c, var1.z);
					  
				  }
			  }

		  }, 0, 4);
		  
		 
	  }
	}
