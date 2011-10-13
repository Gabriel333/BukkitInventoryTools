package dk.gabriel333.BukkitInventoryTools;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
//import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.PopupScreen;

import com.alta189.sqlLibrary.MySQL.mysqlCore;
import com.alta189.sqlLibrary.SQLite.sqlCore;
import dk.gabriel333.register.payment.Method;
import dk.gabriel333.register.payment.Methods;

import de.Keyle.MyWolf.MyWolfPlugin;
import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;
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

	// Hook into MyWolf
	public static Boolean mywolf = false;
	public static MyWolfPlugin myWolfPlugin;

	// SQLITE-MYSQL settings
	public static mysqlCore manageMySQL; // MySQL handler
	public static sqlCore manageSQLite; // SQLite handler
	public static Logger log = Logger.getLogger("Minecraft");
	public static String digilockTable = "BukkitInventoryTools4";
	public static String oldDigilockTable = "BukkitInventoryTools3";

	// USERDATA
	public static int usercounter = 0;
	public static Map<Integer, Integer> userno = new HashMap<Integer, Integer>();
	public static Map<Integer, PopupScreen> popupGetPincode = new HashMap<Integer, PopupScreen>();
	public static Map<Integer, PopupScreen> popupSetPincode = new HashMap<Integer, PopupScreen>();
	public static Map<Integer, GenericTextField> pincode = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> owner = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> closetimer = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> coOwners = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> useCost = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> connectedTo = new HashMap<Integer, GenericTextField>();
	public static Map<Integer, GenericTextField> shared = new HashMap<Integer, GenericTextField>();

	// public static Map<Integer, SpoutBlock> clickedBlock = new
	// HashMap<Integer, SpoutBlock>();

	@Override
	public void onEnable() {
		plugin = this;
		PluginDescriptionFile pdfFile = this.getDescription();

		if (!isSortInventoryInstalled()) {
			G333Plugin.setupPlugin(this);
			G333Config.setupConfig(this);
			setupSpout();
			setupSQL();
			setupRegister();
			setupSpoutBackpack();
			setupMyWolf();
			registerEvents();
			addCommands();
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
		pm.registerEvent(Type.REDSTONE_CHANGE, new BITBlockListener(),
				Priority.Normal, this);
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
		pm.registerEvent(Event.Type.CUSTOM_EVENT, new BITInputListener(),
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT, new BITSpoutListener(),
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT,
				new BITInventoryListener(this), Event.Priority.Normal, this);
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
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		G333Messages.showInfo(pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " is disabled!");
	}

	public void addCommands() {
		// Register commands
		getCommand("Sort").setExecutor(new BITCommandSort(this));
		getCommand("Digilock").setExecutor(new BITCommandDigiLock(this));

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

	private void setupSQL() {
		if (G333Config.config.STORAGE_TYPE.equals("MYSQL")) {
			// Declare MySQL Handler
			manageMySQL = new mysqlCore(log,
					"[" + G333Plugin.PLUGIN_NAME + "]",
					G333Config.config.STORAGE_HOST,
					G333Config.config.STORAGE_DATABASE,
					G333Config.config.STORAGE_USERNAME,
					G333Config.config.STORAGE_PASSWORD);
			G333Messages.showInfo("MySQL Initializing");
			// Initialize MySQL Handler
			manageMySQL.initialize();
			try {
				if (manageMySQL.checkConnection()) {
					// Check if the Connection was successful
					String query;
					G333Messages.showInfo("MySQL connection successful");
					if (!manageMySQL.checkTable(digilockTable)) {
						if (manageMySQL.checkTable(oldDigilockTable)) {
							G333Messages.showInfo("Upgrade " + oldDigilockTable
									+ " to " + digilockTable + ".");
							query = "CREATE TABLE "
									+ digilockTable
									+ " (id INT PRIMARY KEY AUTO_INCREMENT, pincode VARCHAR(20),"
									+ "owner VARCHAR(255), closetimer INT,x INT,y INT,z INT,"
									+ "world VARCHAR(255),shared VARCHAR(255),coowners VARCHAR(255),"
									+ "typeid INT,connectedto VARCHAR(20),usecost INT) AS SELECT pincode, "
									+ "owner,closetimer,x,y,z,world,shared,"
									+ "typeid,connectedto,usecost FROM "
									+ oldDigilockTable + ";";
						} else {
							G333Messages.showInfo("Creating table "
									+ digilockTable);
							query = "CREATE TABLE "
									+ digilockTable
									+ " (id INT PRIMARY KEY AUTO_INCREMENT, pincode VARCHAR(20), "
									+ "owner VARCHAR(255), closetimer INT, x INT, y INT, z INT, "
									+ "world VARCHAR(255), shared VARCHAR(255), coowners VARCHAR(255), "
									+ "typeid INT, connectedto VARCHAR(20), usecost INT);";
						}
						manageMySQL.createTable(query);
					}
				} else {
					G333Messages.showError("MySQL connection failed");
					G333Config.config.STORAGE_HOST = "SQLITE";
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
			if (!manageSQLite.checkTable(digilockTable)) {
				if (manageSQLite.checkTable(oldDigilockTable)) {
					G333Messages.showInfo("Upgrade table " + oldDigilockTable
							+ " to " + digilockTable + ".");
					query = "CREATE TABLE "
							+ digilockTable
							+ " (id INT AUTO_INCREMENT PRIMARY_KEY,"
							+ " pincode VARCHAR(20), owner VARCHAR(255), closetimer INT, x INT,"
							+ " y INT, z INT, world VARCHAR(255), shared VARCHAR(255),"
							+ " coowners VARCHAR(255), typeid INT, connectedto VARCHAR(20),"
							+ " usecost INT) AS (SELECT pincode, owner, closetimer, x, y, z,"
							+ " world, shared, typeid, connectedto, usecost FROM "
							+ oldDigilockTable + ");";

				} else {
					G333Messages.showInfo("Creating table " + digilockTable);
					query = "CREATE TABLE "
							+ digilockTable
							+ " (id INT AUTO_INCREMENT PRIMARY_KEY, pincode VARCHAR(20), "
							+ "owner VARCHAR(255), closetimer INT, x INT, y INT, z INT, "
							+ "world VARCHAR(255), shared VARCHAR(255), coowners VARCHAR(255), "
							+ "typeid INT, connectedto VARCHAR(20), usecost INT);";
				}
				manageSQLite.createTable(query);
			}
		}
	}

}
