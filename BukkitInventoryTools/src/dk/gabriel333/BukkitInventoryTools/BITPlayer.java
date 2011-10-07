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
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import de.Keyle.MyWolf.MyWolfPlugin;
import dk.gabriel333.Library.G333Inventory;

public class BITPlayer {
	
	protected Player player;

	public BITPlayer(SpoutPlayer sPlayer) {
		this.player = sPlayer;
	}

	public void getPlayer(SpoutPlayer sPlayer){
		this.player =  sPlayer;
	}
	
	public String getName(){
		return player.getName();
	}
	
	public void sortinventory(SpoutPlayer sPlayer, ScreenType screentype) {
		// sort the ordinary player inventory
		Inventory inventory = sPlayer.getInventory();
		G333Inventory.sortPlayerInventoryItems(sPlayer);

		// sort the SpoutBackpack if it exists and if it is opened.
		if (BIT.spoutbackpack && BIT.spoutBackpackHandler.isOpenSpoutBackpack(sPlayer)) {
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
	
	public static GenericTextField getPincode = new GenericTextField();
	public static ArrayList<GenericButton> menuButtons = new ArrayList<GenericButton>();
	public static HashMap<UUID, String> BITButtons = new HashMap<UUID, String>();

	// ***************************************************************
	// getPincode: Open GenericPopup and ask for pincode before to
	// unlock the inventory.
	// ***************************************************************
	public PopupScreen popupGetPincode = new GenericPopup();

	public void getPincode(SpoutPlayer sPlayer, SpoutBlock block) {
		int y = 50, height = 20, width = 100;
		int x = 170;

		GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(95));
		itemwidget.setX(x + 2 * height).setY(y);
		itemwidget.setHeight(height * 2).setWidth(height * 2)
				.setDepth(height * 2);
		itemwidget.setTooltip("Locked inventory");
		popupGetPincode.attachWidget(BIT.plugin, itemwidget);
		y = y + 3 * height;

		getPincode.setText("");
		getPincode.setTooltip("Enter the pincode and press unlock.");
		getPincode.setCursorPosition(1).setMaximumCharacters(10);
		getPincode.setX(x).setY(y);
		getPincode.setHeight(height).setWidth(width);
		getPincode.setPasswordField(true);
		popupGetPincode.attachWidget(BIT.plugin, getPincode);
		y = y + height;

		GenericButton unlockButton = new GenericButton("Unlock");
		unlockButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(width);
		// unlockButton.setTooltip("Enter the pincode and press unlock.");
		menuButtons.add(unlockButton);
		BITButtons.put(unlockButton.getId(), "getPincodeUnlock");
		popupGetPincode.attachWidget(BIT.plugin, unlockButton);

		GenericButton cancelButton = new GenericButton("Cancel");
		cancelButton.setAuto(false).setX(x + width + 10).setY(y)
				.setHeight(height).setWidth(width);
		// cancelButton.setTooltip("Cancel");
		popupGetPincode.attachWidget(BIT.plugin, cancelButton);
		menuButtons.add(cancelButton);
		BITButtons.put(cancelButton.getId(), "getPincodeCancel");

		// Open Window
		popupGetPincode.setTransparent(true);
		sPlayer.getMainScreen().attachPopupScreen(popupGetPincode);
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
	public PopupScreen popupSetPincode = new GenericPopup();
	public static GenericTextField setPincode = new GenericTextField();
	public static GenericTextField owner1 = new GenericTextField();
	public static GenericTextField closetimer1 = new GenericTextField();
	public static GenericTextField listOfCoOwners = new GenericTextField();
	public static GenericTextField connectedTo = new GenericTextField();
	public static GenericTextField useCost1 = new GenericTextField();
	
	public void setPincode(SpoutPlayer sPlayer, SpoutBlock block) {
		int height = 20;
		int x, y, w1, w2, w3, w4;

		BITDigiLock digilock;
		if (BITDigiLock.isLocked(block)) {
			sPlayer.sendMessage("the block was locked");
			digilock = BITDigiLock.loadDigiLock(sPlayer, block);
			setPincode.setText(digilock.getPincode());
			owner1.setText(digilock.getOwner());
			listOfCoOwners.setText(digilock.getCoOwners());
			closetimer1.setText(Integer.toString(digilock.getClosetimer()));
			useCost1.setText(Integer.toString(digilock.getUseCost()));
		} else {
			sPlayer.sendMessage("the block was NOT locked");
			setPincode.setText("");
			owner1.setText(sPlayer.getName());
			listOfCoOwners.setText("");
			closetimer1.setText("0");
			useCost1.setText("0");
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
		popupSetPincode.attachWidget(BIT.plugin, itemwidget);
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
		popupSetPincode.attachWidget(BIT.plugin, ownerButton);
		menuButtons.add(ownerButton);
		BITButtons.put(ownerButton.getId(), "OwnerButton");
		// owner1
		owner1.setTooltip("Owner of the DigiLock");
		owner1.setCursorPosition(1).setMaximumCharacters(20);
		owner1.setX(x + w1 + 1).setY(y);
		owner1.setHeight(height).setWidth(w2);
		popupSetPincode.attachWidget(BIT.plugin, owner1);

		// closetimerButton
		GenericButton closetimerButton = new GenericButton("Closetimer");
		closetimerButton.setAuto(false).setX(x + w1+w2+10).setY(y).setHeight(height)
				.setWidth(w1);
		closetimerButton.setTooltip("Set closetimer");
		popupSetPincode.attachWidget(BIT.plugin, closetimerButton);
		menuButtons.add(closetimerButton);
		BITButtons.put(closetimerButton.getId(), "ClosetimerButton");
		// closetimer1
		closetimer1.setTooltip("Autoclosing time in sec.");
		closetimer1.setCursorPosition(1).setMaximumCharacters(4);
		closetimer1.setX(x + w1+1+w2+ 10+ w1+1).setY(y);
		closetimer1.setHeight(height).setWidth(w3);
		popupSetPincode.attachWidget(BIT.plugin, closetimer1);
		
		//useCostButton
		GenericButton useCostButton = new GenericButton("Use cost");
		useCostButton.setAuto(false).setX(x + w1+w2+10+w1+w3+10).setY(y).setHeight(height)
				.setWidth(w1);
		useCostButton.setTooltip("Set cost");
		popupSetPincode.attachWidget(BIT.plugin, useCostButton);
		menuButtons.add(useCostButton);
		BITButtons.put(useCostButton.getId(), "UseCostButton");
		//useCost1
		useCost1.setTooltip("This is the cost to use the DigiLock");
		useCost1.setCursorPosition(1).setMaximumCharacters(4);
		useCost1.setX(x + w1+w2+10+w1+w3+10+w1 + 1).setY(y);
		useCost1.setHeight(height).setWidth(w4);
		popupSetPincode.attachWidget(BIT.plugin, useCost1);
		y = y + height+1;

		// setCoOwnerButton
		GenericButton CoOwnerButton = new GenericButton("CoOwners");
		CoOwnerButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(w1);
		CoOwnerButton.setTooltip("CoOwners must be seperated by a comma.");
		popupSetPincode.attachWidget(BIT.plugin, CoOwnerButton);
		menuButtons.add(CoOwnerButton);
		BITButtons.put(CoOwnerButton.getId(), "CoOwnerButton");
		// listOfCoOwners
		listOfCoOwners.setX(x + w1 + 1).setY(y).setWidth(340).setHeight(height);
		listOfCoOwners.setMaximumCharacters(200);
		listOfCoOwners.setText(listOfCoOwners.getText());
		popupSetPincode.attachWidget(BIT.plugin, listOfCoOwners);
		y = y + height;

		// Second row ------------X=170-270-370------------------------------
		y = 110;
		x = 180;
		w1 = 80;
		w2 = 80;
		// pincode3
		setPincode.setTooltip("Enter/change the pincode...");
		setPincode.setCursorPosition(1).setMaximumCharacters(10);
		setPincode.setX(x).setY(y);
		setPincode.setHeight(height).setWidth(w1);
		popupSetPincode.attachWidget(BIT.plugin, setPincode);
		y = y + height;

		// lockButton
		GenericButton lockButton = new GenericButton("Lock");
		lockButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(w1);
		lockButton.setTooltip("Enter/change the pincode and press lock.");
		popupSetPincode.attachWidget(BIT.plugin, lockButton);
		menuButtons.add(lockButton);
		BITButtons.put(lockButton.getId(), "setPincodeLock");

		// cancelButton
		GenericButton cancelButton2 = new GenericButton("Cancel");
		cancelButton2.setAuto(false).setX(x + w1 + 10).setY(y)
				.setHeight(height).setWidth(w1);
		popupSetPincode.attachWidget(BIT.plugin, cancelButton2);
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
			popupSetPincode.attachWidget(BIT.plugin, removeButton);
		} else {
			//menuButtons.remove(removeButton);
			//BITButtons.remove("setPincodeRemove");
			//popupSetPincode.removeWidget(removeButton);

		}
		popupSetPincode.setDirty(true);

		// Open Window
		popupSetPincode.setTransparent(true);
		sPlayer.getMainScreen().attachPopupScreen(popupSetPincode);

	}

	public void cleanupSetPincode(SpoutPlayer sPlayer) {
		popupSetPincode.removeWidgets(BIT.plugin);
		popupSetPincode.setDirty(true);
	}

	public void cleanupGetPincode(SpoutPlayer sPlayer) {
		popupGetPincode.removeWidgets(BIT.plugin);
		popupGetPincode.setDirty(true);
	}

}
