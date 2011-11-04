package dk.gabriel333.spoutbackpack;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;
import org.getspout.spout.inventory.CustomInventory;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SBEntityListener extends EntityListener {
	private SpoutBackpack	plugin;

	public SBEntityListener(SpoutBackpack plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (!plugin.userHasPermission(player, "backpack.nodrop") && plugin.canOpenBackpack(player.getWorld(), player)) {
				if (!((SpoutPlayer) player).isSpoutCraftEnabled()) {
					plugin.loadInventory(player, player.getWorld());
				}
				if (plugin.inventories.containsKey(player.getName())) {
					ItemStack[] items = plugin.inventories.get(player.getName());
					for (ItemStack item : items) {
						if (item != null && item.getAmount() > 0) {
							player.getWorld().dropItem(player.getLocation(), item);
						}
					}
					CustomInventory inventory = new CustomInventory(plugin.allowedSize(player.getWorld(), player, true),
							plugin.inventoryName);
					for (Integer i = 0; i < plugin.allowedSize(player.getWorld(), player, true); i++) {
						ItemStack item = new ItemStack(0, 0);
						inventory.setItem(i, item);
					}
					plugin.inventories.put(player.getName(), inventory.getContents());
					SBInventorySaveTask.saveInventory(player, player.getWorld());
				}
				if (!((SpoutPlayer) player).isSpoutCraftEnabled()) {

					player.sendMessage(plugin.logTag + plugin.li.getMessage("your") + ChatColor.RED + plugin.inventoryName
							+ ChatColor.WHITE + plugin.li.getMessage("hasbroken"));
				}
			}
		}
	}
}