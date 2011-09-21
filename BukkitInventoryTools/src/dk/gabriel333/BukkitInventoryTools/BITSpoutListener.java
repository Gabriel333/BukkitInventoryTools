package dk.gabriel333.BukkitInventoryTools;

import java.util.UUID;


import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;
import dk.gabriel333.Library.G333Permissions;

public class BITSpoutListener extends SpoutListener {

	public void onCustomEvent(Event event) {
		if (event instanceof ButtonClickEvent) {
			Button button = ((ButtonClickEvent) event).getButton();
			UUID uuid = button.getId();
			SpoutPlayer sPlayer = ((ButtonClickEvent) event).getPlayer();
			SpoutBlock block = (SpoutBlock) sPlayer.getTargetBlock(null, 4);
			BITDigiLock digilock = BITDigiLock.loadDigiLock(sPlayer, block);

			// ************************************
			// Buttons in getPincodeWindow
			// ************************************
			if (BITGui.BITButtons.get(uuid) == "getPincodeUnlock") {
				BITGui.popupGetPincode.close();
				if (digilock.pincode.equals(BITGui.pincode2.getText())||
						G333Permissions.hasPerm(sPlayer, "digilock.admin",
						G333Permissions.QUIET)) {
					if (BITDigiLock.isChest(digilock.getBlock())) {
						SpoutChest sChest = (SpoutChest) block.getState();
						Inventory inv = sChest.getLargestInventory();
						sPlayer.openInventoryWindow(inv);
					} else if (BITDigiLock.isDoor(digilock.getBlock())) {
						BITDigiLock.openDoor(sPlayer,digilock.getBlock());					
					}
				} else {
					G333Messages.sendNotification(sPlayer, "Wrong pincode!");
					if (BITDigiLock.isDoor(digilock.getBlock())) {
						BITDigiLock.closeDoor(sPlayer,digilock.getBlock());
					}
					sPlayer.damage(5);
				}
			} else if (BITGui.BITButtons.get(uuid) == "getPincodeCancel") {
				BITGui.popupGetPincode.close();
			}

			// ************************************
			// Buttons in BITGui.setPincode
			// ************************************

			else if (BITGui.BITButtons.get(uuid) == "setPincodeLock") {
				BITGui.popupSetPincode.close();
				BITDigiLock.SaveDigiLock(sPlayer, block,
						BITGui.pincode3.getText(), BITGui.owner1.getText(), 0,
						BITGui.listOfCoOwners.getText(), "", block.getTypeId(),"");
			} else if ((BITGui.BITButtons.get(uuid) == "setPincodeCancel")) {
				BITGui.popupSetPincode.close();
			} else if ((BITGui.BITButtons.get(uuid) == "setPincodeRemove")) {
				BITGui.popupSetPincode.close();
				if (BITDigiLock.isLocked(block)) {
					digilock.RemoveDigiLock(sPlayer);
				}
			} else if ((BITGui.BITButtons.get(uuid) == "OwnerButton")) {
				
			} else if ((BITGui.BITButtons.get(uuid) == "CoOwnerButton")) {
				
			}

			// ************************************
			// This only happens if I have forgot to handle a button
			// ************************************
			else {
				if (G333Config.g333Config.DEBUG_GUI)
					sPlayer.sendMessage("BITSpoutListener: Unknow button:"
							+ BITGui.BITButtons.get(uuid));
			}
		}
	}

}
