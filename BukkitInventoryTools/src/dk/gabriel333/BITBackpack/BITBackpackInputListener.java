package dk.gabriel333.BITBackpack;

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
import dk.gabriel333.Library.BITConfig;
import dk.gabriel333.Library.BITPermissions;

public class BITBackpackInputListener extends InputListener {

	// private BIT plugin;

	// public BITBackpackInputListener(Plugin plugin) {
	// this.plugin = plugin;
	// }

	@Override
	public void onKeyPressedEvent(KeyPressedEvent event) {
		String keypressed = event.getKey().name();
		ScreenType screentype = event.getScreenType();
		SpoutPlayer sPlayer = event.getPlayer();
		if (!(screentype == ScreenType.GAME_SCREEN
				|| screentype == ScreenType.PLAYER_INVENTORY
				|| screentype == ScreenType.DISPENSER_INVENTORY
				|| screentype == ScreenType.FURNACE_INVENTORY
				|| screentype == ScreenType.WORKBENCH_INVENTORY || screentype == ScreenType.CHEST_INVENTORY)) {
			return;
		}
		if (keypressed.equalsIgnoreCase(BITConfig.LIBRARY_BACKPACK)) {
			if (BITPermissions.hasPerm(event.getPlayer(), "backpack.use",
					BITPermissions.NOT_QUIET)) {
				if (BITBackpack.sizeInConfig(event.getPlayer().getWorld(),
						event.getPlayer()) > 0) {
					if (!BITBackpack.canOpenBackpack(event.getPlayer()
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
										&& BITConfig.SBP_useWidget) {
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
						if (screentype == ScreenType.GAME_SCREEN
								|| screentype == ScreenType.PLAYER_INVENTORY
								|| screentype == ScreenType.DISPENSER_INVENTORY
								|| screentype == ScreenType.FURNACE_INVENTORY
								|| screentype == ScreenType.WORKBENCH_INVENTORY) {
							SpoutPlayer player = event.getPlayer();
							if (BIT.plugin.Method != null
									&& BITConfig.SBP_useWidget) {
								if (BIT.widgets.containsKey(player.getName())) {
									BIT.widgets.get(player.getName())
											.setVisible(true).setDirty(true);
								} else {
									if (!BIT.plugin.Method.hasAccount(player
											.getName())) {
										return;
									}
									GenericLabel widget = new GenericLabel("");
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
											.setX(BITConfig.SBP_widgetX)
											.setY(BITConfig.SBP_widgetY);
									player.getMainScreen().attachWidget(
											BIT.plugin, widget);
									BIT.widgets.put(player.getName(), widget);
								}
							}
							BITBackpack
									.loadInventory(player, player.getWorld());
							Inventory inv = SpoutManager.getInventoryBuilder()
									.construct(
											BITBackpack.allowedSize(
													player.getWorld(), player,
													true), BIT.inventoryName);
							if (BIT.inventories.containsKey(player.getName())) {
								inv.setContents(BIT.inventories.get(player
										.getName()));
								BIT.openedInventories
										.put(player.getName(), inv);
							}
							player.openInventoryWindow(inv);
						}
					} else {
						event.getPlayer().sendMessage(
								BIT.li.getMessage("someoneisusingyour")
										+ ChatColor.RED + BIT.inventoryName
										+ ChatColor.WHITE
										+ BIT.li.getMessage("!"));
					}
				} else {
					event.getPlayer().sendMessage(
							"You need to buy a backpack. Use /backpack buy");
				}
			}
		} else if (keypressed.equalsIgnoreCase(BITConfig.LIBRARY_WORKBENCH)) {
			if (BITPermissions.hasPerm(event.getPlayer(), "backpack.workbench",
					BITPermissions.NOT_QUIET)) {
				if (BITConfig.SBP_workbenchEnabled) {
					if (!BIT.openedInventoriesOthers.containsKey(event
							.getPlayer().getName())) {
						// if (!BIT.openedInventories.containsKey(event
						// .getPlayer().getName())) {
						if (!BITBackpack.hasWorkbench(event.getPlayer())) {
							return;
						}
						if (BITConfig.SBP_workbenchInventory
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
						} else if (screentype == ScreenType.GAME_SCREEN
								|| screentype == ScreenType.PLAYER_INVENTORY
								|| screentype == ScreenType.DISPENSER_INVENTORY
								|| screentype == ScreenType.FURNACE_INVENTORY
								|| screentype == ScreenType.CHEST_INVENTORY) {
							SpoutPlayer player = event.getPlayer();
							if (BIT.openedInventories.containsKey(player
									.getName())) {
								BIT.openedInventories.remove(player.getName());
								player.closeActiveWindow();
							}
							final EntityPlayer entityPlayer = ((CraftPlayer) player)
									.getHandle();
							entityPlayer.netServerHandler
									.sendPacket(new Packet100OpenWindow(
											windowNumber, 1, "Crafting", 9));
							entityPlayer.activeContainer = new BITWorkbench(
									entityPlayer, windowNumber);
						}
						// }

					}
				} else {
					if (BITConfig.DEBUG_PERMISSIONS) {
						event.getPlayer().sendMessage(
								"SBP_workbenchEnabled is false in config.yml");
					}
				}
			}
		} else if (keypressed.equalsIgnoreCase(BITConfig.LIBRARY_SORTKEY)) {

			// The player pressed KEY_E
		} else if (keypressed.equalsIgnoreCase(sPlayer.getInventoryKey()
				.toString())) {
			if (screentype == ScreenType.GAME_SCREEN
					|| screentype == ScreenType.PLAYER_INVENTORY
					|| screentype == ScreenType.DISPENSER_INVENTORY
					|| screentype == ScreenType.FURNACE_INVENTORY
					|| screentype == ScreenType.CHEST_INVENTORY) {
				if (BIT.openedInventories.containsKey(sPlayer.getName())) {
					BIT.openedInventories.remove(sPlayer.getName());
				}
				if (screentype != ScreenType.GAME_SCREEN)
					sPlayer.closeActiveWindow();
				sPlayer.openScreen(ScreenType.PLAYER_INVENTORY);
				// sPlayer.getInventory().
				// sPlayer.openInventoryWindow(null);
			}
		}
	}

}