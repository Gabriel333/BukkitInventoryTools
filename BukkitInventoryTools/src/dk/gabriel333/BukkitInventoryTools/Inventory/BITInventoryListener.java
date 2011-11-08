package dk.gabriel333.BukkitInventoryTools.Inventory;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.BukkitInventoryTools.Book.BITBook;
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
		Inventory inv = event.getInventory();
		short bookId = 0;
		if (inv.contains(Material.BOOK)) {
			BITBook bitBook = new BITBook();
			for (int i = 0; i < inv.getSize(); i++) {
				if (inv.getItem(i).getType() == Material.BOOK) {
					bookId = inv.getItem(i).getDurability();
					if (bookId > 1000) {
						bitBook = BITBook.loadBook(sPlayer, bookId);
						BITBook.setBookName(bookId, bitBook.getTitle());
					}
				}
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
		
		//SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		//ItemStack itemClicked = event.getItem();
		//ItemStack itemHolding = event.getCursor();
		//short bookId = sPlayer.getItemInHand().getDurability();
		//if (itemHolding != null && BIT.holdingKey.equals("KEY_R")) {
		//	sPlayer.sendMessage("ItemClicked:" + itemHolding.getType()
		//			+ " bookId:" + bookId);
		//	event.setCancelled(true);
		//}
		
	}

	public void onInventoryCraft(InventoryCraftEvent event) {
	}

	public void onCustumEvent(Event event) {
	}

}
