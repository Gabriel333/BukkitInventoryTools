package dk.rocologo.BukkitInventoryTools;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.gui.Button;
//import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.rocologo.Library.RLInventory;
import dk.rocologo.Library.RLMessages;

//import dk.gabriel333.SortInventory.*;

public class BITSpoutListener extends SpoutListener {
	public static BIT plugin;

	public void SortInventoryMenu(BIT plugin) {
		BITInventoryMenu.plugin = plugin;
	}

	public void onCustomEvent(Event event) {

		//ScreenType screentype = event.getScreenType();
		
		if (event instanceof ButtonClickEvent) {
			// We are going to need some other way to differentiate button
			// events
			Button button = ((ButtonClickEvent) event).getButton();
			UUID uuid = button.getId();
			SpoutPlayer sPlayer = ((ButtonClickEvent) event).getPlayer();

			if (BITInventoryMenu.sortInventoryMenuButtons.get(uuid) == "Sort") {
				sPlayer.sendMessage("Sort button");
				Block targetblock = sPlayer.getTargetBlock(null, 4);
				if (targetblock.getType() == Material.CHEST) {
					SpoutChest sChest = (SpoutChest) targetblock
							.getState();
					RLInventory.sortInventoryItems(sPlayer,
							sChest.getLargestInventory());
					RLMessages.sendNotification(sPlayer,
							"Chest sorted.");
				} else {
					//SortPlayerInventory.sortinventory(sPlayer,
					//		event.getScreenType());
					RLMessages.sendNotification(sPlayer,
							"Items sorted.");
				}
				
				
				
			}
			if (BITInventoryMenu.sortInventoryMenuButtons.get(uuid) == "Lock") {
				sPlayer.sendMessage("Lock button");
			}
			
			if (BITInventoryMenu.sortInventoryMenuButtons.get(uuid) == "Close") {
				sPlayer.sendMessage("Close button");
				BITInventoryMenu.popup.close();
			}
		}
	}
}
