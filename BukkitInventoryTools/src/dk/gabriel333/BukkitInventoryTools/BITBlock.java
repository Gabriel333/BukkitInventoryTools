package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.Material;
import org.getspout.spoutapi.block.SpoutBlock;

public abstract class BITBlock implements SpoutBlock{

	SpoutBlock block;
	
	public boolean isDoor() {
		if (block.getType().equals(Material.WOOD_DOOR))
			return true;
		else if (block.getType().equals(Material.WOODEN_DOOR))
			return true;
		else if (block.getType().equals(Material.IRON_DOOR))
			return true;
		else if (block.getType().equals(Material.IRON_DOOR_BLOCK))
			return true;
		return false;
	}
	
	
}
