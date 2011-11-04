package dk.gabriel333.spoutbackpack;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.config.Configuration;
import org.getspout.spout.inventory.CustomInventory;

@SuppressWarnings("deprecation")
public class SBInventorySaveTask implements Runnable {

	private static SpoutBackpack	plugin;
	public static Logger			logger	= Logger.getLogger("minecraft");
	public static String			logTag	= "[SpoutBackpack]";

	public static void saveAll() {
		if (plugin.logSaves) {
			logger.info(logTag + plugin.li.getMessage("savinginventories"));
		}
		Player[] players = plugin.getServer().getOnlinePlayers();
		HashMap<String, ItemStack[]> invs = new HashMap<String, ItemStack[]>(plugin.inventories);
		for (Player player : players) {
			saveInventory(player, player.getWorld());
			if (invs.containsKey(player.getName())) {
				invs.remove(player.getName());
			}
		}
	}

	public static void saveInventory(Player player, World world) {
		File saveFile;
		if (plugin.config.getBoolean("Backpack." + world.getName() + ".InventoriesShare?", true)) {
			saveFile = new File(plugin.getDataFolder() + File.separator + "inventories", player.getName() + ".yml");
		} else {
			saveFile = new File(plugin.getDataFolder() + File.separator + "inventories", player.getName() + "_" + world.getName() + ".yml");
		}
		Configuration config = new Configuration(saveFile);
		if (plugin.inventories.containsKey(player.getName())) {
			CustomInventory inv = new CustomInventory(plugin.allowedSize(world, player, true), plugin.inventoryName);
			inv.setContents(plugin.inventories.get(player.getName()));
			Integer i = 0;
			for (i = 0; i < plugin.allowedSize(world, player, true); i++) {
				ItemStack item = inv.getItem(i);
				config.getInt(i.toString() + ".amount", item.getAmount());
				Short durab = item.getDurability();
				config.getInt(i.toString() + ".durability", durab.intValue());
				config.getInt(i.toString() + ".type", item.getTypeId());
				config.setProperty("Size", plugin.allowedSize(world, player, true));
				config.save();
			}
		}
	}

	public SBInventorySaveTask(SpoutBackpack plugin) {
		SBInventorySaveTask.plugin = plugin;
	}

	@Override
	public void run() {
		if (Bukkit.getServer().getOnlinePlayers().length != 0) {
			saveAll();
		}
	}
}