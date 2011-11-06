package dk.gabriel333.spoutbackpack;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;
import org.getspout.spout.inventory.CustomInventory;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.G333Permissions;

public class SBEntityListener extends EntityListener {
	private BIT plugin;

	public SBEntityListener(BIT plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player player = (Player) entity;
			if (!G333Permissions.hasPerm(player, "backpack.nodrop",
					G333Permissions.QUIET)
					&& BIT.canOpenBackpack(player.getWorld(), player)) {
				if (!((SpoutPlayer) player).isSpoutCraftEnabled()) {
					BIT.loadInventory(player, player.getWorld());
				}
				if (BIT.inventories.containsKey(player.getName())) {
					ItemStack[] items = BIT.inventories
							.get(player.getName());
					for (ItemStack item : items) {
						if (item != null && item.getAmount() > 0) {
							player.getWorld().dropItem(player.getLocation(),
									item);
						}
					}
					CustomInventory inventory = new CustomInventory(
							SpoutBackpack.allowedSize(player.getWorld(), player, true),
							BIT.inventoryName);
					for (Integer i = 0; i < SpoutBackpack.allowedSize(player.getWorld(),
							player, true); i++) {
						ItemStack item = new ItemStack(0, 0);
						inventory.setItem(i, item);
					}
					BIT.inventories.put(player.getName(),
							inventory.getContents());
					SBInventorySaveTask
							.saveInventory(player, player.getWorld());
				}
				if (!((SpoutPlayer) player).isSpoutCraftEnabled()) {

					player.sendMessage(plugin.logTag
							+ BIT.li.getMessage("your") + ChatColor.RED
							+ BIT.inventoryName + ChatColor.WHITE
							+ BIT.li.getMessage("hasbroken"));
				}
			}
		}
	}
}