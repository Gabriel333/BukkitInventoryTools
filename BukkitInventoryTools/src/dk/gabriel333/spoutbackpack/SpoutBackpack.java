package dk.gabriel333.spoutbackpack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.getspout.spout.inventory.CustomInventory;
import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Permissions;
import dk.gabriel333.register.payment.Method.MethodAccount;

@SuppressWarnings({})
public class SpoutBackpack implements CommandExecutor {

	public SpoutBackpack(BIT instance) {
	}

	public static Logger logger = Logger.getLogger("minecraft");

	public BIT plugin;

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (commandLabel.equalsIgnoreCase("backpack")
					|| commandLabel.equalsIgnoreCase("bp")) {
				if (BIT.canOpenBackpack(player.getWorld(), player) == true) {
					if (args.length == 0) {
						showHelp(player);
					} else if (args.length == 1) {
						String argument = args[0];
						if (argument.equalsIgnoreCase("reload")) {
							if (G333Permissions.hasPerm(player,
									"backpack.reload",
									G333Permissions.NOT_QUIET)) {
								//BIT.loadOrReloadConfiguration();
								player.sendMessage(BIT.li
										.getMessage("configreloaded1"));
								player.sendMessage(BIT.li
										.getMessage("configreloaded2"));
								player.sendMessage(BIT.li
										.getMessage("configreloaded3"));
								return true;
							}
						} else if (argument.equalsIgnoreCase("help")
								|| argument.equalsIgnoreCase("?")) {
							showHelp(player);
							return true;
						} else if (argument.equalsIgnoreCase("info")) {
							int size = BIT.allowedSize(player.getWorld(),
									player, true);
							if (size == 54) {
								player.sendMessage(BIT.li
										.getMessage("youvegotthebiggest")
										+ ChatColor.RED
										+ BIT.inventoryName
										+ ChatColor.WHITE
										+ BIT.li.getMessage("!"));
							} else {
								player.sendMessage(BIT.li.getMessage("your")
										+ ChatColor.RED + BIT.inventoryName
										+ ChatColor.WHITE
										+ BIT.li.getMessage("has")
										+ ChatColor.RED + size
										+ ChatColor.WHITE
										+ BIT.li.getMessage("slots"));
								if (size < upgradeAllowedSize(
										player.getWorld(), player)
										&& BIT.useEconomy) {
									double cost = calculateCost(size);
									player.sendMessage(BIT.li
											.getMessage("nextupgradecost")
											+ ChatColor.RED
											+ BIT.plugin.Method.format(cost)
											+ ChatColor.WHITE + ".");
								}
							}
							return true;
						} else if (argument.equalsIgnoreCase("upgrade")) {
							if (BIT.allowedSize(player.getWorld(), player,
									false) > upgradeAllowedSize(
									player.getWorld(), player)) {
								if (BIT.useEconomy) {
									startUpgradeProcedure(BIT.allowedSize(
											player.getWorld(), player, true),
											player, player);
								}
							} else if (BIT.allowedSize(player.getWorld(),
									player, true) == 54) {
								player.sendMessage(BIT.li
										.getMessage("youvegotthebiggest")
										+ ChatColor.RED
										+ BIT.inventoryName
										+ ChatColor.WHITE
										+ BIT.li.getMessage("!"));
							} else {
								player.sendMessage(BIT.li
										.getMessage("youvegotthebiggest")
										+ ChatColor.RED
										+ BIT.inventoryName
										+ ChatColor.WHITE
										+ BIT.li.getMessage("foryourpermissions"));
							}
							return true;

						} else if (argument.equalsIgnoreCase("clear")) {
							if (G333Permissions
									.hasPerm(player, "backpack.clear",
											G333Permissions.NOT_QUIET)) {
								if (BIT.inventories.containsKey(player
										.getName())) {
									BIT.inventories.remove(player.getName());
									player.sendMessage(BIT.li
											.getMessage("your")
											+ ChatColor.RED
											+ BIT.inventoryName
											+ ChatColor.WHITE
											+ BIT.li.getMessage("hasbeencleared"));
								} else {
									player.sendMessage(BIT.li
											.getMessage("youdonthavearegistred")
											+ ChatColor.RED
											+ BIT.inventoryName
											+ ChatColor.WHITE
											+ BIT.li.getMessage("!"));
								}
							}
							return true;

						} else {
							showHelp(player);
							return true;

						}
					} else if (args.length == 2) {
						String firstArgument = args[0];
						String playerName = args[1];
						if (firstArgument.equalsIgnoreCase("info")) {
							if (G333Permissions.hasPerm(player,
									"backpack.info.other",
									G333Permissions.NOT_QUIET)) {
								if (BIT.inventories.containsKey(playerName)) {
									Player playerCmd = Bukkit.getServer()
											.getPlayer(playerName);
									int size = BIT.allowedSize(
											playerCmd.getWorld(), playerCmd,
											true);
									if (size == 54) {
										player.sendMessage(BIT.li
												.getMessage("playerhasgotthebiggest")
												+ ChatColor.RED
												+ BIT.inventoryName
												+ ChatColor.WHITE
												+ BIT.li.getMessage("!"));
									} else {
										player.sendMessage(BIT.li
												.getMessage("players")
												+ ChatColor.RED
												+ BIT.inventoryName
												+ ChatColor.WHITE
												+ BIT.li.getMessage("hasbis")
												+ size
												+ BIT.li.getMessage("slots"));
										if (size < upgradeAllowedSize(
												player.getWorld(), player)
												&& BIT.useEconomy) {
											double cost = calculateCost(size);
											player.sendMessage(BIT.li
													.getMessage("nextupgradecost")
													+ ChatColor.RED
													+ BIT.plugin.Method
															.format(cost)
													+ ChatColor.WHITE + ".");
										}
									}
								} else {
									player.sendMessage(ChatColor.RED
											+ BIT.li.getMessage("playernotfound"));
								}
							}
							return true;

						} else if (firstArgument.equalsIgnoreCase("upgrade")) {
							if (playerName.equalsIgnoreCase("workbench")) {
								if (!BIT.hasWorkbench(player)
										&& G333Config.SBP_workbenchBuyable) {
									BIT.setWorkbench(player, true);
								} else
									player.sendMessage(ChatColor.RED
											+ BIT.li.getMessage("youalreadyhaveaccesstotheworkbench"));
							} else {
								if (G333Permissions.hasPerm(player,
										"backpack.upgrade.other",
										G333Permissions.NOT_QUIET)) {
									if (BIT.inventories.containsKey(playerName)) {
										Player playerCmd = Bukkit.getServer()
												.getPlayer(playerName);
										if (BIT.allowedSize(
												playerCmd.getWorld(),
												playerCmd, false) < upgradeAllowedSize(
												playerCmd.getWorld(), playerCmd)) {
											if (BIT.useEconomy) {
												startUpgradeProcedure(
														BIT.allowedSize(
																playerCmd
																		.getWorld(),
																playerCmd, true),
														playerCmd, player);
											}
										} else if (BIT.allowedSize(
												playerCmd.getWorld(),
												playerCmd, true) == 54) {
											player.sendMessage(BIT.li
													.getMessage("playerhasgotthebiggest")
													+ ChatColor.RED
													+ BIT.inventoryName
													+ ChatColor.WHITE
													+ BIT.li.getMessage("!"));
										} else {
											player.sendMessage(BIT.li
													.getMessage("playerhasgotthebiggest")
													+ ChatColor.RED
													+ BIT.inventoryName
													+ ChatColor.WHITE
													+ BIT.li.getMessage("forhispermissions"));
										}
									} else {
										player.sendMessage(ChatColor.RED
												+ BIT.li.getMessage("playernotfound"));
									}
								}
							}
							return true;

						} else if (firstArgument.equalsIgnoreCase("clear")) {
							if (G333Permissions.hasPerm(player,
									"backpack.clear.other",
									G333Permissions.NOT_QUIET)) {
								if (BIT.inventories.containsKey(playerName)) {
									BIT.inventories.remove(playerName);
									player.sendMessage(BIT.li
											.getMessage("frenchonly")
											+ playerName
											+ BIT.li.getMessage("'s")
											+ ChatColor.RED
											+ BIT.inventoryName
											+ ChatColor.WHITE
											+ BIT.li.getMessage("hasbeencleared"));
								} else {
									player.sendMessage(ChatColor.RED
											+ BIT.li.getMessage("playernotfound"));
								}
							}
							return true;

						} else if (firstArgument.equalsIgnoreCase("open")) {
							if (G333Permissions.hasPerm(player,
									"backpack.open.other",
									G333Permissions.NOT_QUIET)) {
								if (BIT.inventories.containsKey(playerName)) {
									if (!BIT.openedInventories
											.containsKey(playerName)) {
										CustomInventory inv = new CustomInventory(
												BIT.allowedSize(
														player.getWorld(),
														player, true),
												BIT.inventoryName);
										BIT.openedInventories.put(playerName,
												inv);
										BIT.openedInventoriesOthers.put(
												player.getName(), playerName);
										inv.setContents(BIT.inventories
												.get(playerName));
										((org.getspout.spoutapi.player.SpoutPlayer) player)
												.openInventoryWindow((Inventory) inv);
									} else {
										player.sendMessage(BIT.li
												.getMessage("playerhasalreadyhis")
												+ ChatColor.RED
												+ BIT.inventoryName
												+ ChatColor.WHITE
												+ BIT.li.getMessage("opened"));
									}
								} else {
									player.sendMessage(ChatColor.RED
											+ BIT.li.getMessage("playernotfound"));
								}
							}
						}
						return true;

					}
				} else {
					showHelp(player);
				}
			}
		}
		return false;
	}

	private void startUpgradeProcedure(int sizeBefore, Player player,
			Player notificationsAndMoneyPlayer) {
		int sizeAfter = sizeBefore + 9;
			double cost = calculateCost(sizeBefore);
			if (BIT.plugin.Method.hasAccount(notificationsAndMoneyPlayer
					.getName())) {
				MethodAccount account = BIT.plugin.Method
						.getAccount(notificationsAndMoneyPlayer.getName());
				if (BIT.plugin.Method.hasAccount(notificationsAndMoneyPlayer
						.getName())) {
					if (account.hasEnough(cost)) {
						account.subtract(cost);
						notificationsAndMoneyPlayer
								.sendMessage("Your account ("
										+ BIT.plugin.Method.getAccount(
												notificationsAndMoneyPlayer
														.getName()).balance()
										+ ") has been deducted " + cost
										+ " bucks");
						if (!player.getName().equals(
								notificationsAndMoneyPlayer.getName())) {
							player.sendMessage(player.getName()
									+ "'s account has been deducted " + cost
									+ " bucks");
						}
					} else {
						if (player.equals(notificationsAndMoneyPlayer)) {
							notificationsAndMoneyPlayer.sendMessage(BIT.li
									.getMessage("notenoughmoneyyour")
									+ ChatColor.RED
									+ BIT.inventoryName
									+ ChatColor.WHITE + ".");
						} else {
							notificationsAndMoneyPlayer.sendMessage(BIT.li
									.getMessage("notenoughmoneyplayer")
									+ ChatColor.RED
									+ BIT.inventoryName
									+ ChatColor.WHITE + ".");
						}
						return;
					}
				} else {
					notificationsAndMoneyPlayer.sendMessage(ChatColor.RED
							+ BIT.li.getMessage("noaccount"));
					return;
				}
			}
			SBInventorySaveTask.saveInventory(player, player.getWorld());
			BIT.inventories.remove(player.getName());
			File saveFile;
			if (G333Config.SBP_InventoriesShare) {
				saveFile = new File(BIT.plugin.getDataFolder() + File.separator
						+ "inventories", player.getName() + ".yml");
			} else {
				saveFile = new File(BIT.plugin.getDataFolder() + File.separator
						+ "inventories", player.getName() + "_"
						+ player.getWorld().getName() + ".yml");
			}
			YamlConfiguration config = new YamlConfiguration();
			try {
				config.load(saveFile);
			} catch (FileNotFoundException e) {
				//e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			config.set("Size", sizeAfter);
			try {
				config.save(saveFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			BIT.loadInventory(player, player.getWorld());
			notificationsAndMoneyPlayer.sendMessage(BIT.li.getMessage("your")
					+ ChatColor.RED + BIT.inventoryName + ChatColor.WHITE
					+ BIT.li.getMessage("hasbeenupgraded"));
			notificationsAndMoneyPlayer.sendMessage(BIT.li
					.getMessage("ithasnow")
					+ ChatColor.RED
					+ sizeAfter
					+ ChatColor.WHITE + BIT.li.getMessage("slots"));
	}

	public void showHelp(Player player) {
		if (G333Permissions.hasPerm(player, "backpack.reload",
				G333Permissions.QUIET)) {
			player.sendMessage(BIT.li.getMessage("reloadcommand"));
		}
		player.sendMessage(BIT.li.getMessage("infocommand") + ChatColor.RED
				+ BIT.inventoryName + ChatColor.WHITE + ".");
		if (BIT.allowedSize(player.getWorld(), player, true) < upgradeAllowedSize(
				player.getWorld(), player) && BIT.useEconomy) {
			player.sendMessage(BIT.li.getMessage("upgradecommand")
					+ ChatColor.RED + BIT.inventoryName + ChatColor.WHITE + ".");
		}
	}

	public double calculateCost(int size) {
		double cost = G333Config.SBP_price54;
		if (size == 9) {
			cost = G333Config.SBP_price18;
		} else if (size == 18) {
			cost = G333Config.SBP_price27;
		} else if (size == 27) {
			cost = G333Config.SBP_price36;
		} else if (size == 36) {
			cost = G333Config.SBP_price45;
		} else if (size == 45) {
			cost = G333Config.SBP_price54;
		}
		return cost;
	}

	private int upgradeAllowedSize(World world, Player player) {
		int size = 9;
		if (G333Permissions.hasPerm(player, "backpack.upgrade54",
				G333Permissions.QUIET)) {
			size = 54;
		} else if (G333Permissions.hasPerm(player, "backpack.upgrade45",
				G333Permissions.QUIET)) {
			size = 45;
		} else if (G333Permissions.hasPerm(player, "backpack.upgrade36",
				G333Permissions.QUIET)) {
			size = 36;
		} else if (G333Permissions.hasPerm(player, "backpack.upgrade27",
				G333Permissions.QUIET)) {
			size = 27;
		} else if (G333Permissions.hasPerm(player, "backpack.upgrade18",
				G333Permissions.QUIET)) {
			size = 18;
		}
		return size;
	}
}