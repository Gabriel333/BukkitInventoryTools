package dk.rocologo.BukkitInventoryTools;

//import org.bukkit.block.Block;

//import org.bukkit.event.block.Action;
import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.rocologo.Library.RLMessages;

public class BITPlayerListener extends PlayerListener {

	static String enteredPincode = "";
	static BITDigiLock digilock;
	static Boolean pincodeIsEntered = false;

	public void onPlayerInteract(PlayerInteractEvent event) {
		
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		Block block = event.getClickedBlock();
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (BITDigiLock.isLocked(sPlayer, block)) {
				RLMessages.sendNotification(sPlayer, "Locked with DigiLock");
				event.setCancelled(true);
			}
			return;
		} else {
			// sPlayer right-clicked
			if (BITDigiLock.isLocked(sPlayer, block)) {
				digilock = BITDigiLock.getDigiLock(sPlayer, block);
				if (digilock.locked) {
					if (!pincodeIsEntered) {
						BITGui.openPincodeWindow(sPlayer);
					} else {
						if (digilock.pincode.equals(enteredPincode)) {
							sPlayer.sendMessage("Correct pincode!");
						} else {
							sPlayer.sendMessage("Wrong pincode!");
							pincodeIsEntered = false;
							enteredPincode = "";
							event.setCancelled(true);
						}
					}
				}
			} else {
				// there is no digilock on this block
				sPlayer.sendMessage("There is no digilock on this block");
			}
		}
	}
}
