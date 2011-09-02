package dk.rocologo.BukkitInventoryTools;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;

import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.gui.Button;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.rocologo.Library.RLInventory;
import dk.rocologo.Library.RLMessages;

public class BITSpoutListener extends SpoutListener {
	public static BIT plugin;

	public BITSpoutListener(BIT bit) {
		plugin = bit;
		// TODO Auto-generated constructor stub
	}

	public void SortInventoryMenu(BIT plugin) {
		BITGui.plugin = plugin;
	}

	public void onCustomEvent(Event event) {
		if (event instanceof ButtonClickEvent) {
			Button button = ((ButtonClickEvent) event).getButton();
			UUID uuid = button.getId();
			SpoutPlayer sPlayer = ((ButtonClickEvent) event).getPlayer();
			Block block = sPlayer.getTargetBlock(null, 4);
			BITDigiLock digilock = BITDigiLock.getDigiLock(sPlayer, block);

			// ************************************
			// Buttons in InventoryMenuWindow
			// ************************************
			if (BITGui.BITButtons.get(uuid) == "Sort") {
				if (digilock.getBlock().getType() == Material.CHEST) {
					SpoutChest sChest = (SpoutChest) digilock.getBlock()
							.getState();
					RLInventory.sortInventoryItems(sPlayer,
							sChest.getLargestInventory());
					RLMessages.sendNotification(sPlayer, "Chest sorted.");
				} else {
					RLMessages.sendNotification(sPlayer, "Items sorted.");
				}
			} else if (BITGui.BITButtons.get(uuid) == "Lock") {
				BITDigiLock.SaveDigiLock(sPlayer, digilock.getBlock(),
						BITGui.pincode.getText(), BITGui.owner.getText(), 0,
						"", "");
				RLMessages.sendNotification(sPlayer, "Inventory locked.");
			} else if (BITGui.BITButtons.get(uuid) == "Owner") {
				BITDigiLock.SaveDigiLock(sPlayer, digilock.getBlock(),
						BITGui.pincode.getText(), BITGui.owner.getText(), 0,
						"", "");
				RLMessages.sendNotification(sPlayer, "Owner changed.");
			} else if (BITGui.BITButtons.get(uuid) == "Reset") {
				BITGui.pincode.setText("");
				BITGui.owner.setText("");
				BITDigiLock.SaveDigiLock(sPlayer, digilock.getBlock(),
						BITGui.pincode.getText(), BITGui.owner.getText(), 0,
						"", "");
				RLMessages.sendNotification(sPlayer, "Pincode/owner removed.");
				BITGui.pincode.setDirty(true);
			} else if (BITGui.BITButtons.get(uuid) == "Close") {
				sPlayer.sendMessage("Close button");
				BITGui.popupInventoryMenu.close();
			} else

			// ************************************
			// Buttons in getPincodeWindow
			// ************************************
			if (BITGui.BITButtons.get(uuid) == "Unlock") {
				BITGui.popupGetPincode.close();
				if (digilock.pincode.equals(BITGui.pincode2.getText())) {
					if (digilock.getBlock().getType() == Material.CHEST) {
						SpoutChest sChest = (SpoutChest) block.getState();
						Inventory inv = sChest.getLargestInventory();
						sPlayer.openInventoryWindow(inv);
					}
				} else {
					sPlayer.sendMessage("Wrong pincode: "
							+ BITGui.pincode2.getText());
					}

				// ************************************
				// This only happens if I have forgot to handle a button
				// ************************************
			} else {
				sPlayer.sendMessage("BITSpoutListener: Unknow button:"
						+ BITGui.BITButtons.get(uuid));
			}

		}
	}
}
