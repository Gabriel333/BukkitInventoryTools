package dk.rocologo.BukkitInventoryTools;

import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.rocologo.Library.RLConfig;
import dk.rocologo.Library.RLMessages;

public class BITInventoryListener extends InventoryListener {

	public BIT plugin;

	public BITInventoryListener(BIT plugin) {
		this.plugin = plugin;
	}

	public void onInventoryOpen(InventoryOpenEvent event) {
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		RLMessages.sendNotification(sPlayer, RLConfig.rLConfig.LIBRARY_SORTKEY
				+ ":Sort " + RLConfig.rLConfig.LIBRARY_MENUKEY + ":Menu");

	}

	public void onInventoryClose(InventoryCloseEvent event) {
		
	}

}
