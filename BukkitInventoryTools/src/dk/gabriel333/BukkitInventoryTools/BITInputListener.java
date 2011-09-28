package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.Door;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.event.input.RenderDistanceChangeEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.*;

public class BITInputListener extends InputListener {

	@Override
	public void onKeyPressedEvent(KeyPressedEvent event) {
		SpoutPlayer sPlayer = event.getPlayer();
		ScreenType screentype = event.getScreenType();
		String keypressed = event.getKey().name();
		SpoutBlock targetblock = (SpoutBlock) sPlayer.getTargetBlock(null, 4);
		// PLAYER_INVENTORY
		if (screentype == ScreenType.PLAYER_INVENTORY) {
			if (keypressed.equals(G333Config.g333Config.LIBRARY_SORTKEY)) {
				if (G333Permissions.hasPerm(sPlayer, "sortinventory.use",
						G333Permissions.NOT_QUIET)) {
					BITPlayer.sortinventory(sPlayer, event.getScreenType());
					if (G333Config.g333Config.SORT_DISPLAYSORTARCHIEVEMENT) {
						G333Messages.sendNotification(sPlayer, "Items sorted.");
					}
				}
			}
		}

		// CHEST_INVENTORY
		else if (screentype == ScreenType.CHEST_INVENTORY) {
			// CHEST or DOUBLECHEST
			if (BITDigiLock.isChest(targetblock)) {
				SpoutChest sChest = (SpoutChest) targetblock.getState();
				if (keypressed.equals(G333Config.g333Config.LIBRARY_SORTKEY)) {
					if (targetblock.getType() == Material.CHEST) {
						if (G333Permissions.hasPerm(sPlayer,
								"sortinventory.use", G333Permissions.NOT_QUIET)) {
							G333Inventory.sortInventoryItems(sPlayer,
									sChest.getLargestInventory());
							G333Inventory.sortPlayerInventoryItems(sPlayer);
						}
						if (G333Config.g333Config.SORT_DISPLAYSORTARCHIEVEMENT) {
							G333Messages.sendNotification(sPlayer, "Chest sorted.");
						}
					}
				} else if (keypressed.equals("KEY_ESCAPE")) {

					// sPlayer.closeActiveWindow();
				}
			} else
			// targetblock is NOT a chest, so it must be SpoutBackPack
			{
				if (keypressed.equals(G333Config.g333Config.LIBRARY_SORTKEY)) {
					if (G333Config.g333Config.SORT_DISPLAYSORTARCHIEVEMENT) {
						G333Messages.sendNotification(sPlayer, "Items sorted.");
					}
					BITPlayer
							.sortinventory(sPlayer, ScreenType.CHEST_INVENTORY);
				}
			}
		}

		// GAME_SCREEN
		else if (screentype == ScreenType.GAME_SCREEN) {
			if (keypressed.equals(G333Config.g333Config.LIBRARY_LOCKKEY)) {
				if (BITDigiLock.isLocked(targetblock)
						&& !BITDigiLock.isLockable(targetblock)) {
					BITDigiLock digilock = BITDigiLock.loadDigiLock(sPlayer,
							targetblock);
					digilock.RemoveDigiLock(sPlayer);
					sPlayer.sendMessage("Warning: You had an DigiLock on a illegal block. The DigiLock has been removed.");
					sPlayer.sendMessage("Make a ticket and tell the developer how it happened on:");
					sPlayer.sendMessage("http://dev.bukkit.org/server-mods/bukkitinventorytools/tickets/");
				}
				if (BITDigiLock.isLockable(targetblock)) {
					if (BITDigiLock.isLocked(targetblock)) {
						BITDigiLock digilock = BITDigiLock.loadDigiLock(
								sPlayer, targetblock);
						if (BITDigiLock.isDoor(targetblock)) {
							BITDigiLock.closeDoor(sPlayer, targetblock);
						}
						if ((sPlayer.getName().equals(digilock.getOwner()) && G333Permissions
								.hasPerm(sPlayer, "digilock.create",
										G333Permissions.NOT_QUIET))
								|| G333Permissions.hasPerm(sPlayer,
										"digilock.admin",
										G333Permissions.NOT_QUIET)) {
							if (G333Config.g333Config.DEBUG_PERMISSIONS)
								sPlayer.sendMessage(ChatColor.GREEN
										+ "1) BITInputlistener: You have permission to open a locked door/chest");
							G333Messages.sendNotification(sPlayer,
									"You are the owner");
							BITGui.setPincode(sPlayer, targetblock);
						} else {
							G333Messages.sendNotification(sPlayer,
									"Locked with Digilock");
							if (G333Config.g333Config.DEBUG_PERMISSIONS) {
								sPlayer.sendMessage(ChatColor.GREEN
										+ "2) BITInputlistener: You DONT have permission to open a locked door/chest");
							}
						}
					} else {
						// TODO: REMEMBER TO REMOVE GABRIEL3333
						// ----------------------------------------------------------
						if (!BITDigiLock.isDoubleDoor(targetblock)) {
							if (sPlayer.isSpoutCraftEnabled()) {
								if (G333Permissions.hasPerm(sPlayer,
										"digilock.use",
										G333Permissions.NOT_QUIET)
										|| G333Permissions.hasPerm(sPlayer,
												"digilock.admin",
												G333Permissions.NOT_QUIET)) {
									if (G333Config.g333Config.DEBUG_PERMISSIONS) {
										sPlayer.sendMessage(ChatColor.GREEN
												+ "3) BITInputlistener: You have permission to open a locked door/chest");
									}
									BITGui.setPincode(sPlayer, targetblock);
								}
							} else {
								if (G333Config.g333Config.DEBUG_PERMISSIONS) {
									sPlayer.sendMessage(ChatColor.GREEN
											+ "4) BITInputlistener: You DONT have permission to open a locked door/chest");
								}
								sPlayer.sendMessage("Install SpoutCraft or use command /dlock to create lock.");
							}
						} else {
							G333Messages.sendNotification(sPlayer,
									"Doubledoors under implementation. :-)");
							Door door = (Door) targetblock.getState().getData();
							sPlayer.sendMessage("Facing:"+door.getFacing()+" Hinge:"+door.getHingeCorner());
							// left door:NORTH,NORTH_EAST Right door:WEST,NORTH_WEST
							// left door:EAST,SOUTH_EAST Right door: NORTH,NORTH_EAST
							// left door:SOUTH,SOUTH_WEST Right door:EAST,SOUTH_EAST
							// left door:WEST,NORTH_WESTRight door:SOUTH,SOUTH_WEST
							
						}
					}
				}
			}
		}

		// FURNACE_INVENTORY SCREEN
		else if (screentype == ScreenType.FURNACE_INVENTORY) {
			if (keypressed.equals(G333Config.g333Config.LIBRARY_SORTKEY)) {
				if (G333Permissions.hasPerm(sPlayer, "sortinventory.use",
						G333Permissions.NOT_QUIET)) {
					G333Inventory.sortPlayerInventoryItems(sPlayer);
				}
				if (G333Config.g333Config.SORT_DISPLAYSORTARCHIEVEMENT) {
					G333Messages.sendNotification(sPlayer, "Inventory sorted.");
				}
			} else if (keypressed.equals("KEY_ESCAPE")) {
				// sPlayer.closeActiveWindow();
			}
		}
		// DISPENCER_INVENTORY SCREEN
		else if (screentype == ScreenType.DISPENSER_INVENTORY) {
			Dispenser dispenser = (Dispenser) targetblock.getState();
			Inventory inventory = dispenser.getInventory();
			if (keypressed.equals(G333Config.g333Config.LIBRARY_SORTKEY)) {
				if (G333Permissions.hasPerm(sPlayer, "sortinventory.use",
						G333Permissions.NOT_QUIET)) {
					G333Inventory.sortInventoryItems(sPlayer, inventory);
					G333Inventory.sortPlayerInventoryItems(sPlayer);
				}
				if (G333Config.g333Config.SORT_DISPLAYSORTARCHIEVEMENT) {
					G333Messages.sendNotification(sPlayer, "Inventory sorted.");
				}
			} else if (keypressed.equals("KEY_ESCAPE")) {
				// sPlayer.closeActiveWindow();
			}
		}

		// CUSTOM_SCREEN
		else if (screentype == ScreenType.CUSTOM_SCREEN) {
			if (keypressed.equals("KEY_ESCAPE")) {
				sPlayer.closeActiveWindow();
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

	// The two key events pass the type of screen, the player, and the key.
	// The screen types are:
	// GAME_SCREEN
	// CHAT_SCREEN
	// CUSTOM_SCREEN
	// PLAYER_INVENTORY
	// CHEST_INVENTORY
	// DISPENSER_INVENTORY
	// FURNACE_INVENTORY
	// INGAME_MENU
	// OPTIONS_MENU
	// VIDEO_SETTINGS_MENU
	// CONTROLS_MENU
	// ACHIEVEMENTS_SCREEN
	// STATISTICS_SCREEN
	// WORKBENCH_INVENTORY
	// SIGN_SCREEN
	// GAME_OVER_SCREEN
	// SLEEP_SCREEN
	// UNKNOWN

}
