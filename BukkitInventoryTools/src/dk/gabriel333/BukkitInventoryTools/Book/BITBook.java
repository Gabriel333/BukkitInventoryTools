package dk.gabriel333.BukkitInventoryTools.Book;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;

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
	BITBook(int bookId, BITInventoryType inventoryType, SpoutBlock sBlock,
			int slotNo, String title, String author, String coAuthors,
			int numberOfPages, String[] pages, Boolean masterCopy,
			int masterCopyId, Boolean forceBookToPlayerInventory,
			Boolean canBeMovedFromInventory, Boolean copyTheBookWhenMoved,
			int useCost) {
		this.bookId = bookId;
		this.inventoryType = inventoryType;
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

	public BITInventoryType getInventoryType() {
		return inventoryType;
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

	public static boolean isWritten(SpoutPlayer sPlayer, SpoutBlock sBlock,
			BITInventoryType inventoryType, int slotNo) {

		// TODO: test if bookId is stored in SQL database
		// if yes load the book into bitBooks and return true
		String query = "SELECT * FROM " + BIT.booksTable + " WHERE (slotno = "
				+ slotNo + " AND world='" + sPlayer.getWorld()
				+ "' AND inventorytype = " + inventoryType + " AND x= "
				+ sBlock.getX() + " AND y= " + sBlock.getY() + " AND z= "
				+ sBlock.getZ() + ");";
		ResultSet result = null;
		if (G333Config.STORAGE_TYPE.equals("MYSQL")) {
			try {
				result = BIT.manageMySQL.sqlQuery(query);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else { // SQLLITE
			result = BIT.manageSQLite.sqlQuery(query);
		}
		try {
			if (result != null && result.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void saveBook(SpoutPlayer sPlayer, SpoutBlock sBlock, int bookId,
			BITInventoryType inventoryType) {
		int id = sPlayer.getEntityId();

		// for (int i = 0; i < numberOfPagesGUI.get(id); i++) {
		// pagesGUI2.get(id)[i] = getPages(i);
		// pagesGUI.get(id).setText(pagesGUI2.get(id)[i]);
		// }

		bitBooks.put(
				bookId,
				new BITBook(bookId, inventoryType, bitBooks.get(bookId)
						.getBlock(), bitBooks.get(bookId).getSlotNo(), titleGUI
						.get(id).getText(), authorGUI.get(id).getText(),
						coAuthorsGUI.get(id).getText(), numberOfPagesGUI
								.get(id), pagesGUI2.get(id), masterCopyGUI
								.get(id), masterCopyIdGUI.get(id),
						forceBookToPlayerInventoryGUI.get(id),
						canBeMovedFromInventoryGUI.get(id),
						copyTheBookWhenMovedGUI.get(id), Integer
								.valueOf(useCostGUI.get(id).getText())));
		String query;
		boolean createBook = true;
		int cost = G333Config.BOOK_COST;
		if (BITBook.isWritten(sPlayer, sBlock,BITInventoryType.PLAYER_INVENTORY, bitBooks.get(bookId).getSlotNo())) {
			for (int i = 0; i < bitBooks.get(bookId).getNumberOfPages(); i++) {
				// TODO: decide what to do when block is null?
				query = "UPDATE " + BIT.booksTable + " SET bookid=" + bookId
						+ ", world='" + sPlayer.getWorld()
						+ "', inventorytype="
						+ bitBooks.get(bookId).getInventoryType() + ", x="
						+ bitBooks.get(bookId).getBlock().getX() + ", y="
						+ bitBooks.get(bookId).getBlock().getY() + ", z="
						+ bitBooks.get(bookId).getBlock().getZ() + ", slotno="
						+ bitBooks.get(bookId).getSlotNo() + ", title='"
						+ bitBooks.get(bookId).getTitle() + "', author="
						+ bitBooks.get(bookId).getAuthor() + "', coauthor='"
						+ bitBooks.get(bookId).getCoAuthors()
						+ "', numberofpages="
						+ bitBooks.get(bookId).getNumberOfPages() + ", pageno="
						+ i + ", page='" + bitBooks.get(bookId).getPages(i)
						+ ", mastercopy='"
						+ bitBooks.get(bookId).getMasterCopy()
						+ "', mastercopyid=" + bitBooks.get(bookId)
						+ ", force='"
						+ bitBooks.get(bookId).getForceBookToPlayerInventory()
						+ "', moved='"
						+ bitBooks.get(bookId).getCanBeMovedFromInventory()
						+ "', copy='"
						+ bitBooks.get(bookId).getCopyTheBookWhenMoved()
						+ "' WHERE bookid=" + bookId + " AND world="
						+ sPlayer.getWorld() + " AND x= "
						+ bitBooks.get(bookId).getBlock().getX() + " AND y= "
						+ bitBooks.get(bookId).getBlock().getY() + " AND z= "
						+ bitBooks.get(bookId).getBlock().getZ()
						+ " AND pageno=" + i + " AND slotno="
						+ bitBooks.get(bookId).getSlotNo() + ";";

				if (G333Config.DEBUG_SQL)
					sPlayer.sendMessage(ChatColor.YELLOW + "Updating book: "
							+ query);
				if (G333Config.STORAGE_TYPE.equals("MYSQL")) {
					try {
						BIT.manageMySQL.insertQuery(query);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				} else {
					BIT.manageSQLite.insertQuery(query);
				}
			}
			G333Messages.sendNotification(sPlayer, "Book updated.");
		} else {
			// NEW BOOK
			if (BIT.useEconomy) {
				if (BIT.plugin.Method.hasAccount(sPlayer.getName()) && cost > 0) {
					if (BIT.plugin.Method.getAccount(sPlayer.getName())
							.hasEnough(cost)) {
						BIT.plugin.Method.getAccount(sPlayer.getName())
								.subtract(cost);
						sPlayer.sendMessage("Your account ("
								+ BIT.plugin.Method.getAccount(
										sPlayer.getName()).balance()
								+ ") has been deducted " + cost + " bucks");
					} else {
						sPlayer.sendMessage("You dont have enough money ("
								+ BIT.plugin.Method.getAccount(
										sPlayer.getName()).balance()
								+ "). Cost is:" + cost);
						createBook = false;
					}
				}
			}
			if (createBook) {
				// sPlayer.sendMessage("Inventorysize:" + inventory.getSize());
				for (int i = 0; i < bitBooks.get(bookId).getNumberOfPages(); i++) {
					query = "INSERT INTO "
							+ BIT.booksTable
							+ "( bookid="
							+ bookId
							+ ", world='"
							+ sPlayer.getWorld()
							+ "', inventorytype="
							+ bitBooks.get(bookId).getInventoryType()
							+ ", x="
							+ bitBooks.get(bookId).getBlock().getX()
							+ ", y="
							+ bitBooks.get(bookId).getBlock().getY()
							+ ", z="
							+ bitBooks.get(bookId).getBlock().getZ()
							+ ", slotno="
							+ bitBooks.get(bookId).getSlotNo()
							+ ", title='"
							+ bitBooks.get(bookId).getTitle()
							+ "', author='"
							+ bitBooks.get(bookId).getAuthor()
							+ "', coauthor='"
							+ bitBooks.get(bookId).getCoAuthors()
							+ "', numberofpages="
							+ bitBooks.get(bookId).getNumberOfPages()
							+ ", pageno="
							+ i
							+ ", page='"
							+ bitBooks.get(bookId).getPages(i)
							+ "', mastercopy='"
							+ bitBooks.get(bookId).getMasterCopy()
							+ "', mastercopyid="
							+ bitBooks.get(bookId)
							+ ", force='"
							+ bitBooks.get(bookId)
									.getForceBookToPlayerInventory()
							+ "', moved='"
							+ bitBooks.get(bookId).getCanBeMovedFromInventory()
							+ "', copy='"
							+ bitBooks.get(bookId).getCopyTheBookWhenMoved()
							+ "') VALUES ( "
							+ bookId
							+ ", '"
							+ sPlayer.getWorld()
							+ "', "
							+ bitBooks.get(bookId).getInventoryType()
							+ ", "
							+ bitBooks.get(bookId).getBlock().getX()
							+ ", "
							+ bitBooks.get(bookId).getBlock().getY()
							+ ", "
							+ bitBooks.get(bookId).getBlock().getZ()
							+ ", "
							+ bitBooks.get(bookId).getSlotNo()
							+ ", '"
							+ bitBooks.get(bookId).getTitle()
							+ "', '"
							+ bitBooks.get(bookId).getAuthor()
							+ "', '"
							+ bitBooks.get(bookId).getCoAuthors()
							+ "', "
							+ bitBooks.get(bookId).getNumberOfPages()
							+ ", "
							+ i
							+ ", '"
							+ bitBooks.get(bookId).getPages(i)
							+ "', '"
							+ bitBooks.get(bookId).getMasterCopy()
							+ "', "
							+ bitBooks.get(bookId)
							+ ", '"
							+ bitBooks.get(bookId)
									.getForceBookToPlayerInventory() + "', '"
							+ bitBooks.get(bookId).getCanBeMovedFromInventory()
							+ "', '"
							+ bitBooks.get(bookId).getCopyTheBookWhenMoved()
							+ "' );";
					// sPlayer.sendMessage("Insert:" + query);
					if (G333Config.DEBUG_SQL)
						sPlayer.sendMessage(ChatColor.YELLOW
								+ "Insert to booksTable: " + query);
					if (G333Config.STORAGE_TYPE.equals("MYSQL")) {
						try {
							BIT.manageMySQL.insertQuery(query);
						} catch (MalformedURLException e) {
							e.printStackTrace();
						} catch (InstantiationException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						}
					} else {
						BIT.manageSQLite.insertQuery(query);
					}
				}
				G333Messages.sendNotification(sPlayer, "Bookshelf created.");
			} else {
				sPlayer.sendMessage("You dont have enough money. Cost is:"
						+ cost);
			}
		}

	}

	public BITBook loadBook(SpoutPlayer sPlayer, int bookId) {
		if (bitBooks.containsKey(bookId)) {
			this.bookId = bitBooks.get(bookId).getBookId();
			this.inventoryType = bitBooks.get(bookId).getInventoryType();
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
			int resBookId = 0;
			int resInventoryType;
			int resX = 0, resY = 0, resZ = 0;
			int resSlotNo = 0;
			String resTitle = "";
			String resAuthor = "";
			String resCoAuthors = "";
			int resNumberOfPages = 0;
			String resPages[] = new String[10];
			int resPageno;
			Boolean resMasterCopy = false;
			int resMasterCopyId = 0;
			Boolean resForceBookToPlayerInventory = false;
			Boolean resCanBeMovedFromInventory = true;
			Boolean resCopyTheBookWhenMoved = false;
			int resUseCost = 0;
			String query = "select * FROM " + BIT.oldBooksTable
					+ " WHERE bookid=" + bookId + " world="
					+ sPlayer.getWorld() + ";";
			ResultSet result = null;
			if (G333Config.STORAGE_TYPE.equals("MYSQL")) {
				try {
					result = BIT.manageMySQL.sqlQuery(query);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} else { // SQLLITE
				result = BIT.manageSQLite.sqlQuery(query);
				// sPlayer.sendMessage("Result:" + result.toString());
			}
			int i = 0;

			try {
				while (result != null && result.next()) {
					// && i < size
					resBookId = result.getInt("bookid");
					resInventoryType = result.getInt("inventorytype");
					resX = result.getInt("x");
					resY = result.getInt("y");
					resZ = result.getInt("z");
					resSlotNo = result.getInt("slotno");
					resTitle = result.getString("title");
					resAuthor = result.getString("author");
					resCoAuthors = result.getString("coauthors");
					resNumberOfPages = result.getInt("numberofpages");
					resPageno = result.getInt("pageno");
					resPages[resPageno] = result.getString("pages");
					resMasterCopy = result.getBoolean("mastercopy");
					resMasterCopyId = result.getInt("mastercopyid");
					resForceBookToPlayerInventory = result.getBoolean("force");
					resCanBeMovedFromInventory = result.getBoolean("moved");
					resCopyTheBookWhenMoved = result.getBoolean("copy");
					;
					resUseCost = result.getInt("usecost");
					// sPlayer.sendMessage("i:" + i + ");
					if (sBlock == null) {

					} else {

					}
					G333Messages.showInfo("i:" + i + " inv_type="
							+ resInventoryType);
					i++;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// G333Messages.showInfo("name:" + name + " owner:" + owner +
			// " coowners:"
			// + coOwners + " usecost:" + useCost);
			// G333Messages.showInfo("inv:" + inventory);

			Location loc = new Location(sPlayer.getWorld(), resX, resY, resZ);
			SpoutBlock sBlock = (SpoutBlock) loc.getBlock();

			bitBooks.put(bookId, new BITBook(resBookId,
					BITInventoryType.PLAYER_INVENTORY, sBlock, resSlotNo,
					resTitle, resAuthor, resCoAuthors, resNumberOfPages,
					resPages, resMasterCopy, resMasterCopyId,
					resForceBookToPlayerInventory, resCanBeMovedFromInventory,
					resCopyTheBookWhenMoved, resUseCost));

			return bitBooks.get(bookId);
		}
		return null;

	}

	public void removeBook(SpoutPlayer sPlayer, int bookId, int destroycost) {
		boolean deleteBook = true;
		if (BIT.useEconomy) {
			if (BIT.plugin.Method.hasAccount(sPlayer.getName())) {
				if (BIT.plugin.Method.getAccount(sPlayer.getName()).hasEnough(
						destroycost)
						|| destroycost < 0) {
					BIT.plugin.Method.getAccount(sPlayer.getName()).subtract(
							destroycost);
					sPlayer.sendMessage("Your account ("
							+ BIT.plugin.Method.getAccount(sPlayer.getName())
									.balance() + ") has been deducted "
							+ destroycost + " bucks");
				} else {
					sPlayer.sendMessage("You dont have enough money ("
							+ BIT.plugin.Method.getAccount(sPlayer.getName())
									.balance() + "). Cost is:" + destroycost);
					deleteBook = false;
				}
			}
		}
		String query = "DELETE FROM " + BIT.booksTable + " WHERE (bookid = "
				+ bitBooks.get(bookId).getBookId() + ");";
		if (deleteBook) {
			if (G333Config.DEBUG_SQL)
				sPlayer.sendMessage(ChatColor.YELLOW + "Removing book: "
						+ query);
			if (G333Config.STORAGE_TYPE.equals("MYSQL")) {
				try {
					BIT.manageMySQL.deleteQuery(query);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} else { // SQLLITE
				BIT.manageSQLite.deleteQuery(query);
			}
			bitBooks.remove(bookId);
			G333Messages.sendNotification(sPlayer, "Book removed.");
		} else {
			G333Messages.sendNotification(sPlayer, "You need more money ("
					+ destroycost + ")");
		}
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
		forceBookToPlayerInventoryButtonGUI.get(id).setAuto(false).setX(x)
				.setY(y).setHeight(buttonHeight).setWidth(buttonWidth);
		forceBookToPlayerInventoryButtonGUI.get(id).setTooltip(
				"Force this book in to all players inventory.");
		popupScreen.get(id).attachWidget(BIT.plugin,
				forceBookToPlayerInventoryButtonGUI.get(id));
		BITButtons.put(forceBookToPlayerInventoryButtonGUI.get(id).getId(),
				"forceBookToPlayerInventoryButton");
		y = y + buttonHeight + 1;

		// canBeMovedFromInventoryButton
		canBeMovedFromInventoryButtonGUI.get(id).setText(
				"Moved:" + canBeMovedFromInventoryGUI.get(id));
		canBeMovedFromInventoryButtonGUI.get(id).setAuto(false).setX(x).setY(y)
				.setHeight(buttonHeight).setWidth(buttonWidth);
		canBeMovedFromInventoryButtonGUI.get(id).setTooltip(
				"If this is 'No', the user cant take a copy.");
		popupScreen.get(id).attachWidget(BIT.plugin,
				canBeMovedFromInventoryButtonGUI.get(id));
		BITButtons.put(canBeMovedFromInventoryButtonGUI.get(id).getId(),
				"canBeMovedFromInventoryButton");
		y = y + buttonHeight + 1;

		// copyTheBookWhenMovedButton
		copyTheBookWhenMovedButtonGUI.get(id).setText(
				"Copy:" + copyTheBookWhenMovedGUI.get(id));
		copyTheBookWhenMovedButtonGUI.get(id).setAuto(false).setX(x).setY(y)
				.setHeight(buttonHeight).setWidth(buttonWidth);
		copyTheBookWhenMovedButtonGUI
				.get(id)
				.setTooltip(
						"Specifies if the book is being copied or moved if the player moves the book.");
		popupScreen.get(id).attachWidget(BIT.plugin,
				copyTheBookWhenMovedButtonGUI.get(id));
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