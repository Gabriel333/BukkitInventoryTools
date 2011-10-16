package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.event.Event;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
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
		if (sPlayer.isSpoutCraftEnabled()) {
			if (G333Config.config.SORT_DISPLAYSORTARCHIEVEMENT) {
				G333Messages.sendNotification(sPlayer, "Sort:"
						+ G333Config.config.LIBRARY_SORTKEY);
			}
		}

	}

	public void onInventoryClose(InventoryCloseEvent event) {
		//SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		//sPlayer.sendMessage("an inventory was closed:"+event.getLocation()+" evtname:"+
		//event.getEventName());
		//BITInventory.closeBitInventory(sPlayer);
	}

	public void onInventoryClick(InventoryClickEvent event) {

	}

	public void onInventoryCraft(InventoryCraftEvent event) {

	}

	public void onCustumEvent(Event event) {

	}

}
