package dk.rocologo.BukkitInventoryTools;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;
import org.getspout.spoutapi.event.input.RenderDistanceChangeEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.rocologo.Library.*;

public class BITInputListener extends InputListener {

	@Override
	public void onKeyPressedEvent(KeyPressedEvent event) {
		SpoutPlayer sPlayer = event.getPlayer();
		if (!BIT.isPlayer(sPlayer)) {
			return;
		} else if (!RLPermissions.hasPerm(sPlayer, "sortinventory.use",
				RLPermissions.NOT_QUIET)) {
			return;
		} else {
			String keypressed = event.getKey().name();
			ScreenType screentype = event.getScreenType();
			sPlayer.sendMessage("Inputlistener, screenType:" + event.getScreenType());
			if (screentype == ScreenType.PLAYER_INVENTORY) {
				if (keypressed.equals(RLConfig.rLConfig.LIBRARY_SORTKEY)) {
					BITPlayer.sortinventory(sPlayer, event.getScreenType());
					RLMessages.sendNotification(sPlayer, "Items sorted.");
				}
			} else if (screentype == ScreenType.CHEST_INVENTORY) {
				Block targetblock = sPlayer.getTargetBlock(null, 4);
				SpoutChest sChest = (SpoutChest) targetblock.getState();
				if (keypressed.equals(RLConfig.rLConfig.LIBRARY_SORTKEY)) {
					if (targetblock.getType() == Material.CHEST) {
						RLInventory.sortInventoryItems(sPlayer,
								sChest.getLargestInventory());
						RLMessages.sendNotification(sPlayer, "Chest sorted.");
					}
				} else
				if (keypressed.equals(RLConfig.rLConfig.LIBRARY_MENUKEY)) {
					sPlayer.sendMessage("Inputlistener: You pressed the menu key");
					sPlayer.sendMessage("Inputlistener, screenType:" + event.getScreenType());
	// // CLOSEWINDOW????
					// virker ikke - sPlayer.closeActiveWindow();
					BITGui.openMenu(sPlayer, targetblock, screentype);
				}
			} else if (screentype == ScreenType.CUSTOM_SCREEN) {
				RLMessages.sendNotification(sPlayer,
						"CUSTOMSCREEN Key:" + event.getKey());
			} else {
				sPlayer.sendMessage("Inputlistener, Unhandled screentype:"+screentype);
			}
				

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
