package dk.gabriel333.BukkitInventoryTools;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spout.inventory.CustomMCInventory;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import de.Keyle.MyWolf.MyWolfPlugin;
import dk.gabriel333.Library.G333Inventory;

public class BITPlayer {

	protected SpoutPlayer sPlayer;

	/**
	 * 
	 * Constructs a new BITPlayer
	 * 
	 */
	public BITPlayer(SpoutPlayer sPlayer) {
		this.sPlayer = sPlayer;
	}

	/**
	 * @return the SpoutPlayer
	 * 
	 */
	public void getPlayer(SpoutPlayer sPlayer) {
		this.sPlayer = sPlayer;
	}

	/**
	 * @return the SpoutPlayers name
	 * 
	 */
	public String getName() {
		return sPlayer.getName();
	}

	/**
	 * Method to sort the players inventory, his backpack or wolfs pack
	 * 
	 * @param sPlayer
	 * @param screentype
	 */
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

	// USERDATA FOR THE PINCODEPOPUP
	public static Map<Integer, Integer> userno = new HashMap<Integer, Integer>();
	public static Map<Integer, PopupScreen> pincodePopupScreen = new HashMap<Integer, PopupScreen>();
	public static Map<Integer, GenericTextField> pincode = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> owner = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> closetimer = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> coOwners = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> useCost = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> connectedTo = new HashMap<Integer, GenericTextField>();


	//public static ArrayList<GenericButton> menuButtons = new ArrayList<GenericButton>();
	public static HashMap<UUID, String> BITButtons = new HashMap<UUID, String>();

	// ***************************************************************
	// getPincode: Open GenericPopup and ask for pincode before to
	// unlock the inventory.
	// ***************************************************************
	public void getPincode(SpoutPlayer sPlayer, SpoutBlock block) {
		int y = 50, height = 20, width = 100;
		int x = 170;
		int id = sPlayer.getEntityId();

		GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(95));
		itemwidget.setX(x + 2 * height).setY(y);
		itemwidget.setHeight(height * 2).setWidth(height * 2)
				.setDepth(height * 2);
		itemwidget.setTooltip("Locked inventory");
		pincodePopupScreen.get(id).attachWidget(
						BIT.plugin, itemwidget);
		y = y + 3 * height;
		
		pincode.get(id).setText("");
		pincode.get(id).setTooltip("Enter the pincode and press unlock.");
		pincode.get(id).setCursorPosition(1).setMaximumCharacters(20);
		pincode.get(id).setX(x).setY(y);
		pincode.get(id).setHeight(height).setWidth(width);
		pincode.get(id).setPasswordField(true);
		pincodePopupScreen.get(id).attachWidget(
						BIT.plugin, pincode.get(id));
		y = y + height;

		GenericButton unlockButton = new GenericButton("Unlock");
		unlockButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(width);
		// unlockButton.setTooltip("Enter the pincode and press unlock.");
		//menuButtons.add(unlockButton);
		BITButtons.put(unlockButton.getId(), "getPincodeUnlock");
		pincodePopupScreen.get(id).attachWidget(BIT.plugin, unlockButton);

		GenericButton cancelButton = new GenericButton("Cancel");
		cancelButton.setAuto(false).setX(x + width + 10).setY(y)
				.setHeight(height).setWidth(width);
		// cancelButton.setTooltip("Cancel");
		pincodePopupScreen.get(id).attachWidget(BIT.plugin, cancelButton);
		//menuButtons.add(cancelButton);
		BITButtons.put(cancelButton.getId(), "getPincodeCancel");

		// Open Window
		pincodePopupScreen.get(id).setTransparent(true);
		sPlayer.getMainScreen().attachPopupScreen(
				pincodePopupScreen.get(id));
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
		int id = sPlayer.getEntityId();
		int height = 20;
		int x, y, w1, w2, w3, w4;

		if (BITDigiLock.isLocked(block)) {
			BITDigiLock digilock = BITDigiLock.loadDigiLock(block);
			pincode.get(id).setText(digilock.getPincode());
			owner.get(id).setText(digilock.getOwner());
			coOwners.get(id).setText(digilock.getCoOwners());
			closetimer.get(id).setText(Integer.toString(digilock.getClosetimer()));
			useCost.get(id).setText(Integer.toString(digilock.getUseCost()));
		} else {
			pincode.get(id).setText("");
			owner.get(id).setText(sPlayer.getName());
			coOwners.get(id).setText("");
			closetimer.get(id).setText("0");
			useCost.get(id).setText("0");
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
		pincodePopupScreen.get(id).attachWidget(BIT.plugin, itemwidget);
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
		pincodePopupScreen.get(id).attachWidget(BIT.plugin, ownerButton);
		//menuButtons.add(ownerButton);
		BITButtons.put(ownerButton.getId(), "OwnerButton");
		// owner1
		owner.get(id).setTooltip("Owner of the DigiLock");
		owner.get(id).setCursorPosition(1).setMaximumCharacters(20);
		owner.get(id).setX(x + w1 + 1).setY(y);
		owner.get(id).setHeight(height).setWidth(w2);
		pincodePopupScreen.get(id).attachWidget(BIT.plugin, owner.get(id));

		// closetimerButton
		GenericButton closetimerButton = new GenericButton("Closetimer");
		closetimerButton.setAuto(false).setX(x + w1 + w2 + 10).setY(y)
				.setHeight(height).setWidth(w1);
		closetimerButton.setTooltip("Set closetimer");
		pincodePopupScreen.get(id).attachWidget(BIT.plugin,
				closetimerButton);
		//menuButtons.add(closetimerButton);
		BITButtons.put(closetimerButton.getId(), "ClosetimerButton");
		// closetimer
		closetimer.get(id).setTooltip("Autoclosing time in sec.");
		closetimer.get(id).setCursorPosition(1).setMaximumCharacters(4);
		closetimer.get(id).setX(x + w1 + 1 + w2 + 10 + w1 + 1).setY(y);
		closetimer.get(id).setHeight(height).setWidth(w3);
		pincodePopupScreen.get(id).attachWidget(BIT.plugin, closetimer.get(id));

		// useCostButton
		GenericButton useCostButton = new GenericButton("Use cost");
		useCostButton.setAuto(false).setX(x + w1 + w2 + 10 + w1 + w3 + 10)
				.setY(y).setHeight(height).setWidth(w1);
		useCostButton.setTooltip("Set cost");
		pincodePopupScreen.get(id).attachWidget(BIT.plugin, useCostButton);
		//menuButtons.add(useCostButton);
		BITButtons.put(useCostButton.getId(), "UseCostButton");
		// useCost1
		useCost.get(id).setTooltip("This is the cost to use the DigiLock");
		useCost.get(id).setCursorPosition(1).setMaximumCharacters(4);
		useCost.get(id).setX(x + w1 + w2 + 10 + w1 + w3 + 10 + w1 + 1).setY(y);
		useCost.get(id).setHeight(height).setWidth(w4);
		pincodePopupScreen.get(id).attachWidget(BIT.plugin, useCost.get(id));
		y = y + height + 1;

		// setCoOwnerButton
		GenericButton CoOwnerButton = new GenericButton("CoOwners");
		CoOwnerButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(w1);
		CoOwnerButton.setTooltip("CoOwners must be seperated by a comma.");
		pincodePopupScreen.get(id).attachWidget(BIT.plugin, CoOwnerButton);
		//menuButtons.add(CoOwnerButton);
		BITButtons.put(CoOwnerButton.getId(), "CoOwnerButton");
		// listOfCoOwners
		coOwners.get(id).setX(x + w1 + 1).setY(y).setWidth(340).setHeight(height);
		coOwners.get(id).setMaximumCharacters(200);
		coOwners.get(id).setText(coOwners.get(id).getText());
		pincodePopupScreen.get(id)
				.attachWidget(BIT.plugin, coOwners.get(id));
		y = y + height;

		// Second row ------------X=170-270-370------------------------------
		y = 110;
		x = 180;
		w1 = 80;
		w2 = 80;
		// pincode3
		pincode.get(id).setTooltip("Enter/change the pincode...");
		pincode.get(id).setCursorPosition(1).setMaximumCharacters(20);
		pincode.get(id).setX(x).setY(y);
		pincode.get(id).setHeight(height).setWidth(w1);
		pincode.get(id).setPasswordField(false);
		pincodePopupScreen.get(id).attachWidget(BIT.plugin,
				pincode.get(id));
		y = y + height;

		// lockButton
		GenericButton lockButton = new GenericButton("Lock");
		lockButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(w1);
		lockButton.setTooltip("Enter/change the pincode and press lock.");
		pincodePopupScreen.get(id).attachWidget(BIT.plugin, lockButton);
		//menuButtons.add(lockButton);
		BITButtons.put(lockButton.getId(), "setPincodeLock");

		// cancelButton
		GenericButton cancelButton2 = new GenericButton("Cancel");
		cancelButton2.setAuto(false).setX(x + w1 + 10).setY(y)
				.setHeight(height).setWidth(w1);
		pincodePopupScreen.get(id).attachWidget(BIT.plugin, cancelButton2);
		//menuButtons.add(cancelButton2);
		BITButtons.put(cancelButton2.getId(), "setPincodeCancel");

		// removeButton
		if (BITDigiLock.isLocked(block)) {
			GenericButton removeButton = new GenericButton("Remove");
			removeButton.setAuto(false).setX(x - w1 - 10).setY(y)
					.setHeight(height).setWidth(w1);
			removeButton.setTooltip("Press Remove to delete the lock.");
			removeButton.setEnabled(true);
			//menuButtons.add(removeButton);
			BITButtons.put(removeButton.getId(), "setPincodeRemove");
			pincodePopupScreen.get(id).attachWidget(BIT.plugin,
					removeButton);
		} else {
			// menuButtons.remove(removeButton);
			// BITButtons.remove("setPincodeRemove");
			// popupSetPincode.removeWidget(removeButton);

		}
		pincodePopupScreen.get(id).setDirty(true);

		// Open Window
		pincodePopupScreen.get(id).setTransparent(true);
		sPlayer.getMainScreen().attachPopupScreen(
				pincodePopupScreen.get(id));

	}

	public void cleanupPincodePopupScreen(SpoutPlayer sPlayer) {
		int id = sPlayer.getEntityId();
		pincodePopupScreen.get(id).removeWidgets(BIT.plugin);
		pincodePopupScreen.get(id).setDirty(true);
	}

}
