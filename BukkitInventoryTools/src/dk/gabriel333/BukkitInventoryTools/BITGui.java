package dk.gabriel333.BukkitInventoryTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BITDigiLock;

public class BITGui {

	private static BIT plugin;

	// GUI
	public static ArrayList<GenericButton> menuButtons = new ArrayList<GenericButton>();
	public static HashMap<UUID, String> BITButtons = new HashMap<UUID, String>();

	public static PopupScreen popupInventoryMenu = new GenericPopup();
	public static GenericTextField pincode = new GenericTextField();
	public static GenericTextField owner = new GenericTextField();

	// ***************************************************************
	// getPincode: Open GenericPopup and ask for pincode before to
	// unlock the inventory.
	// ***************************************************************
	public static PopupScreen popupGetPincode = new GenericPopup();
	public static GenericTextField pincode2 = new GenericTextField();

	public static void getPincode(SpoutPlayer sPlayer, SpoutBlock block) {
		int y = 50, height = 20, width = 100;
		int x = 170;

		GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(95));
		// GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(
		// getPincodeBlock(block)));
		itemwidget.setX(x + 2 * height).setY(y);
		itemwidget.setHeight(height * 2).setWidth(height * 2)
				.setDepth(height * 2);
		itemwidget.setTooltip("Locked inventory");
		popupGetPincode.attachWidget(plugin, itemwidget);
		y = y + 3 * height;

		pincode2.setText("");
		pincode2.setTooltip("Enter the pincode and press unlock.");
		pincode2.setCursorPosition(1).setMaximumCharacters(10);
		pincode2.setX(x).setY(y);
		pincode2.setHeight(height).setWidth(width);
		popupGetPincode.attachWidget(plugin, pincode2);
		y = y + height;

		GenericButton unlockButton = new GenericButton("Unlock");
		unlockButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(width);
		// unlockButton.setTooltip("Enter the pincode and press unlock.");
		menuButtons.add(unlockButton);
		BITButtons.put(unlockButton.getId(), "getPincodeUnlock");
		popupGetPincode.attachWidget(plugin, unlockButton);

		GenericButton cancelButton = new GenericButton("Cancel");
		cancelButton.setAuto(false).setX(x + width + 10).setY(y)
				.setHeight(height).setWidth(width);
		// cancelButton.setTooltip("Cancel");
		popupGetPincode.attachWidget(plugin, cancelButton);
		menuButtons.add(cancelButton);
		BITButtons.put(cancelButton.getId(), "getPincodeCancel");

		// Open Window
		popupGetPincode.setTransparent(true);
		sPlayer.getMainScreen().attachPopupScreen(popupGetPincode);
	}

	public static void closeGetPincode() {

	}

	@SuppressWarnings("unused")
	private static int getPincodeBlock(SpoutBlock block) {
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
	public static PopupScreen popupSetPincode = new GenericPopup();
	public static GenericTextField pincode3 = new GenericTextField();
	public static GenericTextField owner1 = new GenericTextField();
	public static GenericTextField closetimer1 = new GenericTextField();
	public static GenericTextField listOfCoOwners = new GenericTextField();
	public static GenericTextField connectedTo = new GenericTextField();

	public static void setPincode(SpoutPlayer sPlayer, SpoutBlock block) {
		int height = 20;
		int x, y, w1, w2;

		BITDigiLock digilock;
		if (BITDigiLock.isLocked(block)) {
			digilock = BITDigiLock.loadDigiLock(sPlayer, block);
			pincode3.setText(digilock.getPincode());
			owner1.setText(digilock.getOwner());
			listOfCoOwners.setText(digilock.getCoOwners());
			closetimer1.setText(Integer.toString(digilock.getClosetimer()));
		} else {
			pincode3.setText("");
			owner1.setText(sPlayer.getName());
			listOfCoOwners.setText("");
			closetimer1.setText("0");
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
		popupSetPincode.attachWidget(plugin, itemwidget);
		y = y + 3 * height;

		// first row -------- x=20-170-------------------------------------
		x = 10;
		w1 = 60;
		w2 = 80;
		y = 170;
		// ownerButton
		GenericButton ownerButton = new GenericButton("Owner");
		ownerButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(w1);
		ownerButton.setTooltip("Set Owner");
		popupSetPincode.attachWidget(plugin, ownerButton);
		menuButtons.add(ownerButton);
		BITButtons.put(ownerButton.getId(), "OwnerButton");
		// owner1
		owner1.setTooltip("Owner of the DigiLock");
		owner1.setCursorPosition(1).setMaximumCharacters(20);
		owner1.setX(x + w1 + 1).setY(y);
		owner1.setHeight(height).setWidth(w2);
		popupSetPincode.attachWidget(plugin, owner1);

		// closetimerButton
		GenericButton closetimerButton = new GenericButton("Closetimer");
		closetimerButton.setAuto(false).setX(x + 170).setY(y).setHeight(height)
				.setWidth(w1);
		closetimerButton.setTooltip("Set closetimer");
		popupSetPincode.attachWidget(plugin, closetimerButton);
		menuButtons.add(closetimerButton);
		BITButtons.put(closetimerButton.getId(), "ClosetimerButton");
		// closetimer1
		closetimer1.setTooltip("Autoclosing time in sec.");
		closetimer1.setCursorPosition(1).setMaximumCharacters(4);
		closetimer1.setX(x + 170 + w1 + 1).setY(y);
		closetimer1.setHeight(height).setWidth(w2);
		popupSetPincode.attachWidget(plugin, closetimer1);

		y = y + height;

		// setCoOwnerButton
		GenericButton CoOwnerButton = new GenericButton("CoOwners");
		CoOwnerButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(w1);
		CoOwnerButton.setTooltip("CoOwners must be seperated by a comma.");
		popupSetPincode.attachWidget(plugin, CoOwnerButton);
		menuButtons.add(CoOwnerButton);
		BITButtons.put(CoOwnerButton.getId(), "CoOwnerButton");
		// listOfCoOwners
		listOfCoOwners.setX(x + w1 + 1).setY(y).setWidth(340).setHeight(height);
		listOfCoOwners.setMaximumCharacters(200);
		listOfCoOwners.setText(listOfCoOwners.getText());
		popupSetPincode.attachWidget(plugin, listOfCoOwners);
		y = y + height;

		// Second row ------------X=170-270-370------------------------------
		y = 110;
		x = 180;
		w1 = 80;
		w2 = 80;
		// pincode3
		pincode3.setTooltip("Enter/change the pincode...");
		pincode3.setCursorPosition(1).setMaximumCharacters(10);
		pincode3.setX(x).setY(y);
		pincode3.setHeight(height).setWidth(w1);
		popupSetPincode.attachWidget(plugin, pincode3);
		y = y + height;

		// lockButton
		GenericButton lockButton = new GenericButton("Lock");
		lockButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(w1);
		lockButton.setTooltip("Enter/change the pincode and press lock.");
		popupSetPincode.attachWidget(plugin, lockButton);
		menuButtons.add(lockButton);
		BITButtons.put(lockButton.getId(), "setPincodeLock");

		// cancelButton
		GenericButton cancelButton2 = new GenericButton("Cancel");
		cancelButton2.setAuto(false).setX(x + w1 + 10).setY(y)
				.setHeight(height).setWidth(w1);
		popupSetPincode.attachWidget(plugin, cancelButton2);
		menuButtons.add(cancelButton2);
		BITButtons.put(cancelButton2.getId(), "setPincodeCancel");

		// removeButton
		GenericButton removeButton = new GenericButton("Remove");
		if (BITDigiLock.isLocked(block)) {
			removeButton.setAuto(false).setX(x - w1 - 10).setY(y)
					.setHeight(height).setWidth(w1);
			removeButton.setTooltip("Press Remove to delete the lock.");
			removeButton.setEnabled(true);
			menuButtons.add(removeButton);
			BITButtons.put(removeButton.getId(), "setPincodeRemove");
			popupSetPincode.attachWidget(plugin, removeButton);
		} else {
			menuButtons.remove(removeButton);
			BITButtons.remove("setPincodeRemove");
			popupSetPincode.removeWidget(removeButton);
			//removeButton.setEnabled(false).setDirty(true);
		}
		popupSetPincode.setDirty(true);

		// Open Window
		popupSetPincode.setTransparent(true);
		sPlayer.getMainScreen().attachPopupScreen(popupSetPincode);

	}

	public static void cleanupSetPincode(SpoutPlayer sPlayer) {
		sPlayer.getMainScreen().removeWidget(popupSetPincode);
	}

	public static void cleanupGetPincode(SpoutPlayer sPlayer) {
		sPlayer.getMainScreen().removeWidget(popupGetPincode);
	}

}
