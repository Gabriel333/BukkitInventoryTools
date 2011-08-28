package dk.rocologo.BukkitInventoryTools;



import org.bukkit.block.Block;

import org.bukkit.event.block.BlockBreakEvent;
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
		Block block = event.getBlock();
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		sPlayer.sendMessage("BlockBreakEvent:"+block);
		//if (BITDigiLock.isLockable(block)) {
		//	if (BITDigiLock.isLocked(block)) {
		//		BITGui.openPincodeWindow(sPlayer);
		//		sPlayer.sendMessage("Pincode:"+BITGui.pincode.getText()
		//				+" Pincode2:"+BITGui.pincode2.getText());
		//		event.setCancelled(true);
		//	} 
		//}
	}

}
