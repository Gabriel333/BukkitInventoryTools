package dk.gabriel333.spoutbackpack;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.event.inventory.InventoryClickEvent;
import org.getspout.spoutapi.event.inventory.InventoryCloseEvent;
import org.getspout.spoutapi.event.inventory.InventoryListener;
import org.getspout.spoutapi.event.inventory.InventorySlotType;

import dk.gabriel333.BukkitInventoryTools.BIT;

public class SBInventoryListener extends InventoryListener {
	private BIT plugin;

	public SBInventoryListener(BIT plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = event.getPlayer();
		if (!plugin.openedInventoriesOthers.containsKey(player.getName())) {
			if (plugin.openedInventories.containsKey(player.getName())) {
				if (plugin.widgets.containsKey(player.getName()) && plugin.useWidget == true) {
					plugin.widgets.get(player.getName()).setVisible(false).setDirty(true);
				}
				plugin.openedInventories.remove(player.getName());
			}
			Inventory inv = event.getInventory();
			if (inv.getName().equals(plugin.inventoryName) && inv.getSize() == BIT.allowedSize(player.getWorld(), player, true)) {
				plugin.inventories.put(player.getName(), inv.getContents());
			}
		} else {
			if (plugin.openedInventories.containsKey(plugin.openedInventoriesOthers.get(player.getName()))) {
				plugin.openedInventories.remove(plugin.openedInventoriesOthers.get(player.getName()));
			}
			Inventory inv = event.getInventory();
			if (inv.getName().equals(plugin.inventoryName)
					&& inv.getSize() == BIT.allowedSize(
							Bukkit.getServer().getPlayer(plugin.openedInventoriesOthers.get(player.getName())).getWorld(), Bukkit
									.getServer().getPlayer(plugin.openedInventoriesOthers.get(player.getName())), true)) {
				plugin.inventories.put(plugin.openedInventoriesOthers.get(player.getName()), inv.getContents());
				plugin.openedInventoriesOthers.remove(player.getName());
			}
		}
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = event.getPlayer();
		if (plugin.openedInventoriesOthers.containsKey(event.getPlayer().getName())) {
			player = Bukkit.getServer().getPlayer(plugin.openedInventoriesOthers.get(event.getPlayer()));
		}
		InventorySlotType clickedSlotType = event.getSlotType();
		Inventory inv = event.getInventory();
		String invName = inv.getName();
		ItemStack clickedItem = event.getItem();
		int slot = event.getSlot();
		try {
			if ((plugin.openedInventories.containsKey(player.getName()) || plugin.openedInventoriesOthers.containsKey(player.getName()))
					&& clickedSlotType != InventorySlotType.CONTAINER
					&& invName.equals("Inventory")
					&& (plugin.blackOrWhiteList == 1 && plugin.blacklist.contains(clickedItem.getTypeId()) || plugin.blackOrWhiteList == 2
							&& !plugin.whitelist.contains(clickedItem.getTypeId()))) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED + plugin.li.getMessage("yourenotallowedtomovethis") + plugin.inventoryName + "!");
				return;
			}
			if (clickedSlotType == InventorySlotType.CONTAINER && invName.equals(plugin.inventoryName) && clickedItem != null) {
				ItemStack is = inv.getItem(slot);
				is.setAmount(is.getAmount() - clickedItem.getAmount());
				plugin.updateInventory(player, inv.getContents());
			}
		} catch (NullPointerException e) {}
	}
}