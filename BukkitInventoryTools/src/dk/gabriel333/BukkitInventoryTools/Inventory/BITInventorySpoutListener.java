package dk.gabriel333.BukkitInventoryTools.Inventory;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;

import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.Inventory.BITInventory;
import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;

public class BITInventorySpoutListener extends SpoutListener {

	public void onCustomEvent(Event event) {
		if (event instanceof ButtonClickEvent) {
			Button button = ((ButtonClickEvent) event).getButton();
			UUID uuid = button.getId();
			SpoutPlayer sPlayer = ((ButtonClickEvent) event).getPlayer();
			SpoutBlock sBlock = (SpoutBlock) sPlayer.getTargetBlock(null, 5);

			if ((BITInventory.BITBookButtons.get(uuid) == "setPincodeCancel")) {
				sPlayer.closeActiveWindow();
				BITInventory.cleanupPopupScreen(sPlayer);
				BITInventory.BITBookButtons.remove(uuid);

			} else if ((BITInventory.BITBookButtons.get(uuid) == "OwnerButton")) {
				if (validateSetPincodeFields(sPlayer)) {
				}

			} else if ((BITInventory.BITBookButtons.get(uuid) == "CoOwnerButton")) {
				if (validateSetPincodeFields(sPlayer)) {
				}

			} else if ((BITInventory.BITBookButtons.get(uuid) == "UseCostButton")) {
				if (validateSetPincodeFields(sPlayer)) {
				}

			}

			else if ((BITInventory.BITBookButtons.get(uuid) == "CreateBookshelfButton")) {
				if (validateSetPincodeFields(sPlayer)) {
					String coowners = "";
					String name = "";
					String owner = sPlayer.getName();
					int usecost = 0;
					Inventory inventory = SpoutManager.getInventoryBuilder()
							.construct(G333Config.BOOKSHELF_SIZE, name);
					BITInventory.saveBitInventory(sPlayer, sBlock, owner, name,
							coowners, inventory, usecost);
					sPlayer.closeActiveWindow();
					BITInventory.cleanupPopupScreen(sPlayer);
				}
			}

			else if ((BITInventory.BITBookButtons.get(uuid) == "removeBookshelfButton")) {
				if (validateSetPincodeFields(sPlayer)) {
				}
			}

			// ************************************
			// This only happens if I have forgot to handle a button
			// ************************************
			else {
				if (G333Config.DEBUG_GUI)
					sPlayer.sendMessage("BITSpoutListener: Unknow button:"
							+ BITInventory.BITBookButtons.get(uuid));
			}
		}
	}

	private boolean validateSetPincodeFields(SpoutPlayer sPlayer) {
		int id = sPlayer.getEntityId();
		if (BITInventory.useCostGUI.get(id).getText().equals("")) {
			BITInventory.useCostGUI.get(id).setText("0");
			BITInventory.popupScreen.get(id).setDirty(true);
		}

		int useCost = Integer.valueOf(BITInventory.useCostGUI.get(id).getText());
		if (useCost > G333Config.DIGILOCK_USEMAXCOST) {
			G333Messages.sendNotification(sPlayer, "Cost must be less "
					+ G333Config.DIGILOCK_USEMAXCOST);
			BITInventory.useCostGUI.get(id).setText(
					String.valueOf(G333Config.DIGILOCK_USEMAXCOST));
			BITInventory.popupScreen.get(id).setDirty(true);
			return false;
		} else if (useCost < 0) {
			G333Messages.sendNotification(sPlayer, "Cost must be >= 0");
			BITInventory.useCostGUI.get(id).setText("0");
			BITInventory.popupScreen.get(id).setDirty(true);
			return false;
		}

		return true;
	}
}
