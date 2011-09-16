package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.Material;
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
		// if (G333Config.g333Config.DEBUG_GUI)
		// sPlayer.sendMessage("Inputlistener, screenType:"
		// + event.getScreenType() + " targetblock:"
		// + targetblock.getType());
		// PLAYER_INVENTORY
		if (screentype == ScreenType.PLAYER_INVENTORY) {
			if (keypressed.equals(G333Config.g333Config.LIBRARY_SORTKEY)) {
				if (G333Permissions.hasPerm(sPlayer, "sortinventory.use",
						G333Permissions.NOT_QUIET)) {
					BITPlayer.sortinventory(sPlayer, event.getScreenType());
					if (G333Config.g333Config.SORT_DISPLAYSORTARCHIEVEMENT)
						G333Messages.sendNotification(sPlayer, "Items sorted.");
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
						}
						if (G333Config.g333Config.SORT_DISPLAYSORTARCHIEVEMENT)
							G333Messages.sendNotification(sPlayer,
									"Chest sorted.");
					}

				}

				// KEY M and L does not work yet :-(
				/*
				 * else if (keypressed
				 * .equals(G333Config.g333Config.LIBRARY_MENUKEY)) { if
				 * (G333Config.g333Config.DEBUG_GUI)
				 * G333Messages.sendNotification(sPlayer, "LIBRARY_MENUKEY");
				 * sPlayer.closeActiveWindow(); if (targetblock != null) {
				 * sPlayer.sendMessage("openMenu: this does not work yet.");
				 * BITGui.openMenu(sPlayer, targetblock); } }
				 */

				else if (keypressed.equals("KEY_ESCAPE")) {
					sPlayer.closeActiveWindow();
				}
			} else
			// targetblock is NOT a chest, so it must be SpoutBackPack
			{
				BITPlayer.sortinventory(sPlayer, ScreenType.CHEST_INVENTORY);
			}
		}

		// GAME_SCREEN
		else if (screentype == ScreenType.GAME_SCREEN) {
			if (keypressed.equals(G333Config.g333Config.LIBRARY_LOCKKEY)) {
				if (BITDigiLock.isLockable(targetblock)) {
					if (BITDigiLock.isLocked(targetblock)) {
						BITDigiLock digilock = BITDigiLock.loadDigiLock(
								sPlayer, targetblock);
						if ((sPlayer.getName().equals(digilock.getOwner()) && G333Permissions
								.hasPerm(sPlayer, "digilock.use",
										G333Permissions.NOT_QUIET))
								|| G333Permissions.hasPerm(sPlayer,
										"digilock.admin",
										G333Permissions.NOT_QUIET)) {
							G333Messages.sendNotification(sPlayer,
									"You are the owner");
							BITGui.setPincode(sPlayer, targetblock);
						} else {
							G333Messages.sendNotification(sPlayer,
									"Locked with Digilock");
						}
					} else {
						if (sPlayer.isSpoutCraftEnabled()) {
							if (G333Permissions.hasPerm(sPlayer,
									"digilock.use", G333Permissions.NOT_QUIET)
									|| G333Permissions.hasPerm(sPlayer,
											"digilock.admin",
											G333Permissions.NOT_QUIET)) {
								BITGui.setPincode(sPlayer, targetblock);
							}
						} else {
							sPlayer.sendMessage("Install SpoutCraft or use command /dlock to create lock.");
						}

					}
				}
			}
		}

		// CUSTOM_SCREEN
		else if (screentype == ScreenType.CUSTOM_SCREEN) {
			if (keypressed.equals("KEY_ESCAPE")) {
				sPlayer.closeActiveWindow();
			}
		} else {
			// sPlayer.sendMessage("Inputlistener, Unhandled screentype:"
			// + screentype);
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
