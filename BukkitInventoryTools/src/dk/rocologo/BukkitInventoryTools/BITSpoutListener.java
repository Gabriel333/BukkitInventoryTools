package dk.rocologo.BukkitInventoryTools;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
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
				// sPlayer.sendMessage("Sort button");
				if (digilock.getBlock().getType() == Material.CHEST) {
					SpoutChest sChest = (SpoutChest) digilock.getBlock()
							.getState();
					RLInventory.sortInventoryItems(sPlayer,
							sChest.getLargestInventory());
					RLMessages.sendNotification(sPlayer, "Chest sorted.");
				} else {
					// SortPlayerInventory.sortinventory(sPlayer,
					// event.getScreenType());
					RLMessages.sendNotification(sPlayer, "Items sorted.");
				}
			} else if (BITGui.BITButtons.get(uuid) == "Lock") {
				// sPlayer.sendMessage("Lock button: Owner:"
				// + BITGui.owner.getText() + " pincode:"
				// + BITGui.pincode.getText());
				BITDigiLock.SaveDigiLock(sPlayer, digilock.getBlock(),
						BITGui.pincode.getText(), BITGui.owner.getText(), 0,
						"", "");
				RLMessages.sendNotification(sPlayer, "Inventory locked.");
			} else if (BITGui.BITButtons.get(uuid) == "Owner") {
				// sPlayer.sendMessage("Owner button: Owner:"
				// + BITGui.owner.getText() + " pincode:"
				// + BITGui.pincode.getText());
				BITDigiLock.SaveDigiLock(sPlayer, digilock.getBlock(),
						BITGui.pincode.getText(), BITGui.owner.getText(), 0,
						"", "");
				RLMessages.sendNotification(sPlayer, "Owner changed.");
			} else if (BITGui.BITButtons.get(uuid) == "Reset") {
				// sPlayer.sendMessage("Reset button");
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
				// sPlayer.sendMessage("Unlock button");
				BITGui.popupGetPincode.close();
				BITPlayerListener.enteredPincode = BITGui.pincode2.getText();
				if (digilock.pincode.equals(BITPlayerListener.enteredPincode)) {
					sPlayer.sendMessage("BITSpoutListener: Correct pincode.");
					BITPlayerListener.pincodeIsEntered = true;
					BITPlayerListener.digilock.locked = false;
				} else {
					sPlayer.sendMessage("BITSpoutListener: wrong pincode: "
							+ BITPlayerListener.enteredPincode);
					// BITPlayerListener.pincodeIsEntered=false;
					BITPlayerListener.pincodeIsEntered = true;
					BITPlayerListener.digilock.locked = true;
					// BITPlayerListener.enteredPincode="";
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
