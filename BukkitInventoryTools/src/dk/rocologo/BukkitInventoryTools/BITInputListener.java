package dk.rocologo.BukkitInventoryTools;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
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
		String keypressed = event.getKey().name();
		SpoutPlayer sPlayer = event.getPlayer();
		ScreenType screentype = event.getScreenType();
		if (screentype == ScreenType.CHEST_INVENTORY
				|| screentype == ScreenType.PLAYER_INVENTORY) {
			Block block = sPlayer.getTargetBlock(null, 4);
			//sPlayer.sendMessage("1) Inputlistener: eventype:" + event.getType()
				//	+ " screenType:" + event.getScreenType());
	    	// KEY_S
			if (keypressed.equals(RLConfig.rLConfig.LIBRARY_SORTKEY)) {
				
				if (BIT.isPlayer(sPlayer)) {
					if (RLPermissions.hasPerm(sPlayer, "sortinventory.use",
							RLPermissions.NOT_QUIET)) {
						if (block.getType() == Material.CHEST) {
							SpoutChest sChest = (SpoutChest) block.getState();
							RLInventory.sortInventoryItems(sPlayer,
									sChest.getLargestInventory());
							RLMessages.sendNotification(sPlayer,
									"Chest sorted.");
						} else {
							BITPlayer.sortinventory(sPlayer,
									event.getScreenType());
							RLMessages.sendNotification(sPlayer,
									"Items sorted.");
						}
					}
				}
			} else
			// KEY_M
			if (keypressed.equals(RLConfig.rLConfig.LIBRARY_MENUKEY)) {
				BITGui.openMenu(sPlayer,block);
				//InventoryOpenEvent(sPlayer,sPlayer.getInventory(),sPlayer.getInventory());
				//sPlayer.openInventoryWindow((Inventory) inv);
				
				
			}
			
		} else if (screentype==ScreenType.CUSTOM_SCREEN) {
			//  if KEY_RETURN

			// RLMessages.sendNotification(sPlayer, "Key:"+event.getKey());
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
