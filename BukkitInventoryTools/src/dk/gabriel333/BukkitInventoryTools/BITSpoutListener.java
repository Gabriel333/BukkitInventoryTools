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
			BITPlayer bPlayer = new BITPlayer(sPlayer);
			SpoutBlock sBlock = (SpoutBlock) sPlayer.getTargetBlock(null, 4);
			BITDigiLock digilock = BITDigiLock.loadDigiLock(sPlayer, sBlock);

			// ************************************
			// Buttons in getPincodeWindow
			// ************************************
			if (BITPlayer.BITButtons.get(uuid) == "getPincodeUnlock") {
				sPlayer.closeActiveWindow();
				if ((digilock.getPincode().equals(BITPlayer.getPincode.getText()) && G333Permissions
						.hasPerm(sPlayer, "digilock.use", G333Permissions.QUIET))
						|| G333Permissions.hasPerm(sPlayer, "digilock.admin",
								G333Permissions.QUIET)) {
					if (BITDigiLock.isChest(digilock.getBlock())) {
						SpoutChest sChest = (SpoutChest) sBlock.getState();
						Inventory inv = sChest.getLargestInventory();
						sPlayer.openInventoryWindow(inv);

					} else if (BITDigiLock.isDoubleDoor(digilock.getBlock())) {
						BITDigiLock.playDigiLockSound(digilock.getBlock());
						BITDigiLock.openDoubleDoor(sPlayer,
								digilock.getBlock(), digilock.getUseCost());

					} else if (BITDigiLock.isDoor(digilock.getBlock())) {
						BITDigiLock.playDigiLockSound(digilock.getBlock());
						BITDigiLock.openDoor(sPlayer, digilock.getBlock(),
								digilock.getUseCost());

					} else if (digilock.getBlock().getType() == Material.LEVER) {

					} else if (digilock.getBlock().getType() == Material.STONE_BUTTON) {

					} else if (digilock.getBlock().getType() == Material.DISPENSER) {
						BITDigiLock.playDigiLockSound(digilock.getBlock());
						Dispenser dispenser = (Dispenser) sBlock.getState();
						Inventory inv = dispenser.getInventory();
						sPlayer.openInventoryWindow(inv);

					} else if (digilock.getBlock().getType() == Material.FURNACE) {
						BITDigiLock.playDigiLockSound(digilock.getBlock());
						Furnace furnace = (Furnace) sBlock.getState();
						Inventory inv = furnace.getInventory();
						sPlayer.openInventoryWindow(inv);

					} else if (digilock.getBlock().getType() == Material.BOOKSHELF) {

					} else if (digilock.getBlock().getType() == Material.TRAP_DOOR) {
						BITDigiLock.playDigiLockSound(digilock.getBlock());
						BITDigiLock.openTrapdoor(sPlayer, digilock.getBlock(),
								digilock.getUseCost());

					} else if (BITDigiLock.isSign(sBlock)) {

					}
				} else {
					G333Messages.sendNotification(sPlayer, "Wrong pincode!");
					if (BITDigiLock.isDoor(digilock.getBlock())) {
						BITDigiLock.closeDoor(sPlayer, digilock.getBlock(), 0);
					} else if (BITDigiLock.isChest(digilock.getBlock())
							|| digilock.getBlock().getType() == Material.DISPENSER
							|| digilock.getBlock().getType() == Material.FURNACE) {
						sPlayer.closeActiveWindow();
					}
					sPlayer.damage(5);
				}
				bPlayer.popupGetPincode.removeWidgets(BIT.plugin);
				bPlayer.cleanupGetPincode(sPlayer);
			} else if (BITPlayer.BITButtons.get(uuid) == "getPincodeCancel") {
				sPlayer.closeActiveWindow();
				bPlayer.popupGetPincode.removeWidgets(BIT.plugin);
				bPlayer.cleanupGetPincode(sPlayer);
			}

			// ************************************
			// Buttons in sPlayer.setPincode
			// ************************************
			else if (BITPlayer.BITButtons.get(uuid) == "setPincodeLock"
					&& G333Permissions.hasPerm(sPlayer, "digilock.create",
							G333Permissions.QUIET)) {
				if (validateSetPincodeFields(sPlayer)) {
					sPlayer.closeActiveWindow();
					sPlayer.sendMessage("Pincode:"+BITPlayer.setPincode.getText());
					BITDigiLock.SaveDigiLock(sPlayer, sBlock,
							BITPlayer.setPincode.getText(),
							BITPlayer.owner1.getText(),
							Integer.valueOf(BITPlayer.closetimer1.getText()),
							BITPlayer.listOfCoOwners.getText(), "",
							sBlock.getTypeId(), "",
							Integer.valueOf(BITPlayer.useCost1.getText()));
					bPlayer.cleanupSetPincode(sPlayer);
				}

			} else if ((BITPlayer.BITButtons.get(uuid) == "setPincodeCancel")) {
				sPlayer.closeActiveWindow();
				bPlayer.cleanupSetPincode(sPlayer);

			} else if ((BITPlayer.BITButtons.get(uuid) == "setPincodeRemove")) {
				sPlayer.closeActiveWindow();
				bPlayer.cleanupSetPincode(sPlayer);

				if (BITDigiLock.isLocked(sBlock)) {
					digilock.RemoveDigiLock(sPlayer);
				}

			} else if ((BITPlayer.BITButtons.get(uuid) == "OwnerButton")) {
				if (validateSetPincodeFields(sPlayer)) {
				}

			} else if ((BITPlayer.BITButtons.get(uuid) == "CoOwnerButton")) {
				if (validateSetPincodeFields(sPlayer)) {
				}

			} else if ((BITPlayer.BITButtons.get(uuid) == "UseCostButton")) {
				if (validateSetPincodeFields(sPlayer)) {
				}

			} else if ((BITPlayer.BITButtons.get(uuid) == "ClosetimerButton")) {
				if (validateSetPincodeFields(sPlayer)) {
				}

			}

			// ************************************
			// This only happens if I have forgot to handle a button
			// ************************************
			else {
				if (G333Config.g333Config.DEBUG_GUI)
					sPlayer.sendMessage("BITSpoutListener: Unknow button:"
							+ BITPlayer.BITButtons.get(uuid));
			}
		}
	}

	private boolean validateSetPincodeFields(SpoutPlayer sPlayer) {
		BITPlayer bPlayer = new BITPlayer(sPlayer);
		if (BITPlayer.closetimer1.getText().equals("")) {
			BITPlayer.closetimer1.setText("0");
			bPlayer.popupSetPincode.setDirty(true);
		}
		if (BITPlayer.useCost1.getText().equals("")) {
			BITPlayer.useCost1.setText("0");
			bPlayer.popupSetPincode.setDirty(true);
		}
		int closetimer = Integer.valueOf(BITPlayer.closetimer1.getText());
		int useCost = Integer.valueOf(BITPlayer.useCost1.getText());
		if (closetimer < 0) {
			G333Messages.sendNotification(sPlayer, "Closetimer must be > 0");
			BITPlayer.closetimer1.setText("0");
			bPlayer.popupSetPincode.setDirty(true);
			return false;
		} else if (closetimer > 3600) {
			G333Messages.sendNotification(sPlayer, "Closetim. must be<3600");
			BITPlayer.closetimer1.setText("3600");
			bPlayer.popupSetPincode.setDirty(true);
			return false;
		} else if (useCost > G333Config.DIGILOCK_USEMAXCOST) {
			G333Messages.sendNotification(sPlayer, "Cost must be less "
					+ G333Config.DIGILOCK_USEMAXCOST);
			BITPlayer.useCost1.setText(String
					.valueOf(G333Config.DIGILOCK_USEMAXCOST));
			bPlayer.popupSetPincode.setDirty(true);
			return false;
		} else if (useCost < 0) {
			G333Messages.sendNotification(sPlayer, "Cost must be > 0");
			BITPlayer.useCost1.setText("0");
			bPlayer.popupSetPincode.setDirty(true);
			return false;
		}

		return true;
	}
}
