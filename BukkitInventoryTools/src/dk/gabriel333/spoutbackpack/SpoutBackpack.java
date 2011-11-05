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
import register.Method.MethodAccount;
import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Permissions;

@SuppressWarnings({ })
public class SpoutBackpack implements CommandExecutor {
	
	public SpoutBackpack(BIT instance) {
	}

	public static Logger logger = Logger.getLogger("minecraft");
	//public PermissionHandler permissionHandler;
	//public Plugin permissionsBukkitPlugin;
	//public PermissionManager permissionsEx;
	//public Method method = null;
	public BIT plugin;

	// CLJpublic static JailAPI jail;

//*	public void onEnableXXX() {
		
/*		if (plugin.config.getBoolean("Permissions.UsePermissions?", true) == false) {
			if (plugin.config.getBoolean("Permissions.UsePermissionsBukkit?", false) == false) {
				if (plugin.config.getBoolean("Permissions.UsePermissionsEx?", false) == false) {
					if (plugin.config
							.getBoolean("Permissions.UseGroupManager?", false) == false) {
						setupOPSystem();
					} else {
						setupGroupManager();
					}
				} else {
					setupPermissionsEx();
				}
			} else {
				setupPermissionsBukkit();
			}
		} else {
			setupPermissions();
		}*/
		//BIT.setupMobArena();
		// CLJsetupJail();
		// PluginManager pm = Bukkit.getServer().getPluginManager();
		// CKJpm.registerEvent(Type.PLUGIN_ENABLE, new SBRegister(this),
		// Priority.Monitor, this);
		// CLJpm.registerEvent(Type.PLUGIN_DISABLE, new SBRegister(this),
		// Priority.Monitor, this);
		// long delay = 20L * 60 * saveTime;
		// saveTaskId =
		// Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new
		// SBInventorySaveTask(this), delay, delay);
		// logger.info(plugin.logTag + " Version " + getDescription().getVersion() +
		// " is now enabled.");
//		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
//			plugin.loadInventory(player, player.getWorld());
//		}
//		logger.info(plugin.logTag + plugin.li.getMessage("inventoriesloaded"));
//	}*/

	/*

	private boolean setupPermissions() {
		Plugin permissionsPlugin = Bukkit.getServer().getPluginManager()
				.getPlugin("Permissions");
		if (permissionsPlugin == null) {
			return false;
		} else {
			permissionHandler = ((Permissions) permissionsPlugin).getHandler();
			logger.info(plugin.logTag + plugin.li.getMessage("permissionsfound"));
			return true;
		}
	}

	private boolean setupPermissionsBukkit() {
		permissionsBukkitPlugin = Bukkit.getServer().getPluginManager()
				.getPlugin("PermissionsBukkit");
		if (permissionsBukkitPlugin == null) {
			return false;
		} else {
			logger.info(plugin.logTag + plugin.li.getMessage("permissionsbukkitfound"));
			return true;
		}
	}

	private boolean setupPermissionsEx() {
		if (Bukkit.getServer().getPluginManager()
				.isPluginEnabled("PermissionsEx")) {
			permissionsEx = PermissionsEx.getPermissionManager();
			logger.info(plugin.logTag + plugin.li.getMessage("permissionsexfound"));
			return true;
		} else {
			return false;
		}
	}

	private boolean setupGroupManager() {
		Plugin groupManagerPlugin = Bukkit.getServer().getPluginManager()
				.getPlugin("GroupManager");
		if (groupManagerPlugin == null) {
			return false;
		} else {
			//groupManager = (GroupManager) groupManagerPlugin;
			logger.info(plugin.logTag + plugin.li.getMessage("groupmanagerfound"));
			return true;
		}
	}

	private void setupOPSystem() {
		if (permissionHandler == null && permissionsBukkitPlugin == null) {
			logger.info(plugin.logTag + plugin.li.getMessage("opsystemfound"));
		}
	}

*/

	// CLJprivate void setupJail() {
	// CLJ Plugin jailPlugin = getServer().getPluginManager().getPlugin("Jail");
	// CLJ if (jailPlugin != null) {
	// CLJ jail = ((Jail) jailPlugin).API;
	// CLJ logger.info(plugin.logTag + li.getMessage("jailfound"));
	// CLJ return;
	// CLJ } else {
	// CLJ return;
	// CLJ }
	// CLJ}






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
							if (G333Permissions.hasPerm(player, "backpack.reload",
									G333Permissions.NOT_QUIET)) {
								//plugin.loadOrReloadConfiguration();
								player.sendMessage(BIT.li
										.getMessage("configreloaded1"));
								player.sendMessage(BIT.li
										.getMessage("configreloaded2"));
								player.sendMessage(BIT.li
										.getMessage("configreloaded3"));
							}
						} else if (argument.equalsIgnoreCase("help")
								|| argument.equalsIgnoreCase("?")) {
							showHelp(player);
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
										&& BIT.plugin.Method != null
										) {
									double cost = calculateCost(size);
									player.sendMessage(BIT.li
											.getMessage("nextupgradecost")
											+ ChatColor.RED
											+ BIT.plugin.Method.format(cost)
											+ ChatColor.WHITE + ".");
								}
							}
						} else if (argument.equalsIgnoreCase("upgrade")) {
							if (BIT.allowedSize(player.getWorld(), player, true) < upgradeAllowedSize(
									player.getWorld(), player)) {
								if (BIT.plugin.Method != null
										) {
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
										+ BIT.li
												.getMessage("foryourpermissions"));
							}
						} else if (argument.equalsIgnoreCase("clear")) {
							if (G333Permissions.hasPerm(player, "backpack.clear",
									G333Permissions.NOT_QUIET)) {
								if (BIT.inventories.containsKey(player
										.getName())) {
									BIT.inventories.remove(player.getName());
									player.sendMessage(BIT.li
											.getMessage("your")
											+ ChatColor.RED
											+ BIT.inventoryName
											+ ChatColor.WHITE
											+ BIT.li
													.getMessage("hasbeencleared"));
								} else {
									player.sendMessage(BIT.li
											.getMessage("youdonthavearegistred")
											+ ChatColor.RED
											+ BIT.inventoryName
											+ ChatColor.WHITE
											+ BIT.li.getMessage("!"));
								}
							}
						} else {
							showHelp(player);
						}
					} else if (args.length == 2) {
						String firstArgument = args[0];
						String playerName = args[1];
						if (firstArgument.equalsIgnoreCase("info")) {
							if (G333Permissions.hasPerm(player, "backpack.info.other",
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
												+ BIT.li
														.getMessage("hasbis")
												+ size
												+ BIT.li.getMessage("slots"));
										if (size < upgradeAllowedSize(
												player.getWorld(), player)
												&& BIT.plugin.Method != null
												) {
											double cost = calculateCost(size);
											player.sendMessage(BIT.li
													.getMessage("nextupgradecost")
													+ ChatColor.RED
													+ BIT.plugin.Method.format(cost)
													+ ChatColor.WHITE + ".");
										}
									}
								} else {
									player.sendMessage(ChatColor.RED
											+ BIT.li
													.getMessage("playernotfound"));
								}
							}
						} else if (firstArgument.equalsIgnoreCase("upgrade")) {
							if (playerName.equalsIgnoreCase("workbench")) {
								if (!BIT.hasWorkbench(player) && G333Config.SBP_workbenchBuyable) {
									BIT.setWorkbench(player, true);
								} else
									player.sendMessage(ChatColor.RED
											+ BIT.li
													.getMessage("youalreadyhaveaccesstotheworkbench"));
							} else {
								if (G333Permissions.hasPerm(player, "backpack.upgrade.other",
										G333Permissions.NOT_QUIET)) {
									if (BIT.inventories
											.containsKey(playerName)) {
										Player playerCmd = Bukkit.getServer()
												.getPlayer(playerName);
										if (BIT.allowedSize(playerCmd.getWorld(),
												playerCmd, true) < upgradeAllowedSize(
												playerCmd.getWorld(), playerCmd)) {
											if (BIT.plugin.Method != null
													) {
												startUpgradeProcedure(
														BIT.allowedSize(playerCmd
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
													+ BIT.li
															.getMessage("forhispermissions"));
										}
									} else {
										player.sendMessage(ChatColor.RED
												+ BIT.li
														.getMessage("playernotfound"));
									}
								}
							}
						} else if (firstArgument.equalsIgnoreCase("clear")) {
							if (G333Permissions.hasPerm(player, "backpack.clear.other",
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
											+ BIT.li
													.getMessage("hasbeencleared"));
								} else {
									player.sendMessage(ChatColor.RED
											+ BIT.li
													.getMessage("playernotfound"));
								}
							}
						} else if (firstArgument.equalsIgnoreCase("open")) {
							if (G333Permissions.hasPerm(player, "backpack.open.other",
									G333Permissions.NOT_QUIET)) {
								if (BIT.inventories.containsKey(playerName)) {
									if (!BIT.openedInventories
											.containsKey(playerName)) {
										CustomInventory inv = new CustomInventory(
												BIT.allowedSize(player.getWorld(),
														player, true),
												BIT.inventoryName);
										BIT.openedInventories.put(
												playerName, inv);
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
												+ BIT.li
														.getMessage("opened"));
									}
								} else {
									player.sendMessage(ChatColor.RED
											+ BIT.li
													.getMessage("playernotfound"));
								}
							}
						}
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
		if (BIT.plugin.Method.hasAccount(notificationsAndMoneyPlayer.getName())) {
			MethodAccount account = (MethodAccount) BIT.plugin.Method
					.getAccount(notificationsAndMoneyPlayer.getName());
			if (account != null) {
				if (account.hasEnough(cost)) {
					account.subtract(cost);
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
			saveFile = new File(plugin.getDataFolder() + File.separator
					+ "inventories", player.getName() + ".yml");
		} else {
			saveFile = new File(plugin.getDataFolder() + File.separator
					+ "inventories", player.getName() + "_"
					+ player.getWorld().getName() + ".yml");
		}
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(saveFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		config.set("Size", sizeAfter);
		try {
			config.save(saveFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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

/*	public boolean userHasPermission(Player player, String permission) {
		if (permissionHandler != null) {
			return permissionHandler.has(player.getWorld().getName(),
					player.getName(), permission);
		} else if (permissionsBukkitPlugin != null) {
			return player.hasPermission(permission);
		} else if (permissionsEx != null) {
			return permissionsEx.has(player, permission);
		} else if (groupManager != null) {
			return groupManager.getWorldsHolder().getWorldPermissions(player)
					.has(player, permission);
		} else {
			return player.isOp();
		}
	}
*/
	public void showHelp(Player player) {
		if (G333Permissions.hasPerm(player, "backpack.reload",G333Permissions.QUIET)) {
			player.sendMessage(BIT.li.getMessage("reloadcommand"));
		}
		player.sendMessage(BIT.li.getMessage("infocommand") + ChatColor.RED
				+ BIT.inventoryName + ChatColor.WHITE + ".");
		if (BIT.allowedSize(player.getWorld(), player, true) < upgradeAllowedSize(
				player.getWorld(), player)
				&& BIT.plugin.Method != null
				) {
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
		if (G333Permissions.hasPerm(player, "backpack.upgrade54",G333Permissions.QUIET)) {
			size = 54;
		} else if (G333Permissions.hasPerm(player, "backpack.upgrade45",G333Permissions.QUIET)) {
			size = 45;
		} else if (G333Permissions.hasPerm(player, "backpack.upgrade36",G333Permissions.QUIET)) {
			size = 36;
		} else if (G333Permissions.hasPerm(player, "backpack.upgrade27",G333Permissions.QUIET)) {
			size = 27;
		} else if (G333Permissions.hasPerm(player, "backpack.upgrade18",G333Permissions.QUIET)) {
			size = 18;
		}
		return size;
	}


/*	public void onDisableXXX() {
		method = null;
		Bukkit.getServer().getScheduler().cancelTask(BIT.saveTaskId);
		logger.info(plugin.logTag + plugin.li.getMessage("savingallinventories"));
		SBInventorySaveTask.saveAll();
		logger.info(plugin.logTag + plugin.li.getMessage("isnowdisabled"));
	}*/
}