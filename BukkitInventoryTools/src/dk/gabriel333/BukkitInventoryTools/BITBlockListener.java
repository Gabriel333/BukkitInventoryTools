package dk.gabriel333.BukkitInventoryTools;



import org.bukkit.block.Block;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.getspout.spoutapi.player.SpoutPlayer;


public class BITBlockListener extends BlockListener {

	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		/*
		 * Block block = event.getBlock(); if
		 * (ChestLock.isDoor(block.getType())) { LinkedList<LockedDoor> doors =
		 * SaveSystem.getDoors(); for (LockedDoor lockedDoor : doors) { if
		 * (lockedDoor.block.getLocation().equals(block.getLocation()) ||
		 * lockedDoor.isNeighbor(block)) { Door door =
		 * (Door)lockedDoor.block.getState().getData(); //Allows redstone to
		 * close a door but not open it if (!door.isOpen() && lockedDoor.key !=
		 * 0) event.setNewCurrent(event.getOldCurrent()); } } }
		 */
	}

	public void onBlockBreak(BlockBreakEvent event) {
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		Block block = event.getBlock();
		sPlayer.sendMessage("BlockBreakEvent:"+block.getType());
		if (event.isCancelled())
			return;
		if (BITDigiLock.isLocked(sPlayer, block)) {
			// TODO: DAMAGE the user or spawn monster
			event.setCancelled(true);
		} else {
			
		}
	}
	
	public void onBlockDamage(BlockDamageEvent event) {
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		Block block = event.getBlock();
		sPlayer.sendMessage("BlockDamageEvent:"+block.getType());
		if (event.isCancelled())
			return;
		if (BITDigiLock.isLocked(sPlayer, block)) {
			// TODO: DAMAGE the user or spawn monster
			event.setCancelled(true);
		} else {
			
		}
	}

}
