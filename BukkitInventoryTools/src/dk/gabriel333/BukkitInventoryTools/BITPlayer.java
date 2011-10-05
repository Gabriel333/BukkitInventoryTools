package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.getspout.spout.inventory.CustomMCInventory;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import de.Keyle.MyWolf.MyWolfPlugin;
import dk.gabriel333.Library.G333Inventory;

public abstract class BITPlayer implements Player {

	public static void sortinventory(SpoutPlayer sPlayer, ScreenType screentype) {
		// sort the ordinary player inventory
		Inventory inventory = sPlayer.getInventory();
		G333Inventory.sortPlayerInventoryItems(sPlayer);

		// sort the SpoutBackpack if it exists and if it is opened.
		if (BIT.spoutbackpack && BIT.spoutBackpackHandler.isOpenSpoutBackpack(sPlayer)) {
			inventory = BIT.spoutBackpackHandler
					.getOpenedSpoutBackpack(sPlayer);
			if (inventory != null) {
				G333Inventory.sortInventoryItems(sPlayer, inventory);
			}
		}

		// sort the players MyWolfInventory if exists and if is open.
		if (BIT.mywolf) {
			// if the wolf inventory is open then {

			CustomMCInventory inv = MyWolfPlugin.getMyWolf(sPlayer).inv;

			if (inv != null) {
			// test if myWolfInventory is opened and open it
				// this on fails... can not be cast to ... Inventory
				
				// G333Inventory.sortInventoryItems(sPlayer, (Inventory) inv);
			
			 }

		}
	}
}
