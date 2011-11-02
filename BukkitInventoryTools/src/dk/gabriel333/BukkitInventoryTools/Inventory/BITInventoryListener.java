package dk.gabriel333.BukkitInventoryTools.Inventory;

import org.bukkit.event.Event;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
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
			if (G333Config.SORT_DISPLAYSORTARCHIEVEMENT) {
				G333Messages.sendNotification(sPlayer, "Sort:"
						+ G333Config.LIBRARY_SORTKEY);
			}
		}
	}

	public void onInventoryClose(InventoryCloseEvent event) {
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		if (event.getInventory().getName().equals("Bookshelf")) {
			int id = sPlayer.getEntityId();
			BITInventory bitInventory = BITInventory.openedInventories.get(id);
			BITInventory.saveBitInventory(sPlayer, bitInventory);
			BITInventory.openedInventories.remove(id);
		}
	}

	public void onInventoryClick(InventoryClickEvent event) {
		/*
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		ItemStack itemClicked = event.getItem();
		ItemStack itemHolding = event.getCursor();
		short bookId = sPlayer.getItemInHand().getDurability();
		if (itemClicked != null && BIT.holdingKey.equals("KEY_LCONTROL")) {
			sPlayer.sendMessage("(1)ItemClicked:" + itemClicked.getType()
					+ " bookId:" + bookId);
		}
		if (itemHolding != null && BIT.holdingKey.equals("KEY_LCONTROL")) {
			sPlayer.sendMessage("(2)ItemClicked:" + itemHolding.getType()
					+ " bookId:" + bookId);
		}
		if (itemClicked != null) {
			sPlayer.sendMessage("(3)ItemClicked:" + itemClicked.getType()
					+ "id:" + itemClicked.getDurability());

		}
		if (itemHolding != null) {
			sPlayer.sendMessage("(4)itemHolding:" + itemHolding.getType()
					+ " id:" + itemHolding.getDurability());
		}
		*/
	}

	public void onInventoryCraft(InventoryCraftEvent event) {

	}

	public void onCustumEvent(Event event) {

	}

}
