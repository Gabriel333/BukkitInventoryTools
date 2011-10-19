package dk.gabriel333.BukkitInventoryTools.Book;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.BukkitInventoryTools.BITDigiLock;
import dk.gabriel333.BukkitInventoryTools.Inventory.BITInventory;
import dk.gabriel333.Library.*;

public class BITBookInputListener extends InputListener {

	@Override
	public void onKeyPressedEvent(KeyPressedEvent event) {
		SpoutPlayer sPlayer = event.getPlayer();
		// BITPlayer bPlayer = new BITPlayer(sPlayer);
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
		// BITBlock bBlock = (BITBlock) targetblock;

		// PLAYER_INVENTORY
		if (screentype == ScreenType.PLAYER_INVENTORY) {
			sPlayer.sendMessage("1 - BITBlockInputListner");
			BITBook bitBook = new BITBook();
			int bookId = 1;
			bitBook.getNextBookId();
			int slotNo = sPlayer.getInventory().getHeldItemSlot();
			String title = "My first book";
			String author = sPlayer.getName();
			String coAuthors = "";
			int numberOfPages = 3;
			GenericTextField[] pages = new GenericTextField[numberOfPages];
			Boolean masterCopy = false;
			int masterCopyId = 0;
			Boolean forceBookToPlayerInventory = false;
			Boolean canBeMovedFromInventory = true;
			Boolean copyTheBookWhenMoved = false;
			int useCost = 0;
			
			bitBook.setBitBook(bookId, sBlock, slotNo,
					title, author, coAuthors, numberOfPages, pages,
					masterCopy, masterCopyId,
					forceBookToPlayerInventory,
					canBeMovedFromInventory, copyTheBookWhenMoved,
					useCost);
			if (keypressed.equals(G333Config.LIBRARY_READKEY)) {
				sPlayer.sendMessage("2 - BITBlockInputListner");
				
				//if (G333Permissions.hasPerm(sPlayer, "book.use",
				//		G333Permissions.NOT_QUIET)) {
					// TODO: Handle readBook in players inventory
					sPlayer.sendMessage("ItemInHand" + sPlayer.getItemInHand());
					sPlayer.sendMessage("Block:" + sBlock.getType()
							+ " is it writeable?" + BITBook.isWriteable(sBlock));
					
					
					bitBook.openBook(sPlayer);

				//}
			} else if (keypressed.equals("KEY_ESCAPE")) {
				bitBook.closeBook(sPlayer);

			} else if (keypressed.equals("KEY_RETURN")) {
				//TODO: save book
				bitBook.closeBook(sPlayer);

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
		if (BITBook.isWriteable(sBlock)) {

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
							G333Inventory.sortPlayerInventoryItems(sPlayer);
							int id = sPlayer.getEntityId();
							BITInventory bitInventory = BITInventory.openedInventories
									.get(id);
							Inventory inventory = bitInventory.getInventory();
							G333Inventory
									.sortInventoryItems(sPlayer, inventory);
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

						}
					} else if (keypressed.equals("KEY_ESCAPE")) {
						int id = sPlayer.getEntityId();
						BITInventory bitInventory = BITInventory.openedInventories
								.get(id);
						BITInventory.saveBitInventory(sPlayer, bitInventory);
						BITInventory.openedInventories.remove(id);
					}
				} else
				// sBlock is NOT a chest/Bookshelf/SpoutBackpack
				{
					if (!keypressed.equals("KEY_ESCAPE"))
						sPlayer.sendMessage("I cant sort this Inventory. Make a Ticket to Gabriel333.");
				}
			}

			// GAME_SCREEN
			else if (screentype == ScreenType.GAME_SCREEN) {
				if (keypressed.equals(G333Config.LIBRARY_READKEY)) {

					// IS BOOK CREATED
					if (BITBook.isBookCreated()) {
						if ((G333Permissions.hasPerm(sPlayer, "book.use",
								G333Permissions.NOT_QUIET))
								|| G333Permissions
										.hasPerm(sPlayer, "book.admin",
												G333Permissions.NOT_QUIET)) {

							sPlayer.sendMessage("ItemInHand"
									+ sPlayer.getItemInHand());

							// TODO: open an exting book

						}
					} else {
						if (sPlayer.isSpoutCraftEnabled()) {
							if (G333Permissions.hasPerm(sPlayer, "book.create",
									G333Permissions.NOT_QUIET)
									|| G333Permissions.hasPerm(sPlayer,
											"book.admin",
											G333Permissions.NOT_QUIET)) {
								// TODO: open empty book

								sPlayer.sendMessage("ItemInHand"
										+ sPlayer.getItemInHand());

							}
						} else {
							sPlayer.sendMessage("Install SpoutCraft or use command /book to create a book");
						}

					}
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
				if (keypressed.equals("KEY_ESCAPE")) {
					// sPlayer.closeActiveWindow();
					// bPlayer.cleanupPopupScreen(sPlayer);

				} else if (keypressed.equals("KEY_RETURN")) {

				} else if (keypressed.equals(G333Config.LIBRARY_READKEY)) {
					// TODO: Handle the Book

					sPlayer.sendMessage("ItemInHand" + sPlayer.getItemInHand());

				}
			}
		} else {
			// UNHANDLED SCREENTYPE
		}
	}
}