package dk.gabriel333.BukkitInventoryTools;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
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
			SpoutBlock sBlock = (SpoutBlock) sPlayer.getTargetBlock(null, 5);
			if (sBlock != null) {
				BITDigiLock digilock = BITDigiLock.loadDigiLock(sBlock);
				UUID uuid2 = sPlayer.getUniqueId();
				// ************************************
				// Buttons in getPincodeWindow
				// ************************************
				if (BITPlayer.BITButtons.get(uuid) == "getPincodeUnlock") {
					BIT.popupGetPincode.get(uuid2).close();
					if ((digilock.getPincode().equals(
							BIT.pincode.get(uuid2).getText()) && G333Permissions
							.hasPerm(sPlayer, "digilock.use",
									G333Permissions.QUIET))
							|| G333Permissions.hasPerm(sPlayer,
									"digilock.admin", G333Permissions.QUIET)) {
						if (BITDigiLock.isChest(digilock.getBlock())) {
							SpoutChest sChest = (SpoutChest) sBlock.getState();
							Inventory inv = sChest.getLargestInventory();
							sPlayer.openInventoryWindow(inv);

						} else if (BITDigiLock
								.isDoubleDoor(digilock.getBlock())) {
							BITDigiLock.playDigiLockSound(digilock.getBlock());
							BITDigiLock.openDoubleDoor(sPlayer,
									digilock.getBlock(), digilock.getUseCost());

						} else if (BITDigiLock.isDoor(digilock.getBlock())) {
							BITDigiLock.playDigiLockSound(digilock.getBlock());
							BITDigiLock.openDoor(sPlayer, digilock.getBlock(),
									digilock.getUseCost());

						} else if (digilock.getBlock().getType() == Material.LEVER) {
							BITDigiLock.leverOn(sPlayer, sBlock,
									digilock.getUseCost());
							BITDigiLock.playDigiLockSound(sBlock);

						} else if (digilock.getBlock().getType() == Material.STONE_BUTTON) {
							if (!BITDigiLock.isButtonOn(digilock.getBlock())) {
								BITDigiLock.pressButtonOn(sPlayer, digilock.getBlock(),
										digilock.getUseCost());
								BITDigiLock.playDigiLockSound(sBlock);
							}
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
							BITDigiLock.openTrapdoor(sPlayer,
									digilock.getBlock(), digilock.getUseCost());

						} else if (BITDigiLock.isSign(sBlock)) {
							if (sPlayer.isSpoutCraftEnabled()) {
								Sign sign = (Sign) sBlock.getState();
								sPlayer.openSignEditGUI(sign);
							} else {

							}

						}
					} else {
						G333Messages
								.sendNotification(sPlayer, "Wrong pincode!");
						if (BITDigiLock.isDoor(digilock.getBlock())) {
							BITDigiLock.closeDoor(sPlayer, digilock.getBlock(),
									0);
						} else if (BITDigiLock.isChest(digilock.getBlock())
								|| digilock.getBlock().getType() == Material.DISPENSER
								|| digilock.getBlock().getType() == Material.FURNACE) {
							sPlayer.closeActiveWindow();
						}
						sPlayer.damage(5);
					}
					BIT.popupSetPincode.get(uuid2).removeWidgets(BIT.plugin);
					bPlayer.cleanupGetPincode(sPlayer);
				} else if (BITPlayer.BITButtons.get(uuid) == "getPincodeCancel") {
					sPlayer.closeActiveWindow();
					BIT.popupSetPincode.get(uuid2).removeWidgets(BIT.plugin);
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
						BITDigiLock.SaveDigiLock(sPlayer, sBlock, BIT.pincode
								.get(uuid2).getText(), BIT.owner.get(uuid2)
								.getText(), Integer.valueOf(BIT.closetimer.get(
								uuid2).getText()), BIT.coOwners.get(uuid2)
								.getText(), "", sBlock.getTypeId(), "", Integer
								.valueOf(BIT.useCost.get(uuid2).getText()));
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
					if (G333Config.config.DEBUG_GUI)
						sPlayer.sendMessage("BITSpoutListener: Unknow button:"
								+ BITPlayer.BITButtons.get(uuid));
				}
			}
		}
	}

	private boolean validateSetPincodeFields(SpoutPlayer sPlayer) {
		UUID uuid = sPlayer.getUniqueId();
		if (BIT.closetimer.get(uuid).getText().equals("")) {
			BIT.closetimer.get(uuid).setText("0");
			BIT.popupSetPincode.get(uuid).setDirty(true);
		}
		if (BIT.useCost.get(uuid).getText().equals("")) {
			BIT.useCost.get(uuid).setText("0");
			BIT.popupSetPincode.get(uuid).setDirty(true);
		}
		int closetimer = Integer.valueOf(BIT.closetimer.get(uuid).getText());
		int useCost = Integer.valueOf(BIT.useCost.get(uuid).getText());
		if (closetimer < 0) {
			G333Messages.sendNotification(sPlayer, "Closetimer must be > 0");
			BIT.closetimer.get(uuid).setText("0");
			BIT.popupSetPincode.get(uuid).setDirty(true);
			return false;
		} else if (closetimer > 3600) {
			G333Messages.sendNotification(sPlayer, "Closetim. must be<3600");
			BIT.closetimer.get(uuid).setText("3600");
			BIT.popupSetPincode.get(uuid).setDirty(true);
			return false;
		} else if (useCost > G333Config.DIGILOCK_USEMAXCOST) {
			G333Messages.sendNotification(sPlayer, "Cost must be less "
					+ G333Config.DIGILOCK_USEMAXCOST);
			BIT.useCost.get(uuid).setText(
					String.valueOf(G333Config.DIGILOCK_USEMAXCOST));
			BIT.popupSetPincode.get(uuid).setDirty(true);
			return false;
		} else if (useCost < 0) {
			G333Messages.sendNotification(sPlayer, "Cost must be > 0");
			BIT.useCost.get(uuid).setText("0");
			BIT.popupSetPincode.get(uuid).setDirty(true);
			return false;
		}

		return true;
	}
}
