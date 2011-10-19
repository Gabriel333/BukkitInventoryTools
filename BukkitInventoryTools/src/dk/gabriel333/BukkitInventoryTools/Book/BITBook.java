package dk.gabriel333.BukkitInventoryTools.Book;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;

public class BITBook {

	private BIT plugin;

	public BITBook() {
		super();
	}

	public BITBook(Plugin plugin) {
		plugin = this.plugin;
	}

	protected SpoutBlock sBlock;
	protected int bookId;
	protected int slotNo;
	protected String title;
	protected String author;
	protected String coAuthors;
	protected int numberOfPages;
	protected GenericTextField[] pages;
	protected Boolean masterCopy;
	protected int masterCopyId;
	protected Boolean forceBookToPlayerInventory;
	protected Boolean canBeMovedFromInventory;
	protected Boolean copyTheBookWhenMoved;
	protected int useCost;

	/**
	 * Contructs a new BITBook
	 * 
	 * @param bookId
	 * @param sBlock
	 * @param slotNo
	 * @param title
	 * @param author
	 * @param coAuthors
	 * @param numberOfPages
	 * @param pages
	 * @param masterCopy
	 * @param masterCopyId
	 * @param forceBookToPlayerInventory
	 * @param canBeMovedFromInventory
	 * @param copyTheBookWhenMoved
	 * @param useCost
	 */
	BITBook(int bookId, SpoutBlock sBlock, int slotNo, String title,
			String author, String coAuthors, int numberOfPages,
			GenericTextField[] pages, Boolean masterCopy, int masterCopyId,
			Boolean forceBookToPlayerInventory,
			Boolean canBeMovedFromInventory, Boolean copyTheBookWhenMoved,
			int useCost) {
		this.bookId = bookId;
		this.sBlock = sBlock;
		this.slotNo = slotNo;
		this.title = title;
		this.author = author;
		this.coAuthors = coAuthors;
		this.numberOfPages = numberOfPages;
		this.pages = pages;
		this.masterCopy = masterCopy;
		this.masterCopyId = masterCopyId;
		this.forceBookToPlayerInventory = forceBookToPlayerInventory;
		this.canBeMovedFromInventory = canBeMovedFromInventory;
		this.copyTheBookWhenMoved = copyTheBookWhenMoved;
		this.useCost = useCost;
	}

	protected Map<Integer, BITBook> bitBooks = new HashMap<Integer, BITBook>();
	public static Map<Integer, PopupScreen> popupScreen = new HashMap<Integer, PopupScreen>();
	public static Map<UUID, String> BITButtons = new HashMap<UUID, String>();

	// Parameters for the bookPopupScreen
	public static Map<Integer, GenericTextField> titleGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> authorGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> coAuthorsGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> masterCopyGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> forceBookToPlayerInventoryGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> canBeMovedFromInventoryGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> copyTheBookWhenMovedGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> useCostGUI = new HashMap<Integer, GenericTextField>();

	public void setBitBook(int bookId, SpoutBlock sBlock, int slotNo,
			String title, String author, String coAuthors, int numberOfPages,
			GenericTextField[] pages, Boolean masterCopy, int masterCopyId,
			Boolean forceBookToPlayerInventory,
			Boolean canBeMovedFromInventory, Boolean copyTheBookWhenMoved,
			int useCost) {
		this.bookId = bookId;
		this.sBlock = sBlock;
		this.slotNo = slotNo;
		this.title = title;
		this.author = author;
		this.coAuthors = coAuthors;
		this.numberOfPages = numberOfPages;
		this.pages = pages;
		this.masterCopy = masterCopy;
		this.masterCopyId = masterCopyId;
		this.forceBookToPlayerInventory = forceBookToPlayerInventory;
		this.canBeMovedFromInventory = canBeMovedFromInventory;
		this.copyTheBookWhenMoved = copyTheBookWhenMoved;
		this.useCost = useCost;
	}

	public int getBookId() {
		return bookId;
	}

	public int getNextBookId() {
		int nextId = 1;
		if (!bitBooks.isEmpty()) {
			while (bitBooks.containsKey(nextId)) {
				nextId++;
			}
		}
		return nextId;
	}

	public String getAuthor() {
		return author;
	}

	public String getCoAuthors() {
		return coAuthors;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public static boolean isWritten() {
		// TODO: check if the book is written (true) or if it is empty
		return false;
	}

	public void read(Player player, int page) {
		// TODO: Open Book and show it on the Screen
	}

	public GenericTextField getPage(int pageNo) {
		if (pageNo < 0 || pageNo > numberOfPages) {
			return null;
		} else {
			return pages[pageNo];
		}
	}

	public int pageCount() {
		return numberOfPages;
	}

	protected final static Material writeableMaterials[] = { Material.BOOK,
			Material.PAINTING, Material.PAPER, Material.MAP, Material.SIGN,
			Material.SIGN_POST, Material.WALL_SIGN };

	/**
	 * Check if the Block is made of a writeable material.
	 * 
	 * @param block
	 * @return true or false
	 */
	public static boolean isWriteable(Block block) {
		for (Material i : writeableMaterials) {
			if (i == block.getType())
				return true;
		}
		return false;
	}

	public static boolean isWriteable(Material material) {
		for (Material i : writeableMaterials) {
			if (i == material)
				return true;
		}
		return false;
	}

	public void saveBook() {
		// TODO: save the Book to SQL
	}

	public BITBook loadBook() {
		// TODO: Load the book from SQL
		return null;
	}

	public void removeBook() {
		// TODO: remove the book from SQL
	}

	public void openBook(SpoutPlayer sPlayer, int pageNo) {
		int y = 20, itemHeight = 20;
		int x = 100;
		int textFieldHeight = 100, textFieldWidth = 320;
		int buttonHeight = 18, buttonWidth = 80;
		int textFieldHeight2 = 15, textFieldWidth2 = 76;

		int id = sPlayer.getEntityId();

		titleGUI.get(id).setText(title);
		authorGUI.get(id).setText(author);
		coAuthorsGUI.get(id).setText(coAuthors);
		masterCopyGUI.get(id).setText(masterCopy.toString());
		forceBookToPlayerInventoryGUI.get(id).setText(
				forceBookToPlayerInventory.toString());
		canBeMovedFromInventoryGUI.get(id).setText(
				canBeMovedFromInventory.toString());
		copyTheBookWhenMovedGUI.get(id)
				.setText(copyTheBookWhenMoved.toString());
		useCostGUI.get(id).setText(String.valueOf(useCost));

		// ItemWidget
		GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(340));
		itemwidget.setX(y).setY(y);
		itemwidget.setHeight(itemHeight).setWidth(itemHeight)
				.setDepth(itemHeight);
		itemwidget.setTooltip("Locked inventory");
		popupScreen.get(id).attachWidget(BIT.plugin, itemwidget);

		// + Button
		GenericButton nextPageBookButton = new GenericButton("+");
		nextPageBookButton.setAuto(false)
				.setX(x + textFieldWidth - 2 * buttonHeight - 5)
				.setY(y + 2 * itemHeight + 10 - buttonHeight - 1)
				.setHeight(buttonHeight).setWidth(buttonHeight);
		nextPageBookButton.setTooltip("Page "+String.valueOf(pageNo));
		popupScreen.get(id).attachWidget(BIT.plugin, nextPageBookButton);
		BITButtons.put(nextPageBookButton.getId(), "nextPageButton");

		// - Button
		GenericButton previousPageBookButton = new GenericButton("-");
		previousPageBookButton.setAuto(false)
				.setX(x + textFieldWidth - buttonHeight)
				.setY(y + 2 * itemHeight + 10 - buttonHeight - 1)
				.setHeight(buttonHeight).setWidth(buttonHeight);
		previousPageBookButton.setTooltip("Page "+String.valueOf(pageNo));
		popupScreen.get(id).attachWidget(BIT.plugin, previousPageBookButton);
		BITButtons.put(previousPageBookButton.getId(), "previousPageButton");

		// Title
		titleGUI.get(id).getText();
		titleGUI.get(id).setTooltip("Title of the book");
		titleGUI.get(id).setCursorPosition(1).setMaximumCharacters(30);
		titleGUI.get(id).setX(x).setY(y + 2 * itemHeight + 7 - buttonHeight);
		titleGUI.get(id).setHeight(textFieldHeight2)
				.setWidth(textFieldWidth2*2);
		popupScreen.get(id).attachWidget(BIT.plugin, titleGUI.get(id));

		y = y + 2 * itemHeight + 10;

		BITBook book = new BITBook();
		book.loadBook();
		// Page
		GenericTextField page = new GenericTextField();
		// page=book.getPage(1);
		page.setText("");
		page.setTooltip("Enter the text in the book.");
		page.setX(x).setY(y);
		page.setHeight(textFieldHeight).setWidth(textFieldWidth);
		// page.setMarginLeft(20).setMarginBottom(20);
		page.setCursorPosition(1).setMaximumCharacters(1000);
		page.setMaximumLines(8);
		page.setFocus(true);
		popupScreen.get(id).attachWidget(BIT.plugin, page);
		y = y + textFieldHeight + 10;

		// SaveButton
		GenericButton saveButton = new GenericButton("Save");
		saveButton.setAuto(false)
				.setX(x + textFieldWidth - 2 * (buttonWidth - 20) - 5).setY(y)
				.setHeight(buttonHeight).setWidth(buttonWidth - 20);
		BITButtons.put(saveButton.getId(), "saveBookButton");
		popupScreen.get(id).attachWidget(BIT.plugin, saveButton);

		// cancelButton
		GenericButton cancelBookButton = new GenericButton("Cancel");
		cancelBookButton.setAuto(false)
				.setX(x + textFieldWidth - buttonWidth + 20).setY(y)
				.setHeight(buttonHeight).setWidth(buttonWidth - 20);
		popupScreen.get(id).attachWidget(BIT.plugin, cancelBookButton);
		BITButtons.put(cancelBookButton.getId(), "cancelBookButton");

		x = 10;
		y = 67;

		// masterCopyButton
		GenericButton masterCopyButton = new GenericButton("Master:"
				+ yesNo(masterCopy));
		masterCopyButton.setAuto(false).setX(x).setY(y).setHeight(buttonHeight)
				.setWidth(buttonWidth);
		popupScreen.get(id).attachWidget(BIT.plugin, masterCopyButton);
		BITButtons.put(masterCopyButton.getId(), "masterCopyButton");
		y = y + buttonHeight + 1;

		// forceBookToPlayerInventoryButton
		GenericButton forceBookToPlayerInventoryButton = new GenericButton(
				"ToPlayer:" + yesNo(forceBookToPlayerInventory));
		forceBookToPlayerInventoryButton.setAuto(false).setX(x).setY(y)
				.setHeight(buttonHeight).setWidth(buttonWidth);
		popupScreen.get(id).attachWidget(BIT.plugin,
				forceBookToPlayerInventoryButton);
		BITButtons.put(forceBookToPlayerInventoryButton.getId(),
				"forceBookToPlayerInventoryButton");
		y = y + buttonHeight + 1;

		// canBeMovedFromInventoryButton
		GenericButton canBeMovedFromInventoryButton = new GenericButton(
				"Moveable:" + yesNo(canBeMovedFromInventory));
		canBeMovedFromInventoryButton.setAuto(false).setX(x).setY(y)
				.setHeight(buttonHeight).setWidth(buttonWidth);
		popupScreen.get(id).attachWidget(BIT.plugin,
				canBeMovedFromInventoryButton);
		BITButtons.put(canBeMovedFromInventoryButton.getId(),
				"canBeMovedFromInventoryButton");
		y = y + buttonHeight + 1;

		// copyTheBookWhenMovedButton
		GenericButton copyTheBookWhenMovedButton = new GenericButton("Copy:"
				+ yesNo(copyTheBookWhenMoved));
		copyTheBookWhenMovedButton.setAuto(false).setX(x).setY(y)
				.setHeight(buttonHeight).setWidth(buttonWidth);
		popupScreen.get(id)
				.attachWidget(BIT.plugin, copyTheBookWhenMovedButton);
		BITButtons.put(copyTheBookWhenMovedButton.getId(),
				"copyTheBookWhenMovedButton");
		y = y + buttonHeight + 1;

		// useCost
		useCostGUI.get(id).getText();
		useCostGUI.get(id).setTooltip("Enter the cost to read the book.");
		useCostGUI.get(id).setCursorPosition(1).setMaximumCharacters(20);
		useCostGUI.get(id).setX(x + 2).setY(y);
		useCostGUI.get(id).setHeight(textFieldHeight2)
				.setWidth(textFieldWidth2);
		popupScreen.get(id).attachWidget(BIT.plugin, useCostGUI.get(id));
		y = y + buttonHeight + 2;

		// Author
		authorGUI.get(id).getText();
		authorGUI.get(id).setTooltip("Author of the book.");
		authorGUI.get(id).setCursorPosition(1).setMaximumCharacters(20);
		authorGUI.get(id).setX(x + 2).setY(y);
		authorGUI.get(id).setHeight(textFieldHeight2).setWidth(textFieldWidth2);
		popupScreen.get(id).attachWidget(BIT.plugin, authorGUI.get(id));
		y = y + buttonHeight + 2;

		// coAuthor
		coAuthorsGUI.get(id).getText();
		coAuthorsGUI.get(id).setTooltip("coAuthors of the book.");
		coAuthorsGUI.get(id).setCursorPosition(1).setMaximumCharacters(50);
		coAuthorsGUI.get(id).setX(x + 2).setY(y);
		coAuthorsGUI.get(id).setHeight(textFieldHeight2)
				.setWidth(textFieldWidth2 * 3 + 40);
		popupScreen.get(id).attachWidget(BIT.plugin, coAuthorsGUI.get(id));

		// Open Window
		popupScreen.get(id).setTransparent(true);
		sPlayer.getMainScreen().attachPopupScreen(popupScreen.get(id));

	}

	public void cleanupPopupScreen(SpoutPlayer sPlayer) {
		int playerId = sPlayer.getEntityId();
		popupScreen.get(playerId).removeWidgets(BIT.plugin);
		popupScreen.get(playerId).setDirty(true);
		sPlayer.getMainScreen().removeWidgets(BIT.plugin);
	}

	private String yesNo(Boolean b) {
		if (b)
			return "Yes";
		else
			return "No";
	}

}