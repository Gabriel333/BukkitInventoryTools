package dk.gabriel333.BukkitInventoryTools.Book;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.BukkitInventoryTools.DigiLock.BITDigiLock;
import dk.gabriel333.BukkitInventoryTools.Inventory.BITInventory;
import dk.gabriel333.Library.*;

public class BITBookInputListener extends InputListener {

	@Override
	public void onKeyPressedEvent(KeyPressedEvent event) {
		SpoutPlayer sPlayer = event.getPlayer();
		ScreenType screentype = event.getScreenType();
		String keypressed = event.getKey().name();
		if (G333Config.DEBUG_EVENTS) {
			sPlayer.sendMessage("BITBookInputListn.key:" + keypressed
					+ " Screentype:" + screentype);
		}
		if (!(keypressed.equals(G333Config.LIBRARY_READKEY)
				|| keypressed.equals("KEY_ESCAPE") || keypressed
					.equals("KEY_RETURN")))
			return;
		SpoutBlock sBlock = (SpoutBlock) sPlayer.getTargetBlock(null, 5);
		ItemStack itemInHand = sPlayer.getInventory().getItemInHand();
		int id = sPlayer.getEntityId();

		// PLAYER_INVENTORY
		if (screentype == ScreenType.PLAYER_INVENTORY) {
			if (keypressed.equals(G333Config.LIBRARY_READKEY)) {

				handleItemInHand(sPlayer, sBlock, itemInHand);
			}

		} else
		// SpoutBackpack
		if ((BIT.spoutbackpack && BIT.spoutBackpackHandler
				.isOpenSpoutBackpack(sPlayer))
				&& screentype == ScreenType.CHEST_INVENTORY) {
			if (keypressed.equals(G333Config.LIBRARY_READKEY)
					&& BIT.spoutbackpack
					&& BIT.spoutBackpackHandler.isOpenSpoutBackpack(sPlayer)) {
				// TODO: handle readBook in a SpoutBackPack
			}
		} else
		// Inventories connected to a block
	
			if (BITBook.isWriteable(itemInHand.getType())) {

			// CHEST_INVENTORY
			if (screentype == ScreenType.CHEST_INVENTORY) {

				// CHEST or DOUBLECHEST
				if (BITDigiLock.isChest(sBlock)) {
					// SpoutChest sChest = (SpoutChest) sBlock.getState();
					if (keypressed.equals(G333Config.LIBRARY_READKEY)) {
						if (sBlock.getType() == Material.CHEST) {
							if (G333Permissions.hasPerm(sPlayer, "book.use",
									G333Permissions.NOT_QUIET)) {

								sPlayer.sendMessage("ItemInHand"
										+ sPlayer.getItemInHand());

							}
						}
					}

				} else

				// BOOKSHELF INVENTORY
				if (BITDigiLock.isBookshelf(sBlock)) {
					if (keypressed.equals(G333Config.LIBRARY_READKEY)) {
						if (G333Permissions.hasPerm(sPlayer, "book.use",
								G333Permissions.NOT_QUIET)) {
				/*			BITSortInventory.sortPlayerInventoryItems(sPlayer);
							BITInventory bitInventory = BITInventory.openedInventories
									.get(id);
							Inventory inventory = bitInventory.getInventory();
							//BITSortInventory.sortInventoryItems(sPlayer,
									inventory);
							bitInventory.setInventory(sBlock,
									bitInventory.getOwner(),
									bitInventory.getName(),
									bitInventory.getCoOwners(), inventory,
									bitInventory.getUseCost());
							BITInventory
									.saveBitInventory(sPlayer, bitInventory);
							BITInventory.openedInventories.remove(id);
							BITInventory.openedInventories
									.put(id, bitInventory);
									*/
							handleItemInHand(sPlayer, sBlock, itemInHand);

						}
					} else if (keypressed.equals("KEY_ESCAPE")) {
						BITInventory bitInventory = BITInventory.openedInventories
								.get(id);
						BITInventory.saveBitInventory(sPlayer, bitInventory);
						BITInventory.openedInventories.remove(id);
						sPlayer.closeActiveWindow();
						BITBook.cleanupPopupScreen(sPlayer);
						BITBook.bitBooks.remove(BITBook.currentBookId.get(id));
						BITBook.currentBookId.put(id, (short) 0);

					}
				} 
			}

			// GAME_SCREEN
			else if (screentype == ScreenType.GAME_SCREEN) {
				if (keypressed.equals(G333Config.LIBRARY_READKEY)
						&& BITBook.isWriteable(itemInHand.getType())
						&& itemInHand.getAmount() == 1) {

					// sBlock = null;
					handleItemInHand(sPlayer, sBlock, itemInHand);

				}
			}

			// FURNACE_INVENTORY SCREEN
			else if (screentype == ScreenType.FURNACE_INVENTORY) {
				if (keypressed.equals(G333Config.LIBRARY_READKEY)) {
					if (G333Permissions.hasPerm(sPlayer, "book.use",
							G333Permissions.NOT_QUIET)) {
						// TODO: Handle the Book

						sPlayer.sendMessage("ItemInHand"
								+ sPlayer.getItemInHand());

					}
				}
			}

			// DISPENCER_INVENTORY SCREEN
			else if (screentype == ScreenType.DISPENSER_INVENTORY) {
				if (keypressed.equals(G333Config.LIBRARY_READKEY)) {
					if (G333Permissions.hasPerm(sPlayer, "book.use",
							G333Permissions.NOT_QUIET)) {
						if (sBlock.getType() == Material.DISPENSER) {
							// Dispenser dispenser = (Dispenser)
							// sBlock.getState();
							// Inventory inventory = dispenser.getInventory();

							// TODO: Handle the Book
							sPlayer.sendMessage("ItemInHand"
									+ sPlayer.getItemInHand());
						}
					}

				}
			}

			// CUSTOM_SCREEN
			else if (screentype == ScreenType.CUSTOM_SCREEN) {
				// G333Messages.sendNotification(sPlayer, "Key:"+keypressed);
				if (keypressed.equals("KEY_ESCAPE")) {
					sPlayer.closeActiveWindow();
					BITBook.cleanupPopupScreen(sPlayer);
					BITBook.bitBooks.remove(BITBook.currentBookId.get(id));
					BITBook.currentBookId.put(id, (short) 0);

					// sPlayer.sendMessage("You pressed escape - dropping current book");
					// BITBook.bitBooks.remove(BITBook.currentBookId.get(id));
					// BITBook.currentBookId.put(id, 0);

				}
			}
		} else {
			// UNHANDLED SCREENTYPE
		}
	}

	private void handleItemInHand(SpoutPlayer sPlayer, SpoutBlock sBlock,
			ItemStack itemInHand) {
		if (BITBook.isWriteable(itemInHand.getType())
				&& itemInHand.getAmount() == 1) {

			short bookId =itemInHand.getDurability(); 
			BITBook bitBook = new BITBook();
			if (bookId>0) {
				if (G333Permissions.hasPerm(sPlayer, "book.use",
						G333Permissions.NOT_QUIET)
						|| G333Permissions.hasPerm(sPlayer, "book.admin",
								G333Permissions.NOT_QUIET)) {
					bitBook = BITBook.loadBook(sPlayer, bookId);
					bitBook.openBook(sPlayer, bitBook.getBookId());
				}
			} else {
				// new book
				if (G333Permissions.hasPerm(sPlayer, "book.create",
						G333Permissions.NOT_QUIET)) {
					int id = sPlayer.getEntityId();
					bookId = BITBook.getNextBookId();
					sPlayer.sendMessage("Creating new book with id:" + bookId);
					BITBook.currentBookId.put(id, bookId);

					String title = "Title";
					String author = sPlayer.getName();
					String coAuthors = "";
					int numberOfPages = 3;
					String[] pages = new String[numberOfPages];
					// TODO: delete the next three lines.
					pages[0] = "This is page number 1";
					pages[1] = "This is page number 2";
					pages[2] = "This is page number 3";
					Boolean masterCopy = false;
					short masterCopyId = 0;
					Boolean forceBookToPlayerInventory = false;
					Boolean canBeMovedFromInventory = true;
					Boolean copyTheBookWhenMoved = false;
					int useCost = 0;

					bitBook.setBitBook(bookId, title, author, coAuthors, numberOfPages, pages,
							masterCopy, masterCopyId,
							forceBookToPlayerInventory,
							canBeMovedFromInventory, copyTheBookWhenMoved,
							useCost);
					BITBook.bitBooks.put(bookId, bitBook);
					//sPlayer.getItemInHand().setDurability(bookId);
					bitBook.openBook(sPlayer, bookId);

				}
			}
		} else {
			if (BITBook.isWriteable(itemInHand.getType())) {
				if (itemInHand.getAmount() != 1) {
					sPlayer.sendMessage("There must only be one item in the slot");
				}
			} else {
				sPlayer.sendMessage("You cant write in a :"
						+ itemInHand.getType());
			}
		}
	}
}
