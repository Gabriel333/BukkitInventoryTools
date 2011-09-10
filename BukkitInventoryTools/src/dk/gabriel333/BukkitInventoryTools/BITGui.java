package dk.gabriel333.BukkitInventoryTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.G333Config;

public class BITGui {

	public static BIT plugin;

	public BITGui(BIT plugin) {
		BITGui.plugin = plugin;
	}

	public static ArrayList<GenericButton> menuButtons = new ArrayList<GenericButton>();
	public static HashMap<UUID, String> BITButtons = new HashMap<UUID, String>();

	public static PopupScreen popupInventoryMenu = new GenericPopup();
	public static PopupScreen popupGetPincode = new GenericPopup();

	public static GenericTextField pincode;
	public static GenericTextField pincode2 = new GenericTextField();
	public static GenericTextField owner;

	public static void openMenu(SpoutPlayer sPlayer, Block block) {

		String pin;
		String own;
		pin = BITDigiLock.getPincodeFromSQL(sPlayer, block);
		own = BITDigiLock.getOwnerFromSQL(sPlayer, block);

		GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(95));
		itemwidget.setX(150).setY(100);
		itemwidget.setHeight(60).setWidth(60).setDepth(60);
		itemwidget.setTooltip("Locked Inventory");
		popupInventoryMenu.attachWidget(plugin, itemwidget);

		int y = 30, height = 20, width = 50;
		int x1 = 250;
		int x2 = 310;

		GenericLabel label = new GenericLabel("Inventory Menu");
		label.setAuto(false).setX(x1).setY(y).setHeight(height).setWidth(width);
		label.setAlign(WidgetAnchor.CENTER_LEFT);
		// label.setTextColor(new Color(1.0F, 0, 0, 1.0F)); //This makes the
		// label red.
		// label.shiftYPos(20); //This moves the label down 20 in Y-Axis
		// label.shiftXPos(20);
		popupInventoryMenu.attachWidget(plugin, label);
		// change label text later
		// label.setText("Hello").setDirty(true);
		y = y + height;

		GenericButton sortButton = new GenericButton("Sort");
		sortButton.setAuto(false).setX(x1).setY(y).setHeight(height)
				.setWidth(width);
		// button.setHexColor(1); //This makes the button text ????yellow.
		// button.setHoverColor(2); //When you hover over with a mouse this
		// makes the text ????red.
		sortButton.setTooltip("Sort your inventory.");
		popupInventoryMenu.attachWidget(plugin, sortButton); // Attach the
																// widget to the
																// popup
		y = y + height;

		GenericButton lockButton = new GenericButton("Lock");
		lockButton.setAuto(false).setX(x1).setY(y).setHeight(height)
				.setWidth(width);
		// button.setHexColor(1); 
		// button.setHoverColor(2); 
		lockButton.setTooltip("Enter 4 digits pincode and press lock.");
		popupInventoryMenu.attachWidget(plugin, lockButton);

		pincode = new GenericTextField();
		pincode.setText(pin); // The default text
		pincode.setTooltip("Enter 4 digits pincode and press lock.");
		// textfieldPincode.setFieldColor(hex);
		// textfieldPincode.setAnchor(WidgetAnchor.CENTER_CENTER);
		// textfieldPincode.setMaximumCharacters(4);
		pincode.setCursorPosition(1); // Puts the cursor on the third spot
		// textfieldPincode.setFieldColor(new Color(1.0F, 1.0F, 1.0F, 1.0F));
		// textfieldPincode.setBordorColor(new Color(0, 0, 0, 1.0F));
		pincode.setMaximumCharacters(4); // Can max write 10 Characters
		pincode.setX(x2).setY(y);
		pincode.setHeight(height).setWidth(width);
		popupInventoryMenu.attachWidget(plugin, pincode);

		menuButtons.add(lockButton);
		BITButtons.put(lockButton.getId(), "Lock");

		y = y + height;

		GenericButton resetButton = new GenericButton("Reset");
		resetButton.setAuto(false).setX(x1).setY(y).setHeight(height)
				.setWidth(width);
		// button.setHexColor(1);
		// button.setHoverColor(2);
		resetButton.setTooltip("Reset lock");
		popupInventoryMenu.attachWidget(plugin, resetButton);
		y = y + height;

		GenericButton ownerButton = new GenericButton("Owner");
		ownerButton.setAuto(false).setX(x1).setY(y).setHeight(height)
				.setWidth(width);
		// button.setHexColor(1); 
		// button.setHoverColor(2); 
		ownerButton.setTooltip("Enter owners name");
		popupInventoryMenu.attachWidget(plugin, ownerButton);
		owner = new GenericTextField();
		owner.setText(own); // The default text
		owner.setTooltip("Enter owners name and press owner.");
		// textfieldPincode.setFieldColor(hex);
		// textfieldPincode.setAnchor(WidgetAnchor.CENTER_CENTER);
		// textfieldPincode.setMaximumCharacters(4);
		owner.setCursorPosition(1); // Puts the cursor on the third spot
		// textfieldPincode.setFieldColor(new Color(1.0F, 1.0F, 1.0F, 1.0F));
		// textfieldPincode.setBordorColor(new Color(0, 0, 0, 1.0F));
		owner.setMaximumCharacters(20); 
		owner.setX(x2).setY(y);
		owner.setHeight(height).setWidth(2 * width);
		popupInventoryMenu.attachWidget(plugin, owner);
		y = y + height;

		GenericButton closeButton = new GenericButton("Close");
		closeButton.setAuto(false).setX(x1).setY(y).setHeight(height)
				.setWidth(width);
		// button.setHexColor(1); 
		// button.setHoverColor(2); 
		closeButton.setTooltip("Close the menu.");
		popupInventoryMenu.attachWidget(plugin, closeButton);
		y = y + height;

		// GenericTexture texture = new GenericTexture();
		// texture.setUrl("http://dl.dropbox.com/u/36067670/Safety/Icons/dialog-information.png");
		// //Have to be a png or jpg
		// texture.setX(300).setY(220);
		// texture.setWidth(32).setHeight(32); 
		// popup.attachWidget(plugin, texture);

		popupInventoryMenu.setTransparent(false);

		if (G333Config.g333Config.DEBUG_GUI)
			sPlayer.sendMessage("BITGui... was here");
		// sPlayer.closeActiveWindow();
		sPlayer.getMainScreen().attachPopupScreen(popupInventoryMenu);

		menuButtons.add(sortButton);
		BITButtons.put(sortButton.getId(), "Sort");

		menuButtons.add(ownerButton);
		BITButtons.put(ownerButton.getId(), "Owner");

		menuButtons.add(resetButton);
		BITButtons.put(resetButton.getId(), "Reset");

		menuButtons.add(closeButton);
		BITButtons.put(closeButton.getId(), "Close");
	}

	public static void openPincodeWindow(SpoutPlayer sPlayer) {
		int y = 50, height = 20, width = 100;
		int x = 170;

		GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(95));
		itemwidget.setX(x + 2 * height).setY(y);
		itemwidget.setHeight(height * 2).setWidth(height * 2)
				.setDepth(height * 2);
		itemwidget.setTooltip("Locked inventory");
		popupGetPincode.attachWidget(plugin, itemwidget);
		y = y + 3 * height;

		pincode2.setText("");
		pincode2.setTooltip("Enter 4 digits pincode and press unlock.");
		// pincode2.setAnchor(WidgetAnchor.CENTER_CENTER);
		// pincode2.setMaximumCharacters(5);
		pincode2.setCursorPosition(1);
		pincode2.setX(x).setY(y);
		pincode2.setHeight(height).setWidth(width);
		// textfieldPincode.setFieldColor(hex);
		// textfieldPincode.setFieldColor(new Color(1.0F, 1.0F, 1.0F, 1.0F));
		// textfieldPincode.setBordorColor(new Color(0, 0, 0, 1.0F));

		y = y + height;

		GenericButton unlockButton = new GenericButton("Enter pincode");
		unlockButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(width);
		unlockButton.setTooltip("Enter 4 digits pincode and press unlock.");
		// button.setHexColor(1);
		// button.setHoverColor(2);
		popupGetPincode.attachWidget(plugin, unlockButton);
		popupGetPincode.attachWidget(plugin, pincode2);

		// Open Window
		popupGetPincode.setTransparent(true);
		// sPlayer.closeActiveWindow();
		sPlayer.getMainScreen().attachPopupScreen(popupGetPincode);

		// Save button
		menuButtons.add(unlockButton);
		BITButtons.put(unlockButton.getId(), "Unlock");

	}

}
