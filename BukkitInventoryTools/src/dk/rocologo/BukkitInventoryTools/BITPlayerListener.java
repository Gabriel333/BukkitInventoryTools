package dk.rocologo.BukkitInventoryTools;

import org.bukkit.block.Block;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

import org.getspout.spoutapi.player.SpoutPlayer;

import dk.rocologo.Library.RLMessages;

public class BITPlayerListener extends PlayerListener {

	// static String enteredPincode = "";
	// static BITDigiLock digilock;
	// static Boolean pincodeIsEntered = false;

	public void onPlayerInteract(PlayerInteractEvent event) {

		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		sPlayer.sendMessage("BITPlayerListener, Name:" + event.getEventName() + " type:"
				+ event.getType());
		Block block = event.getClickedBlock();
		
		sPlayer.sendMessage("Action:"+event.getAction());
		
		if (block != null) {
			if (BITDigiLock.isLocked(sPlayer, block)) {
				if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					RLMessages.sendNotification(sPlayer, "Locked with DigiLock");
				} else {
					BITGui.openPincodeWindow(sPlayer);
				}
				event.setCancelled(true);
			} else {
				// there is no digilock on this block
				sPlayer.sendMessage("There is no digilock on this block");
			}
		} else {
			sPlayer.sendMessage("Block was null");
		}
		
		
	}
}
