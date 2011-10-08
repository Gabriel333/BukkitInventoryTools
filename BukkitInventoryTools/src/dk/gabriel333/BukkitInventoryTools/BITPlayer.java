package dk.gabriel333.BukkitInventoryTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spout.inventory.CustomMCInventory;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import de.Keyle.MyWolf.MyWolfPlugin;
import dk.gabriel333.Library.G333Inventory;

public class BITPlayer {

	protected Player player;

	public BITPlayer(SpoutPlayer sPlayer) {
		this.player = sPlayer;
	}

	public void getPlayer(SpoutPlayer sPlayer) {
		this.player = sPlayer;
	}

	public String getName() {
		return player.getName();
	}

	public void sortinventory(SpoutPlayer sPlayer, ScreenType screentype) {
		// sort the ordinary player inventory
		Inventory inventory = sPlayer.getInventory();
		G333Inventory.sortPlayerInventoryItems(sPlayer);

		// sort the SpoutBackpack if it exists and if it is opened.
		if (BIT.spoutbackpack
				&& BIT.spoutBackpackHandler.isOpenSpoutBackpack(sPlayer)) {
			inventory = BIT.spoutBackpackHandler
					.getOpenedSpoutBackpack(sPlayer);
			if (inventory != null) {
				G333Inventory.sortInventoryItems(sPlayer, inventory);
			}
		}

		// sort the players MyWolfInventory if exists and if is open.
		if (BIT.mywolf) {
			// if the wolf inventory is open then {

			CustomMCInventory inv = MyWolfPlugin.getMyWolf(sPlayer).inv;

			if (inv != null) {
				// test if myWolfInventory is opened and open it
				// this on fails... can not be cast to ... Inventory

				// G333Inventory.sortInventoryItems(sPlayer, (Inventory) inv);

			}
		}
	}

	public static ArrayList<GenericButton> menuButtons = new ArrayList<GenericButton>();
	public static HashMap<UUID, String> BITButtons = new HashMap<UUID, String>();

	// ***************************************************************
	// getPincode: Open GenericPopup and ask for pincode before to
	// unlock the inventory.
	// ***************************************************************
	public void getPincode(SpoutPlayer sPlayer, SpoutBlock block) {
		int y = 50, height = 20, width = 100;
		int x = 170;
		UUID uuid = sPlayer.getUniqueId();

		GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(95));
		itemwidget.setX(x + 2 * height).setY(y);
		itemwidget.setHeight(height * 2).setWidth(height * 2)
				.setDepth(height * 2);
		itemwidget.setTooltip("Locked inventory");
		BIT.popupGetPincode.get(uuid).attachWidget(
						BIT.plugin, itemwidget);
		y = y + 3 * height;
		
		BIT.pincode.get(uuid).setText("");
		BIT.pincode.get(uuid).setTooltip("Enter the pincode and press unlock.");
		BIT.pincode.get(uuid).setCursorPosition(1).setMaximumCharacters(10);
		BIT.pincode.get(uuid).setX(x).setY(y);
		BIT.pincode.get(uuid).setHeight(height).setWidth(width);
		BIT.pincode.get(uuid).setPasswordField(true);
		BIT.popupGetPincode.get(uuid).attachWidget(
						BIT.plugin, BIT.pincode.get(uuid));
		y = y + height;

		GenericButton unlockButton = new GenericButton("Unlock");
		unlockButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(width);
		// unlockButton.setTooltip("Enter the pincode and press unlock.");
		menuButtons.add(unlockButton);
		BITButtons.put(unlockButton.getId(), "getPincodeUnlock");
		BIT.popupGetPincode.get(uuid).attachWidget(BIT.plugin, unlockButton);

		GenericButton cancelButton = new GenericButton("Cancel");
		cancelButton.setAuto(false).setX(x + width + 10).setY(y)
				.setHeight(height).setWidth(width);
		// cancelButton.setTooltip("Cancel");
		BIT.popupGetPincode.get(uuid).attachWidget(BIT.plugin, cancelButton);
		menuButtons.add(cancelButton);
		BITButtons.put(cancelButton.getId(), "getPincodeCancel");

		// Open Window
		BIT.popupGetPincode.get(uuid).setTransparent(true);
		sPlayer.getMainScreen().attachPopupScreen(
				BIT.popupGetPincode.get(uuid));
	}

	@SuppressWarnings("unused")
	private int getPincodeBlock(SpoutBlock block) {
		switch (block.getTypeId()) {
		case 23:
			return 23; // Dispenser
		case 47:
			return 47; // Bookshelf
		case 54:
			return 54; // Chest
		case 61:
			return 61; // Furnace
		case 62:
			return 62; // Burning Furnace
		case 64:
			return 324; // Wooden door
		case 69:
			return 69; // Lever
		case 71:
			return 330; // Iron door
		case 77:
			return 77; // Stone button
		case 96:
			return 69; // Trap_door
		}
		return 95;
	}

	// ***************************************************************
	// setPincode: Open GenericPopup and enter a pincode to lock the
	// inventory.
	// ***************************************************************
	public void setPincode(SpoutPlayer sPlayer, SpoutBlock block) {
		UUID uuid = sPlayer.getUniqueId();
		int height = 20;
		int x, y, w1, w2, w3, w4;

		if (BITDigiLock.isLocked(block)) {
			BITDigiLock digilock = BITDigiLock.loadDigiLock(sPlayer, block);
			BIT.pincode.get(uuid).setText(digilock.getPincode());
			BIT.owner.get(uuid).setText(digilock.getOwner());
			BIT.coOwners.get(uuid).setText(digilock.getCoOwners());
			BIT.closetimer.get(uuid).setText(Integer.toString(digilock.getClosetimer()));
			BIT.useCost.get(uuid).setText(Integer.toString(digilock.getUseCost()));
		} else {
			BIT.pincode.get(uuid).setText("");
			BIT.owner.get(uuid).setText(sPlayer.getName());
			BIT.coOwners.get(uuid).setText("");
			BIT.closetimer.get(uuid).setText("0");
			BIT.useCost.get(uuid).setText("0");
		}

		// GIF
		x = 170;
		y = 50;
		GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(95));
		// GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(
		// getPincodeBlock(block)));
		itemwidget.setX(x + 2 * height).setY(y);
		itemwidget.setHeight(height * 2).setWidth(height * 2)
				.setDepth(height * 2);
		if (!BITDigiLock.isLocked(block)) {
			itemwidget.setTooltip("Unlocked inventory");
		} else {
			itemwidget.setTooltip("Locked inventory");
		}
		BIT.popupSetPincode.get(uuid).attachWidget(BIT.plugin, itemwidget);
		y = y + 3 * height;

		// first row -------- x=20-170-------------------------------------
		x = 10;
		w1 = 60;
		w2 = 80;
		w3 = 50;
		w4 = 50;

		y = 170;
		// ownerButton
		GenericButton ownerButton = new GenericButton("Owner");
		ownerButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(w1);
		ownerButton.setTooltip("Set Owner");
		BIT.popupSetPincode.get(uuid).attachWidget(BIT.plugin, ownerButton);
		menuButtons.add(ownerButton);
		BITButtons.put(ownerButton.getId(), "OwnerButton");
		// owner1
		BIT.owner.get(uuid).setTooltip("Owner of the DigiLock");
		BIT.owner.get(uuid).setCursorPosition(1).setMaximumCharacters(20);
		BIT.owner.get(uuid).setX(x + w1 + 1).setY(y);
		BIT.owner.get(uuid).setHeight(height).setWidth(w2);
		BIT.popupSetPincode.get(uuid).attachWidget(BIT.plugin, BIT.owner.get(uuid));

		// closetimerButton
		GenericButton closetimerButton = new GenericButton("Closetimer");
		closetimerButton.setAuto(false).setX(x + w1 + w2 + 10).setY(y)
				.setHeight(height).setWidth(w1);
		closetimerButton.setTooltip("Set closetimer");
		BIT.popupSetPincode.get(uuid).attachWidget(BIT.plugin,
				closetimerButton);
		menuButtons.add(closetimerButton);
		BITButtons.put(closetimerButton.getId(), "ClosetimerButton");
		// closetimer
		BIT.closetimer.get(uuid).setTooltip("Autoclosing time in sec.");
		BIT.closetimer.get(uuid).setCursorPosition(1).setMaximumCharacters(4);
		BIT.closetimer.get(uuid).setX(x + w1 + 1 + w2 + 10 + w1 + 1).setY(y);
		BIT.closetimer.get(uuid).setHeight(height).setWidth(w3);
		BIT.popupSetPincode.get(uuid).attachWidget(BIT.plugin, BIT.closetimer.get(uuid));

		// useCostButton
		GenericButton useCostButton = new GenericButton("Use cost");
		useCostButton.setAuto(false).setX(x + w1 + w2 + 10 + w1 + w3 + 10)
				.setY(y).setHeight(height).setWidth(w1);
		useCostButton.setTooltip("Set cost");
		BIT.popupSetPincode.get(uuid).attachWidget(BIT.plugin, useCostButton);
		menuButtons.add(useCostButton);
		BITButtons.put(useCostButton.getId(), "UseCostButton");
		// useCost1
		BIT.useCost.get(uuid).setTooltip("This is the cost to use the DigiLock");
		BIT.useCost.get(uuid).setCursorPosition(1).setMaximumCharacters(4);
		BIT.useCost.get(uuid).setX(x + w1 + w2 + 10 + w1 + w3 + 10 + w1 + 1).setY(y);
		BIT.useCost.get(uuid).setHeight(height).setWidth(w4);
		BIT.popupSetPincode.get(uuid).attachWidget(BIT.plugin, BIT.useCost.get(uuid));
		y = y + height + 1;

		// setCoOwnerButton
		GenericButton CoOwnerButton = new GenericButton("CoOwners");
		CoOwnerButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(w1);
		CoOwnerButton.setTooltip("CoOwners must be seperated by a comma.");
		BIT.popupSetPincode.get(uuid).attachWidget(BIT.plugin, CoOwnerButton);
		menuButtons.add(CoOwnerButton);
		BITButtons.put(CoOwnerButton.getId(), "CoOwnerButton");
		// listOfCoOwners
		BIT.coOwners.get(uuid).setX(x + w1 + 1).setY(y).setWidth(340).setHeight(height);
		BIT.coOwners.get(uuid).setMaximumCharacters(200);
		BIT.coOwners.get(uuid).setText(BIT.coOwners.get(uuid).getText());
		BIT.popupSetPincode.get(uuid)
				.attachWidget(BIT.plugin, BIT.coOwners.get(uuid));
		y = y + height;

		// Second row ------------X=170-270-370------------------------------
		y = 110;
		x = 180;
		w1 = 80;
		w2 = 80;
		// pincode3
		BIT.pincode.get(uuid).setTooltip("Enter/change the pincode...");
		BIT.pincode.get(uuid).setCursorPosition(1).setMaximumCharacters(10);
		BIT.pincode.get(uuid).setX(x).setY(y);
		BIT.pincode.get(uuid).setHeight(height).setWidth(w1);
		BIT.pincode.get(uuid).setPasswordField(false);
		BIT.popupSetPincode.get(uuid).attachWidget(BIT.plugin,
				BIT.pincode.get(uuid));
		y = y + height;

		// lockButton
		GenericButton lockButton = new GenericButton("Lock");
		lockButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(w1);
		lockButton.setTooltip("Enter/change the pincode and press lock.");
		BIT.popupSetPincode.get(uuid).attachWidget(BIT.plugin, lockButton);
		menuButtons.add(lockButton);
		BITButtons.put(lockButton.getId(), "setPincodeLock");

		// cancelButton
		GenericButton cancelButton2 = new GenericButton("Cancel");
		cancelButton2.setAuto(false).setX(x + w1 + 10).setY(y)
				.setHeight(height).setWidth(w1);
		BIT.popupSetPincode.get(uuid).attachWidget(BIT.plugin, cancelButton2);
		menuButtons.add(cancelButton2);
		BITButtons.put(cancelButton2.getId(), "setPincodeCancel");

		// removeButton
		if (BITDigiLock.isLocked(block)) {
			GenericButton removeButton = new GenericButton("Remove");
			removeButton.setAuto(false).setX(x - w1 - 10).setY(y)
					.setHeight(height).setWidth(w1);
			removeButton.setTooltip("Press Remove to delete the lock.");
			removeButton.setEnabled(true);
			menuButtons.add(removeButton);
			BITButtons.put(removeButton.getId(), "setPincodeRemove");
			BIT.popupSetPincode.get(uuid).attachWidget(BIT.plugin,
					removeButton);
		} else {
			// menuButtons.remove(removeButton);
			// BITButtons.remove("setPincodeRemove");
			// popupSetPincode.removeWidget(removeButton);

		}
		BIT.popupSetPincode.get(uuid).setDirty(true);

		// Open Window
		BIT.popupSetPincode.get(uuid).setTransparent(true);
		sPlayer.getMainScreen().attachPopupScreen(
				BIT.popupSetPincode.get(uuid));

	}

	public void cleanupSetPincode(SpoutPlayer sPlayer) {
		UUID uuid = sPlayer.getUniqueId();
		BIT.popupSetPincode.get(uuid).removeWidgets(BIT.plugin);
		BIT.popupSetPincode.get(uuid).setDirty(true);
	}

	public void cleanupGetPincode(SpoutPlayer sPlayer) {
		UUID uuid = sPlayer.getUniqueId();
		BIT.popupGetPincode.get(uuid).removeWidgets(BIT.plugin);
		BIT.popupGetPincode.get(uuid).setDirty(true);
	}

}
