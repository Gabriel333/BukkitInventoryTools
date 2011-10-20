package dk.gabriel333.BukkitInventoryTools.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.getspout.spout.inventory.CustomMCInventory;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import de.Keyle.MyWolf.MyWolfPlugin;
import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.BukkitInventoryTools.BITDigiLock;
import dk.gabriel333.BukkitInventoryTools.Sort.G333Inventory;
import dk.gabriel333.Library.G333Config;


public class BITPlayer {
	
	private BIT plugin;
	
	public BITPlayer(){
		super();
	}
	
	public BITPlayer(Plugin plugin) {
		plugin = this.plugin;
	}

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

	// USERDATA DigiLock
	public static Map<Integer, PopupScreen> popupScreen = new HashMap<Integer, PopupScreen>();
	public static Map<Integer, Integer> userno = new HashMap<Integer, Integer>();
	public static Map<Integer, GenericTextField> pincode = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> owner = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> closetimer = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> coOwners = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> useCost = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> connectedTo = new HashMap<Integer, GenericTextField>();

    // Buttons for DigiLock
	public static HashMap<UUID, String> BITButtons = new HashMap<UUID, String>();

	/**
	 * 
	 * @param sPlayer
	 */
	public void cleanupPopupScreen(SpoutPlayer sPlayer) {
		int id = sPlayer.getEntityId();
		popupScreen.get(id).removeWidgets(BIT.plugin);
		popupScreen.get(id).setDirty(true);
		sPlayer.getMainScreen().removeWidgets(BIT.plugin);
	}

	/**
	 * 
	 * @param sBlock
	 * @return
	 */
	public static int getPincodeBlock(SpoutBlock sBlock) {
		switch (sBlock.getTypeId()) {
		case 23:
			return 23; // Dispenser - looks nice.
		case 47:
			return 47; // Bookshelf - looks nice.
		case 54:
			return 95; // Chest - looks nice.
		case 61:
			return 61; // Furnace - looks nice.
		case 62:
			return 62; // Burning Furnace
		case 63:
			return 63; //SIGN_POST
		case 64:
			// return 324; // Wooden door
			return 95;
		case 68:
		    return 68;
		case 69:
			// return 69; // Lever
			return 95;
		case 71:
			// return 330; // Iron door
			return 95;
		case 77:
			// return 77; // Stone button
			return 95;
		case 96:
			return 96; // Trap_door
		}
		return 95;
	}

	/**
	 * 
	 * @param sBlock
	 * @return
	 */

	// ***************************************************************
	//
	// getPincode: Open GenericPopup and ask for pincode before to
	// unlock the inventory.
	//
	// ***************************************************************
	/**
	 * Open Generic PopupScreen and ask for the pincode.
	 * 
	 * @param sPlayer
	 * @param sBlock
	 * 
	 * @author Gabriel333
	 * @author Christian.L.Jensen
	 */
	public void getPincode(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		int y = 50, height = 20, width = 100;
		int x = 170;
		int id = sPlayer.getEntityId();
		addUserData(id);

		GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(
				getPincodeBlock(sBlock)));
		itemwidget.setX(x + 2 * height).setY(y);
		itemwidget.setHeight(height * 2).setWidth(height * 2)
				.setDepth(height * 2);
		itemwidget.setTooltip("Locked inventory");
		popupScreen.get(id).attachWidget(BIT.plugin, itemwidget);
		y = y + 3 * height;

		pincode.get(id).setText("");
		pincode.get(id).setTooltip("Enter the pincode and press unlock.");
		pincode.get(id).setCursorPosition(1).setMaximumCharacters(20);
		pincode.get(id).setX(x).setY(y);
		pincode.get(id).setHeight(height).setWidth(width);
		pincode.get(id).setPasswordField(true);
		pincode.get(id).setFocus(true);
		popupScreen.get(id).attachWidget(BIT.plugin, pincode.get(id));
		y = y + height;

		GenericButton unlockButton = new GenericButton("Unlock");
		unlockButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(width);
		BITButtons.put(unlockButton.getId(), "getPincodeUnlock");
		popupScreen.get(id).attachWidget(BIT.plugin, unlockButton);

		GenericButton cancelButton = new GenericButton("Cancel");
		cancelButton.setAuto(false).setX(x + width + 10).setY(y)
				.setHeight(height).setWidth(width);
		popupScreen.get(id).attachWidget(BIT.plugin, cancelButton);
		BITButtons.put(cancelButton.getId(), "getPincodeCancel");

		// Open Window
		popupScreen.get(id).setTransparent(true);
		sPlayer.getMainScreen().attachPopupScreen(popupScreen.get(id));
	}

	/**
	 * setPincode - Open GenericPopup and enter a pincode to lock the inventory.
	 * 
	 * @param sPlayer
	 * @param sBlock
	 */
	public void setPincode(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		int id = sPlayer.getEntityId();
		int height = 20;
		int x, y, w1, w2, w3, w4;
		addUserData(id);
		if (BITDigiLock.isLocked(sBlock)) {
			BITDigiLock digilock = BITDigiLock.loadDigiLock(sBlock);
			pincode.get(id).setText(digilock.getPincode());
			owner.get(id).setText(digilock.getOwner());
			coOwners.get(id).setText(digilock.getCoOwners());
			closetimer.get(id).setText(
					Integer.toString(digilock.getClosetimer()));
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
		GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(
				getPincodeBlock(sBlock)));
		itemwidget.setX(x + 2 * height).setY(y);
		itemwidget.setHeight(height * 2).setWidth(height * 2)
				.setDepth(height * 2);
		if (!BITDigiLock.isLocked(sBlock)) {
			itemwidget.setTooltip("Unlocked inventory");
		} else {
			itemwidget.setTooltip("Locked inventory");
		}
		popupScreen.get(id).attachWidget(BIT.plugin, itemwidget);
		y = y + 3 * height;

		GenericLabel costToCreate = new GenericLabel("CostToCreate: "
				+ String.valueOf(G333Config.DIGILOCK_COST));
		costToCreate.setAuto(true).setX(175).setY(y-10).setHeight(10)
				.setWidth(140);
		costToCreate.setTooltip("The cost to create a new DigiLock");
		popupScreen.get(id).attachWidget(BIT.plugin, costToCreate);

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
		popupScreen.get(id).attachWidget(BIT.plugin, ownerButton);
		BITButtons.put(ownerButton.getId(), "OwnerButton");
		// owner1
		owner.get(id).setTooltip("Owner of the DigiLock");
		owner.get(id).setCursorPosition(1).setMaximumCharacters(20);
		owner.get(id).setX(x + w1 + 1).setY(y);
		owner.get(id).setHeight(height).setWidth(w2);
		popupScreen.get(id).attachWidget(BIT.plugin, owner.get(id));

		// closetimerButton
		GenericButton closetimerButton = new GenericButton("Closetimer");
		closetimerButton.setAuto(false).setX(x + w1 + w2 + 10).setY(y)
				.setHeight(height).setWidth(w1);
		closetimerButton.setTooltip("Set closetimer");
		popupScreen.get(id).attachWidget(BIT.plugin, closetimerButton);
		BITButtons.put(closetimerButton.getId(), "ClosetimerButton");
		// closetimer
		closetimer.get(id).setTooltip("Autoclosing time in sec.");
		closetimer.get(id).setCursorPosition(1).setMaximumCharacters(4);
		closetimer.get(id).setX(x + w1 + 1 + w2 + 10 + w1 + 1).setY(y);
		closetimer.get(id).setHeight(height).setWidth(w3);
		popupScreen.get(id).attachWidget(BIT.plugin, closetimer.get(id));

		// useCostButton
		GenericButton useCostButton = new GenericButton("Use cost");
		useCostButton.setAuto(false).setX(x + w1 + w2 + 10 + w1 + w3 + 10)
				.setY(y).setHeight(height).setWidth(w1);
		useCostButton.setTooltip("Set cost");
		popupScreen.get(id).attachWidget(BIT.plugin, useCostButton);
		BITButtons.put(useCostButton.getId(), "UseCostButton");
		// useCost1
		useCost.get(id).setTooltip("This is the cost to use the DigiLock");
		useCost.get(id).setCursorPosition(1).setMaximumCharacters(4);
		useCost.get(id).setX(x + w1 + w2 + 10 + w1 + w3 + 10 + w1 + 1).setY(y);
		useCost.get(id).setHeight(height).setWidth(w4);
		popupScreen.get(id).attachWidget(BIT.plugin, useCost.get(id));
		y = y + height + 1;

		// setCoOwnerButton
		GenericButton CoOwnerButton = new GenericButton("CoOwners");
		CoOwnerButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(w1);
		CoOwnerButton.setTooltip("CoOwners must be seperated by a comma.");
		popupScreen.get(id).attachWidget(BIT.plugin, CoOwnerButton);
		BITButtons.put(CoOwnerButton.getId(), "CoOwnerButton");
		// listOfCoOwners
		coOwners.get(id).setX(x + w1 + 1).setY(y).setWidth(340)
				.setHeight(height);
		coOwners.get(id).setMaximumCharacters(200);
		coOwners.get(id).setText(coOwners.get(id).getText());
		popupScreen.get(id).attachWidget(BIT.plugin, coOwners.get(id));
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
		pincode.get(id).setFocus(true);
		popupScreen.get(id).attachWidget(BIT.plugin, pincode.get(id));
		y = y + height;

		// lockButton
		GenericButton lockButton = new GenericButton("Lock");
		lockButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(w1);
		lockButton.setTooltip("Enter/change the pincode and press lock.");
		popupScreen.get(id).attachWidget(BIT.plugin, lockButton);
		BITButtons.put(lockButton.getId(), "setPincodeLock");

		// cancelButton
		GenericButton cancelButton2 = new GenericButton("Cancel");
		cancelButton2.setAuto(false).setX(x + w1 + 10).setY(y)
				.setHeight(height).setWidth(w1);
		popupScreen.get(id).attachWidget(BIT.plugin, cancelButton2);
		BITButtons.put(cancelButton2.getId(), "setPincodeCancel");

		// removeButton
		if (BITDigiLock.isLocked(sBlock)) {
			GenericButton removeButton = new GenericButton("Remove");
			removeButton.setAuto(false).setX(x - w1 - 10).setY(y)
					.setHeight(height).setWidth(w1);
			removeButton.setTooltip("Press Remove to delete the lock.");
			removeButton.setEnabled(true);
			BITButtons.put(removeButton.getId(), "setPincodeRemove");
			popupScreen.get(id).attachWidget(BIT.plugin, removeButton);
		}

		// Open Window
		// popupScreen.get(id).setDirty(true);
		popupScreen.get(id).setTransparent(true);
		sPlayer.getMainScreen().attachPopupScreen(popupScreen.get(id));

	}

	
	public static void removeUserData(int id) {
		if (BITPlayer.userno.containsKey(id)) {
			// DigiLock
			userno.remove(id);
			popupScreen.remove(id);
			pincode.remove(id);
			owner.remove(id);
			coOwners.remove(id);
			closetimer.remove(id);
			useCost.remove(id);
			connectedTo.remove(id);
		}
	}

	public static void addUserData(int id) {
		if (!BITPlayer.userno.containsKey(id)) {
			//DigiLock
			userno.put(id, new Integer(id));
			popupScreen.put(id, new GenericPopup());
			pincode.put(id, new GenericTextField());
			owner.put(id, new GenericTextField());
			coOwners.put(id, new GenericTextField());
			closetimer.put(id, new GenericTextField());
			useCost.put(id, new GenericTextField());
			connectedTo.put(id, new GenericTextField());
		}
	}
	
/*	public static void clearAllUserData() {
			// DigiLock
			BITPlayer.userno.clear();
			BITPlayer.popupScreen.clear();
			BITPlayer.pincode.clear();
			BITPlayer.owner.clear();
			BITPlayer.coOwners.clear();
			BITPlayer.closetimer.clear();
			BITPlayer.useCost.clear();
			BITPlayer.connectedTo.clear();
			// BITBook
			BITBook.popupScreen.clear();
			BITBook.titleGUI.clear();
			BITBook.authorGUI.clear();
			BITBook.coAuthorsGUI.clear();
			BITBook.masterCopyGUI.clear();
			BITBook.forceBookToPlayerInventoryGUI.clear();
			BITBook.canBeMovedFromInventoryGUI.clear();
			BITBook.copyTheBookWhenMovedGUI.clear();
			BITBook.useCostGUI.clear();
			G333Messages.showInfo("Userdata has been cleared");
	}*/

}
