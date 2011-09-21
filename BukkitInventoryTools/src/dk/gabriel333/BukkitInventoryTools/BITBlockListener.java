package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.material.Door;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BITBlockListener extends BlockListener {

	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (BITDigiLock.isDoor(block)) {
			Door door = (Door) block.getState().getData();
			if (BITDigiLock.isLocked(block)) {
				//BITDigiLock digilock = digilock.loadDigiLock(sPlayer, block);
				if (!door.isOpen()) {
					event.setNewCurrent(event.getOldCurrent());
				}
			}
		}

	}

	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		SpoutBlock blockOnTop = block.getRelative(BlockFace.UP);
		if (BITDigiLock.isLocked(block) ||  BITDigiLock.isLocked(blockOnTop)) {
			sPlayer.damage(10);
			event.setCancelled(true);
		} 
	}

	/*public void onBlockDamage(BlockDamageEvent event) {
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		if (BITDigiLock.isLocked(block)) {
			sPlayer.damage(1);
			event.setCancelled(true);
		} 
	}*/

}
