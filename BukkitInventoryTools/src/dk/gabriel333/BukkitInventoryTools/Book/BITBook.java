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
	//	super();
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
	 * @param id
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

	protected Map<Integer, BITBook> bitBooks = new HashMap<Integer,BITBook>();
	public static Map<Integer, PopupScreen> popupScreen = new HashMap<Integer, PopupScreen>();
	public static Map<UUID, String> BITButtons = new HashMap<UUID, String>();

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

	public static boolean isBookCreated() {
		// TODO: check if the book is created (true) or if it is empty
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

	static boolean isWriteable(Material material) {
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

	public void openBook(SpoutPlayer sPlayer) {
		// open BookGUI
		int y = 40, itemHeight = 20;
		int x = 100;
		int textFieldHeight = 60, textFieldWidth = 300;
		int buttonHeight = 20, buttonWidth = 80;
		int id = sPlayer.getEntityId();

		GenericItemWidget itemwidget = new GenericItemWidget(new ItemStack(340));
		itemwidget.setX(x).setY(y);
		itemwidget.setHeight(itemHeight).setWidth(itemHeight)
				.setDepth(itemHeight);
		itemwidget.setTooltip("Locked inventory");
		popupScreen.get(id).attachWidget(BIT.plugin, itemwidget);
		y = y + 2 * itemHeight+10;

		BITBook book = new BITBook();
		book.loadBook();
		GenericTextField page = new GenericTextField();
		//page=book.getPage(1);

		page.setText("");
		page.setTooltip("Enter the text in the book.");
		page.setCursorPosition(1).setMaximumCharacters(500);
		page.setMaximumLines(10);
		page.setX(x).setY(y);
		page.setHeight(textFieldHeight).setWidth(textFieldWidth);
		page.setFocus(true);
		popupScreen.get(id).attachWidget(BIT.plugin, page);
		y = y + textFieldHeight + 10;

		GenericButton saveButton = new GenericButton("Save");
		saveButton.setAuto(false).setX(x).setY(y).setHeight(buttonHeight)
				.setWidth(buttonWidth);
		BITButtons.put(saveButton.getId(), "saveBookButton");
		popupScreen.get(id).attachWidget(BIT.plugin, saveButton);

		GenericButton cancelBookButton = new GenericButton("Cancel");
		cancelBookButton.setAuto(false).setX(x + buttonWidth + 10).setY(y)
				.setHeight(buttonHeight).setWidth(buttonWidth);
		popupScreen.get(id).attachWidget(BIT.plugin, cancelBookButton);
		BITButtons.put(cancelBookButton.getId(), "cancelBookButton");

		// Open Window
		popupScreen.get(id).setTransparent(true);
		sPlayer.getMainScreen().attachPopupScreen(popupScreen.get(id));
	}

	public void closeBook(SpoutPlayer sPlayer) {
		int playerId = sPlayer.getEntityId();
		popupScreen.get(playerId).removeWidgets(BIT.plugin);
		popupScreen.get(playerId).setDirty(true);
		sPlayer.getMainScreen().removeWidgets(BIT.plugin);
	}

}