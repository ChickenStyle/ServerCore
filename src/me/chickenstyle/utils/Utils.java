package me.chickenstyle.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import me.chickenstyle.dimensions.IDimension;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.transform.AffineTransform;
import com.sk89q.worldedit.session.ClipboardHolder;


public class Utils {
	public static String color(String text) {
		return ChatColor.translateAlternateColorCodes('&', text);
	}
	
	
    public static void generateMeteor(Location centerBlock, int radius,Material[] ores) {
        
    	Random rnd = new Random();
        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();

        for(int x = bx - radius; x <= bx + radius; x++) {
            for(int y = by - radius; y <= by + radius; y++) {
                for(int z = bz - radius; z <= bz + radius; z++) {

                    double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));

                    if(distance < radius * radius) {

                        Location loc = new Location(centerBlock.getWorld(), x, y, z);
                        
                        loc.getBlock().setType(ores[rnd.nextInt(ores.length)]);
                        
                        if (ores[0] == Material.AIR) {
                        	if (new Random().nextBoolean()) {
                        		loc.getBlock().setType(Material.FIRE);
                        	}
                        }
                    }

                }
            }
        }

    }
    /*
	public static void loadSchematic(Location loc, IDimension dimension, Clipboard clipboard, boolean ignoreAir) {

		// Pastes schematic
		EditSession editSession = null;
		try {
			com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(loc.getWorld());
			editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, 69420);


			ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
			clipboardHolder.setTransform(new AffineTransform().rotateY(90 * new Random().nextInt(4)));
			Operation operation = clipboardHolder.createPaste(editSession)
					.to(BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())).ignoreAirBlocks(ignoreAir)
					.build();

			Operations.complete(operation);
			editSession.flushSession();
		} catch (WorldEditException ex) {
			ex.printStackTrace();
		}

		int maxArea = Math.max(clipboard.getRegion().getLength(), clipboard.getRegion().getWidth());
		maxArea = Math.max(maxArea, clipboard.getRegion().getHeight());
		replaceChests(dimension,loc,maxArea);

	}*/

    public static int loadSchematic(Location loc, Clipboard clipboard, boolean ignoreAir) {
    	
		// Pastes schematic
		EditSession editSession = null;
		try {
			com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(loc.getWorld());
			editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(adaptedWorld, 69420);
			
			
			ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
			clipboardHolder.setTransform(new AffineTransform().rotateY(90 * new Random().nextInt(4)));
			Operation operation = clipboardHolder.createPaste(editSession)
		            .to(BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())).ignoreAirBlocks(ignoreAir)
		            .build();
			
		    Operations.complete(operation);
		    editSession.flushSession();
		} catch (WorldEditException ex) {
			ex.printStackTrace();
		} 
		
		int maxArea = Math.max(clipboard.getRegion().getLength(), clipboard.getRegion().getWidth());
		maxArea = Math.max(maxArea, clipboard.getRegion().getHeight());
		return maxArea;

    }
    
    public static void replaceChests(IDimension dimension,Location center,int radius) {
		for (int x = center.getBlockX() - (radius/2);x <= center.getBlockX() + (radius/2);x++) {
			for (int y = center.getBlockY(); y <= center.getBlockY() + (radius - (radius * 0.3));y++) {
				for (int z = center.getBlockZ() - (radius/2);z <= center.getBlockZ() + (radius/2);z++) {
					if (center.getWorld().getBlockAt(x, y, z).getType().equals(Material.CHEST)) {
						
						Random rnd = new Random();
						int counter = 0;
						HashMap<ItemStack,Integer> loots = dimension.getChestLoot();
						ItemStack[] chestLoot = new ItemStack[loots.keySet().size()];
						
						for (ItemStack loot:loots.keySet()) {
							int chance = rnd.nextInt(100);
							if (loots.get(loot) >= chance) {
								chestLoot[counter] = loot;
								counter++;
							}
						}
						
						Chest chest = (Chest) center.getWorld().getBlockAt(x, y, z).getState();
						chest.update();
						chest.getSnapshotInventory().setContents(randomiseChest(chestLoot));
						chest.update();
					}	
					
				}
			}
		}
    }
    
    
    private static ItemStack[] randomiseChest(ItemStack[] arr) {
    	ItemStack[] itemArr = arr.clone();
    	ItemStack[] inv = new ItemStack[27];
    	
    	for (int i = 0; i < 27;i++) {
    		inv[i] = new ItemStack(Material.AIR);
    	}
    	
    	
    	List<ItemStack> itemList = Arrays.asList(itemArr);
    	Collections.shuffle(itemList);
    	itemList.toArray(itemArr);
    	Random rnd = new Random();
    	
    	int counter = 0;
    	boolean flag = true;
    	do {
    		for (int i = 0; i < 27;i++) {
    			if (counter <= arr.length - 1) {
					if (inv[i].getType().equals(Material.AIR)) {
						if (rnd.nextDouble() < 0.15) {
	    					inv[i] = itemArr[counter];
	    					counter++;
						}
					}
    			} else {
    				flag = false;
    			}
    		}
    		
    		
    	} while(flag);
    	return inv;
    }
    
    public static boolean emptyArea(Location center, int radius,Material groundType) {
		int airAmount = 0;
		int floorAmount = 0;
		
		for (int x = center.getBlockX() - (int) (radius/2);x <= center.getBlockX() + (int) (radius/2);x++) {
			for (int y = center.getBlockY(); y <= center.getBlockY() + radius;y++) {
				for (int z = center.getBlockZ() - (int) (radius/2);z <= center.getBlockZ() + (int) (radius/2);z++) {
					if (center.getWorld().getBlockAt(x, y, z).getType().equals(Material.AIR)) {
						airAmount++;
					}
					
					// Checks if there is floor under the schematic
					if (y == center.getBlockY()) {
						if (center.getWorld().getBlockAt(x, y - 1, z).getType().equals(groundType)) {
							floorAmount++;
						}
					}
					
					
				}
			}
		}

		return airAmount >= radius*radius*radius-(radius/1.5) && floorAmount >= radius*radius-(radius/2);

    }

}