package dk.gabriel333.spoutbackpack;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet100OpenWindow;
import net.minecraft.server.Packet101CloseWindow;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.inventory.Inventory;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Permissions;

public class SBInputListener extends InputListener {
	// private BIT plugin;

	// public static Logger logger = Logger.getLogger("minecraft");

	// public SBInputListener(Plugin plugin) {
	// this.plugin = plugin;
	// }

	@Override
	public void onKeyPressedEvent(KeyPressedEvent event) {
		String keypressed = event.getKey().name();
		ScreenType screentype = event.getScreenType();
		if (!(screentype == ScreenType.GAME_SCREEN
				|| screentype == ScreenType.PLAYER_INVENTORY
				|| screentype == ScreenType.DISPENSER_INVENTORY
				|| screentype == ScreenType.FURNACE_INVENTORY
				|| screentype == ScreenType.WORKBENCH_INVENTORY || screentype == ScreenType.CHEST_INVENTORY)) {
			return;
		}
		if (keypressed.equalsIgnoreCase(G333Config.LIBRARY_BACKPACK)) {
			if (SpoutBackpack.SizeInConfig(event.getPlayer().getWorld(),
					event.getPlayer()) > 0) {
				if (G333Permissions.hasPerm(event.getPlayer(), "backpack.use",
						G333Permissions.NOT_QUIET)) {
					if (!SpoutBackpack.canOpenBackpack(event.getPlayer()
							.getWorld(), event.getPlayer())) {
						return;
					}
					if (screentype == ScreenType.CHEST_INVENTORY) {
						CraftPlayer player = (CraftPlayer) event.getPlayer();
						if (!BIT.openedInventoriesOthers.containsKey(player
								.getName())) {
							if (BIT.openedInventories.containsKey(player
									.getName())) {
								if (BIT.widgets.containsKey(player.getName())
										&& G333Config.SBP_useWidget == true) {
									BIT.widgets.get(player.getName())
											.setVisible(false).setDirty(true);
								}
								Inventory inv = BIT.openedInventories
										.get(player.getName());
								BIT.inventories.put(player.getName(),
										inv.getContents());
								event.getPlayer().closeActiveWindow();
							}
						} else {
							if (BIT.openedInventories
									.containsKey(BIT.openedInventoriesOthers
											.get(player.getName()))) {
								Inventory inv = BIT.openedInventories
										.get(BIT.openedInventoriesOthers
												.get(player.getName()));
								BIT.inventories.put(BIT.openedInventoriesOthers
										.get(player.getName()), inv
										.getContents());
								BIT.openedInventoriesOthers.remove(player
										.getName());
								event.getPlayer().closeActiveWindow();
							}
						}
					}
					if (!BIT.openedInventoriesOthers.containsValue(event
							.getPlayer().getName())) {
						if (screentype != null
								&& (screentype == ScreenType.GAME_SCREEN
										|| screentype == ScreenType.PLAYER_INVENTORY
										|| screentype == ScreenType.DISPENSER_INVENTORY
										|| screentype == ScreenType.FURNACE_INVENTORY || screentype == ScreenType.WORKBENCH_INVENTORY)) {
							SpoutPlayer player = event.getPlayer();
							if (BIT.plugin.Method != null
									&& G333Config.SBP_useWidget == true) {
								if (BIT.widgets.containsKey(player.getName())) {
									BIT.widgets.get(player.getName())
											.setVisible(true).setDirty(true);
								} else {
									GenericLabel widget = new GenericLabel("");
									if (!BIT.plugin.Method.hasAccount(player
											.getName())) {
										return;
									}
									widget.setText(
											BIT.li.getMessage("money")
													+ String.format(BIT.plugin.Method
															.format(BIT.plugin.Method
																	.getAccount(
																			player.getName())
																	.balance())))
											.setTextColor(
													new Color(1.0F, 1.0F, 1.0F,
															1.0F))
											.setX(G333Config.SBP_widgetX)
											.setY(G333Config.SBP_widgetY);
									player.getMainScreen().attachWidget(
											BIT.plugin, widget);
									BIT.widgets.put(player.getName(), widget);
								}
							}
							Inventory inv = SpoutManager.getInventoryBuilder()
									.construct(
											SpoutBackpack.allowedSize(
													player.getWorld(), player,
													true), BIT.inventoryName);
							BIT.openedInventories.put(player.getName(), inv);
							if (BIT.inventories.containsKey(player.getName())) {
								inv.setContents(BIT.inventories.get(player
										.getName()));
							}
							player.openInventoryWindow((Inventory) inv);
						}
					} else {
						event.getPlayer().sendMessage(
								BIT.li.getMessage("someoneisusingyour")
										+ ChatColor.RED + BIT.inventoryName
										+ ChatColor.WHITE
										+ BIT.li.getMessage("!"));
					}
				}
			} else {
				event.getPlayer().sendMessage(
						"You need to buy a backpack. Use /backpack buy");
			}
		} else if (keypressed.equalsIgnoreCase(G333Config.LIBRARY_WORKBENCH)) {
			if (G333Config.SBP_workbenchEnabled) {
				if (G333Permissions.hasPerm(event.getPlayer(),
						"backpack.workbench", G333Permissions.NOT_QUIET)) {
					if (!BIT.openedInventoriesOthers.containsKey(event
							.getPlayer().getName())) {
						if (!BIT.openedInventories.containsKey(event
								.getPlayer().getName())) {
							if (!SpoutBackpack.hasWorkbench(event.getPlayer())) {
								return;
							}
							if (G333Config.SBP_workbenchInventory == true
									&& !event.getPlayer().getInventory()
											.contains(Material.WORKBENCH)) {
								return;
							}
							final int windowNumber = 1;
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
			} else {
				if (G333Config.DEBUG_PERMISSIONS) {
					event.getPlayer().sendMessage(
							"SBP_workbenchEnabled is false in config.yml");
				}
			}
		} else if (keypressed.equalsIgnoreCase(G333Config.LIBRARY_SORTKEY)) {

		}
	}

}