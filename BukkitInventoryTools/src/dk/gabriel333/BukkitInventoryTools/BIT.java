package dk.gabriel333.BukkitInventoryTools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
//import org.getspout.spout.inventory.CustomInventory;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.gui.GenericLabel;

import com.alta189.sqlLibrary.MySQL.mysqlCore;
import com.alta189.sqlLibrary.SQLite.sqlCore;
import com.garbagemule.MobArena.MobArenaHandler;
import com.matejdro.bukkit.jail.Jail;
import com.matejdro.bukkit.jail.JailAPI;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import dk.gabriel333.register.payment.Method;
import dk.gabriel333.register.payment.Methods;
import dk.gabriel333.spoutbackpack.SBEntityListener;
import dk.gabriel333.spoutbackpack.SBInputListener;
import dk.gabriel333.spoutbackpack.SBInventoryListener;
import dk.gabriel333.spoutbackpack.SBInventorySaveTask;
import dk.gabriel333.spoutbackpack.SBLanguageInterface;
import dk.gabriel333.spoutbackpack.SBPlayerListener;
import dk.gabriel333.spoutbackpack.SBLanguageInterface.Language;
import dk.gabriel333.spoutbackpack.SpoutBackpack;
import de.Keyle.MyWolf.MyWolfPlugin;
import dk.gabriel333.BukkitInventoryTools.Commands.*;
import dk.gabriel333.BukkitInventoryTools.Listeners.*;
import dk.gabriel333.BukkitInventoryTools.Inventory.*;
import dk.gabriel333.BukkitInventoryTools.Book.*;
import dk.gabriel333.BukkitInventoryTools.DigiLock.*;
import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;
import dk.gabriel333.Library.G333Permissions;
import dk.gabriel333.Library.G333Plugin;

import me.neatmonster.spoutbackpack.SBHandler;

public class BIT extends JavaPlugin {

	public static BIT plugin;

	public static Boolean spout = false;

	// Hook into register
	public static Boolean useEconomy = false;
	public Methods Methods;
	public Method Method;

	// Hook into SpoutBackpack
	public static SBHandler spoutBackpackHandler;
	public static Boolean spoutbackpack = false;
	public static Map<String, ItemStack[]> inventories = new HashMap<String, ItemStack[]>();
	public static Map<String, Inventory> openedInventories = new HashMap<String, Inventory>();
	public static Map<String, String> openedInventoriesOthers = new HashMap<String, String>();
	public static Map<String, GenericLabel> widgets = new HashMap<String, GenericLabel>();
	public static String inventoryName = "Backpack";
	public static SBLanguageInterface li;
	public static MobArenaHandler mobArenaHandler;
	public List<Player> portals = new ArrayList<Player>();
	public String logTag = "[BITSpoutBackpack]";
	public YamlConfiguration config;
	public static int saveTaskId;

	// Hook into MyWolf
	public static Boolean mywolf = false;
	public static MyWolfPlugin myWolfPlugin;

	@Override
	public void onEnable() {
		plugin = this;
		PluginDescriptionFile pdfFile = this.getDescription();

		if (!isSortInventoryInstalled()) {
			G333Plugin.setupPlugin(this);
			G333Config.bitSetupConfig();
			setupSpout();
			setupSQL();
			setupRegister();
			setupSpoutBackpack();
			setupMyWolf();
			registerEvents();
			addCommands();
			setupBook();
			setupMobArena();
			setupJail();
			li = new SBLanguageInterface(loadLanguage());
			// SpoutBackpack
			for (Player player : Bukkit.getServer().getOnlinePlayers()) {
				BIT.loadInventory(player, player.getWorld());
			}
			G333Messages.showInfo("BIT version " + pdfFile.getVersion()
					+ " is enabled!");
		} else {
			G333Messages.showError(pdfFile.getName() + " version "
					+ pdfFile.getVersion() + " could not be installed!");
		}
	}

	// Test for SortInventory
	private Boolean isSortInventoryInstalled() {
		Plugin sortInventoryPlugin = this.getServer().getPluginManager()
				.getPlugin("SortInventory");
		if (sortInventoryPlugin != null) {
			G333Messages
					.showError("SortInventory is outdated and conflicts with BukkitInventoryTools!");
			return true;
		}
		return false;
	}

	public void registerEvents() {
		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_BREAK, new BITBlockListener(),
				Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGE, new BITBlockListener(),
				Priority.Normal, this);
		// REDSTONE_CHANGE is disabled because of memory leak
		// pm.registerEvent(Type.REDSTONE_CHANGE, new BITBlockListener(),
		// Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_PHYSICS, new BITBlockListener(),
				Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_FROMTO, new BITBlockListener(),
				Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_FORM, new BITBlockListener(),
				Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_BURN, new BITBlockListener(),
				Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_FADE, new BITBlockListener(),
				Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_SPREAD, new BITBlockListener(),
				Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_IGNITE, new BITBlockListener(),
				Priority.Normal, this);
		pm.registerEvent(Type.SIGN_CHANGE, new BITBlockListener(),
				Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_PISTON_EXTEND, new BITBlockListener(),
				Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_PISTON_RETRACT, new BITBlockListener(),
				Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, new BITPlayerListener(),
				Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, new BITPlayerListener(),
				Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_LOGIN, new BITPlayerListener(),
				Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, new BITPlayerListener(),
				Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, new BITPlayerListener(),
				Priority.Normal, this);
		// BITDigiLock Listeners
		pm.registerEvent(Event.Type.CUSTOM_EVENT, new BITDigiLockInputListener(
				this), Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT,
				new BITDigiLockSpoutListener(), Event.Priority.Normal, this);
		// BITIventory Listeners
		pm.registerEvent(Event.Type.CUSTOM_EVENT,
				new BITInventorySpoutListener(this), Event.Priority.Normal,
				this);

		// BITBook Listeners
		pm.registerEvent(Event.Type.CUSTOM_EVENT,
				new BITInventoryListener(this), Event.Priority.Low, this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT, new BITBookInputListener(),
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT, new BITBookSpoutListener(),
				Event.Priority.Normal, this);

		// BITKeyboardListener
		pm.registerEvent(Event.Type.CUSTOM_EVENT, new BITKeyboardListener(),
				Event.Priority.Normal, this);

		// SpoutBackpack Listeners
		pm.registerEvent(Type.CUSTOM_EVENT, new SBInputListener(),
				Priority.Normal, this);
		pm.registerEvent(Type.CUSTOM_EVENT, new SBInventoryListener(this),
				Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_JOIN, new SBPlayerListener(this),
				Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_TELEPORT, new SBPlayerListener(this),
				Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_PORTAL, new SBPlayerListener(this),
				Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_KICK, new SBPlayerListener(this),
				Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_QUIT, new SBPlayerListener(this),
				Priority.Normal, this);
		pm.registerEvent(Type.ENTITY_DEATH, new SBEntityListener(this),
				Priority.Normal, this);

	}

	@Override
	public void onDisable() {
		Bukkit.getServer().getScheduler().cancelTask(saveTaskId);
		SBInventorySaveTask.saveAll();

		PluginDescriptionFile pdfFile = this.getDescription();
		G333Messages.showInfo(pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " is disabled!");
	}

	public void addCommands() {
		// Register commands
		getCommand("Sort").setExecutor(new BITCommandSort(this));
		getCommand("Digilock").setExecutor(new BITCommandDigiLock(this));
		getCommand("Bookshelf").setExecutor(new BITCommandBookshelf(this));
		getCommand("Backpack").setExecutor(new SpoutBackpack(this));
	}

	private void setupSpout() {
		Plugin spoutPlugin = this.getServer().getPluginManager()
				.getPlugin("Spout");
		if (spoutPlugin != null) {
			spout = true;
			G333Messages.showInfo("Spout is detected.");
		} else {
			G333Messages.showError("BIT is dependend on Spout!");
		}
	}

	private void setupRegister() {
		Plugin iconomyPlugin = this.getServer().getPluginManager()
				.getPlugin("iConomy");
		Plugin boseconomyPlugin = this.getServer().getPluginManager()
				.getPlugin("BOSEconomy");
		Plugin essentialsPlugin = this.getServer().getPluginManager()
				.getPlugin("Essentials");
		Plugin multiCurrencyPlugin = this.getServer().getPluginManager()
				.getPlugin("MultiCurrency");
		if (iconomyPlugin != null || boseconomyPlugin != null
				|| essentialsPlugin != null || multiCurrencyPlugin != null) {
			getServer().getPluginManager().registerEvent(Type.PLUGIN_ENABLE,
					new BITServerListener(this), Priority.Monitor, this);
			getServer().getPluginManager().registerEvent(Type.PLUGIN_DISABLE,
					new BITServerListener(this), Priority.Monitor, this);
            useEconomy=true;
		}
		
	}

	public static boolean isPlayer(CommandSender sender) {
		if (sender instanceof Player)
			return true;
		return false;
	}

	private void setupSpoutBackpack() {
		if (spoutBackpackHandler == null) {
			Plugin spoutBackpackPlugin = this.getServer().getPluginManager()
					.getPlugin("SpoutBackpack");
			if (spoutBackpackPlugin != null) {
				if (spout == true) {
					spoutBackpackHandler = new SBHandler();
					spoutbackpack = true;
					G333Messages.showInfo("SpoutBackpack is detected.");
				} else {
					G333Messages
							.showWarning("SpoutBackpack is detected, but spout is not detected.");
					spoutbackpack = false;
				}
			}
		}
	}

	private void setupMyWolf() {
		if (myWolfPlugin == null) {
			myWolfPlugin = (MyWolfPlugin) this.getServer().getPluginManager()
					.getPlugin("MyWolf");
			if (myWolfPlugin != null) {
				if (spout == true) {
					mywolf = true;
					G333Messages.showInfo("MyWolf is detected.");
				} else {
					G333Messages
							.showWarning("MyWolf is detected, but spout is not detected.");
					mywolf = false;
				}
			}
		}
		// you get access to MyWolf inventory with:
		// CustomMCInventory inv = myWolfPlugin.getMyWolf(sPlayer).inv;
	}

	// SQLITE-MYSQL settings
	public static mysqlCore manageMySQL; // MySQL handler
	public static sqlCore manageSQLite; // SQLite handler
	public static Logger log = Logger.getLogger("Minecraft");
	public static String digilockTable = "BukkitInventoryTools5";
	public static String oldDigilockTable = "BukkitInventoryTools4";
	public static String bitInventoryTable = "Bookshelf";
	public static String oldBitInventoryTable = "Bookshelf_NONE";
	public static String bookTable = "Book2";
	public static String oldBookTable = "Book";

	private void setupSQL() {
		if (G333Config.STORAGE_TYPE.equals("MYSQL")) {
			// Declare MySQL Handler
			manageMySQL = new mysqlCore(log,
					"[" + G333Plugin.PLUGIN_NAME + "]",
					G333Config.STORAGE_HOST, G333Config.STORAGE_DATABASE,
					G333Config.STORAGE_USERNAME, G333Config.STORAGE_PASSWORD);
			G333Messages.showInfo("MySQL Initializing");
			// Initialize MySQL Handler
			manageMySQL.initialize();
			try {
				if (manageMySQL.checkConnection()) {
					// Check if the Connection was successful
					String query;
					G333Messages.showInfo("MySQL connection successful");

					// Check DigiLockTable
					if (!manageMySQL.checkTable(digilockTable)) {
						if (manageMySQL.checkTable(oldDigilockTable)) {
							G333Messages.showInfo("Upgrade " + oldDigilockTable
									+ " to " + digilockTable + ".");
							query = "CREATE TABLE "
									+ digilockTable
									+ " (x INT, y INT, z INT, world VARCHAR(255), "
									+ "owner VARCHAR(255), pincode VARCHAR(255), "
									+ " coowners VARCHAR(255), users VARCHAR(255), closetimer INT, "
									+ "typeid INT, connectedto VARCHAR(255), usecost INT) "
									+ "AS SELECT x, y, z, world, owner, pincode, "
									+ "'none', coowners, closetimer, typeid, connectedto, usecost FROM "
									+ oldDigilockTable + ";";
						} else {
							G333Messages.showInfo("Creating table "
									+ digilockTable);
							query = "CREATE TABLE "
									+ digilockTable
									+ " (x INT, y INT, z INT, world VARCHAR(255), owner VARCHAR(255), "
									+ "pincode VARCHAR(255), coowners VARCHAR(255), users VARCHAR(255), closetimer INT, "
									+ "typeid INT, connectedto VARCHAR(255), usecost INT);";
						}
						manageMySQL.createTable(query);
					}

					// Check BookshelfTable
					if (!manageMySQL.checkTable(bitInventoryTable)) {
						if (manageMySQL.checkTable(oldBitInventoryTable)) {
							G333Messages.showInfo("Upgrade "
									+ oldBitInventoryTable + " to "
									+ bitInventoryTable + ".");
							query = "CREATE TABLE "
									+ bitInventoryTable
									+ " (playername VARCHAR(255), x INT, y INT, z INT, world VARCHAR(255), "
									+ "owner VARCHAR(255), "
									+ "name VARCHAR(255), "
									+ "coowners VARCHAR(255), "
									+ "usecost INT, slotno INT, "
									+ "itemstack_type INT, itemstack_amount INT, itemstack_durability INT) "
									+ "AS SELECT plyername, x, y, z, world, owner, name, coowners, usecost, "
									+ "itemstack_type, itemstack_amount, itemstack_durability FROM "
									+ oldBitInventoryTable + ";";
						} else {
							G333Messages.showInfo("Creating table "
									+ bitInventoryTable);
							query = "CREATE TABLE "
									+ bitInventoryTable
									+ " (playername VARCHAR(255), x INT, y INT, z INT, world VARCHAR(255), "
									+ "owner VARCHAR(255), "
									+ "name VARCHAR(255), "
									+ "coowners VARCHAR(255), "
									+ "usecost INT, slotno INT, "
									+ "itemstack_type INT, itemstack_amount INT, "
									+ "itemstack_durability INT); ";
						}
						manageMySQL.createTable(query);
					}

					// Check BooksTable
					if (!manageMySQL.checkTable(bookTable)) {
						if (manageMySQL.checkTable(oldBookTable)) {
							G333Messages.showInfo("Upgrade " + oldBookTable
									+ " to " + bookTable + ".");
							query = "CREATE TABLE "
									+ bookTable
									+ " (bookid INT, title VARCHAR(255),"
									+ " author VARCHAR(255), coauthors VARCHAR(255), "
									+ " numberofpages INT, pageno INT, bodytext TEXT,"
									+ " mastercopy BOOLEAN, mastercopyid INT,"
									+ " forcebook BOOLEAN, moved BOOLEAN, copy BOOLEAN, usecost INT)"
									+ " AS select bookid, title,"
									+ " author, coauthors, "
									+ " numberofpages, pageno, bodytext,"
									+ " mastercopy, mastercopyid,"
									+ " forcebook, moved, copy, usecost FROM "
									+ oldBookTable + ";";
						} else {
							G333Messages
									.showInfo("Creating table " + bookTable);
							query = "CREATE TABLE "
									+ bookTable
									+ " (bookid INT, title VARCHAR(255),"
									+ " author VARCHAR(255), coauthors VARCHAR(255), "
									+ " numberofpages INT, pageno INT, bodytext TEXT,"
									+ " mastercopy BOOLEAN, mastercopyid INT,"
									+ " forcebook BOOLEAN, moved BOOLEAN, copy BOOLEAN, usecost INT);";
						}
						manageMySQL.createTable(query);
					}
				} else {
					G333Messages.showError("MySQL connection failed");
					G333Config.STORAGE_HOST = "SQLITE";
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			// SQLite
			G333Messages.showInfo("SQLite Initializing");
			// Declare SQLite handler
			manageSQLite = new sqlCore(log, "[" + G333Plugin.PLUGIN_NAME + "]",
					G333Plugin.PLUGIN_NAME, G333Plugin.PLUGIN_FOLDER);
			// Initialize SQLite handler
			manageSQLite.initialize();
			// Check if the table exists, if it doesn't create it
			String query = "";
			String insert = "";
			if (!manageSQLite.checkTable(digilockTable)) {
				if (manageSQLite.checkTable(oldDigilockTable)) {
					G333Messages.showInfo("Upgrade table " + oldDigilockTable
							+ " to " + digilockTable + ".");
					query = "CREATE TABLE "
							+ digilockTable
							+ " (x INTEGER, y INTEGER, z INTEGER, world TEXT, owner TEXT,"
							+ " pincode TEXT, "
							+ " coowners TEXT, users TEXT, closetimer INTEGER,"
							+ " typeid INTEGER, connectedto TEXT, usecost INTEGER);";
					insert = "insert into "
							+ digilockTable
							+ " (x, y, z, world, owner, pincode, "
							+ "coowners, users, closetimer, usecost, connectedto, typeid) "
							+ "select x, y, z, world, owner, pincode,"
							+ "'none', coowners, closetimer, usecost, connectedto, typeid FROM "
							+ oldDigilockTable + ";";
					// G333Messages.showInfo("Create Table:" + query);
					// G333Messages.showInfo("Insert:" + insert);
					manageSQLite.createTable(query);
					manageSQLite.insertQuery(insert);

				} else {
					G333Messages.showInfo("Creating table " + digilockTable);
					query = "CREATE TABLE "
							+ digilockTable
							+ " (x INTEGER, y INTEGER, z INTEGER, world TEXT, owner TEXT,"
							+ " pincode TEXT,"
							+ " coowners TEXT, users TEXT, closetimer INTEGER,"
							+ " typeid INTEGER, connectedto TEXT, usecost INTEGER);";
					manageSQLite.createTable(query);
				}
			} else {
				// G333Messages.showInfo(digilockTable + " exists.");
			}

			// Check BookshelfTable
			if (!manageSQLite.checkTable(bitInventoryTable)) {
				if (manageSQLite.checkTable(oldBitInventoryTable)) {
					G333Messages.showInfo("Upgrade " + oldBitInventoryTable
							+ " to " + bitInventoryTable + ".");
					query = "CREATE TABLE "
							+ bitInventoryTable
							+ " (playername VARCHAR(255), x INT, y INT, z INT, world VARCHAR(255), "
							+ "owner VARCHAR(255), "
							+ "name VARCHAR(255), "
							+ "coowners VARCHAR(255), "
							+ "usecost INT, slotno INT, "
							+ "itemstack_type INT, itemstack_amount INT, itemstack_durability INT); ";
					insert = "insert into "
							+ bitInventoryTable
							+ " (playername, x, y, z, world, "
							+ "owner, "
							+ "name, "
							+ "coowners, "
							+ "usecost, slotno, "
							+ "itemstack_type, itemstack_amount, itemstack_durability) "
							+ "select playername, x, y, z, world, owner, name,"
							+ "coowners, usecost, "
							+ "itemstack_type, itemstack_amount, itemstack_durability FROM "
							+ oldDigilockTable + ";";
					manageSQLite.createTable(query);
					manageSQLite.insertQuery(insert);
				} else {
					G333Messages
							.showInfo("Creating table " + bitInventoryTable);
					query = "CREATE TABLE "
							+ bitInventoryTable
							+ " (playername VARCHAR(255), x INT, y INT, z INT, world VARCHAR(255), "
							+ "owner VARCHAR(255), "
							+ "name VARCHAR(255), "
							+ "coowners VARCHAR(255), "
							+ "usecost INT, slotno int, "
							+ "itemstack_type INT, itemstack_amount NT, itemstack_durability INT);";
					manageSQLite.createTable(query);
				}
			} else {
				// G333Messages.showInfo(bitInventoryTable + " exists.");
			}

			// Check BooksTable
			if (!manageSQLite.checkTable(bookTable)) {
				if (manageSQLite.checkTable(oldBookTable)) {
					G333Messages.showInfo("Upgrade " + oldBookTable + " to "
							+ bookTable + ".");
					query = "CREATE TABLE "
							+ bookTable
							+ " (bookid INT, title TEXT,"
							+ " author TEXT, coauthors TEXT, "
							+ " numberofpages INT, pageno INT, bodytext TEXT,"
							+ " mastercopy BOOLEAN, mastercopyid INT,"
							+ " forcebook BOOLEAN, moved BOOLEAN, copy BOOLEAN, usecost INT);";
					insert = "insert into " + bookTable + " (bookid, title,"
							+ " author, coauthors, "
							+ " numberofpages, pageno, bodytext,"
							+ " mastercopy, mastercopyid,"
							+ " forcebook, moved, copy, usecost) "
							+ "select bookid, title," + " author, coauthors, "
							+ " numberofpages, pageno, bodytext,"
							+ " mastercopy, mastercopyid,"
							+ " force, moved, copy, usecost FROM "
							+ oldBookTable + ";";
					manageSQLite.createTable(query);
					manageSQLite.insertQuery(insert);
				} else {
					G333Messages.showInfo("Creating table " + bookTable);
					query = "CREATE TABLE "
							+ bookTable
							+ " (bookid INT, title TEXT,"
							+ " author TEXT, coauthors TEXT, "
							+ " numberofpages INT, pageno INT, bodytext TEXT,"
							+ " mastercopy BOOLEAN, mastercopyid INT,"
							+ " forcebook BOOLEAN, moved BOOLEAN, copy BOOLEAN, usecost INT);";

					manageSQLite.createTable(query);
				}
			} else {
				// G333Messages.showInfo(bookTable + " exists.");
			}
		}
	}

	// Playerdata
	public static Map<Integer, String> holdingKey = new HashMap<Integer, String>();
	public static Map<Integer, Integer> userno = new HashMap<Integer, Integer>();

	public static void removeUserData(int id) {
		if (userno.containsKey(id)) {
			// DigiLock
			holdingKey.remove(id);
			userno.remove(id);
		}
	}

	public static void addUserData(int id) {
		if (!userno.containsKey(id)) {
			// DigiLock
			userno.put(id, new Integer(id));
			holdingKey.put(id, "");
		}
	}

	public static void setupBook() {
		// THIS PREVENTS BOOK FROM STACKING
		try {
			boolean ok = false;
			try {
				// attempt to make books with different data values stack
				// separately
				Field field1 = net.minecraft.server.Item.class
						.getDeclaredField("bs");
				if (field1.getType() == boolean.class) {
					field1.setAccessible(true);
					field1.setBoolean(net.minecraft.server.Item.BOOK, true);
					ok = true;
				}
			} catch (Exception e) {
			}
			if (!ok) {
				// otherwise limit stack size to 1
				Field field2 = net.minecraft.server.Item.class
						.getDeclaredField("maxStackSize");
				field2.setAccessible(true);
				field2.setInt(net.minecraft.server.Item.BOOK, 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	static WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager()
				.getPlugin("WorldGuard");
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}
		return (WorldGuardPlugin) plugin;
	}

	private void setupMobArena() {
		if (mobArenaHandler != null) {
			return;
		}
		Plugin mobArenaPlugin = Bukkit.getServer().getPluginManager()
				.getPlugin("MobArena");
		if (mobArenaPlugin == null) {
			return;
		}
		mobArenaHandler = new MobArenaHandler();
		G333Messages.showInfo("MobArena detected.");
		return;
	}

	// long delay = 20L * 60 * saveTime;
	public static JailAPI jail;

	private void setupJail() {
		Plugin jailPlugin = getServer().getPluginManager().getPlugin("Jail");
		if (jailPlugin != null) {
			jail = ((Jail) jailPlugin).API;
			G333Messages.showInfo("Jail detected.");
			return;
		} else {
			return;
		}
	}


	


	public void updateInventory(Player player, ItemStack[] is) {
		BIT.inventories.put(player.getName(), is);
	}

	public static boolean canOpenBackpack(World world, Player player) {
		boolean canOpenBackpack = false;

		if (G333Permissions.hasPerm(player, "backpack.size54",
				G333Permissions.QUIET)
				|| G333Permissions.hasPerm(player, "backpack.size45",
						G333Permissions.QUIET)
				|| G333Permissions.hasPerm(player, "backpack.size36",
						G333Permissions.QUIET)
				|| G333Permissions.hasPerm(player, "backpack.size27",
						G333Permissions.QUIET)
				|| G333Permissions.hasPerm(player, "backpack.size18",
						G333Permissions.QUIET)
				|| G333Permissions.hasPerm(player, "backpack.size9",
						G333Permissions.QUIET)) {

			canOpenBackpack = true;
		} else {
			canOpenBackpack = false;
		}

		if (getWorldGuard() != null) {
			Location location = player.getLocation();
			com.sk89q.worldedit.Vector vector = new com.sk89q.worldedit.Vector(
					location.getX(), location.getY(), location.getZ());
			Map<String, ProtectedRegion> regions = getWorldGuard()
					.getGlobalRegionManager().get(location.getWorld())
					.getRegions();
			List<String> inRegions = new ArrayList<String>();
			for (String key_ : regions.keySet()) {
				ProtectedRegion region = regions.get(key_);
				if (region.contains(vector)) {
					inRegions.add(key_);
				}
			}
			for (String region : G333Config.SBP_noBackpackRegions) {
				if (inRegions.contains(region)) {
					canOpenBackpack = false;
				}
			}
		}
		if (mobArenaHandler != null) {
			if (mobArenaHandler.inRegion(player.getLocation())) {
				canOpenBackpack = false;
			}
		}
		if (jail != null) {
			if (jail.isPlayerJailed(player.getName()) == true) {
				canOpenBackpack = false;
			}
		}
		return canOpenBackpack;
	}

	public static boolean hasWorkbench(Player player) {
		if (G333Permissions.hasPerm(player, "backpack.workbench",
				G333Permissions.NOT_QUIET))
			return true;
		File saveFile;
		if (G333Config.SBP_InventoriesShare) {
			saveFile = new File(plugin.getDataFolder() + File.separator
					+ "inventories", player.getName() + ".yml");
		} else {
			saveFile = new File(plugin.getDataFolder() + File.separator
					+ "inventories", player.getName() + "_"
					+ player.getWorld().getName() + ".yml");
		}
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(saveFile);
		} catch (FileNotFoundException e) {
			//G333Messages
			//		.showInfo("The workbench file did not exist for player:"
			//				+ player.getName());
			// e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		boolean enabled = config.getBoolean("Workbench", false);
		try {
			config.save(saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (enabled)
			return true;
		return false;
	}

	public static void setWorkbench(Player player, boolean enabled) {
		File saveFile;
		if (G333Config.SBP_InventoriesShare) {
			saveFile = new File(plugin.getDataFolder() + File.separator
					+ "inventories", player.getName() + ".yml");
		} else {
			saveFile = new File(plugin.getDataFolder() + File.separator
					+ "inventories", player.getName() + "_"
					+ player.getWorld().getName() + ".yml");
		}
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(saveFile);
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		config.set("Workbench", enabled);
		try {
			config.save(saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean isOpenBackpack(Player player) {
		return BIT.openedInventories.containsKey(player.getName());
	}

	public static Inventory getOpenedBackpack(Player player) {
		return BIT.openedInventories.get(player.getName());
	}

	public Inventory getClosedBackpack(Player player) {
		Inventory inventory = SpoutManager.getInventoryBuilder().construct(
				SpoutBackpack.allowedSize(
						player.getWorld(), player, true), BIT.inventoryName);
		if (BIT.inventories.containsKey(player.getName())) {
			inventory.setContents(BIT.inventories.get(player.getName()));
		}
		return inventory;
	}

	public void setClosedBackpack(Player player, Inventory inventory) {
		BIT.inventories.put(player.getName(), inventory.getContents());
		return;
	}

	public static void loadInventory(Player player, World world) {
		if (BIT.inventories.containsKey(player.getName())) {
			return;
		}
		File saveFile;
		if (G333Config.SBP_InventoriesShare) {
			saveFile = new File(BIT.plugin.getDataFolder() + File.separator
					+ "inventories", player.getName() + ".yml");
		} else {
			saveFile = new File(BIT.plugin.getDataFolder() + File.separator
					+ "inventories", player.getName() + "_" + world.getName()
					+ ".yml");
		}
		@SuppressWarnings({})
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(saveFile);
		} catch (FileNotFoundException e) {
			//G333Messages
			//		.showWarning("The Inventoryfile was not found for user:"
			//				+ player.getName());
			// e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		int size = SpoutBackpack.allowedSize(world, player, true);
		Inventory inv = SpoutManager.getInventoryBuilder().construct(
				size, BIT.inventoryName);
		if (saveFile.exists()) {
			Integer i = 0;
			for (i = 0; i < size; i++) {
				ItemStack item = new ItemStack(0, 0);
				item.setAmount(config.getInt(i.toString() + ".amount", 0));
				item.setTypeId(config.getInt(i.toString() + ".type", 0));
				Integer durability = config.getInt(
						i.toString() + ".durability", 0);
				item.setDurability(Short.parseShort(durability.toString()));
				inv.setItem(i, item);
			}
		}
		BIT.inventories.put(player.getName(), inv.getContents());
	}

	public Language loadLanguage() {
		if (G333Config.SBP_language.equalsIgnoreCase("EN")) {
			return Language.ENGLISH;
		} else if (G333Config.SBP_language.equalsIgnoreCase("FR")) {
			return Language.FRENCH;
		} else {
			G333Messages
					.showInfo("SpoutBackpack: language set to ENGLISH by default.");
			return Language.ENGLISH;
		}
	}

}
