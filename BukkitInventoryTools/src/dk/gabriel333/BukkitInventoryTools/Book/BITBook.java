package dk.gabriel333.BukkitInventoryTools.Book;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericItemWidget;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.PopupScreen;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.BukkitInventoryTools.BITEnums.BITInventoryType;

public class BITBook {

	private BIT plugin;

	public BITBook() {
		super();
	}

	public BITBook(Plugin plugin) {
		plugin = this.plugin;
	}

	protected int bookId;
	protected BITInventoryType inventoryType;
	protected SpoutBlock sBlock;
	protected int slotNo;
	protected String title;
	protected String author;
	protected String coAuthors;
	protected int numberOfPages;
	protected String[] pages;
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
			String author, String coAuthors, int numberOfPages, String[] pages,
			Boolean masterCopy, int masterCopyId,
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

	public static HashMap<Integer, BITBook> bitBooks = new HashMap<Integer, BITBook>();

	public static Map<Integer, PopupScreen> popupScreen = new HashMap<Integer, PopupScreen>();
	public static Map<UUID, String> BITButtons = new HashMap<UUID, String>();
	public static Map<Integer, Integer> userno = new HashMap<Integer, Integer>();

	// Parameters for the bookPopupScreen
	public static Map<Integer, Integer> currentBookId = new HashMap<Integer, Integer>();
	public static Map<Integer, GenericTextField> titleGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, Integer> currentPageNo = new HashMap<Integer, Integer>();
	public static Map<Integer, Integer> numberOfPagesGUI = new HashMap<Integer, Integer>();
	public static Map<Integer, GenericTextField> pagesGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, String[]> pagesGUI2 = new HashMap<Integer, String[]>();
	public static Map<Integer, GenericTextField> authorGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> coAuthorsGUI = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, Boolean> masterCopyGUI = new HashMap<Integer, Boolean>();
	public static Map<Integer, GenericButton> masterCopyButtonGUI = new HashMap<Integer, GenericButton>();
	public static Map<Integer, Integer> masterCopyIdGUI = new HashMap<Integer, Integer>();
	public static Map<Integer, Boolean> forceBookToPlayerInventoryGUI = new HashMap<Integer, Boolean>();
	public static Map<Integer, GenericButton> forceBookToPlayerInventoryButtonGUI = new HashMap<Integer, GenericButton>();
	public static Map<Integer, GenericButton> canBeMovedFromInventoryButtonGUI = new HashMap<Integer, GenericButton>();
	public static Map<Integer, Boolean> canBeMovedFromInventoryGUI = new HashMap<Integer, Boolean>();
	public static Map<Integer, GenericButton> copyTheBookWhenMovedButtonGUI = new HashMap<Integer, GenericButton>();
	public static Map<Integer, Boolean> copyTheBookWhenMovedGUI = new HashMap<Integer, Boolean>();
	public static Map<Integer, GenericTextField> useCostGUI = new HashMap<Integer, GenericTextField>();

	public void setBitBook(int bookId, SpoutBlock sBlock, int slotNo,
			String title, String author, String coAuthors, int numberOfPages,
			String[] pages, Boolean masterCopy, int masterCopyId,
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

	public int getNumberOfPages() {
		return numberOfPages;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public static boolean isWritten(BITInventoryType inventoryType, int SlotNo) {
		
			// TODO: test if bookId is stored in SQL database
			// if yes load the book into bitBooks and return true
		

		return true;
	}

	public static void saveBook(SpoutPlayer sPlayer, int bookId) {
		int id = sPlayer.getEntityId();

		// for (int i = 0; i < numberOfPagesGUI.get(id); i++) {
		// pagesGUI2.get(id)[i] = getPages(i);
		// pagesGUI.get(id).setText(pagesGUI2.get(id)[i]);
		// }

		bitBooks.put(
				bookId,
				new BITBook(bookId, bitBooks.get(bookId).getBlock(), bitBooks
						.get(bookId).getSlotNo(), titleGUI.get(id).getText(),
						authorGUI.get(id).getText(), coAuthorsGUI.get(id)
								.getText(), numberOfPagesGUI.get(id), pagesGUI2
								.get(id), masterCopyGUI.get(id),
						masterCopyIdGUI.get(id), forceBookToPlayerInventoryGUI
								.get(id), canBeMovedFromInventoryGUI.get(id),
						copyTheBookWhenMovedGUI.get(id), Integer
								.valueOf(useCostGUI.get(id).getText())));
		// TODO: save the Book to SQL
	}

	public void loadBook(int bookId) {
		if (bitBooks.containsKey(bookId)) {
			this.bookId = bitBooks.get(bookId).getBookId();
			this.sBlock = bitBooks.get(bookId).getBlock();
			this.slotNo = bitBooks.get(bookId).getSlotNo();
			this.title = bitBooks.get(bookId).getTitle();
			this.author = bitBooks.get(bookId).getAuthor();
			this.coAuthors = bitBooks.get(bookId).getCoAuthors();
			this.numberOfPages = bitBooks.get(bookId).getNumberOfPages();
			this.pages = bitBooks.get(bookId).getPages();
			this.masterCopy = bitBooks.get(bookId).getMasterCopy();
			this.masterCopyId = bitBooks.get(bookId).getMasterCopyId();
			this.forceBookToPlayerInventory = bitBooks.get(bookId)
					.getForceBookToPlayerInventory();
			this.canBeMovedFromInventory = bitBooks.get(bookId)
					.getCanBeMovedFromInventory();
			this.copyTheBookWhenMoved = bitBooks.get(bookId)
					.getCopyTheBookWhenMoved();
			this.useCost = bitBooks.get(bookId).getUseCost();
		} else {
			// TODO: Load the book from SQL
		}
	}

	private int getUseCost() {
		return useCost;
	}

	private Boolean getCopyTheBookWhenMoved() {
		return copyTheBookWhenMoved;
	}

	private Boolean getCanBeMovedFromInventory() {
		return canBeMovedFromInventory;
	}

	private Boolean getForceBookToPlayerInventory() {
		return forceBookToPlayerInventory;
	}

	private int getMasterCopyId() {
		return masterCopyId;
	}

	private Boolean getMasterCopy() {
		return masterCopy;
	}

	private String[] getPages() {
		return pages;
	}

	private String getPages(int i) {
		return pages[i];
	}

	private int getSlotNo() {
		return slotNo;
	}

	private SpoutBlock getBlock() {
		return sBlock;
	}

	public void removeBook() {
		// TODO: remove the book from SQL
	}

	public void openBook(SpoutPlayer sPlayer, int bookId) {
		int y = 20, itemHeight = 20;
		int x = 100;
		int textFieldHeight = 100, textFieldWidth = 320;
		int buttonHeight = 18, buttonWidth = 80;
		int textFieldHeight2 = 15, textFieldWidth2 = 76;

		int id = sPlayer.getEntityId();
		addUserData(id);
		titleGUI.get(id).setText(title);
		authorGUI.get(id).setText(author);
		coAuthorsGUI.get(id).setText(coAuthors);
		currentPageNo.put(id, 0);
		numberOfPagesGUI.put(id, getNumberOfPages());
		for (int i = 0; i < getNumberOfPages(); i++) {
			pagesGUI2.get(id)[i] = getPages(i);
			pagesGUI.get(id).setText(pagesGUI2.get(id)[i]);
		}
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
		nextPageBookButton.setTooltip("Next page ("
				+ String.valueOf(currentPageNo.get(id) + 1) + ")");
		popupScreen.get(id).attachWidget(BIT.plugin, nextPageBookButton);
		BITButtons.put(nextPageBookButton.getId(), "nextPageButton");

		// - Button
		GenericButton previousPageBookButton = new GenericButton("-");
		previousPageBookButton.setAuto(false)
				.setX(x + textFieldWidth - buttonHeight)
				.setY(y + 2 * itemHeight + 10 - buttonHeight - 1)
				.setHeight(buttonHeight).setWidth(buttonHeight);
		previousPageBookButton.setTooltip("Previous page ("
				+ String.valueOf(currentPageNo.get(id) - 1) + ")");
		popupScreen.get(id).attachWidget(BIT.plugin, previousPageBookButton);
		BITButtons.put(previousPageBookButton.getId(), "previousPageButton");

		// Title
		titleGUI.get(id).getText();
		titleGUI.get(id).setTooltip("Title of the book");
		titleGUI.get(id).setCursorPosition(1).setMaximumCharacters(30);
		titleGUI.get(id).setX(x).setY(y + 2 * itemHeight + 7 - buttonHeight);
		titleGUI.get(id).setHeight(textFieldHeight2)
				.setWidth(textFieldWidth2 * 2);
		popupScreen.get(id).attachWidget(BIT.plugin, titleGUI.get(id));

		y = y + 2 * itemHeight + 10;

		pagesGUI.get(id).setText(pagesGUI2.get(id)[currentPageNo.get(id)]);
		pagesGUI.get(id).setTooltip("Enter the text in the book.");
		pagesGUI.get(id).setX(x).setY(y);
		pagesGUI.get(id).setHeight(textFieldHeight).setWidth(textFieldWidth);
		// page.setMarginLeft(20).setMarginBottom(20);
		pagesGUI.get(id).setCursorPosition(1).setMaximumCharacters(1000);
		pagesGUI.get(id).setMaximumLines(8);
		pagesGUI.get(id).setFocus(true);
		popupScreen.get(id).attachWidget(BIT.plugin, pagesGUI.get(id));
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
		masterCopyButtonGUI.get(id).setText("Master:" + masterCopyGUI.get(id));
		masterCopyButtonGUI.get(id).setAuto(false).setX(x).setY(y)
				.setHeight(buttonHeight).setWidth(buttonWidth);
		masterCopyButtonGUI.get(id).setTooltip(
				"The masterCopy keeps all copies updated aumatically.");
		popupScreen.get(id).attachWidget(BIT.plugin,
				masterCopyButtonGUI.get(id));
		BITButtons.put(masterCopyButtonGUI.get(id).getId(), "masterCopyButton");
		y = y + buttonHeight + 1;

		// forceBookToPlayerInventoryButton
		forceBookToPlayerInventoryButtonGUI.get(id).setText(
				"Force:" + forceBookToPlayerInventoryGUI.get(id));
		forceBookToPlayerInventoryButtonGUI.get(id).setAuto(false).setX(x).setY(y)
				.setHeight(buttonHeight).setWidth(buttonWidth);
		forceBookToPlayerInventoryButtonGUI.get(id)
				.setTooltip("Force this book in to all players inventory.");
		popupScreen.get(id).attachWidget(BIT.plugin,
				forceBookToPlayerInventoryButtonGUI.get(id));
		BITButtons.put(forceBookToPlayerInventoryButtonGUI.get(id).getId(),
				"forceBookToPlayerInventoryButton");
		y = y + buttonHeight + 1;

		// canBeMovedFromInventoryButton
		canBeMovedFromInventoryButtonGUI.get(id).setText("Moved:" + canBeMovedFromInventoryGUI.get(id));
		canBeMovedFromInventoryButtonGUI.get(id).setAuto(false).setX(x).setY(y)
				.setHeight(buttonHeight).setWidth(buttonWidth);
		canBeMovedFromInventoryButtonGUI.get(id)
				.setTooltip("If this is 'No', the user cant take a copy.");
		popupScreen.get(id).attachWidget(BIT.plugin,
				canBeMovedFromInventoryButtonGUI.get(id));
		BITButtons.put(canBeMovedFromInventoryButtonGUI.get(id).getId(),
				"canBeMovedFromInventoryButton");
		y = y + buttonHeight + 1;

		// copyTheBookWhenMovedButton
		copyTheBookWhenMovedButtonGUI.get(id).setText("Copy:"
				+ copyTheBookWhenMovedGUI.get(id));
		copyTheBookWhenMovedButtonGUI.get(id).setAuto(false).setX(x).setY(y)
				.setHeight(buttonHeight).setWidth(buttonWidth);
		copyTheBookWhenMovedButtonGUI.get(id)
				.setTooltip("Specifies if the book is being copied or moved if the player moves the book.");
		popupScreen.get(id)
				.attachWidget(BIT.plugin, copyTheBookWhenMovedButtonGUI.get(id));
		BITButtons.put(copyTheBookWhenMovedButtonGUI.get(id).getId(),
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

	public static void cleanupPopupScreen(SpoutPlayer sPlayer) {
		int playerId = sPlayer.getEntityId();
		if (popupScreen.containsKey(playerId)) {
			popupScreen.get(playerId).removeWidgets(BIT.plugin);
			popupScreen.get(playerId).setDirty(true);
			sPlayer.getMainScreen().removeWidgets(BIT.plugin);
		}
	}

	@SuppressWarnings("unused")
	private String yesNo(Boolean b) {
		if (b)
			return "Yes";
		else
			return "No";
	}

	@SuppressWarnings("unused")
	private Boolean trueFalse(String str) {
		if (str.equals("Yes"))
			return true;
		else
			return false;
	}

	public static void removeUserDataxx(int id) {
		if (userno.containsKey(id)) {
			// BITBook
			popupScreen.remove(id);
			titleGUI.remove(id);
			currentPageNo.remove(id);
			numberOfPagesGUI.remove(id);
			pagesGUI.remove(id);
			pagesGUI2.remove(id);
			authorGUI.remove(id);
			coAuthorsGUI.remove(id);
			masterCopyGUI.remove(id);
			masterCopyButtonGUI.remove(id);
			masterCopyIdGUI.remove(id);
			forceBookToPlayerInventoryGUI.remove(id);
			forceBookToPlayerInventoryButtonGUI.remove(id);
			canBeMovedFromInventoryGUI.remove(id);
			canBeMovedFromInventoryButtonGUI.remove(id);
			copyTheBookWhenMovedGUI.remove(id);
			copyTheBookWhenMovedButtonGUI.remove(id);
			useCostGUI.remove(id);
			currentBookId.remove(id);
		}
	}

	public static void addUserData(int id) {
		if (!userno.containsKey(id)) {
			// BITBook
			popupScreen.put(id, new GenericPopup());
			titleGUI.put(id, new GenericTextField());
			currentPageNo.put(id, 0);
			numberOfPagesGUI.put(id, 0);
			pagesGUI.put(id, new GenericTextField());
			pagesGUI2.put(id, new String[10]);
			authorGUI.put(id, new GenericTextField());
			coAuthorsGUI.put(id, new GenericTextField());
			masterCopyGUI.put(id, false);
			masterCopyButtonGUI.put(id, new GenericButton());
			masterCopyIdGUI.put(id, 0);
			forceBookToPlayerInventoryGUI.put(id, false);
			forceBookToPlayerInventoryButtonGUI.put(id, new GenericButton());
			canBeMovedFromInventoryGUI.put(id, true);
			canBeMovedFromInventoryButtonGUI.put(id, new GenericButton());
			copyTheBookWhenMovedGUI.put(id, false);
			copyTheBookWhenMovedButtonGUI.put(id, new GenericButton());
			useCostGUI.put(id, new GenericTextField());
			currentBookId.put(id, 0);
		}
	}

	public static void showNextPage(SpoutPlayer sPlayer) {
		int id = sPlayer.getEntityId();
		int i = currentPageNo.get(id);
		pagesGUI2.get(id)[i] = pagesGUI.get(id).getText();
		if (i == (numberOfPagesGUI.get(id) - 1)) {
			i = 0;
		} else {
			i++;
		}
		currentPageNo.put(id, i);
		pagesGUI.get(id).setText(pagesGUI2.get(id)[i]);
		pagesGUI.get(id).setDirty(true);
	}

	public static void showPreviousPage(SpoutPlayer sPlayer) {
		int id = sPlayer.getEntityId();
		int i = currentPageNo.get(id);
		pagesGUI2.get(id)[i] = pagesGUI.get(id).getText();
		if (i == 0) {
			i = numberOfPagesGUI.get(id) - 1;
		} else {
			i--;
		}
		currentPageNo.put(id, i);
		pagesGUI.get(id).setText(pagesGUI2.get(id)[i]);
		pagesGUI.get(id).setDirty(true);
	}

}