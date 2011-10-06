package dk.gabriel333.BukkitInventoryTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;

import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BITPlayer;


public abstract class BITPopupScreen implements PopupScreen {
	
	public BITPlayer bPlayer;
	
	public static ArrayList<GenericButton> menuButtons = new ArrayList<GenericButton>();
	public static HashMap<UUID, String> BITButtons = new HashMap<UUID, String>();

		// ***************************************************************
		// getPincode: Open GenericPopup and ask for pincode before to
		// unlock the inventory.
		// ***************************************************************
	public void getPincode(SpoutPlayer sPlayer, SpoutBlock block) {
		int y = 50, height = 20, width = 100;
		int x = 170;

		GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(95));
		// GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(
		// getPincodeBlock(block)));
		itemwidget.setX(x + 2 * height).setY(y);
		itemwidget.setHeight(height * 2).setWidth(height * 2)
				.setDepth(height * 2);
		itemwidget.setTooltip("Locked inventory");
		bPlayer.popupGetPincode.attachWidget(BIT.plugin, itemwidget);
		y = y + 3 * height;

		bPlayer.pincode2.setText("");
		bPlayer.pincode2.setTooltip("Enter the pincode and press unlock.");
		bPlayer.pincode2.setCursorPosition(1).setMaximumCharacters(10);
		bPlayer.pincode2.setX(x).setY(y);
		bPlayer.pincode2.setHeight(height).setWidth(width);
		bPlayer.popupGetPincode.attachWidget(BIT.plugin, bPlayer.pincode2);
		y = y + height;

		GenericButton unlockButton = new GenericButton("Unlock");
		unlockButton.setAuto(false).setX(x).setY(y).setHeight(height)
				.setWidth(width);
		// unlockButton.setTooltip("Enter the pincode and press unlock.");
		menuButtons.add(unlockButton);
		BITButtons.put(unlockButton.getId(), "getPincodeUnlock");
		bPlayer.popupGetPincode.attachWidget(BIT.plugin, unlockButton);

		GenericButton cancelButton = new GenericButton("Cancel");
		cancelButton.setAuto(false).setX(x + width + 10).setY(y)
				.setHeight(height).setWidth(width);
		// cancelButton.setTooltip("Cancel");
		bPlayer.popupGetPincode.attachWidget(BIT.plugin, cancelButton);
		menuButtons.add(cancelButton);
		BITButtons.put(cancelButton.getId(), "getPincodeCancel");

		// Open Window
		bPlayer.popupGetPincode.setTransparent(true);
		sPlayer.getMainScreen().attachPopupScreen(bPlayer.popupGetPincode);
	}

}

