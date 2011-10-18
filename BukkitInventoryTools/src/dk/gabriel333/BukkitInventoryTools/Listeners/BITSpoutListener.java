package dk.gabriel333.BukkitInventoryTools.Listeners;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BITDigiLock;
import dk.gabriel333.BukkitInventoryTools.Inventory.BITInventory;
import dk.gabriel333.BukkitInventoryTools.Player.BITPlayer;
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
			int id = sPlayer.getEntityId();
			if (BITDigiLock.isLockable(sBlock)) {
				BITDigiLock digilock = BITDigiLock.loadDigiLock(sBlock);
				// ************************************
				// Buttons in getPincodeWindow
				// ************************************
				if (BITPlayer.BITButtons.get(uuid) == "getPincodeUnlock") {
					BITPlayer.popupScreen.get(id).close();
					bPlayer.cleanupPopupScreen(sPlayer);
					if ((digilock.getPincode().equals(
							BITPlayer.pincode.get(id).getText()) && G333Permissions
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
							BITInventory bitInventory = BITInventory.loadBitInventory(sPlayer, sBlock);
							bitInventory.openBitInventory(sPlayer,bitInventory);
							//Inventory inv = BITInventory.loadBitInventory(sPlayer, sBlock).inventory;
							//sPlayer.openInventoryWindow(inv);

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
						if (BITDigiLock.isDoor(digilock.getBlock())) {
							BITDigiLock.closeDoor(sPlayer, digilock.getBlock(),
									0);
						} else if (BITDigiLock.isChest(sBlock)
								|| BITDigiLock.isDispenser(sBlock)
								|| sBlock.getType() == Material.FURNACE) {
							sPlayer.closeActiveWindow();
							bPlayer.cleanupPopupScreen(sPlayer);
							BITPlayer.BITButtons.remove(uuid);
						} else if (BITDigiLock.isLever(sBlock)) {
							BITDigiLock.leverOff(sPlayer, sBlock);
						}
						sPlayer.damage(5);
					}
					bPlayer.cleanupPopupScreen(sPlayer);
					//BITPlayer.menuButtons.remove(BITPlayer.BITButtons.get(uuid));
					BITPlayer.BITButtons.remove(uuid);

				} else if (BITPlayer.BITButtons.get(uuid) == "getPincodeCancel") {
					sPlayer.closeActiveWindow();
					bPlayer.cleanupPopupScreen(sPlayer);
					//BITPlayer.menuButtons.remove(BITPlayer.BITButtons.get(uuid));
					BITPlayer.BITButtons.remove(uuid);
				}

				// ************************************
				// Buttons in sPlayer.setPincode
				// ************************************
				else if (BITPlayer.BITButtons.get(uuid) == "setPincodeLock"
						&& G333Permissions.hasPerm(sPlayer, "digilock.create",
								G333Permissions.QUIET)) {
					if (validateSetPincodeFields(sPlayer)) {
						sPlayer.closeActiveWindow();
						BITDigiLock
								.SaveDigiLock(sPlayer, sBlock,
										BITPlayer.pincode.get(id).getText(),
										BITPlayer.owner.get(id).getText(), Integer
												.valueOf(BITPlayer.closetimer.get(id)
														.getText()),
										BITPlayer.coOwners.get(id).getText(), 
										sBlock.getTypeId(), "", Integer
												.valueOf(BITPlayer.useCost.get(id)
														.getText()));
						bPlayer.cleanupPopupScreen(sPlayer);
						//BITPlayer.menuButtons.remove(BITPlayer.BITButtons.get(uuid));
						BITPlayer.BITButtons.remove(uuid);
					}

				} else if ((BITPlayer.BITButtons.get(uuid) == "setPincodeCancel")) {
					sPlayer.closeActiveWindow();
					bPlayer.cleanupPopupScreen(sPlayer);
					//BITPlayer.menuButtons.remove(BITPlayer.BITButtons.get(uuid));
					BITPlayer.BITButtons.remove(uuid);
					
				} else if ((BITPlayer.BITButtons.get(uuid) == "setPincodeRemove")) {
					sPlayer.closeActiveWindow();
					bPlayer.cleanupPopupScreen(sPlayer);
					//BITPlayer.menuButtons.remove(BITPlayer.BITButtons.get(uuid));
					BITPlayer.BITButtons.remove(uuid);

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
				
				else if ((BITPlayer.BITButtons.get(uuid) == "CreateBookshelfButton")) {
					if (validateSetPincodeFields(sPlayer)) {
						String coowners = "";
						String name = "";
						String owner = sPlayer.getName();
						int usecost = 0;
						Inventory inventory = SpoutManager.getInventoryBuilder()
								.construct(G333Config.BOOKSHELF_SIZE, name);
						BITInventory.saveBitInventory(sPlayer, sBlock, owner, name,
								coowners, inventory, usecost);
						sPlayer.closeActiveWindow();
						bPlayer.cleanupPopupScreen(sPlayer);
					}

				}
				
				else if ((BITPlayer.BITButtons.get(uuid) == "removeBookshelfButton")) {
					if (validateSetPincodeFields(sPlayer)) {
					}

				}

				// ************************************
				// This only happens if I have forgot to handle a button
				// ************************************
				else {
					if (G333Config.DEBUG_GUI)
						sPlayer.sendMessage("BITSpoutListener: Unknow button:"
								+ BITPlayer.BITButtons.get(uuid));
				}
			}
		}
	}

	private boolean validateSetPincodeFields(SpoutPlayer sPlayer) {
		int id = sPlayer.getEntityId();
		if (BITPlayer.closetimer.get(id).getText().equals("")) {
			BITPlayer.closetimer.get(id).setText("0");
			BITPlayer.popupScreen.get(id).setDirty(true);
		}
		if (BITPlayer.useCost.get(id).getText().equals("")) {
			BITPlayer.useCost.get(id).setText("0");
			BITPlayer.popupScreen.get(id).setDirty(true);
		}
		int closetimer = Integer.valueOf(BITPlayer.closetimer.get(id).getText());
		int useCost = Integer.valueOf(BITPlayer.useCost.get(id).getText());
		if (closetimer < 0) {
			G333Messages.sendNotification(sPlayer, "Closetimer must be > 0");
			BITPlayer.closetimer.get(id).setText("0");
			BITPlayer.popupScreen.get(id).setDirty(true);
			return false;
		} else if (closetimer > 3600) {
			G333Messages.sendNotification(sPlayer, "Closetim. must be<3600");
			BITPlayer.closetimer.get(id).setText("3600");
			BITPlayer.popupScreen.get(id).setDirty(true);
			return false;
		} else if (useCost > G333Config.DIGILOCK_USEMAXCOST) {
			G333Messages.sendNotification(sPlayer, "Cost must be less "
					+ G333Config.DIGILOCK_USEMAXCOST);
			BITPlayer.useCost.get(id).setText(
					String.valueOf(G333Config.DIGILOCK_USEMAXCOST));
			BITPlayer.popupScreen.get(id).setDirty(true);
			return false;
		} else if (useCost < 0) {
			G333Messages.sendNotification(sPlayer, "Cost must be > 0");
			BITPlayer.useCost.get(id).setText("0");
			BITPlayer.popupScreen.get(id).setDirty(true);
			return false;
		}

		return true;
	}
}
