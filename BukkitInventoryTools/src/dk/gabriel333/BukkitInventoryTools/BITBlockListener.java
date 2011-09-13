package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.event.Cancellable;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.getspout.spoutapi.block.SpoutBlock;

public class BITBlockListener extends BlockListener {

	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (((Cancellable) event).isCancelled())
			return;
		if (BITDigiLock.isLocked(block)) {
			// TODO: DAMAGE the user or spawn monster
			((Cancellable) event).setCancelled(true);
		} else {
			
		}
		
	}

	public void onBlockBreak(BlockBreakEvent event) {
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (event.isCancelled())
			return;
		if (BITDigiLock.isLocked(block)) {
			// TODO: DAMAGE the user or spawn monster
			event.setCancelled(true);
		} else {
			
		}
	}
	
	public void onBlockDamage(BlockDamageEvent event) {
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (event.isCancelled())
			return;
		if (BITDigiLock.isLocked(block)) {
			// TODO: DAMAGE the user or spawn monster
			event.setCancelled(true);
		} else {
			
		}
	}

}
