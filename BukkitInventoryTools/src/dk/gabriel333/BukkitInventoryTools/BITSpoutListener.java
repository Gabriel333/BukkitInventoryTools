package dk.gabriel333.BukkitInventoryTools;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
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
			SpoutBlock sBlock = (SpoutBlock) sPlayer.getTargetBlock(null, 4);
			BITDigiLock digilock = BITDigiLock.loadDigiLock(sPlayer, sBlock);

			// ************************************
			// Buttons in getPincodeWindow
			// ************************************
			if (BITGui.BITButtons.get(uuid) == "getPincodeUnlock") {
				BITGui.popupGetPincode.close();
				if ((digilock.getPincode().equals(BITGui.pincode2.getText()) && G333Permissions
						.hasPerm(sPlayer, "digilock.use", G333Permissions.QUIET))
						|| G333Permissions.hasPerm(sPlayer, "digilock.admin",
								G333Permissions.QUIET)) {
					if (BITDigiLock.isChest(digilock.getBlock())) {
						SpoutChest sChest = (SpoutChest) sBlock.getState();
						Inventory inv = sChest.getLargestInventory();
						sPlayer.openInventoryWindow(inv);

					} else if(BITDigiLock.isDoubleDoor(digilock.getBlock())) {
						BITDigiLock.openDigiLockSound(digilock.getBlock());
						BITDigiLock.openDoubleDoor(sPlayer, digilock.getBlock());
						
					} else if (BITDigiLock.isDoor(digilock.getBlock())) {
						BITDigiLock.openDigiLockSound(digilock.getBlock());
						BITDigiLock.openDoor(sPlayer, digilock.getBlock());

					} else if (digilock.getBlock().getType() == Material.LEVER) {

					} else if (digilock.getBlock().getType() == Material.STONE_BUTTON) {

					} else if (digilock.getBlock().getType() == Material.DISPENSER) {
						Dispenser dispenser = (Dispenser) sBlock.getState();
						Inventory inv = dispenser.getInventory();
						sPlayer.openInventoryWindow(inv);

					} else if (digilock.getBlock().getType() == Material.FURNACE) {
						Furnace furnace = (Furnace) sBlock.getState();
						Inventory inv = furnace.getInventory();
						sPlayer.openInventoryWindow(inv);

					} else if (digilock.getBlock().getType() == Material.BOOKSHELF) {

					} else if (digilock.getBlock().getType() == Material.TRAP_DOOR) {
						BITDigiLock.openDigiLockSound(digilock.getBlock());
						BITDigiLock.openTrapdoor(sPlayer, digilock.getBlock());

					} else if (BITDigiLock.isSign(sBlock)) {

					}
				} else {
					G333Messages.sendNotification(sPlayer, "Wrong pincode!");
					if (BITDigiLock.isDoor(digilock.getBlock())) {
						BITDigiLock.closeDoor(sPlayer, digilock.getBlock());
					} else if (BITDigiLock.isChest(digilock.getBlock())
							|| digilock.getBlock().getType() == Material.DISPENSER
							|| digilock.getBlock().getType() == Material.FURNACE) {
						sPlayer.closeActiveWindow();
					}
					sPlayer.damage(5);
				}
				BITGui.popupGetPincode.removeWidgets(BIT.plugin);
				BITGui.cleanupGetPincode(sPlayer);
			} else if (BITGui.BITButtons.get(uuid) == "getPincodeCancel") {
				BITGui.popupGetPincode.close();
				BITGui.popupGetPincode.removeWidgets(BIT.plugin);
				BITGui.cleanupGetPincode(sPlayer);
			}

			// ************************************
			// Buttons in BITGui.setPincode
			// ************************************
			else if (BITGui.BITButtons.get(uuid) == "setPincodeLock"
					&& G333Permissions.hasPerm(sPlayer, "digilock.create",
							G333Permissions.QUIET)) {
				if (validateSetPincodeFields(sPlayer)) {
					BITGui.popupSetPincode.close();
					BITGui.popupSetPincode.removeWidgets(BIT.plugin);
					BITDigiLock.SaveDigiLock(sPlayer, sBlock,
							BITGui.pincode3.getText(), BITGui.owner1.getText(),
							Integer.valueOf(BITGui.closetimer1.getText()),
							BITGui.listOfCoOwners.getText(), "",
							sBlock.getTypeId(), "");
					BITGui.cleanupSetPincode(sPlayer);
					}

			} else if ((BITGui.BITButtons.get(uuid) == "setPincodeCancel")) {
				BITGui.popupSetPincode.close();
				BITGui.popupSetPincode.removeWidgets(BIT.plugin);
				BITGui.cleanupSetPincode(sPlayer);
				
			} else if ((BITGui.BITButtons.get(uuid) == "setPincodeRemove")) {
				BITGui.popupSetPincode.close();
				BITGui.popupSetPincode.removeWidgets(BIT.plugin);
				BITGui.cleanupSetPincode(sPlayer);
				
				if (BITDigiLock.isLocked(sBlock)) {
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

	private boolean validateSetPincodeFields(SpoutPlayer sPlayer) {
		if (BITGui.closetimer1.getText().equals("")) {
			BITGui.closetimer1.setText("0");
		}
		int closetimer = Integer.valueOf(BITGui.closetimer1.getText());
		if (closetimer < 0 || closetimer > 3600) {
			G333Messages.sendNotification(sPlayer, "Error in closetimer");
			return false;
		}
		return true;
	}
}
