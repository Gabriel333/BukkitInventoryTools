package dk.gabriel333.spoutbackpack;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet100OpenWindow;
import net.minecraft.server.Packet101CloseWindow;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.inventory.Inventory;
import org.getspout.spout.inventory.CustomInventory;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.G333Config;

public class SBInputListener extends InputListener {
	private BIT plugin;

	// public static Logger logger = Logger.getLogger("minecraft");

	// public SBInputListener(Plugin plugin) {
	// this.plugin = plugin;
	// }

	@Override
	public void onKeyPressedEvent(KeyPressedEvent event) {
		if (event.getKey().equals(G333Config.LIBRARY_BACKPACK)) {
			ScreenType screentype = event.getScreenType();
			if (screentype == ScreenType.WORKBENCH_INVENTORY) {
				return;
			}
			if (plugin.canOpenBackpack(event.getPlayer().getWorld(),
					event.getPlayer()) == false) {
				return;
			}
			if (screentype == ScreenType.CHEST_INVENTORY) {
				CraftPlayer player = (CraftPlayer) event.getPlayer();
				if (!plugin.openedInventoriesOthers.containsKey(player
						.getName())) {
					if (plugin.openedInventories.containsKey(player.getName())) {
						if (plugin.widgets.containsKey(player.getName())
								&& plugin.useWidget == true) {
							plugin.widgets.get(player.getName())
									.setVisible(false).setDirty(true);
						}
						Inventory inv = plugin.openedInventories.get(player
								.getName());
						plugin.inventories.put(player.getName(),
								inv.getContents());
						event.getPlayer().closeActiveWindow();
					}
				} else {
					if (plugin.openedInventories
							.containsKey(plugin.openedInventoriesOthers
									.get(player.getName()))) {
						Inventory inv = plugin.openedInventories
								.get(plugin.openedInventoriesOthers.get(player
										.getName()));
						plugin.inventories.put(plugin.openedInventoriesOthers
								.get(player.getName()), inv.getContents());
						plugin.openedInventoriesOthers.remove(player.getName());
						event.getPlayer().closeActiveWindow();
					}
				}
			}
			if (!plugin.openedInventoriesOthers.containsValue(event.getPlayer()
					.getName())) {
				if (screentype != null
						&& (screentype == ScreenType.GAME_SCREEN
								|| screentype == ScreenType.PLAYER_INVENTORY
								|| screentype == ScreenType.DISPENSER_INVENTORY
								|| screentype == ScreenType.FURNACE_INVENTORY || screentype == ScreenType.WORKBENCH_INVENTORY)) {
					SpoutPlayer player = event.getPlayer();
					if (BIT.plugin.Method != null && plugin.useWidget == true) {
						if (plugin.widgets.containsKey(player.getName())) {
							plugin.widgets.get(player.getName())
									.setVisible(true).setDirty(true);
						} else {
							GenericLabel widget = new GenericLabel("");
							if (!BIT.plugin.Method.hasAccount(player.getName())) {
								return;
							}
							widget.setText(
									plugin.li.getMessage("money")
											+ String.format(BIT.plugin.Method.format(BIT.plugin.Method
													.getAccount(
															player.getName())
													.balance())))
									.setTextColor(
											new Color(1.0F, 1.0F, 1.0F, 1.0F))
									.setX(plugin.widgetX).setY(plugin.widgetY);
							player.getMainScreen().attachWidget(BIT.plugin,
									widget);
							plugin.widgets.put(player.getName(), widget);
						}
					}
					CustomInventory inv = new CustomInventory(BIT.allowedSize(
							player.getWorld(), player, true),
							plugin.inventoryName);
					plugin.openedInventories.put(player.getName(), inv);
					if (plugin.inventories.containsKey(player.getName())) {
						inv.setContents(plugin.inventories.get(player.getName()));
					}
					player.openInventoryWindow((Inventory) inv);
				}
			} else {
				event.getPlayer().sendMessage(
						plugin.li.getMessage("someoneisusingyour")
								+ ChatColor.RED + plugin.inventoryName
								+ ChatColor.WHITE + plugin.li.getMessage("!"));
			}
		} else if (event.getKey().equals(G333Config.LIBRARY_WORKBENCH)
				&& plugin.workbenchEnabled == true) {
			if (!plugin.openedInventoriesOthers.containsKey(event.getPlayer()
					.getName())) {
				if (!plugin.openedInventories.containsKey(event.getPlayer()
						.getName())) {
					if (!plugin.hasWorkbench(event.getPlayer())) {
						return;
					}
					if (plugin.workbenchInventory == true
							&& !event.getPlayer().getInventory()
									.contains(Material.WORKBENCH)) {
						return;
					}
					final int windowNumber = 1;
					ScreenType screentype = event.getScreenType();
					if (screentype == ScreenType.WORKBENCH_INVENTORY) {
						SpoutPlayer player = event.getPlayer();
						final EntityPlayer entityPlayer = ((CraftPlayer) player)
								.getHandle();
						entityPlayer.netServerHandler
								.sendPacket(new Packet101CloseWindow(
										windowNumber));
					} else if (screentype != null
							&& (screentype == ScreenType.GAME_SCREEN
									|| screentype == ScreenType.PLAYER_INVENTORY
									|| screentype == ScreenType.DISPENSER_INVENTORY
									|| screentype == ScreenType.FURNACE_INVENTORY || screentype == ScreenType.CHEST_INVENTORY)) {
						SpoutPlayer player = event.getPlayer();
						final EntityPlayer entityPlayer = ((CraftPlayer) player)
								.getHandle();
						entityPlayer.netServerHandler
								.sendPacket(new Packet100OpenWindow(
										windowNumber, 1, "Crafting", 9));
						entityPlayer.activeContainer = new SBWorkbench(
								entityPlayer, windowNumber);
					}
				}
			}
		}
	}

}