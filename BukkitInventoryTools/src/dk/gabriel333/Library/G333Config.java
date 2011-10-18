package dk.gabriel333.Library;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import dk.gabriel333.BukkitInventoryTools.BIT;

public class G333Config {
	
	final static int LATEST_VERSION=1;
	
	public static int LIBRARY_VERSION;
	public static String LIBRARY_LANGUAGE;
	public static String LIBRARY_SORTKEY;
	public static String LIBRARY_MENUKEY;
	public static String LIBRARY_LOCKKEY;
	
	public static String STORAGE_TYPE;
	public static String STORAGE_HOST;
	public static String STORAGE_USERNAME;
	public static String STORAGE_PASSWORD;
	public static String STORAGE_DATABASE;
	
	public static Boolean SORT_DISPLAYSORTARCHIEVEMENT;
	private static String SORT_SORTSEQ;
	public static String[] SORTSEQ;
	private static String SORT_TOOLS;
	public static String[] tools;
	private static String SORT_WEAPONS;
	public static String[] weapons;
	private static String SORT_ARMORS;
	public static String[] armors;
	private static String SORT_FOODS;
	public static String[] foods;
	private static String SORT_VEHICLES;
	public static String[] vehicles;
	private static String SORT_BUCKETS;
	public static String[] buckets;
	
	public static int DIGILOCK_COST;
	public static int DIGILOCK_USEMAXCOST;
	public static int DIGILOCK_DESTROYCOST;
	
	public static int BOOKSHELF_COST;
	public static int BOOKSHELF_SIZE;
	public static int BOOKSHELF_DESTROYCOST;
	
	public static Boolean DEBUG_PERMISSIONS;
	public static Boolean DEBUG_SORTINVENTORY;
	public static Boolean DEBUG_ONENABLE;
	public static Boolean DEBUG_KEYBOARD;
	public static Boolean DEBUG_SQL;
	public static Boolean DEBUG_GUI;
	public static Boolean DEBUG_DOOR;
	public static Boolean DEBUG_EVENTS;
	
	private static String CONFIG_FILE = "config.yml";
	public static YamlConfiguration config;
	private static Boolean dosave = false;
	
	public static void bitSetupConfig() {
		if (!BIT.plugin.getDataFolder().exists())
			BIT.plugin.getDataFolder().mkdirs();
		File configfile = new File(BIT.plugin.getDataFolder(),
				CONFIG_FILE);
		if (!configfile.exists()) {
			G333Messages.showInfo("Loading config.yml from BukkitInventoryTools.jar");
			loadFileFromJar(CONFIG_FILE);
		}
		config = new YamlConfiguration();
		try {
			config.load(configfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		//General
		LIBRARY_VERSION = getIntParm("Library.Version", 0);
		LIBRARY_LANGUAGE = getStringParm("Library.Language", "EN");
		LIBRARY_SORTKEY = getStringParm("Library.SortKey", "KEY_S");
		LIBRARY_MENUKEY = getStringParm("Library.MenuKey", "KEY_M");
		LIBRARY_LOCKKEY = getStringParm("Library.LockKey", "KEY_L");
		//SQL
		STORAGE_TYPE=getStringParm("Storage.Type", "SQLite");
		STORAGE_HOST=getStringParm("Storage.Host", "SQLite");
		STORAGE_USERNAME=getStringParm("Storage.Username", "Admin");
		STORAGE_PASSWORD=getStringParm("Storage.Password", "Changethis");
		STORAGE_DATABASE=getStringParm("Storage.Database", "SortInventory");
		//Sort                                              
		SORT_DISPLAYSORTARCHIEVEMENT = getBooleanParm("Sort.DisplaySortArchievement", true);
        SORT_SORTSEQ = getStringParm("Sort.SortSEQ", "STONE,COBBLESTONE,DIRT,WOOD");
		SORTSEQ = SORT_SORTSEQ.split(",");
		
		SORT_TOOLS = getStringParm("Sort.Tools", "256,257,258,269,270,271,273,274,275,277,278,279,284,285,286,290,291,292,293,294,346");
		tools = SORT_TOOLS.split(",");
		SORT_WEAPONS = getStringParm("Sort.Weapons", "267,268,272,276,283,261");
		weapons = SORT_WEAPONS.split(",");
		SORT_ARMORS = getStringParm("Sort.Armors", "298,299,300,301,302,303,304,305,306,307,308,309,310,311,312,313,314,315,316,317");
		armors = SORT_ARMORS.split(",");
		SORT_FOODS = getStringParm("Sort.Foods", "260,282,297,319,320,322,349,350,354,357");
		foods = SORT_FOODS.split(",");
		SORT_VEHICLES = getStringParm("Sort.Vehicles", "328,333,342,343");
		vehicles = SORT_VEHICLES.split(",");
		SORT_BUCKETS = getStringParm("Sort.Buckets","326,327,335");
		buckets = SORT_BUCKETS.split(",");
		
		//Digilock
		DIGILOCK_COST = getIntParm("DigiLock.Cost", 100);
		DIGILOCK_USEMAXCOST = getIntParm("DigiLock.UseMaxCost", 0);
		DIGILOCK_DESTROYCOST = getIntParm("DigiLock.DestroyCost", -10);
		
		//Bookshelf
		BOOKSHELF_COST = getIntParm("Bookshelf.Cost", 50);
		BOOKSHELF_SIZE = getIntParm("Bookshelf.Size", 9);
		BOOKSHELF_DESTROYCOST = getIntParm("Bookshelf.DestroyCost", 10);
				
		//Debug
		DEBUG_PERMISSIONS = getBooleanParm("Debug.Permissions", false);
		DEBUG_SORTINVENTORY = getBooleanParm("Debug.Inventory", false);
		DEBUG_ONENABLE = getBooleanParm("Debug.OnEnable", false);
		DEBUG_KEYBOARD = getBooleanParm("Debug.Keyboard", false);
		DEBUG_SQL = getBooleanParm("Debug.SQL", false);
		DEBUG_GUI = getBooleanParm("Debug.GUI", false);
		DEBUG_DOOR = getBooleanParm("Debug.Door", false);
		DEBUG_EVENTS = getBooleanParm("Debug.Events", false);
		
		if (dosave || LATEST_VERSION>LIBRARY_VERSION) {
			config.options().header("##########################################################\n"+
					" This is an autogenerated config.yml, because you had an #\n"+
					" old version of the config.yml. I recommended that you   #\n"+
					" backup your current config.yml and then delete it from  #\n"+
					" from the plugin directory and reload the server, to     #\n"+
					" get a fresh config.yml                                  #\n"+
					"                                                         #\n"+
					"                                                         #\n"+
					"##########################################################");
			G333Messages.showWarning("YOUR CONFIG.YML IS NOT UP TO DATE!");
			try {
				config.save(configfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
	}

	private static int getIntParm(String path, int def) {
		if (!config.contains(path)) {
			config.set(path, def);
			dosave=true;
			G333Messages.showWarning("Missing parameter:" +path);
		} 
		return config.getInt(path, def);
	}

	private static String getStringParm(String path, String def) {
		if (!config.contains(path)) {
			config.set(path, def);
			dosave=true;
			G333Messages.showWarning("Missing parameter:" +path);
		} 
		return config.getString(path, def);
	}

	private static Boolean getBooleanParm(String path, Boolean def) {
		if (!config.contains(path)) {
			config.set(path, def);
			dosave=true;
			G333Messages.showWarning("Missing parameter:" +path);
		} 
		return config.getBoolean(path, def);
	}
	
	private static File loadFileFromJar(String filename) {
		File actual = new File(G333Plugin.PLUGIN_FOLDER, filename);
		if (!actual.exists()) {
			InputStream input = G333Config.class.getResourceAsStream("/"
					+ filename);
			if (input != null) {
				FileOutputStream output = null;
				try {
					output = new FileOutputStream(actual);
					byte[] buf = new byte[8192];
					int length = 0;

					while ((length = input.read(buf)) > 0) {
						output.write(buf, 0, length);
					}
					G333Messages.showInfo("The file: " + filename
							+ " has been created.");
				} catch (Exception e) {
					G333Messages.showStackTrace(e);
				} finally {
					try {
						input.close();
					} catch (Exception e) {
					}
					try {
						output.flush();
						output.close();
					} catch (Exception e) {
					}
				}
			}
		}
		return actual;
	}

}
