package dk.rocologo.BukkitInventoryTools;

import org.bukkit.block.Block;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;
import org.getspout.spoutapi.player.SpoutPlayer;



//import dk.rocologo.Library.RLConfig;
//import dk.rocologo.Library.RLMessages;

public class BITInventoryListener extends InventoryListener {

	public BIT plugin;

	public BITInventoryListener(BIT plugin) {
		this.plugin = plugin;
	}

	public void onInventoryOpen(InventoryOpenEvent event) {
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		Block block = sPlayer.getTargetBlock(null, 4);
		//RLMessages.sendNotification(sPlayer, RLConfig.rLConfig.LIBRARY_SORTKEY
			//	+ ":Sort " + RLConfig.rLConfig.LIBRARY_MENUKEY + ":Menu");

		sPlayer.sendMessage("BITInventoryListener, pincodeIsEntered:"
				+ BITPlayerListener.pincodeIsEntered);

		if (BITDigiLock.isLocked(sPlayer, block)) {
			if (BITPlayerListener.pincodeIsEntered
					&& BITPlayerListener.digilock.locked) {
				sPlayer.sendMessage("BITInventoryListener: okay");
			} else {
				sPlayer.sendMessage("BITInventoryListener: NO");
				
			}
			event.setCancelled(true);
			// BITDigiLock digilock = BITDigiLock.getDigiLock(sPlayer, block);

			// if (BITPlayerListener.pincodeIsEntered) {
			// if (BITPlayerListener.enteredPincode.equals(digilock.pincode)) {
			// RLMessages.sendNotification(sPlayer,
			// RLConfig.rLConfig.LIBRARY_SORTKEY + ":Sort "
			// + RLConfig.rLConfig.LIBRARY_MENUKEY
			// + ":Menu");
			// } else
			// sPlayer.sendMessage("BITInventoryListener - Wrong pincode");
			// event.setCancelled(true);
			// }

		}
	}

	public void onInventoryClose(InventoryCloseEvent event) {
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		Block block = sPlayer.getTargetBlock(null, 4);
		if (BITDigiLock.isLocked(sPlayer, block)) {
			// BITDigiLock digilock = BITDigiLock.getDigiLock(sPlayer, block);
			// sPlayer.sendMessage("Inv.close.evt: digilock.locked=true");
			BITPlayerListener.digilock.locked = true;
			BITPlayerListener.enteredPincode = "";
			BITPlayerListener.pincodeIsEntered = false;
		}
	}

}
