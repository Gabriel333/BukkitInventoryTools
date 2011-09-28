package dk.gabriel333.BukkitInventoryTools;

import java.net.MalformedURLException;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.alta189.sqlLibrary.MySQL.mysqlCore;
import com.alta189.sqlLibrary.SQLite.sqlCore;
import com.nijikokun.register.payment.Method;
import com.nijikokun.register.payment.Methods;
import de.Keyle.MyWolf.MyWolfPlugin;
import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;
import dk.gabriel333.Library.G333Plugin;

import me.neatmonster.spoutbackpack.SBHandler;

public class BIT extends JavaPlugin {

	public static BIT plugin;
	
	public static Boolean spout = false;

	// Hook into register
	public Methods Methods;
	public Method Method;

	// Hook into SpoutBackpack
	public static SBHandler spoutBackpackHandler; // The Backpack
													// inventoryHandler
	public static Boolean spoutbackpack = false; // is SpoutBackpack installed.

	// Hook into MyWolf
	public static Boolean mywolf = false;
	public static MyWolfPlugin myWolfPlugin;

	// SQLITE-MYSQL settings
	public static mysqlCore manageMySQL; // MySQL handler
	public static sqlCore manageSQLite; // SQLite handler
	public static Logger log = Logger.getLogger("Minecraft");
	public static String digilockTable = "BukkitInventoryTools2";

	// Test for SortInventory

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
			G333Messages.showInfo(pdfFile.getName() + " version "
					+ pdfFile.getVersion() + " is enabled!");
		} else {
			G333Messages.showError(pdfFile.getName() + " version "
					+ pdfFile.getVersion() + " could not be installed!");
		}
	}

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
		pm.registerEvent(Event.Type.CUSTOM_EVENT, new BITInputListener(),
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT, new BITSpoutListener(),
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT,
				new BITInventoryListener(this), Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_INTERACT, new BITPlayerListener(),
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
			G333Messages.showError("Safety is dependend on Spout!");
		}
	}

	private void setupRegister() {
		Methods.setMethod(plugin); 
		//Methods.createMethod(BIT.plugin);
		Method = Methods.getMethod();
		if (Methods.getMethod() != null)
			G333Messages.showInfo("Register enabled: " + Method.getName()
					+ " v" + Method.getVersion() + ").");
		else
			G333Messages.showInfo("Register disabled.");
			
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
		if (G333Config.g333Config.STORAGE_TYPE.equals("MYSQL")) {
			// Declare MySQL Handler
			manageMySQL = new mysqlCore(log,
					"[" + G333Plugin.PLUGIN_NAME + "]",
					G333Config.g333Config.STORAGE_HOST,
					G333Config.g333Config.STORAGE_DATABASE,
					G333Config.g333Config.STORAGE_USERNAME,
					G333Config.g333Config.STORAGE_PASSWORD);
			G333Messages.showInfo("MySQL Initializing");
			// Initialize MySQL Handler
			manageMySQL.initialize();
			try {
				if (manageMySQL.checkConnection()) {
					// Check if the Connection was successful
					String query;
					G333Messages.showInfo("MySQL connection successful");
					if (!manageMySQL.checkTable(BIT.digilockTable)) {
						if (manageMySQL.checkTable("BukkitInventoryTools")) {
							// DB Version1 String query =
							// "CREATE TABLE BukkitInventoryTools (id INT PRIMARY KEY AUTO_INCREMENT, pincode VARCHAR(4), owner VARCHAR(255), closetimer INT, x INT, y INT, z INT, world VARCHAR(255), shared VARCHAR(255), coowners VARCHAR(255));";
							query = "CREATE TABLE "
									+ BIT.digilockTable
									+ " (id INT PRIMARY KEY AUTO_INCREMENT, pincode VARCHAR(4),"
									+ "owner VARCHAR(255), closetimer INT,x INT,y INT,z INT,"
									+ "world VARCHAR(255),shared VARCHAR(255),coowners VARCHAR(255),"
									+ "typeid INT,connectedto VARCHAR(20)) AS SELECT pincode, "
									+ "owner,closetimer,x,y,z,world,shared,"
									+ "typeid,connectedto FROM BukkitInventoryTools;";
							G333Messages
									.showInfo("Upgrade BukkitInventoryTools to BukkitInventoryTools2");
						} else {
							query = "CREATE TABLE "
									+ BIT.digilockTable
									+ " (id INT PRIMARY KEY AUTO_INCREMENT, pincode VARCHAR(4), "
									+ "owner VARCHAR(255), closetimer INT, x INT, y INT, z INT, "
									+ "world VARCHAR(255), shared VARCHAR(255), coowners VARCHAR(255), "
									+ "typeid INT, connectedto VARCHAR(20));";
							G333Messages.showInfo("Creating table "
									+ BIT.digilockTable);
						}
						// Use mysqlCore.createTable(query) to create tables
						manageMySQL.createTable(query);
					}
				} else {
					G333Messages.showError("MySQL connection failed");
					G333Config.g333Config.STORAGE_HOST = "SQLITE";
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
			if (!manageSQLite.checkTable(BIT.digilockTable)) {
				if (manageSQLite.checkTable("BukkitInventoryTools")) {
					G333Messages
							.showInfo("Upgrade table BukkitInventoryTools to BukkitInventoryTools2");
					query = "CREATE TABLE "
							+ BIT.digilockTable
							+ " (id INT AUTO_INCREMENT PRIMARY_KEY, "
							+ "pincode VARCHAR(4), owner VARCHAR(255), closetimer INT, x INT, "
							+ "y INT, z INT, world VARCHAR(255), shared VARCHAR(255), "
							+ "coowners VARCHAR(255), typeid INT, connectedto VARCHAR(20))"
							+ "AS SELECT pincode, owner, closetimer, x, y, z, world, shared, "
							+ "typeid, connectedto FROM BukkitInventoryTools;";

				}

				G333Messages.showInfo("Creating table " + BIT.digilockTable);
				query = "CREATE TABLE "
						+ BIT.digilockTable
						+ " (id INT AUTO_INCREMENT PRIMARY_KEY, pincode VARCHAR(4), "
						+ "owner VARCHAR(255), closetimer INT, x INT, y INT, z INT, "
						+ "world VARCHAR(255), shared VARCHAR(255), coowners VARCHAR(255), "
						+ "typeid INT, connectedto VARCHAR(20));";
				manageSQLite.createTable(query);
				// Use sqlCore.createTable(query) to create tables
			}
		}
	}

}
