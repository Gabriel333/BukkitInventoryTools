package dk.gabriel333.BukkitInventoryTools.Book;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;
import dk.gabriel333.BukkitInventoryTools.BITEnums.BITInventoryType;

public class BITBookSpoutListener extends SpoutListener {

	public void onCustomEvent(Event event) {
		if (event instanceof ButtonClickEvent) {
			Button button = ((ButtonClickEvent) event).getButton();
			UUID uuid = button.getId();
			SpoutPlayer sPlayer = ((ButtonClickEvent) event).getPlayer();
			SpoutBlock sBlock = (SpoutBlock) sPlayer.getTargetBlock(null, 5);
			ItemStack itemInHand = sPlayer.getInventory().getItemInHand();
			int id = sPlayer.getEntityId();

			if (BITBook.isWriteable(sBlock)
					|| BITBook.isWriteable(itemInHand.getType())) {
				sPlayer.sendMessage("BITBookSpoutListener-CurrentBookId:"
						+ BITBook.currentBookId.get(id) + " button="
						+ BITBook.BITButtons.get(uuid));

				if (BITBook.BITButtons.get(uuid) == "saveBookButton") {
					BITBook.popupScreen.get(id).close();
					BITBook.cleanupPopupScreen(sPlayer);
					BITBook.BITButtons.remove(uuid);
					sPlayer.sendMessage("bookId="
							+ BITBook.currentBookId.get(id) + " title="
							+ BITBook.titleGUI.get(id).getText());
					BITBook.saveBook(sPlayer, sBlock, BITBook.currentBookId.get(id), BITInventoryType.PLAYER_INVENTORY);

				} else if (BITBook.BITButtons.get(uuid) == "cancelBookButton") {
					BITBook.popupScreen.get(id).close();
					BITBook.cleanupPopupScreen(sPlayer);
					BITBook.BITButtons.remove(uuid);
					sPlayer.sendMessage("You clicked cancel - dropping current book id:"
							+ BITBook.currentBookId.get(id));
					BITBook.bitBooks.remove(BITBook.currentBookId.get(id));
					BITBook.currentBookId.put(id, 0);

				}
				/*
				 * else if ((BITBook.BITButtons.get(uuid) == "authorButton")) {
				 * if (validateFields(sPlayer)) {
				 * 
				 * } } else if ((BITBook.BITButtons.get(uuid) ==
				 * "coAuthorButton")) { if (validateFields(sPlayer)) {
				 * 
				 * } }
				 */
				else if ((BITBook.BITButtons.get(uuid) == "nextPageButton")) {
					if (validateFields(sPlayer)) {
						BITBook.showNextPage(sPlayer);

					}
				} else if ((BITBook.BITButtons.get(uuid) == "previousPageButton")) {
					if (validateFields(sPlayer)) {
						BITBook.showPreviousPage(sPlayer);

					}
				} else if ((BITBook.BITButtons.get(uuid) == "masterCopyButton")) {
					if (validateFields(sPlayer)) {
						if (BITBook.masterCopyGUI.get(id)) {
							BITBook.masterCopyGUI.put(id, false);
						} else {
							BITBook.masterCopyGUI.put(id, true);
						}
						BITBook.masterCopyButtonGUI.get(id).setText(
								"Master:" + BITBook.masterCopyGUI.get(id));
						BITBook.masterCopyButtonGUI.get(id).setDirty(true);
					}
				} else if ((BITBook.BITButtons.get(uuid) == "forceBookToPlayerInventoryButton")) {
					if (validateFields(sPlayer)) {
						if (BITBook.forceBookToPlayerInventoryGUI.get(id)) {
							BITBook.forceBookToPlayerInventoryGUI
									.put(id, false);
						} else {
							BITBook.forceBookToPlayerInventoryGUI.put(id, true);
						}
						BITBook.forceBookToPlayerInventoryButtonGUI
								.get(id)
								.setText(
										"Master:"
												+ BITBook.masterCopyGUI.get(id));
						BITBook.forceBookToPlayerInventoryButtonGUI.get(id)
								.setDirty(true);
					}
				} else if ((BITBook.BITButtons.get(uuid) == "canBeMovedFromInventoryButton")) {
					if (validateFields(sPlayer)) {
						if (BITBook.canBeMovedFromInventoryGUI.get(id)) {
							BITBook.canBeMovedFromInventoryGUI.put(id, false);
						} else {
							BITBook.canBeMovedFromInventoryGUI.put(id, true);
						}
						BITBook.canBeMovedFromInventoryButtonGUI
								.get(id)
								.setText(
										"Master:"
												+ BITBook.masterCopyGUI.get(id));
						BITBook.canBeMovedFromInventoryButtonGUI.get(id)
								.setDirty(true);
					}
				} else if ((BITBook.BITButtons.get(uuid) == "copyTheBookWhenMovedButton")) {
					if (validateFields(sPlayer)) {
						if (BITBook.copyTheBookWhenMovedGUI.get(id)) {
							BITBook.copyTheBookWhenMovedGUI.put(id, false);
						} else {
							BITBook.copyTheBookWhenMovedGUI.put(id, true);
						}
						BITBook.copyTheBookWhenMovedButtonGUI.get(id).setText(
								"Master:" + BITBook.masterCopyGUI.get(id));
						BITBook.copyTheBookWhenMovedButtonGUI.get(id).setDirty(
								true);
					}
				}

				/*
				 * else if ((BITBook.BITButtons.get(uuid) == "useCostButton")) {
				 * if (validateFields(sPlayer)) {
				 * 
				 * } }
				 */

				// ************************************
				// This only happens if I have forgot to handle a button
				// ************************************
				else {
					if (G333Config.DEBUG_GUI)
						sPlayer.sendMessage("BITBookSpoutListener: Unknow button:"
								+ BITBook.BITButtons.get(uuid));
				}
			}
		}
	}

	private boolean validateFields(SpoutPlayer sPlayer) {
		int id = sPlayer.getEntityId();
		if (BITBook.useCostGUI.get(id).getText().equals("")) {
			BITBook.useCostGUI.get(id).setText("0");
			BITBook.popupScreen.get(id).setDirty(true);
		}
		int useCost = Integer.valueOf(BITBook.useCostGUI.get(id).getText());

		if (useCost > G333Config.BOOK_USEMAXCOST) {
			G333Messages.sendNotification(sPlayer, "Cost must be less "
					+ G333Config.BOOK_USEMAXCOST);
			BITBook.useCostGUI.get(id).setText(
					String.valueOf(G333Config.DIGILOCK_USEMAXCOST));
			BITBook.popupScreen.get(id).setDirty(true);
			return false;
		} else if (useCost < 0) {
			G333Messages.sendNotification(sPlayer, "Cost must be > 0");
			BITBook.useCostGUI.get(id).setText("0");
			BITBook.popupScreen.get(id).setDirty(true);
			return false;
		}

		return true;
	}
}
