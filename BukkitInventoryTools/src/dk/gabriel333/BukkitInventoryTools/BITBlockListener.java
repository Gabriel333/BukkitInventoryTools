package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.material.Door;
import org.getspout.spoutapi.block.SpoutBlock;

public class BITBlockListener extends BlockListener {

	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (BITDigiLock.isDoor(block)) {
			Door door = (Door) block.getState().getData();
			if (BITDigiLock.isLocked(block)) {
				// TODO: DAMAGE the user or spawn monster
				if (!door.isOpen()) {
					event.setNewCurrent(event.getOldCurrent());
				}
			}
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
