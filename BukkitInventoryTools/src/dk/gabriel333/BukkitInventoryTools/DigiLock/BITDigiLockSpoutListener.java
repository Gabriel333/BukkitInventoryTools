package dk.gabriel333.BukkitInventoryTools.DigiLock;

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

import dk.gabriel333.BukkitInventoryTools.Inventory.BITInventory;
import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;
import dk.gabriel333.Library.G333Permissions;

public class BITDigiLockSpoutListener extends SpoutListener {

	public void onCustomEvent(Event event) {
		if (event instanceof ButtonClickEvent) {
			Button button = ((ButtonClickEvent) event).getButton();
			UUID uuid = button.getId();
			SpoutPlayer sPlayer = ((ButtonClickEvent) event).getPlayer();
			SpoutBlock sBlock = (SpoutBlock) sPlayer.getTargetBlock(null, 5);
			int id = sPlayer.getEntityId();
			if (BITDigiLock.isLockable(sBlock)) {
				BITDigiLock digilock = BITDigiLock.loadDigiLock(sBlock);
				// ************************************
				// Buttons in getPincodeWindow
				// ************************************
				if (BITDigiLock.BITDigiLockButtons.get(uuid) == "getPincodeUnlock") {
					BITDigiLock.popupScreen.get(id).close();
					BITDigiLock.cleanupPopupScreen(sPlayer);
					if ((digilock.getPincode().equals(
							BITDigiLock.pincodeGUI.get(id).getText()) && G333Permissions
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

						} else if (BITDigiLock.isLever(sBlock)) {
							BITDigiLock.leverOn(sPlayer, sBlock,
									digilock.getUseCost());
							BITDigiLock.playDigiLockSound(sBlock);

						} else if (BITDigiLock.isButton(sBlock)) {
							if (!BITDigiLock.isButtonOn(digilock.getBlock())) {
								BITDigiLock.pressButtonOn(sPlayer,
										digilock.getBlock(),
										digilock.getUseCost());
								BITDigiLock.playDigiLockSound(sBlock);
							}
						} else if (BITDigiLock.isDispenser(sBlock)) {
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
							BITInventory bitInventory = BITInventory
									.loadBitInventory(sPlayer, sBlock);
							bitInventory
									.openBitInventory(sPlayer, bitInventory);

						} else if (BITDigiLock.isTrapdoor(sBlock)) {
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
						if (BITDigiLock.isDoubleDoor(digilock.getBlock())) {
							BITDigiLock.closeDoubleDoor(sPlayer, digilock.getBlock(),
									0);
						} else if (BITDigiLock.isDoor(digilock.getBlock())) {
							BITDigiLock.closeDoor(sPlayer, digilock.getBlock(),
									0);
						} else if (BITDigiLock.isTrapdoor(digilock.getBlock())) {
							BITDigiLock.closeTrapdoor(sPlayer, digilock.getBlock());
						} else if (BITDigiLock.isChest(sBlock)
								|| BITDigiLock.isDispenser(sBlock)
								|| sBlock.getType() == Material.FURNACE) {
							sPlayer.closeActiveWindow();
							BITDigiLock.cleanupPopupScreen(sPlayer);
							BITDigiLock.BITDigiLockButtons.remove(uuid);
						} else if (BITDigiLock.isLever(sBlock)) {
							BITDigiLock.leverOff(sPlayer, sBlock);
						}
						sPlayer.damage(5);
					}
					BITDigiLock.cleanupPopupScreen(sPlayer);
					BITDigiLock.BITDigiLockButtons.remove(uuid);

				} else if (BITDigiLock.BITDigiLockButtons.get(uuid) == "getPincodeCancel") {
					sPlayer.closeActiveWindow();
					BITDigiLock.cleanupPopupScreen(sPlayer);
					BITDigiLock.BITDigiLockButtons.remove(uuid);
				}

				// ************************************
				// Buttons in sPlayer.setPincode
				// ************************************
				else if (BITDigiLock.BITDigiLockButtons.get(uuid) == "setPincodeLock"
						&& G333Permissions.hasPerm(sPlayer, "digilock.create",
								G333Permissions.QUIET)) {
					if (validateSetPincodeFields(sPlayer)) {
						sPlayer.closeActiveWindow();
						BITDigiLock.SaveDigiLock(sPlayer, sBlock,
								BITDigiLock.pincodeGUI.get(id).getText(),
								BITDigiLock.ownerGUI.get(id).getText(), Integer
										.valueOf(BITDigiLock.closetimerGUI.get(
												id).getText()),
								BITDigiLock.coOwnersGUI.get(id).getText(),
								sBlock.getTypeId(), "", Integer
										.valueOf(BITDigiLock.useCostGUI.get(id)
												.getText()));
						BITDigiLock.cleanupPopupScreen(sPlayer);
						BITDigiLock.BITDigiLockButtons.remove(uuid);
					}

				} else if ((BITDigiLock.BITDigiLockButtons.get(uuid) == "setPincodeCancel")) {
					sPlayer.closeActiveWindow();
					BITDigiLock.cleanupPopupScreen(sPlayer);
					BITDigiLock.BITDigiLockButtons.remove(uuid);

				} else if ((BITDigiLock.BITDigiLockButtons.get(uuid) == "setPincodeRemove")) {
					sPlayer.closeActiveWindow();
					BITDigiLock.cleanupPopupScreen(sPlayer);
					BITDigiLock.BITDigiLockButtons.remove(uuid);

					if (BITDigiLock.isLocked(sBlock)) {
						digilock.RemoveDigiLock(sPlayer);
					}

				} else if ((BITDigiLock.BITDigiLockButtons.get(uuid) == "OwnerButton")) {
					if (validateSetPincodeFields(sPlayer)) {
					}

				} else if ((BITDigiLock.BITDigiLockButtons.get(uuid) == "CoOwnerButton")) {
					if (validateSetPincodeFields(sPlayer)) {
					}

				} else if ((BITDigiLock.BITDigiLockButtons.get(uuid) == "UseCostButton")) {
					if (validateSetPincodeFields(sPlayer)) {
					}

				} else if ((BITDigiLock.BITDigiLockButtons.get(uuid) == "ClosetimerButton")) {
					if (validateSetPincodeFields(sPlayer)) {
					}

				}

				// ************************************
				// This only happens if I have forgot to handle a button
				// ************************************
				else {
					if (G333Config.DEBUG_GUI)
						sPlayer.sendMessage("BITSpoutListener: Unknow button:"
								+ BITDigiLock.BITDigiLockButtons.get(uuid));
				}
			}
		}
	}

	private boolean validateSetPincodeFields(SpoutPlayer sPlayer) {
		int id = sPlayer.getEntityId();
		if (BITDigiLock.closetimerGUI.get(id).getText().equals("")) {
			BITDigiLock.closetimerGUI.get(id).setText("0");
			BITDigiLock.popupScreen.get(id).setDirty(true);
		}
		if (BITDigiLock.useCostGUI.get(id).getText().equals("")) {
			BITDigiLock.useCostGUI.get(id).setText("0");
			BITDigiLock.popupScreen.get(id).setDirty(true);
		}
		int closetimer = Integer.valueOf(BITDigiLock.closetimerGUI.get(id)
				.getText());
		int useCost = Integer.valueOf(BITDigiLock.useCostGUI.get(id).getText());
		if (closetimer < 0) {
			G333Messages.sendNotification(sPlayer, "Closetimer must be > 0");
			BITDigiLock.closetimerGUI.get(id).setText("0");
			BITDigiLock.popupScreen.get(id).setDirty(true);
			return false;
		} else if (closetimer > 3600) {
			G333Messages.sendNotification(sPlayer, "Closetim. must be<3600");
			BITDigiLock.closetimerGUI.get(id).setText("3600");
			BITDigiLock.popupScreen.get(id).setDirty(true);
			return false;
		} else if (useCost > G333Config.DIGILOCK_USEMAXCOST) {
			G333Messages.sendNotification(sPlayer, "Cost must be less "
					+ G333Config.DIGILOCK_USEMAXCOST);
			BITDigiLock.useCostGUI.get(id).setText(
					String.valueOf(G333Config.DIGILOCK_USEMAXCOST));
			BITDigiLock.popupScreen.get(id).setDirty(true);
			return false;
		} else if (useCost < 0) {
			G333Messages.sendNotification(sPlayer, "Cost must be > 0");
			BITDigiLock.useCostGUI.get(id).setText("0");
			BITDigiLock.popupScreen.get(id).setDirty(true);
			return false;
		}

		return true;
	}
}
