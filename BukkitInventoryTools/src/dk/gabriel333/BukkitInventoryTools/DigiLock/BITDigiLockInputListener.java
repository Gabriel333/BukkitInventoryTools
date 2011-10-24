package dk.gabriel333.BukkitInventoryTools.DigiLock;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.Lever;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.event.input.RenderDistanceChangeEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.BukkitInventoryTools.Inventory.BITInventory;
import dk.gabriel333.BukkitInventoryTools.Sort.BITSortInventory;
import dk.gabriel333.Library.*;

public class BITDigiLockInputListener extends InputListener {

	@Override
	public void onKeyPressedEvent(KeyPressedEvent event) {
		SpoutPlayer sPlayer = event.getPlayer();
		ScreenType screentype = event.getScreenType();
		String keypressed = event.getKey().name();
		if (!(keypressed.equals(G333Config.LIBRARY_SORTKEY)
				|| keypressed.equals(G333Config.LIBRARY_LOCKKEY)
				|| keypressed.equals("KEY_ESCAPE")
				|| keypressed.equals("KEY_RETURN") 
				)
					)
			return;
		SpoutBlock targetblock = (SpoutBlock) sPlayer.getTargetBlock(null, 5);

		// SpoutBackpack
		if ((BIT.spoutbackpack && BIT.spoutBackpackHandler
				.isOpenSpoutBackpack(sPlayer))
				&& screentype == ScreenType.CHEST_INVENTORY) {
			if (keypressed.equals(G333Config.LIBRARY_SORTKEY)
					&& BIT.spoutbackpack
					&& BIT.spoutBackpackHandler.isOpenSpoutBackpack(sPlayer)) {
				Inventory inventory = sPlayer.getInventory();
				inventory = BIT.spoutBackpackHandler
						.getOpenedSpoutBackpack(sPlayer);
				if (inventory != null) {
					BITSortInventory.sortInventoryItems(sPlayer, inventory);
				}
				BITSortInventory.sortPlayerInventoryItems(sPlayer);
				if (keypressed.equals(G333Config.LIBRARY_SORTKEY)) {
					if (G333Config.SORT_DISPLAYSORTARCHIEVEMENT) {
						G333Messages.sendNotification(sPlayer, "Items sorted.");
					}
				}
			}
		} else

		// PLAYER_INVENTORY
		if (screentype == ScreenType.PLAYER_INVENTORY) {
			if (keypressed.equals(G333Config.LIBRARY_SORTKEY)) {
				if (G333Permissions.hasPerm(sPlayer, "sortinventory.use",
						G333Permissions.NOT_QUIET)) {
					BITSortInventory.sortPlayerInventoryItems(sPlayer);
					if (G333Config.SORT_DISPLAYSORTARCHIEVEMENT) {
						G333Messages.sendNotification(sPlayer, "Items sorted.");
					}
				}
			}
			// Inventories connected to a block
		} else if (BITDigiLock.isLockable(targetblock)) {

			// CHEST_INVENTORY
			if (screentype == ScreenType.CHEST_INVENTORY) {

				// CHEST or DOUBLECHEST
				if (BITDigiLock.isChest(targetblock)) {
					SpoutChest sChest = (SpoutChest) targetblock.getState();
					if (keypressed.equals(G333Config.LIBRARY_SORTKEY)) {
						if (targetblock.getType() == Material.CHEST) {
							if (G333Permissions.hasPerm(sPlayer,
									"sortinventory.use",
									G333Permissions.NOT_QUIET)) {
								BITSortInventory.sortInventoryItems(sPlayer,
										sChest.getLargestInventory());
								BITSortInventory
										.sortPlayerInventoryItems(sPlayer);
								if (G333Config.SORT_DISPLAYSORTARCHIEVEMENT) {
									G333Messages.sendNotification(sPlayer,
											"Chest sorted.");
								}
							}
						}
					}

				} else if (BITDigiLock.isBookshelf(targetblock)) {

					// BOOKSHELF INVENTORY
					if (keypressed.equals(G333Config.LIBRARY_SORTKEY)) {
						if (G333Permissions.hasPerm(sPlayer,
								"sortinventory.use", G333Permissions.NOT_QUIET)) {
							BITSortInventory.sortPlayerInventoryItems(sPlayer);
							int id = sPlayer.getEntityId();
							BITInventory bitInventory = BITInventory.openedInventories
									.get(id);
							Inventory inventory = bitInventory.getInventory();
							BITSortInventory.sortInventoryItems(sPlayer,
									inventory);
							bitInventory.setInventory(targetblock,
									bitInventory.getOwner(),
									bitInventory.getName(),
									bitInventory.getCoOwners(), inventory,
									bitInventory.getUseCost());
							BITInventory
									.saveBitInventory(sPlayer, bitInventory);
							BITInventory.openedInventories.remove(id);
							BITInventory.openedInventories
									.put(id, bitInventory);
							if (G333Config.SORT_DISPLAYSORTARCHIEVEMENT) {
								G333Messages.sendNotification(sPlayer,
										"Bookshelf sorted.");
							}
						}
					} else if (keypressed.equals("KEY_ESCAPE")
							) {
						int id = sPlayer.getEntityId();
						BITInventory bitInventory = BITInventory.openedInventories
								.get(id);
						BITInventory.saveBitInventory(sPlayer, bitInventory);
						BITInventory.openedInventories.remove(id);
					}
				} else
				// targetblock is NOT a chest/Bookshelf/SpoutBackpack
				{
					if (!keypressed.equals("KEY_ESCAPE"))
						sPlayer.sendMessage("I cant sort this Inventory. Make a Ticket to Gabriel333.");
				}
			}

			// GAME_SCREEN
			else if (screentype == ScreenType.GAME_SCREEN) {
				if (keypressed.equals(G333Config.LIBRARY_LOCKKEY)) {
					if (BITDigiLock.isLocked(targetblock)
							&& !BITDigiLock.isLockable(targetblock)) {
						BITDigiLock digilock = BITDigiLock
								.loadDigiLock(targetblock);
						digilock.RemoveDigiLock(sPlayer);
						sPlayer.sendMessage("Warning: You had an DigiLock on a illegal block. The DigiLock has been removed.");
						sPlayer.sendMessage("Make a ticket and tell the developer how it happened on:");
						sPlayer.sendMessage("http://dev.bukkit.org/server-mods/bukkitinventorytools/tickets/");
					}
					if (BITDigiLock.isLockable(targetblock)) {
						if (BITDigiLock.isLocked(targetblock)) {
							BITDigiLock digilock = BITDigiLock
									.loadDigiLock(targetblock);
							if (BITDigiLock.isDoubleDoor(targetblock)) {
								BITDigiLock.closeDoubleDoor(sPlayer,
										targetblock, 0);
							} else if (BITDigiLock.isDoor(targetblock)) {
								BITDigiLock.closeDoor(sPlayer, targetblock, 0);
							} else if (BITDigiLock.isTrapdoor(targetblock)) {
								BITDigiLock.closeTrapdoor(sPlayer, targetblock);
							}
							if ((sPlayer.getName().equals(digilock.getOwner()) && G333Permissions
									.hasPerm(sPlayer, "digilock.create",
											G333Permissions.NOT_QUIET))
									|| G333Permissions.hasPerm(sPlayer,
											"digilock.admin",
											G333Permissions.NOT_QUIET)) {
								if (G333Config.DEBUG_PERMISSIONS)
									sPlayer.sendMessage(ChatColor.GREEN
											+ "1) BITInputlistener: You have permission to open a locked door/chest");
								G333Messages.sendNotification(sPlayer,
										"You are the owner");
								BITDigiLock.setPincode(sPlayer, targetblock);
							} else {
								G333Messages.sendNotification(sPlayer,
										"Locked with Digilock");
								if (G333Config.DEBUG_PERMISSIONS) {
									sPlayer.sendMessage(ChatColor.GREEN
											+ "2) BITInputlistener: You DONT have permission to open a locked door/chest");
								}
							}
						} else { // TARGETBLOCK IS NOT LOCKED
							if (sPlayer.isSpoutCraftEnabled()) {
								if (G333Permissions.hasPerm(sPlayer,
										"bookshelf.create",
										G333Permissions.NOT_QUIET)
										|| G333Permissions.hasPerm(sPlayer,
												"digilock.admin",
												G333Permissions.NOT_QUIET)) {
									if (BITDigiLock.isBookshelf(targetblock)) {
										if (!BITInventory
												.isBitInventoryCreated(targetblock)) {
											String coowners = "";
											String name = "";
											String owner = sPlayer.getName();
											int usecost = 0;
											Inventory inventory = SpoutManager
													.getInventoryBuilder()
													.construct(
															G333Config.BOOKSHELF_SIZE,
															name);
											BITInventory.saveBitInventory(
													sPlayer, targetblock,
													owner, name, coowners,
													inventory, usecost);
										}
									}

								}
								if (G333Permissions.hasPerm(sPlayer,
										"digilock.create",
										G333Permissions.NOT_QUIET)
										|| G333Permissions.hasPerm(sPlayer,
												"digilock.admin",
												G333Permissions.NOT_QUIET)) {
									if (G333Config.DEBUG_PERMISSIONS) {
										sPlayer.sendMessage(ChatColor.GREEN
												+ "3) BITInputlistener: You have permission to open a locked door/chest");
									}
									if (BITDigiLock.isDoubleDoor(targetblock)) {
										SpoutBlock leftdoor = BITDigiLock
												.getLeftDoubleDoor(targetblock);
										BITDigiLock.closeDoubleDoor(sPlayer,
												leftdoor, 0);
										BITDigiLock.setPincode(sPlayer,
												leftdoor);
									} else if (BITDigiLock.isDoor(targetblock)) {
										BITDigiLock.closeDoor(targetblock);
										BITDigiLock.setPincode(sPlayer,
												targetblock);
									} else {
										BITDigiLock.setPincode(sPlayer,
												targetblock);
									}

								}
							} else {
								sPlayer.sendMessage("Install SpoutCraft or use command /dlock to create lock.");
							}

						}
					}
				}
			}

			// FURNACE_INVENTORY SCREEN
			else if (screentype == ScreenType.FURNACE_INVENTORY) {
				if (keypressed.equals(G333Config.LIBRARY_SORTKEY)) {
					if (G333Permissions.hasPerm(sPlayer, "sortinventory.use",
							G333Permissions.NOT_QUIET)) {
						BITSortInventory.sortPlayerInventoryItems(sPlayer);
					}
					if (G333Config.SORT_DISPLAYSORTARCHIEVEMENT) {
						G333Messages.sendNotification(sPlayer,
								"Inventory sorted.");
					}
				}
			}

			// DISPENCER_INVENTORY SCREEN
			else if (screentype == ScreenType.DISPENSER_INVENTORY) {
				if (keypressed.equals(G333Config.LIBRARY_SORTKEY)) {
					if (G333Permissions.hasPerm(sPlayer, "sortinventory.use",
							G333Permissions.NOT_QUIET)) {
						if (targetblock.getType() == Material.DISPENSER) {
							Dispenser dispenser = (Dispenser) targetblock
									.getState();
							Inventory inventory = dispenser.getInventory();
							BITSortInventory.sortInventoryItems(sPlayer,
									inventory);
							BITSortInventory.sortPlayerInventoryItems(sPlayer);
						}
					}
					if (G333Config.SORT_DISPLAYSORTARCHIEVEMENT) {
						G333Messages.sendNotification(sPlayer,
								"Dispenser sorted.");
					}
				}
			}

			// CUSTOM_SCREEN
			else if (screentype == ScreenType.CUSTOM_SCREEN) {
				if (keypressed.equals("KEY_ESCAPE")||keypressed.equals("KEY_E")) {
					// TODO: the lever must swing back to off, when the
					// player press ESC. Next lines does not work. :-(
					// if (BITDigiLock.isLever(targetblock)) {
					// if ( BITDigiLock.isLeverOn(targetblock)) {
					// BITDigiLock.leverOff(sPlayer, targetblock);
					// } else {
					// BITDigiLock.leverOn(sPlayer, targetblock,0);
					// }
					// sPlayer.sendMessage("setting lever to off");
					// Lever lever = (Lever) targetblock.getState().getData();
					// lever.setPowered(false);
					// }
					if (BITDigiLock.isLever(targetblock)) {
						Lever lever = (Lever) targetblock.getState().getData();
						// lever.setPowered(false);
						targetblock.setData((byte) (lever.getData() | 8));
					}
					sPlayer.closeActiveWindow();
					BITDigiLock.cleanupPopupScreen(sPlayer);

				} else if (keypressed.equals("KEY_RETURN")) {

				}
			}
		}

		else {
			// UNHANDLED SCREENTYPE
		}

	}

	@Override
	public void onKeyReleasedEvent(KeyReleasedEvent event) {
		// SpoutPlayer sPlayer = event.getPlayer();
		// Keyboard keyUp = event.getKey();
		// event.getPlayer().sendMessage(
		// "sPlayer:" + sPlayer.getName() + "Pressed key:" + keyUp);
	}

	@Override
	public void onRenderDistanceChange(RenderDistanceChangeEvent event) {

	}

}
