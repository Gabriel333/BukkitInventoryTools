package dk.gabriel333.BukkitInventoryTools.Book;

import java.util.UUID;

import org.bukkit.event.Event;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;

public class BITBookSpoutListener extends SpoutListener {

	public void onCustomEvent(Event event) {
		if (event instanceof ButtonClickEvent) {
			Button button = ((ButtonClickEvent) event).getButton();
			UUID uuid = button.getId();
			SpoutPlayer sPlayer = ((ButtonClickEvent) event).getPlayer();
			SpoutBlock sBlock = (SpoutBlock) sPlayer.getTargetBlock(null, 5);
			int id = sPlayer.getEntityId();
			if (BITBook.isWriteable(sBlock)) {
				BITBook bitBook = new BITBook();
				//TODO: get actual book from BITBooks(bookId)
				bitBook.loadBook();
				
				sPlayer.sendMessage("Button:"+BITBook.BITButtons.get(uuid));
				// ************************************
				// Buttons in BITBook
				// ************************************
				if (BITBook.BITButtons.get(uuid) == "saveBookButton") {
					BITBook.popupScreen.get(id).close();
					bitBook.cleanupPopupScreen(sPlayer);
					BITBook.BITButtons.remove(uuid);
					//TODO: save BITBook

				} else if (BITBook.BITButtons.get(uuid) == "cancelBookButton") {
					BITBook.popupScreen.get(id).close();
					bitBook.cleanupPopupScreen(sPlayer);
					BITBook.BITButtons.remove(uuid);
								
				} else if ((BITBook.BITButtons.get(uuid) == "AuthorButton")) {
					if (validateFields(sPlayer)){
						
					}
				} else if ((BITBook.BITButtons.get(uuid) == "CoAuthorButton")) {
					if (validateFields(sPlayer)){
						
					}
				} else if ((BITBook.BITButtons.get(uuid) == "nextPageButton")) {
					if (validateFields(sPlayer)){
						
					}
				} else if ((BITBook.BITButtons.get(uuid) == "previousPageButton")) {
					if (validateFields(sPlayer)){
						
					}
				} else if ((BITBook.BITButtons.get(uuid) == "MasterCopyButton")) {
					if (validateFields(sPlayer)){
						
					}
				} else if ((BITBook.BITButtons.get(uuid) == "forceBookToPlayerInventoryButton")) {
					if (validateFields(sPlayer)){
						
					}
				} else if ((BITBook.BITButtons.get(uuid) == "canBeMovedFromInventoryButton")) {
					if (validateFields(sPlayer)){
						
					}
				} else if ((BITBook.BITButtons.get(uuid) == "copyTheBookWhenMovedButton")) {
					if (validateFields(sPlayer)){
						
					}
				} else if ((BITBook.BITButtons.get(uuid) == "UseCostButton")) {
					if (validateFields(sPlayer)){
						
					}
				} 
				
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
