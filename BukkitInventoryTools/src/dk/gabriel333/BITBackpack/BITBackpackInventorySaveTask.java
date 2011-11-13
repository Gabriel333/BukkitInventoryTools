package dk.gabriel333.BITBackpack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;
//import org.getspout.spout.inventory.CustomInventory;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.BITConfig;

public class BITBackpackInventorySaveTask implements Runnable {

	// private static BIT plugin;

	// public SBInventorySaveTask(BIT plugin) {
	// SBInventorySaveTask.plugin = plugin;
	// }

	public static Logger logger = Logger.getLogger("minecraft");
	public static String logTag = "[BITSpoutBackpack]";

	public static void saveAll() {
		Player[] players = Bukkit.getServer().getOnlinePlayers();
		HashMap<String, ItemStack[]> invs = new HashMap<String, ItemStack[]>(
				BIT.inventories);
		for (Player player : players) {
			saveInventory(player, player.getWorld());
			if (invs.containsKey(player.getName())) {
				invs.remove(player.getName());
			}
		}
	}

	public static void saveInventory(Player player, World world) {
		File saveFile;
		if (BITConfig.getBooleanParm("Backpack.InventoriesShare."
				+ player.getWorld().getName(), true)) {
			saveFile = new File(BIT.plugin.getDataFolder() + File.separator
					+ "inventories", player.getName() + ".yml");
		} else {
			saveFile = new File(BIT.plugin.getDataFolder() + File.separator
					+ "inventories", player.getName() + "_" + world.getName()
					+ ".yml");
		}
		YamlConfiguration config = new YamlConfiguration();
		if (BIT.inventories.containsKey(player.getName())) {
			Inventory inv = SpoutManager.getInventoryBuilder()
					.construct(
							BITBackpack.allowedSize(player.getWorld(),
									player, true),
							BIT.inventoryName);
			inv.setContents(BIT.inventories.get(player.getName()));
			Integer i = 0;
			for (i = 0; i < BITBackpack.allowedSize(world, player, true); i++) {
				ItemStack item = inv.getItem(i);
				config.set(i.toString() + ".amount", item.getAmount());
				Short durab = item.getDurability();
				config.set(i.toString() + ".durability", durab.intValue());
				config.set(i.toString() + ".type", item.getTypeId());
				config.set("Size", BITBackpack.allowedSize(world, player, true));
				try {
					config.save(saveFile);
				} catch (IOException e) {
					player.sendMessage("The backpack could not be saved.");
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void run() {
		if (Bukkit.getServer().getOnlinePlayers().length != 0) {
			saveAll();
		}
	}
}