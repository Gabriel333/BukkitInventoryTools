package dk.gabriel333.Library;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

public class G333Config extends Configuration {
	public String LIBRARY_LANGUAGE;
	public String LIBRARY_SORTKEY;
	public String LIBRARY_MENUKEY;
	public String LIBRARY_LOCKKEY;
	public Boolean SORT_DISPLAYSORTARCHIEVEMENT;
	private String SORT_SORTSEQ;
	public String[] SORTSEQ;
	
	private String SORT_TOOLS;
	public static String[] tools;
	private String SORT_WEAPONS;
	public static String[] weapons;
	private String SORT_ARMORS;
	public static String[] armors;
	private String SORT_FOODS;
	public static String[] foods;
	private String SORT_VEHICLES;
	public static String[] vehicles;
	private String SORT_BUCKETS;
	public static String[] buckets;
	
	public String STORAGE_TYPE;
	public String STORAGE_HOST;
	public String STORAGE_USERNAME;
	public String STORAGE_PASSWORD;
	public String STORAGE_DATABASE;
	public Boolean DEBUG_PERMISSIONS;
	public Boolean DEBUG_SORTINVENTORY;
	public Boolean DEBUG_ONENABLE;
	public Boolean DEBUG_KEYBOARD;
	public Boolean DEBUG_SQL;
	public Boolean DEBUG_GUI;

	public G333Config(File file) {
		super(file);
		if (!file.exists())
			loadFileFromJar(CONFIG_FILE);
		load();
		//General
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
		SORT_DISPLAYSORTARCHIEVEMENT = getBooleanParm("Sort.DisplaySortArchievement",true);
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
		
		//Debug
		DEBUG_PERMISSIONS = getBooleanParm("Debug.Permissions", false);
		DEBUG_SORTINVENTORY = getBooleanParm("Debug.Inventory", false);
		DEBUG_ONENABLE = getBooleanParm("Debug.OnEnable", false);
		DEBUG_KEYBOARD = getBooleanParm("Debug.Keyboard", false);
		DEBUG_SQL = getBooleanParm("Debug.SQL", false);
		DEBUG_GUI = getBooleanParm("Debug.GUI", false);
		setHeader(
				"###########################################################",
				"# This is an autogenerated config.yml, because you had an #",
				"# old version of the config.yml. I recommended that you   #",
				"# backup your current config.yml and then delete it from  #",
				"# from the plugin directory and reload the server, to     #",
				"# get a fresh config.yml                                  #",
				"#                                                         #",
				"#                                                         #",
				"###########################################################");
		if (dosave) {
			G333Messages.showWarning("YOUR CONFIG.YML IS NOT UPTODATE");
			save();
		}
			
	}

	public static String CONFIG_FILE = "config.yml";
	public static G333Config g333Config;

	public static void setupConfig(Plugin plugin) {
		if (!plugin.getDataFolder().exists())
			plugin.getDataFolder().mkdirs();
		File configfile = new File(plugin.getDataFolder(),
				G333Config.CONFIG_FILE);
		g333Config = new G333Config(configfile);
	}

	private Boolean dosave = false;

	private String getStringParm(String string, String def) {
		String str = getString(string);
		if (str == null) {
			dosave = true;
			G333Messages.showWarning("Missing parameter:" +string);
			return getString(string,def);
		} else
			return str;
	}

	private Boolean getBooleanParm(String string, Boolean def) {
		String str = getString(string);
		if (str == null) {
			dosave = true;
			G333Messages.showWarning("Missing parameter:" +string);
		} 
		return getBoolean(string,def);
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
					G333Messages.showWarning("The file: " + filename
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
