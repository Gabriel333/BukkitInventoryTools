package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.Material;
import org.bukkit.block.Block;
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
		Block targetblock = sPlayer.getTargetBlock(null, 4);
		if (G333Config.g333Config.DEBUG_GUI)
			sPlayer.sendMessage("Inputlistener, screenType:"
					+ event.getScreenType() + " targetblock:"
					+ targetblock.getType());
		if (screentype == ScreenType.PLAYER_INVENTORY) {
			if (keypressed.equals(G333Config.g333Config.LIBRARY_SORTKEY)) {
				if (G333Permissions.hasPerm(sPlayer, "sortinventory.use",
						G333Permissions.NOT_QUIET)) {
					BITPlayer.sortinventory(sPlayer, event.getScreenType());
					G333Messages.sendNotification(sPlayer, "Items sorted.");
				}
			}
		} else if (screentype == ScreenType.CHEST_INVENTORY) {
			SpoutChest sChest = (SpoutChest) targetblock.getState();
			if (keypressed.equals(G333Config.g333Config.LIBRARY_SORTKEY)) {
				if (targetblock.getType() == Material.CHEST) {
					G333Inventory.sortInventoryItems(sPlayer,
							sChest.getLargestInventory());
					G333Messages.sendNotification(sPlayer, "Chest sorted.");
				}
			// KEY M does not work yet :-(	
			} else if (keypressed.equals(G333Config.g333Config.LIBRARY_MENUKEY)) {
				if (G333Config.g333Config.DEBUG_GUI)
					G333Messages.sendNotification(sPlayer, "LIBRARY_MENUKEY");
				sPlayer.sendMessage("CloseActiveWindow");
				sPlayer.closeActiveWindow();
				if (targetblock != null) {
					sPlayer.sendMessage("openMenu");
					BITGui.openMenu(sPlayer, targetblock);
				}
			} else if (keypressed.equals("KEY_ESCAPE")) {
				if (G333Config.g333Config.DEBUG_GUI) sPlayer.sendMessage("CloseActiveWindow");
				sPlayer.closeActiveWindow();
			}
		} else if (screentype == ScreenType.GAME_SCREEN) {
			if (G333Config.g333Config.DEBUG_GUI)
				G333Messages
						.sendNotification(sPlayer, "GAME_SCR:" + keypressed);
			if (keypressed.equals(G333Config.g333Config.LIBRARY_LOCKKEY)) {
				if (targetblock != null)
					if (targetblock.getType() == Material.CHEST) {
						
						if (!BITDigiLock.isLocked(sPlayer, targetblock)) {
							if (G333Config.g333Config.DEBUG_GUI) sPlayer.sendMessage("openMenu");
							BITGui.openMenu(sPlayer, targetblock);
						}
					}
			}
		} else if (screentype == ScreenType.CUSTOM_SCREEN) {
			if (G333Config.g333Config.DEBUG_GUI)
				G333Messages.sendNotification(sPlayer,
						"CUSTOMSCR:" + event.getKey());
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
