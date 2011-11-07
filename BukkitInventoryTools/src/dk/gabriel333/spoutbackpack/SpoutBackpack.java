package dk.gabriel333.spoutbackpack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.getspout.spoutapi.SpoutManager;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
//import org.getspout.spout.inventory.CustomInventory;
import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;
import dk.gabriel333.Library.G333Permissions;
import dk.gabriel333.register.payment.Method.MethodAccount;
import dk.gabriel333.spoutbackpack.SBLanguageInterface.Language;

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
			if (G333Permissions.hasPerm(sender, "backpack.use",
					G333Permissions.NOT_QUIET)) {
				// if (commandLabel.equalsIgnoreCase("backpack")
				// || commandLabel.equalsIgnoreCase("bp")) {
				if (canOpenBackpack(player.getWorld(), player)) {
					if (args.length == 0) {
						// showHelp(player);
						// open inventory
						if (allowedSize(player.getWorld(), player, true) > 0) {
							String playerName = sender.getName();
							if (BIT.inventories.containsKey(playerName)) {
								if (!BIT.openedInventories
										.containsKey(playerName)) {
									Inventory inv = SpoutManager
											.getInventoryBuilder().construct(
													SpoutBackpack.allowedSize(
															player.getWorld(),
															player, true),
													BIT.inventoryName);
									BIT.openedInventories.put(playerName, inv);
									// BIT.openedInventoriesOthers.put(
									// player.getName(), playerName);
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
						} else {
							player.sendMessage(
									"You need to buy a backpack. Use /backpack buy");
							return true;
						}
					} else if (args.length == 1) {
						String argument = args[0];
						if (argument.equalsIgnoreCase("reload")) {
							if (G333Permissions.hasPerm(player,
									"backpack.reload",
									G333Permissions.NOT_QUIET)) {
								// BIT.loadOrReloadConfiguration();
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
							int size = allowedSize(player.getWorld(), player,
									true);
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
						} else if (argument.equalsIgnoreCase("upgrade")
								|| argument.equalsIgnoreCase("buy")) {
							if (allowedSize(player.getWorld(), player, false) > SizeInConfig(
									player.getWorld(), player)
									&& upgradeAllowedSize(player.getWorld(),
											player) > SizeInConfig(
											player.getWorld(), player)) {
								if (BIT.useEconomy) {
									startUpgradeProcedure(
											allowedSize(player.getWorld(),
													player, true), player,
											player);
								}
							} else if (allowedSize(player.getWorld(), player,
									true) == 54) {
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
									int size = allowedSize(
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
								if (!hasWorkbench(player)
										&& G333Config.SBP_workbenchBuyable) {
									setWorkbench(player, true);
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
										if (allowedSize(playerCmd.getWorld(),
												playerCmd, false) < upgradeAllowedSize(
												playerCmd.getWorld(), playerCmd)) {
											if (BIT.useEconomy) {
												startUpgradeProcedure(
														allowedSize(playerCmd
																.getWorld(),
																playerCmd, true),
														playerCmd, player);
											}
										} else if (allowedSize(
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
										Inventory inv = SpoutManager
												.getInventoryBuilder()
												.construct(
														SpoutBackpack
																.allowedSize(
																		player.getWorld(),
																		player,
																		true),
														BIT.inventoryName);
										// CustomInventory inv = new
										// CustomInventory(
										// allowedSize(player.getWorld(),
										// player, true),
										// BIT.inventoryName);
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
				// }
			}
		} else {
			// the user has not permission to use the SputBackpack.
			return true;
		}
		return false;
	}

	private void startUpgradeProcedure(int sizeBefore, Player player,
			Player notificationsAndMoneyPlayer) {
		int sizeAfter = sizeBefore + 9;
		double cost = calculateCost(sizeBefore);
		if (BIT.plugin.Method.hasAccount(notificationsAndMoneyPlayer.getName())) {
			MethodAccount account = BIT.plugin.Method
					.getAccount(notificationsAndMoneyPlayer.getName());
			if (BIT.plugin.Method.hasAccount(notificationsAndMoneyPlayer
					.getName())) {
				if (account.hasEnough(cost)) {
					account.subtract(cost);
					notificationsAndMoneyPlayer.sendMessage("Your account ("
							+ BIT.plugin.Method.getAccount(
									notificationsAndMoneyPlayer.getName())
									.balance() + ") has been deducted " + cost
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
			// e.printStackTrace();
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
		loadInventory(player, player.getWorld());
		notificationsAndMoneyPlayer.sendMessage(BIT.li.getMessage("your")
				+ ChatColor.RED + BIT.inventoryName + ChatColor.WHITE
				+ BIT.li.getMessage("hasbeenupgraded"));
		notificationsAndMoneyPlayer.sendMessage(BIT.li.getMessage("ithasnow")
				+ ChatColor.RED + sizeAfter + ChatColor.WHITE
				+ BIT.li.getMessage("slots"));
	}

	public void showHelp(Player player) {
		if (G333Permissions.hasPerm(player, "backpack.reload",
				G333Permissions.QUIET)) {
			player.sendMessage(BIT.li.getMessage("reloadcommand"));
		}
		player.sendMessage(BIT.li.getMessage("infocommand") + ChatColor.RED
				+ BIT.inventoryName + ChatColor.WHITE + ".");
		if (allowedSize(player.getWorld(), player, true) < upgradeAllowedSize(
				player.getWorld(), player) && BIT.useEconomy) {
			player.sendMessage(BIT.li.getMessage("upgradecommand")
					+ ChatColor.RED + BIT.inventoryName + ChatColor.WHITE + ".");
		}
	}

	public double calculateCost(int size) {
		double cost = G333Config.SBP_price9;
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

	public static int SizeInConfig(World world, Player player) {
		File saveFile;
		if (G333Config.SBP_InventoriesShare) {
			saveFile = new File(BIT.plugin.getDataFolder() + File.separator
					+ "inventories", player.getName() + ".yml");
		} else {
			saveFile = new File(BIT.plugin.getDataFolder() + File.separator
					+ "inventories", player.getName() + "_" + world.getName()
					+ ".yml");
		}
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(saveFile);
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		return config.getInt("Size", 0);
	}

	public static int allowedSize(World world, Player player,
			boolean configurationCheck) {
		int size = 9;
		if (G333Permissions.hasPerm(player, "backpack.size54",
				G333Permissions.QUIET)) {
			size = 54;
		} else if (G333Permissions.hasPerm(player, "backpack.size45",
				G333Permissions.QUIET)) {
			size = 45;
		} else if (G333Permissions.hasPerm(player, "backpack.size36",
				G333Permissions.QUIET)) {
			size = 36;
		} else if (G333Permissions.hasPerm(player, "backpack.size27",
				G333Permissions.QUIET)) {
			size = 27;
		} else if (G333Permissions.hasPerm(player, "backpack.size18",
				G333Permissions.QUIET)) {
			size = 18;
		} else if (G333Permissions.hasPerm(player, "backpack.size9",
				G333Permissions.QUIET)) {
			size = 9;
		}
		if (configurationCheck) {
			if (SizeInConfig(world, player) < size) {
				size = SizeInConfig(world, player);
			}
		}
		return size;
	}

	public static Inventory getClosedBackpack(Player player) {
		Inventory inventory = SpoutManager.getInventoryBuilder().construct(
				SpoutBackpack.allowedSize(player.getWorld(), player, true),
				BIT.inventoryName);
		if (BIT.inventories.containsKey(player.getName())) {
			inventory.setContents(BIT.inventories.get(player.getName()));
		}
		return inventory;
	}

	public static void setClosedBackpack(Player player, Inventory inventory) {
		BIT.inventories.put(player.getName(), inventory.getContents());
		return;
	}

	public static boolean isOpenBackpack(Player player) {
		return BIT.openedInventories.containsKey(player.getName());
	}

	public static Inventory getOpenedBackpack(Player player) {
		return BIT.openedInventories.get(player.getName());
	}

	public static void updateInventory(Player player, ItemStack[] is) {
		BIT.inventories.put(player.getName(), is);
	}

	public static boolean canOpenBackpack(World world, Player player) {
		boolean canOpenBackpack = false;

		if (G333Permissions.hasPerm(player, "backpack.size54",
				G333Permissions.QUIET)
				|| G333Permissions.hasPerm(player, "backpack.size45",
						G333Permissions.QUIET)
				|| G333Permissions.hasPerm(player, "backpack.size36",
						G333Permissions.QUIET)
				|| G333Permissions.hasPerm(player, "backpack.size27",
						G333Permissions.QUIET)
				|| G333Permissions.hasPerm(player, "backpack.size18",
						G333Permissions.QUIET)
				|| G333Permissions.hasPerm(player, "backpack.size9",
						G333Permissions.QUIET)) {

			canOpenBackpack = true;
		} else {
			canOpenBackpack = false;
		}

		if (BIT.getWorldGuard() != null) {
			Location location = player.getLocation();
			com.sk89q.worldedit.Vector vector = new com.sk89q.worldedit.Vector(
					location.getX(), location.getY(), location.getZ());
			Map<String, ProtectedRegion> regions = BIT.getWorldGuard()
					.getGlobalRegionManager().get(location.getWorld())
					.getRegions();
			List<String> inRegions = new ArrayList<String>();
			for (String key_ : regions.keySet()) {
				ProtectedRegion region = regions.get(key_);
				if (region.contains(vector)) {
					inRegions.add(key_);
				}
			}
			for (String region : G333Config.SBP_noBackpackRegions) {
				if (inRegions.contains(region)) {
					canOpenBackpack = false;
				}
			}
		}
		if (BIT.mobArenaHandler != null) {
			if (BIT.mobArenaHandler.inRegion(player.getLocation())) {
				canOpenBackpack = false;
			}
		}
		if (BIT.jail != null) {
			if (BIT.jail.isPlayerJailed(player.getName()) == true) {
				canOpenBackpack = false;
			}
		}
		return canOpenBackpack;
	}

	public static boolean hasWorkbench(Player player) {
		if (G333Permissions.hasPerm(player, "backpack.workbench",
				G333Permissions.NOT_QUIET))
			return true;
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
			// G333Messages
			// .showInfo("The workbench file did not exist for player:"
			// + player.getName());
			// e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		boolean enabled = config.getBoolean("Workbench", false);
		try {
			config.save(saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (enabled)
			return true;
		return false;
	}

	public static void setWorkbench(Player player, boolean enabled) {
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
			// e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		config.set("Workbench", enabled);
		try {
			config.save(saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void loadInventory(Player player, World world) {
		if (BIT.inventories.containsKey(player.getName())) {
			return;
		}
		File saveFile;
		if (G333Config.SBP_InventoriesShare) {
			saveFile = new File(BIT.plugin.getDataFolder() + File.separator
					+ "inventories", player.getName() + ".yml");
		} else {
			saveFile = new File(BIT.plugin.getDataFolder() + File.separator
					+ "inventories", player.getName() + "_" + world.getName()
					+ ".yml");
		}
		@SuppressWarnings({})
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(saveFile);
		} catch (FileNotFoundException e) {
			// G333Messages
			// .showWarning("The Inventoryfile was not found for user:"
			// + player.getName());
			// e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		int size = SpoutBackpack.allowedSize(world, player, true);
		Inventory inv = SpoutManager.getInventoryBuilder().construct(size,
				BIT.inventoryName);
		if (saveFile.exists()) {
			Integer i = 0;
			for (i = 0; i < size; i++) {
				ItemStack item = new ItemStack(0, 0);
				item.setAmount(config.getInt(i.toString() + ".amount", 0));
				item.setTypeId(config.getInt(i.toString() + ".type", 0));
				Integer durability = config.getInt(
						i.toString() + ".durability", 0);
				item.setDurability(Short.parseShort(durability.toString()));
				inv.setItem(i, item);
			}
		}
		BIT.inventories.put(player.getName(), inv.getContents());
	}

	public static Language loadLanguage() {
		if (G333Config.SBP_language.equalsIgnoreCase("EN")) {
			return Language.ENGLISH;
		} else if (G333Config.SBP_language.equalsIgnoreCase("FR")) {
			return Language.FRENCH;
		} else {
			G333Messages
					.showInfo("SpoutBackpack: language set to ENGLISH by default.");
			return Language.ENGLISH;
		}
	}

}