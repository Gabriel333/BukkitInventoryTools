package dk.gabriel333.BukkitInventoryTools.Book;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

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
		if (BITBook.isWriteable(itemInHand.getType())) {
			if (keypressed.equals(G333Config.LIBRARY_READKEY)) {

				handleItemInHand(sPlayer, sBlock, itemInHand);
			} else if (keypressed.equals("KEY_ESCAPE")
					&& screentype != ScreenType.GAME_SCREEN) {
				// BITInventory bitInventory =
				// BITInventory.openedInventories.get(id);
				// BITInventory.saveBitInventory(sPlayer, bitInventory);
				BITInventory.openedInventories.remove(id);
				sPlayer.closeActiveWindow();
				BITBook.cleanupPopupScreen(sPlayer);
				BITBook.bitBooks.remove(BITBook.currentBookId.get(id));
				BITBook.currentBookId.put(id, (short) 10000);
			}
		}
	}

	private void handleItemInHand(SpoutPlayer sPlayer, SpoutBlock sBlock,
			ItemStack itemInHand) {
		if (BITBook.isWriteable(itemInHand.getType())
				&& itemInHand.getAmount() == 1) {

			short bookId = itemInHand.getDurability();
			BITBook bitBook = new BITBook();
			if (bookId > 10000) {
				if (G333Permissions.hasPerm(sPlayer, "book.use",
						G333Permissions.NOT_QUIET)
						|| G333Permissions.hasPerm(sPlayer, "book.admin",
								G333Permissions.NOT_QUIET)) {
					bitBook = BITBook.loadBook(sPlayer, bookId);
					bitBook.openBook(sPlayer, bookId);
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

					bitBook.setBitBook(bookId, title, author, coAuthors,
							numberOfPages, pages, masterCopy, masterCopyId,
							forceBookToPlayerInventory,
							canBeMovedFromInventory, copyTheBookWhenMoved,
							useCost);
					BITBook.bitBooks.put(bookId, bitBook);
					// sPlayer.getItemInHand().setDurability(bookId);
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
