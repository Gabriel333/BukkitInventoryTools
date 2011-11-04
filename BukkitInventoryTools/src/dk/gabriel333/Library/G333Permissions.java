package dk.gabriel333.Library;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.player.SpoutPlayer;

//import Permissions 3 classes
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

//import bPermissions
import de.bananaco.permissions.worlds.WorldPermissionsManager;

//import PermissionsEx classes
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class G333Permissions {

	public static String PERMISSION_NODE;
	public final static Boolean QUIET = true;
	public final static Boolean NOT_QUIET = false;

	// Hook into Permissions 3.xxx
	private static Plugin permissions3Plugin;
	private static PermissionHandler permission3Handler;
	public static Boolean permissions3 = false;

	// Hook into PermissionsBukkit
	private static Plugin permissionsBukkitPlugin;
	public static Boolean permissionsBukkit = false;

	// Hook into PermissionsEx
	private static PermissionManager permissionsexManager;
	public static Boolean permissionsex = false;

	// Hook into bPermissions
	public static WorldPermissionsManager wpm = null;
	public static Boolean bPermissions = false;

	// Initialize all permissionsplugins
	public static void setupPermissions(Plugin plugin) {
		PERMISSION_NODE = plugin.getDescription().getName() + ".";
		if (permissions3 || permissionsBukkit || permissionsex || bPermissions) {
			G333Messages
					.showWarning("Your permission system is allready detected!");
			return;
		} else {
			// PermissionsBukkit
			permissionsBukkitPlugin = plugin.getServer().getPluginManager()
					.getPlugin("PermissionsBukkit");
			if (permissionsBukkitPlugin != null) {
				permissionsBukkit = true;
				G333Messages.showInfo("PermissionsBukkit is detected.");
			} else

			// PermissionEx
			if (Bukkit.getServer().getPluginManager()
					.isPluginEnabled("PermissionsEx")) {
				permissionsexManager = PermissionsEx.getPermissionManager();
				G333Messages.showInfo("PermissionsEx is detected.");
				permissionsex = true;
			} else

			// bPermissions
			if (Bukkit.getServer().getPluginManager()
					.isPluginEnabled("bPermissions")) {

				try {
					wpm = de.bananaco.permissions.Permissions.getWorldPermissionsManager();
					bPermissions = true;
					G333Messages.showInfo("bPermissions is detected.");
				} catch (Exception e) {

				}
			} else
			// Permission3
			if (permissions3Plugin == null) {
				permissions3Plugin = plugin.getServer().getPluginManager()
						.getPlugin("Permissions");
				if (permissions3Plugin != null) {
					permission3Handler = ((Permissions) permissions3Plugin)
							.getHandler();
					permissions3 = true;
					G333Messages
							.showInfo("Permissions3/SuperpermBridge is detected. "
									+ ((Permissions) permissions3Plugin)
											.getDescription().getFullName());
				}
			}

			// No permission systems found
			if (!(permissions3 || permissionsBukkit || permissionsex || bPermissions)) {
				G333Messages.showInfo("Defaulting to build-in permissions.");
				return;
			}
		}
	}

	// Test if the player has permissions to do the action
	public static boolean hasPerm(CommandSender sender, String label,
			Boolean quiet) {

		// How to hook into PermissionsBukkit
		// Basic Permission Check
		// In this example (MyPlugin) is meant to represent the name of your
		// plugin,
		// for example... iConomy would look like:
		// Player player = (Player) sender;
		// if (player.hasPermission("a.custom.node") {
		// return true;
		// }

		// How to hook into Permissions 3.1.6
		// Basic Permission Check
		// In this example (MyPlugin) is meant to represent the name of your
		// plugin,
		// for example... iConomy would look like:
		// if (!(MyPlugin).permissionHandler.has(player, "a.custom.node")) {
		// return;
		// }
		// Checking if a user belongs to a group
		// if (!(MyPlugin).permissionHandler.inGroup(world, name, groupName)) {
		// return;
		// }

		// Permission check
		// if(permissions.has(player, "yourplugin.permission")){
		// yay!
		// } else {
		// houston, we have a problems :)
		// }
		SpoutPlayer sPlayer = (SpoutPlayer) sender;
		Boolean hasPermission = false;

		// Fallback builtin Permission system / PermissionsBukkit system
		if (sPlayer.hasPermission((PERMISSION_NODE + label).toLowerCase())
				|| sPlayer.hasPermission((PERMISSION_NODE + "*").toLowerCase())) {
			hasPermission = true;
		} else if (permissionsex) {
			hasPermission = permissionsexManager.has(sPlayer,
					(PERMISSION_NODE + label).toLowerCase());
		} else if (bPermissions) {
			 hasPermission = wpm.getPermissionSet(sPlayer.getWorld()).has(sPlayer,
			 (PERMISSION_NODE + label).toLowerCase());
		} else if (permissions3) {
			// or SuperpermBridge
			hasPermission = permission3Handler.has(sPlayer,
					(PERMISSION_NODE + label).toLowerCase());
		}

		// return permission
		if (hasPermission) {
			if (G333Config.DEBUG_PERMISSIONS)
				sPlayer.sendMessage(ChatColor.GREEN
						+ "G333Permissions: You have permission to: "
						+ (PERMISSION_NODE + label).toLowerCase());
			return true;
		} else if (NOT_QUIET) {
			if (G333Config.DEBUG_PERMISSIONS)
				sPlayer.sendMessage(ChatColor.RED
						+ "G333Permissions: You DONT have permission to: "
						+ (PERMISSION_NODE + label).toLowerCase());
			sPlayer.sendMessage(ChatColor.RED
					+ "You to dont have permission to do this." + " ("
					+ (G333Plugin.PLUGIN_NAME + "." + label).toLowerCase()
					+ ")");
		}
		return false;
	}
}
