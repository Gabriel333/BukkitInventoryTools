package dk.rocologo.BukkitInventoryTools;

import java.net.MalformedURLException;
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

import com.alta189.sqlLibrary.MySQL.mysqlCore;
import com.alta189.sqlLibrary.SQLite.sqlCore;

import de.Keyle.MyWolf.MyWolfPlugin;
import dk.rocologo.Library.RLConfig;
import dk.rocologo.Library.RLMessages;
import dk.rocologo.Library.RLPlugin;

import me.neatmonster.spoutbackpack.SBHandler;

public class BIT extends JavaPlugin {
	public static BIT instance;

	public static Boolean spout = false;

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

	// GUI
	// public Screen sortScreen = new GenericPopup();

	@Override
	public void onEnable() {
		instance = this;

		RLPlugin.setupPlugin(this);
		RLConfig.setupConfig(this);
		setupSpout();
		setupSQL();
		setupSpoutBackpack();
		setupMyWolf();
		setupGUI();
		registerEvents();
		addCommands();

		PluginDescriptionFile pdfFile = this.getDescription();
		RLMessages.showInfo(pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " is enabled!");
	}

	private void setupGUI() {
		// TODO Auto-generated method stub
		// sortScreen=sPlayer.getMainScreen();
	}

	public void registerEvents() {
		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_BREAK, new BITBlockListener(),
				Priority.Normal, this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT, new BITInputListener(),
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT, new BITSpoutListener(this),
				Event.Priority.Normal, this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT,
				new BITInventoryListener(this), Event.Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_INTERACT, new BITPlayerListener(),
				Priority.Normal, this);
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		RLMessages.showInfo(pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " is disabled!");
	}

	public void addCommands() {
		// Register commands
		getCommand("Sort").setExecutor(new BITCommandSort(this));
	}

	private void setupSpout() {
		Plugin spoutPlugin = this.getServer().getPluginManager()
				.getPlugin("Spout");
		if (spoutPlugin != null) {
			spout = true;
			RLMessages.showInfo("Spout is detected.");
		} else {
			RLMessages.showError("Safety is dependend on Spout!");
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
					RLMessages.showInfo("SpoutBackpack is detected.");
				} else {
					RLMessages
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
					RLMessages.showInfo("MyWolf is detected.");
				} else {
					RLMessages
							.showWarning("MyWolf is detected, but spout is not detected.");
					mywolf = false;
				}

			}

		}

		// you get access to MyWolf inventory with:
		// CustomMCInventory inv = myWolfPlugin.getMyWolf(sPlayer).inv;
	}

	private void setupSQL() {

		if (RLConfig.rLConfig.DEBUG_SQL) {
			RLMessages
					.showInfo("Storagetype:" + RLConfig.rLConfig.STORAGE_TYPE);
		}

		if (RLConfig.rLConfig.STORAGE_TYPE.equals("MySQL")) {
			// Declare MySQL Handler
			manageMySQL = new mysqlCore(log, "[" + RLPlugin.PLUGIN_NAME + "]",
					RLConfig.rLConfig.STORAGE_HOST,
					RLConfig.rLConfig.STORAGE_DATABASE,
					RLConfig.rLConfig.STORAGE_USERNAME,
					RLConfig.rLConfig.STORAGE_PASSWORD);
			RLMessages.showInfo("MySQL Initializing");
			// Initialize MySQL Handler
			manageMySQL.initialize();
			try {
				if (manageMySQL.checkConnection()) {
					// Check if the Connection was successful
					RLMessages.showInfo("MySQL connection successful");
					if (!manageMySQL.checkTable("BukkitInventoryTools")) {
						// Check if the table exists in the database if not
						// create it
						RLMessages
								.showInfo("Creating table BukkitInventoryTools");
						String query = "CREATE TABLE BukkitInventoryTools (id INT AUTO_INCREMENT PRIMARY_KEY, pincode VARCHAR(4), owner VARCHAR(255), closetimer INT, x INT, y INT, z INT);";
						manageMySQL.createTable(query);
						// Use mysqlCore.createTable(query) to create tables
					}
				} else {
					RLMessages.showError("MySQL connection failed");
					RLConfig.rLConfig.STORAGE_HOST = "SQLITE";
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// SQLite
			RLMessages.showInfo("SQLite Initializing");
			// Declare SQLite handler
			manageSQLite = new sqlCore(log, "[" + RLPlugin.PLUGIN_NAME + "]",
					RLPlugin.PLUGIN_NAME, RLPlugin.PLUGIN_FOLDER);
			// Initialize SQLite handler
			manageSQLite.initialize();
			// Check if the table exists, if it doesn't create it
			if (!manageSQLite.checkTable("BukkitInventoryTools")) {
				RLMessages.showInfo("Creating table BukkitInventoryTools");
				String query = "CREATE TABLE BukkitInventoryTools (id INT AUTO_INCREMENT PRIMARY_KEY, pincode VARCHAR(4), owner VARCHAR(255), closetimer INT, x INT, y INT, z INT);";
				manageSQLite.createTable(query);
				// Use sqlCore.createTable(query) to create tables
			}
		}
	}

}
