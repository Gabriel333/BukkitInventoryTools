package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.Material;
import org.getspout.spoutapi.block.SpoutBlock;


public class BITBlock {
	
	SpoutBlock sBlock;
	
	public boolean isDoor() {
		if (sBlock.getType().equals(Material.WOOD_DOOR))
			return true;
		else if (sBlock.getType().equals(Material.WOODEN_DOOR))
			return true;
		else if (sBlock.getType().equals(Material.IRON_DOOR))
			return true;
		else if (sBlock.getType().equals(Material.IRON_DOOR_BLOCK))
			return true;
		return false;
	}


}
