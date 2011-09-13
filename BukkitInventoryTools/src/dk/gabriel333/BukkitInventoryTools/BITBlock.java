package dk.gabriel333.BukkitInventoryTools;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.SpoutBlock;



public abstract class BITBlock implements SpoutBlock{
	
	private Plugin plugin;

	public BITBlock(Plugin plugin) {
		plugin = this.plugin;
	}

	protected SpoutBlock sBlock;
	protected Block block;
	
	/**
	 * Constructs a new BITBlock
	 * 
	 */
	BITBlock(SpoutBlock block) {
		this.sBlock = block;
	}
	
	BITBlock(Block block) {
		this.sBlock = (SpoutBlock) block;
	}
	
	public boolean isDoor() {
		if (sBlock.getType().equals(Material.CHEST))
			return true;
		else if (sBlock.getType().equals(Material.LOCKED_CHEST))
			return true;
		return false;
	}
	
	public boolean isChest() {
		if (sBlock.getType().equals(Material.CHEST))
			return true;
		else if (sBlock.getType().equals(Material.LOCKED_CHEST))
			return true;
		return false;
	}
}
