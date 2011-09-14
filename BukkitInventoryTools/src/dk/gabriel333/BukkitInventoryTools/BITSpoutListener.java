package dk.gabriel333.BukkitInventoryTools;

import java.util.UUID;

import org.bukkit.Material;

import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Inventory;
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
			// Buttons in InventoryMenuWindow
			// ************************************
			if (BITGui.BITButtons.get(uuid) == "Sort") {
				if (G333Permissions.hasPerm(sPlayer, "sortinventory.use",
						G333Permissions.NOT_QUIET)) {
					if (digilock.getBlock().getType() == Material.CHEST) {
						SpoutChest sChest = (SpoutChest) digilock.getBlock()
								.getState();
						G333Inventory.sortInventoryItems(sPlayer,
								sChest.getLargestInventory());
						G333Messages.sendNotification(sPlayer, "Chest sorted.");
					} else {
						G333Messages.sendNotification(sPlayer, "Items sorted.");
					}
				} else {
					G333Messages.sendNotification(sPlayer,
							"No permission to sort.");
				}
			} else if (BITGui.BITButtons.get(uuid) == "Lock") {
				if (G333Permissions.hasPerm(sPlayer, "digilock.use",
						G333Permissions.NOT_QUIET)
						|| G333Permissions.hasPerm(sPlayer, "digilock.admin",
								G333Permissions.NOT_QUIET)) {
					if (G333Config.g333Config.DEBUG_GUI)
						sPlayer.sendMessage("Block:"
								+ digilock.getBlock().getType() + " Pin:"
								+ BITGui.pincode.getText() + " owner:"
								+ BITGui.owner.getText());
					BITDigiLock.SaveDigiLock(sPlayer, digilock.getBlock(),
							BITGui.pincode.getText(), BITGui.owner.getText(),
							0, null, null);
					G333Messages.sendNotification(sPlayer, "Inventory locked.");
				} else {
					G333Messages.sendNotification(sPlayer,
							"No permission to lock.");
				}
			} else if (BITGui.BITButtons.get(uuid) == "Owner") {
				if ((G333Permissions.hasPerm(sPlayer, "digilock.use",
						G333Permissions.NOT_QUIET) && digilock.owner == sPlayer
						.getName())
						|| G333Permissions.hasPerm(sPlayer, "digilock.admin",
								G333Permissions.NOT_QUIET)) {
					BITDigiLock.SaveDigiLock(sPlayer, digilock.getBlock(),
							BITGui.pincode.getText(), BITGui.owner.getText(),
							0, null, null);
					G333Messages.sendNotification(sPlayer, "Owner changed.");
				} else {
					G333Messages.sendNotification(sPlayer,
							"No permission to change owner.");
				}
			} else if (BITGui.BITButtons.get(uuid) == "Reset") {
				if (G333Permissions.hasPerm(sPlayer, "digilock.admin",
						G333Permissions.NOT_QUIET)
						|| (G333Permissions.hasPerm(sPlayer, "digilock.use",
								G333Permissions.NOT_QUIET) && digilock.owner == sPlayer
								.getName())) {
					BITGui.pincode.setText("");
					BITGui.owner.setText("SHARED");
					BITDigiLock.SaveDigiLock(sPlayer, digilock.getBlock(),
							BITGui.pincode.getText(), BITGui.owner.getText(),
							0, "", "");
					G333Messages.sendNotification(sPlayer,
							"Pincode/owner removed.");
					BITGui.pincode.setDirty(true);
				} else {
					G333Messages.sendNotification(sPlayer,
							"No permission to reset lock.");
				}
			} else if (BITGui.BITButtons.get(uuid) == "Close") {
				BITGui.popupInventoryMenu.close();
			}

			// ************************************
			// Buttons in getPincodeWindow
			// ************************************
			else if (BITGui.BITButtons.get(uuid) == "getPincodeUnlock") {
				BITGui.popupGetPincode.close();
				if (digilock.pincode.equals(BITGui.pincode2.getText())) {
					if (BITDigiLock.isChest(digilock.getBlock())) {
						SpoutChest sChest = (SpoutChest) block.getState();
						Inventory inv = sChest.getLargestInventory();
						sPlayer.openInventoryWindow(inv);
					} else if (BITDigiLock.isDoor(digilock.getBlock())) {
						// TODO: open door the door
						
						BITDigiLock.openDoor(digilock.getBlock());
					}
				} else {
					// BITGui.popupGetPincode.close();
					G333Messages.sendNotification(sPlayer, "Wrong pincode!");
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
						BITGui.pincode3.getText(), sPlayer.getDisplayName(), 0,
						"", "");
			} else if ((BITGui.BITButtons.get(uuid) == "setPincodeCancel")) {
				BITGui.popupSetPincode.close();
			} else if ((BITGui.BITButtons.get(uuid) == "setPincodeRemove")) {
				BITGui.popupSetPincode.close();
				if (BITDigiLock.isLocked(block)) {
					BITDigiLock.RemoveDigiLock(sPlayer, digilock);
				}
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
