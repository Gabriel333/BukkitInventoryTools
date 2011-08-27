package dk.rocologo.BukkitInventoryTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BITInventoryMenu {
	
	public static BIT plugin;

	public BITInventoryMenu(BIT plugin) {
		BITInventoryMenu.plugin = plugin;
	}
	
	public static ArrayList<GenericButton> menuButtons = new ArrayList<GenericButton>();
	public static HashMap<UUID, String> sortInventoryMenuButtons = new HashMap<UUID, String>();
	public static PopupScreen popup = new GenericPopup(); // Create a new popup
	
	public static GenericTextField textfieldPincode;
	public static GenericTextField textfieldOwner;
	
	public static void openMenu(SpoutPlayer sPlayer) {
		
		GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(35)); //This makes it display Wool
		itemwidget.setData((short) 11); //Wool have data 0-15, 8 is gray, 11 is blue.
		itemwidget.setX(370).setY(50);
		itemwidget.setHeight(10).setWidth(10).setDepth(10); //Due to that an itemwidget is 3D you need 3 dimensions.
		itemwidget.setTooltip("Gray wool"); //Displays a tooltip saying "Gray wool" the hovering over the widget
		popup.attachWidget(plugin, itemwidget);

		GenericLabel label = new GenericLabel("Inventory Menu");
		label.setAuto(false).setX(300).setY(30).setHeight(20).setWidth(50);
		label.setAlign(WidgetAnchor.CENTER_LEFT);
		//label.setTextColor(new Color(1.0F, 0, 0, 1.0F)); //This makes the label red.
		//label.shiftYPos(20); //This moves the label down 20 in Y-Axis
		//label.shiftXPos(20);
		popup.attachWidget(plugin, label); //The plugin part does setVisible and setDirty automatically.
		// change label text later
		// label.setText("Hello").setDirty(true);
		
		GenericButton sortButton =new GenericButton("Sort");
		sortButton.setAuto(false).setX(300).setY(50).setHeight(20).setWidth(50);
		//button.setHexColor(1); //This makes the button text ????yellow.
		//button.setHoverColor(2); //When you hover over with a mouse this makes the text ????red.
		sortButton.setTooltip("Sort your inventory.");
		popup.attachWidget(plugin, sortButton); // Attach the widget to the popup
		
		GenericButton lockButton =new GenericButton("Lock");
		lockButton.setAuto(false).setX(300).setY(70).setHeight(20).setWidth(50);
		//button.setHexColor(1); //This makes the button text ????yellow.
		//button.setHoverColor(2); //When you hover over with a mouse this makes the text ????red.
		lockButton.setTooltip("Enter 4 digits pincode and press lock.");
		popup.attachWidget(plugin, lockButton); // Attach the widget to the popup
		
		GenericButton closeButton =new GenericButton("Close");
		closeButton.setAuto(false).setX(300).setY(90).setHeight(20).setWidth(50);
		//button.setHexColor(1); //This makes the button text ????yellow.
		//button.setHoverColor(2); //When you hover over with a mouse this makes the text ????red.
		closeButton.setTooltip("Sort your inventory.");
		popup.attachWidget(plugin, closeButton); // Attach the widget to the popup
		
		textfieldPincode = new GenericTextField();
		textfieldPincode.setText("0000"); //The default text
		textfieldPincode.setTooltip("Enter 4 digits pincode and press lock.");
		//textfieldPincode.setFieldColor(hex);
		//textfieldPincode.setAnchor(WidgetAnchor.CENTER_CENTER);
		//textfieldPincode.setMaximumCharacters(4);
		textfieldPincode.setCursorPosition(1); //Puts the cursor on the third spot
		//textfieldPincode.setFieldColor(new Color(1.0F, 1.0F, 1.0F, 1.0F)); //Makes the text-entry area white
		//textfieldPincode.setBordorColor(new Color(0, 0, 0, 1.0F)); //Makes the border black
		textfieldPincode.setMaximumCharacters(4); //Can max write 10 Characters
		textfieldPincode.setX(355).setY(70);
		textfieldPincode.setHeight(20).setWidth(50); //This makes the textfield 20*200 in size.
		popup.attachWidget(plugin, textfieldPincode);
		
		textfieldOwner = new GenericTextField();
		textfieldOwner.setText(sPlayer.getName()); //The default text
		textfieldOwner.setTooltip("Enter 4 digits pincode and press lock.");
		//textfieldPincode.setFieldColor(hex);
		//textfieldPincode.setAnchor(WidgetAnchor.CENTER_CENTER);
		//textfieldPincode.setMaximumCharacters(4);
		textfieldOwner.setCursorPosition(1); //Puts the cursor on the third spot
		//textfieldPincode.setFieldColor(new Color(1.0F, 1.0F, 1.0F, 1.0F)); //Makes the text-entry area white
		//textfieldPincode.setBordorColor(new Color(0, 0, 0, 1.0F)); //Makes the border black
		textfieldOwner.setMaximumCharacters(20); //Can max write 10 Characters
		textfieldOwner.setX(355).setY(90);
		textfieldOwner.setHeight(20).setWidth(90); //This makes the textfield 20*200 in size.
		popup.attachWidget(plugin, textfieldOwner);

		// GenericGradient gradient = new GenericGradient();
		// gradient.setTopColor(new Color(1.0F, 1.0F, 1.0F, 1.0F)); //Red, Green, Blue, Alpha. This makes the gradient white
		// gradient.setBottomColor(new Color(1.0F, 0, 0, 1.0F)); //Red, Green, Blue, Alpha. This makes the gradient red
		// gradient.setX(300).setY(150).setHeight(32).setWidth(32);//This makes it 32*32.
		// popup.attachWidget(plugin, gradient);
		
		// GenericSlider slider = new GenericSlider();
		// slider.setSliderPosition(0.25F); //This defaults the slider to 1/4 of the way
		// slider.setX(300).setY(200);
		// slider.setHeight(10).setWidth(50); //Makes the slider 10*100.
		// popup.attachWidget(plugin, slider);
		
		// GenericTexture texture = new GenericTexture();
		// texture.setUrl("http://dl.dropbox.com/u/36067670/Safety/Icons/dialog-information.png"); //Have to be a png or jpg
		// texture.setX(300).setY(220);
		// texture.setWidth(32).setHeight(32); //Use the same size as the png here.
		// popup.attachWidget(plugin, texture);
		
		popup.setTransparent(false); 
		
		sPlayer.getMainScreen().attachPopupScreen(popup);
		
        //GenericLabel rankLabel = null;
        //UUID rankLabelId = (UUID) getRankLabels().get(player.getName());
        // rankLabel = (GenericLabel) sPlayer.getMainScreen().getWidget(rankLabelId);
	
		
		
		menuButtons.add(closeButton);
		sortInventoryMenuButtons.put(closeButton.getId(), "Close");	
		
		menuButtons.add(lockButton);
		sortInventoryMenuButtons.put(lockButton.getId(), "Lock");	

		menuButtons.add(sortButton);
		sortInventoryMenuButtons.put(sortButton.getId(), "Sort");	


		
	}

}
