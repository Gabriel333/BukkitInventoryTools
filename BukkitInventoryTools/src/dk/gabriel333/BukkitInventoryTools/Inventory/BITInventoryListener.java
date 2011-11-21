package dk.gabriel333.BukkitInventoryTools.Inventory;

import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryCraftEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventoryOpenEvent;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.BukkitInventoryTools.Book.BITBook;
import dk.gabriel333.Library.BITConfig;
import dk.gabriel333.Library.BITMessages;
import dk.gabriel333.Library.BITPermissions;

public class BITInventoryListener extends InventoryListener {

	public BIT plugin;

	public BITInventoryListener(BIT plugin) {
		this.plugin = plugin;
	}

	public void onInventoryOpen(InventoryOpenEvent event) {
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		if (sPlayer.isSpoutCraftEnabled()) {
			if (BITConfig.SORT_DISPLAYSORTARCHIEVEMENT
					&& BITPermissions.hasPerm(sPlayer, "sortinventory.use",
							BITPermissions.QUIET)) {
				BITMessages.sendNotification(sPlayer, "Sort:"
						+ BITConfig.LIBRARY_SORTKEY);
			}
		}
		// CHEST_INVENTORY / BOOKSHELF_INVENTORY
		Inventory inv = event.getInventory();
		setBookNamesAndCleanup(sPlayer, inv);

		if (!inv.getName().equals(sPlayer.getInventory().getName())) {
			inv = sPlayer.getInventory();
			setBookNamesAndCleanup(sPlayer, inv);
		}
	}

	private void setBookNamesAndCleanup(SpoutPlayer sPlayer, Inventory inv) {
		short bookId = 0;
		if (inv.contains(Material.BOOK)) {
			BITBook bitBook = new BITBook();
			for (int i = 0; i < inv.getSize(); i++) {
				if (inv.getItem(i).getType() == Material.BOOK) {
					bookId = inv.getItem(i).getDurability();
					if (bookId > 1000) {
						if (BITBook.isWritten(sPlayer, bookId)) {
							bitBook = BITBook.loadBook(sPlayer, bookId);
							BITBook.setBookName(bookId, bitBook.getTitle(),
									bitBook.getAuthor());
						} else {
							BITMessages
									.showInfo("Wiping unknown BITBook in slot "
											+ i + " (Id:" + bookId
											+ ") in inventory:" + inv.getName());
							ItemStack is = inv.getItem(i);
							is.setDurability((short) 0);
						}

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

		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		// int id = sPlayer.getEntityId();
		ItemStack itemClicked = event.getItem();
		ItemStack itemHolding = event.getCursor();
		// short bookId = sPlayer.getItemInHand().getDurability();
		if (itemClicked != null) {
			if (itemClicked.getType().equals(Material.BOOK)
					&& itemClicked.getDurability() > 1000) {
				short bookId = itemClicked.getDurability();
				if (BITBook.isWritten(sPlayer, bookId)) {
					BITBook bitBook = BITBook.loadBook(sPlayer, bookId);
					BITBook.setBookName(bookId, bitBook.getTitle(),
							bitBook.getAuthor());
					if (!(bitBook.getCanBeMovedFromInventory() && (sPlayer
							.getName().equalsIgnoreCase(bitBook.getAuthor()))||
							bitBook.getCoAuthors().contains(sPlayer.getName()))) {
						event.setCancelled(true);
					}
					if (bitBook.getCopyTheBookWhenMoved()) {
						int slotNo = event.getSlot();
						ItemStack book = itemClicked;
						event.getInventory().setItem(slotNo, book);
						short nextBookId = BITBook.getNextBookId();
						BITBook newBook = bitBook;
						newBook.setBookId(nextBookId);
						itemClicked.setDurability(nextBookId);
						BITBook.bitBooks.put((short) nextBookId, bitBook);
						BITBook.saveBook(sPlayer, nextBookId);
						sPlayer.sendMessage("BIT:This Book is being copied when moved");
					}

				}
			}
		} else if (itemHolding != null) {
			if (itemHolding.getType().equals(Material.BOOK)
					&& itemHolding.getDurability() > 1000) {
				short bookId = itemHolding.getDurability();
				if (BITBook.isWritten(sPlayer, bookId)) {

				}
			}
		}
		// if (itemHolding != null && BIT.holdingKey.equals("KEY_R")) {
		// sPlayer.sendMessage("ItemClicked:" + itemHolding.getType()
		// + " bookId:" + bookId);
		// event.setCancelled(true);
		// }

	}

	public void onInventoryCraft(InventoryCraftEvent event) {
	}

	public void onCustumEvent(Event event) {
	}

}
