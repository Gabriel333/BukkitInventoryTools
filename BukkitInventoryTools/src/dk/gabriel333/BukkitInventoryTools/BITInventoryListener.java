package dk.gabriel333.BukkitInventoryTools;

import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;

public class BITInventoryListener extends InventoryListener {

	public BIT plugin;

	public BITInventoryListener(BIT plugin) {
		this.plugin = plugin;
	}

	public void onInventoryOpen(InventoryOpenEvent event) {
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		G333Messages.sendNotification(sPlayer, "Sort:"
				+ G333Config.g333Config.LIBRARY_SORTKEY);
		// + G333Config.g333Config.LIBRARY_MENUKEY + ":Menu"

	}

	public void onInventoryClose(InventoryCloseEvent event) {

	}

}
