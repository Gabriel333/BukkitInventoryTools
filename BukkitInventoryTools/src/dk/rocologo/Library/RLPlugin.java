package dk.rocologo.Library;

import org.bukkit.plugin.Plugin;

public class RLPlugin {
	
	public static String PLUGIN_NAME; 
	public static String PLUGIN_FOLDER;

	public static void setupPlugin(Plugin plugin) {
		PLUGIN_NAME=plugin.getDescription().getName();
		PLUGIN_FOLDER = plugin.getDataFolder().toString();
		RLPermissions.setupPermissions(plugin);
	}
	

}
