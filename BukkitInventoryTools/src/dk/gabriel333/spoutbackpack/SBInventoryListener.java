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
import dk.gabriel333.Library.G333Config;

public class SBInventoryListener extends InventoryListener {
	//private BIT plugin;

	//public SBInventoryListener(BIT plugin) {
	//	this.plugin = plugin;
	//}

	@Override
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = event.getPlayer();
		if (!BIT.openedInventoriesOthers.containsKey(player.getName())) {
			if (BIT.openedInventories.containsKey(player.getName())) {
				if (BIT.widgets.containsKey(player.getName())
						&& G333Config.SBP_useWidget == true) {
					BIT.widgets.get(player.getName()).setVisible(false)
							.setDirty(true);
				}
				BIT.openedInventories.remove(player.getName());
			}
			Inventory inv = event.getInventory();
			if (inv.getName().equals(BIT.inventoryName)
					&& inv.getSize() == SpoutBackpack.allowedSize(player.getWorld(),
							player, true)) {
				BIT.inventories.put(player.getName(), inv.getContents());
			}
		} else {
			if (BIT.openedInventories
					.containsKey(BIT.openedInventoriesOthers.get(player
							.getName()))) {
				BIT.openedInventories.remove(BIT.openedInventoriesOthers
						.get(player.getName()));
			}
			Inventory inv = event.getInventory();
			if (inv.getName().equals(BIT.inventoryName)
					&& inv.getSize() == SpoutBackpack.allowedSize(
							Bukkit.getServer()
									.getPlayer(
											BIT.openedInventoriesOthers
													.get(player.getName()))
									.getWorld(),
							Bukkit.getServer().getPlayer(
									BIT.openedInventoriesOthers.get(player
											.getName())), true)) {
				BIT.inventories.put(
						BIT.openedInventoriesOthers.get(player.getName()),
						inv.getContents());
				BIT.openedInventoriesOthers.remove(player.getName());
			}
		}
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = event.getPlayer();
		if (BIT.openedInventoriesOthers.containsKey(event.getPlayer()
				.getName())) {
			player = Bukkit.getServer().getPlayer(
					BIT.openedInventoriesOthers.get(event.getPlayer()));
		}
		InventorySlotType clickedSlotType = event.getSlotType();
		Inventory inv = event.getInventory();
		String invName = inv.getName();
		ItemStack clickedItem = event.getItem();
		int slot = event.getSlot();
		try {
			if ((BIT.openedInventories.containsKey(player.getName()) || BIT.openedInventoriesOthers
					.containsKey(player.getName()))
					&& clickedSlotType != InventorySlotType.CONTAINER
					&& invName.equals("Inventory")
					&& (G333Config.SBP_blackOrWhiteList == 1
							&& G333Config.blacklist.contains(String
									.valueOf(clickedItem.getTypeId())) || G333Config.SBP_blackOrWhiteList == 2
							&& !G333Config.whitelist.contains(String
									.valueOf(clickedItem.getTypeId())))) {
				event.setCancelled(true);
				player.sendMessage(ChatColor.RED
						+ BIT.li.getMessage("yourenotallowedtomovethis")
						+ BIT.inventoryName + "!");
				return;
			}
			if (clickedSlotType == InventorySlotType.CONTAINER
					&& invName.equals(BIT.inventoryName)
					&& clickedItem != null) {
				ItemStack is = inv.getItem(slot);
				is.setAmount(is.getAmount() - clickedItem.getAmount());
				SpoutBackpack.updateInventory(player, inv.getContents());
			}
		} catch (NullPointerException e) {
		}
	}
}