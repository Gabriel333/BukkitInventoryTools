package dk.gabriel333.spoutbackpack;

import java.io.File;
import java.util.logging.Logger;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;
import org.getspout.spout.inventory.CustomInventory;
import register.Method;
import register.Method.MethodAccount;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

//CLJimport com.matejdro.bukkit.jail.Jail;
//CLJimport com.matejdro.bukkit.jail.JailAPI;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import dk.gabriel333.BukkitInventoryTools.BIT;

@SuppressWarnings({ "deprecation" })
public class SpoutBackpack {

	public static Logger logger = Logger.getLogger("minecraft");
	public PermissionHandler permissionHandler;
	public Plugin permissionsBukkitPlugin;
	public PermissionManager permissionsEx;
	public Method method = null;
	public BIT plugin;

	// CLJpublic static JailAPI jail;

	public void onEnableXXX() {
		
		if (plugin.config.getBoolean("Permissions.UsePermissions?", true) == false) {
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
		}
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
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			plugin.loadInventory(player, player.getWorld());
		}
		logger.info(plugin.logTag + plugin.li.getMessage("inventoriesloaded"));
	}

	

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
			groupManager = (GroupManager) groupManagerPlugin;
			logger.info(plugin.logTag + plugin.li.getMessage("groupmanagerfound"));
			return true;
		}
	}

	private void setupOPSystem() {
		if (permissionHandler == null && permissionsBukkitPlugin == null) {
			logger.info(plugin.logTag + plugin.li.getMessage("opsystemfound"));
		}
	}



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

	@SuppressWarnings({})




	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (commandLabel.equalsIgnoreCase("backpack")
					|| commandLabel.equalsIgnoreCase("bp")) {
				if (plugin.canOpenBackpack(player.getWorld(), player) == true) {
					if (args.length == 0) {
						showHelp(player);
					} else if (args.length == 1) {
						String argument = args[0];
						if (argument.equalsIgnoreCase("reload")) {
							if (userHasPermission(player, "backpack.reload")) {
								plugin.loadOrReloadConfiguration();
								player.sendMessage(plugin.li
										.getMessage("configreloaded1"));
								player.sendMessage(plugin.li
										.getMessage("configreloaded2"));
								player.sendMessage(plugin.li
										.getMessage("configreloaded3"));
							}
						} else if (argument.equalsIgnoreCase("help")
								|| argument.equalsIgnoreCase("?")) {
							showHelp(player);
						} else if (argument.equalsIgnoreCase("info")) {
							int size = BIT.allowedSize(player.getWorld(),
									player, true);
							if (size == 54) {
								player.sendMessage(plugin.li
										.getMessage("youvegotthebiggest")
										+ ChatColor.RED
										+ plugin.inventoryName
										+ ChatColor.WHITE
										+ plugin.li.getMessage("!"));
							} else {
								player.sendMessage(plugin.li.getMessage("your")
										+ ChatColor.RED + plugin.inventoryName
										+ ChatColor.WHITE
										+ plugin.li.getMessage("has")
										+ ChatColor.RED + size
										+ ChatColor.WHITE
										+ plugin.li.getMessage("slots"));
								if (size < upgradeAllowedSize(
										player.getWorld(), player)
										&& method != null
										&& (permissionHandler != null || permissionsBukkitPlugin != null)
										|| permissionsEx != null
										|| groupManager != null) {
									double cost = calculateCost(size);
									player.sendMessage(plugin.li
											.getMessage("nextupgradecost")
											+ ChatColor.RED
											+ method.format(cost)
											+ ChatColor.WHITE + ".");
								}
							}
						} else if (argument.equalsIgnoreCase("upgrade")) {
							if (BIT.allowedSize(player.getWorld(), player, true) < upgradeAllowedSize(
									player.getWorld(), player)) {
								if (method != null
										&& (permissionHandler != null || permissionsBukkitPlugin != null)
										|| permissionsEx != null
										|| groupManager != null) {
									startUpgradeProcedure(BIT.allowedSize(
											player.getWorld(), player, true),
											player, player);
								}
							} else if (BIT.allowedSize(player.getWorld(),
									player, true) == 54) {
								player.sendMessage(plugin.li
										.getMessage("youvegotthebiggest")
										+ ChatColor.RED
										+ plugin.inventoryName
										+ ChatColor.WHITE
										+ plugin.li.getMessage("!"));
							} else {
								player.sendMessage(plugin.li
										.getMessage("youvegotthebiggest")
										+ ChatColor.RED
										+ plugin.inventoryName
										+ ChatColor.WHITE
										+ plugin.li
												.getMessage("foryourpermissions"));
							}
						} else if (argument.equalsIgnoreCase("clear")) {
							if (userHasPermission(player, "backpack.clear")) {
								if (plugin.inventories.containsKey(player
										.getName())) {
									plugin.inventories.remove(player.getName());
									player.sendMessage(plugin.li
											.getMessage("your")
											+ ChatColor.RED
											+ plugin.inventoryName
											+ ChatColor.WHITE
											+ plugin.li
													.getMessage("hasbeencleared"));
								} else {
									player.sendMessage(plugin.li
											.getMessage("youdonthavearegistred")
											+ ChatColor.RED
											+ plugin.inventoryName
											+ ChatColor.WHITE
											+ plugin.li.getMessage("!"));
								}
							}
						} else if (argument.equalsIgnoreCase("debug")) {
							if (permissionHandler != null) {
								logger.info(plugin.li
										.getMessage("usingpermissions"));
							} else if (permissionsBukkitPlugin != null) {
								logger.info(plugin.li
										.getMessage("usingpermissionsbukkit"));
							} else if (permissionsEx != null) {
								logger.info(plugin.li
										.getMessage("usingpermissionsex"));
							} else if (groupManager != null) {
								logger.info(plugin.li
										.getMessage("usinggroupmanager"));
							}
							if (method != null) {
								logger.info(plugin.li
										.getMessage("usingeconomy"));
							}
							logger.info(plugin.li
									.getMessage("yourpermissionsgiveyoua")
									+ BIT.allowedSize(player.getWorld(), player,
											false)
									+ plugin.li.getMessage("slotsbis")
									+ plugin.inventoryName + ".");
							logger.info(plugin.li
									.getMessage("yourpermissionsallowyoutoupgradetoa")
									+ upgradeAllowedSize(player.getWorld(),
											player)
									+ plugin.li.getMessage("slotsbis")
									+ plugin.inventoryName + ".");
						} else {
							showHelp(player);
						}
					} else if (args.length == 2) {
						String firstArgument = args[0];
						String playerName = args[1];
						if (firstArgument.equalsIgnoreCase("info")) {
							if (userHasPermission(player, "backpack.info.other")) {
								if (plugin.inventories.containsKey(playerName)) {
									Player playerCmd = Bukkit.getServer()
											.getPlayer(playerName);
									int size = BIT.allowedSize(
											playerCmd.getWorld(), playerCmd,
											true);
									if (size == 54) {
										player.sendMessage(plugin.li
												.getMessage("playerhasgotthebiggest")
												+ ChatColor.RED
												+ plugin.inventoryName
												+ ChatColor.WHITE
												+ plugin.li.getMessage("!"));
									} else {
										player.sendMessage(plugin.li
												.getMessage("players")
												+ ChatColor.RED
												+ plugin.inventoryName
												+ ChatColor.WHITE
												+ plugin.li
														.getMessage("hasbis")
												+ size
												+ plugin.li.getMessage("slots"));
										if (size < upgradeAllowedSize(
												player.getWorld(), player)
												&& method != null
												&& (permissionHandler != null || permissionsBukkitPlugin != null)
												|| permissionsEx != null
												|| groupManager != null) {
											double cost = calculateCost(size);
											player.sendMessage(plugin.li
													.getMessage("nextupgradecost")
													+ ChatColor.RED
													+ method.format(cost)
													+ ChatColor.WHITE + ".");
										}
									}
								} else {
									player.sendMessage(ChatColor.RED
											+ plugin.li
													.getMessage("playernotfound"));
								}
							}
						} else if (firstArgument.equalsIgnoreCase("upgrade")) {
							if (playerName.equalsIgnoreCase("workbench")) {
								if (!plugin.hasWorkbench(player) && plugin.workbenchBuyable) {
									plugin.setWorkbench(player, true);
								} else
									player.sendMessage(ChatColor.RED
											+ plugin.li
													.getMessage("youalreadyhaveaccesstotheworkbench"));
							} else {
								if (userHasPermission(player,
										"backpack.upgrade.other")) {
									if (plugin.inventories
											.containsKey(playerName)) {
										Player playerCmd = Bukkit.getServer()
												.getPlayer(playerName);
										if (BIT.allowedSize(playerCmd.getWorld(),
												playerCmd, true) < upgradeAllowedSize(
												playerCmd.getWorld(), playerCmd)) {
											if (method != null
													&& (permissionHandler != null || permissionsBukkitPlugin != null)
													|| permissionsEx != null
													|| groupManager != null) {
												startUpgradeProcedure(
														BIT.allowedSize(playerCmd
																.getWorld(),
																playerCmd, true),
														playerCmd, player);
											}
										} else if (BIT.allowedSize(
												playerCmd.getWorld(),
												playerCmd, true) == 54) {
											player.sendMessage(plugin.li
													.getMessage("playerhasgotthebiggest")
													+ ChatColor.RED
													+ plugin.inventoryName
													+ ChatColor.WHITE
													+ plugin.li.getMessage("!"));
										} else {
											player.sendMessage(plugin.li
													.getMessage("playerhasgotthebiggest")
													+ ChatColor.RED
													+ plugin.inventoryName
													+ ChatColor.WHITE
													+ plugin.li
															.getMessage("forhispermissions"));
										}
									} else {
										player.sendMessage(ChatColor.RED
												+ plugin.li
														.getMessage("playernotfound"));
									}
								}
							}
						} else if (firstArgument.equalsIgnoreCase("clear")) {
							if (userHasPermission(player,
									"backpack.clear.other")) {
								if (plugin.inventories.containsKey(playerName)) {
									plugin.inventories.remove(playerName);
									player.sendMessage(plugin.li
											.getMessage("frenchonly")
											+ playerName
											+ plugin.li.getMessage("'s")
											+ ChatColor.RED
											+ plugin.inventoryName
											+ ChatColor.WHITE
											+ plugin.li
													.getMessage("hasbeencleared"));
								} else {
									player.sendMessage(ChatColor.RED
											+ plugin.li
													.getMessage("playernotfound"));
								}
							}
						} else if (firstArgument.equalsIgnoreCase("open")) {
							if (userHasPermission(player, "backpack.open.other")) {
								if (plugin.inventories.containsKey(playerName)) {
									if (!plugin.openedInventories
											.containsKey(playerName)) {
										CustomInventory inv = new CustomInventory(
												BIT.allowedSize(player.getWorld(),
														player, true),
												plugin.inventoryName);
										plugin.openedInventories.put(
												playerName, inv);
										plugin.openedInventoriesOthers.put(
												player.getName(), playerName);
										inv.setContents(plugin.inventories
												.get(playerName));
										((org.getspout.spoutapi.player.SpoutPlayer) player)
												.openInventoryWindow((Inventory) inv);
									} else {
										player.sendMessage(plugin.li
												.getMessage("playerhasalreadyhis")
												+ ChatColor.RED
												+ plugin.inventoryName
												+ ChatColor.WHITE
												+ plugin.li
														.getMessage("opened"));
									}
								} else {
									player.sendMessage(ChatColor.RED
											+ plugin.li
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
		if (method.hasAccount(notificationsAndMoneyPlayer.getName())) {
			MethodAccount account = method
					.getAccount(notificationsAndMoneyPlayer.getName());
			if (account != null) {
				if (account.hasEnough(cost)) {
					account.subtract(cost);
				} else {
					if (player.equals(notificationsAndMoneyPlayer)) {
						notificationsAndMoneyPlayer.sendMessage(plugin.li
								.getMessage("notenoughmoneyyour")
								+ ChatColor.RED
								+ plugin.inventoryName
								+ ChatColor.WHITE + ".");
					} else {
						notificationsAndMoneyPlayer.sendMessage(plugin.li
								.getMessage("notenoughmoneyplayer")
								+ ChatColor.RED
								+ plugin.inventoryName
								+ ChatColor.WHITE + ".");
					}
					return;
				}
			} else {
				notificationsAndMoneyPlayer.sendMessage(ChatColor.RED
						+ plugin.li.getMessage("noaccount"));
				return;
			}
		}
		SBInventorySaveTask.saveInventory(player, player.getWorld());
		plugin.inventories.remove(player.getName());
		File saveFile;
		if (plugin.config.getBoolean("Backpack." + player.getWorld().getName()
				+ ".InventoriesShare?", true)) {
			saveFile = new File(plugin.getDataFolder() + File.separator
					+ "inventories", player.getName() + ".yml");
		} else {
			saveFile = new File(plugin.getDataFolder() + File.separator
					+ "inventories", player.getName() + "_"
					+ player.getWorld().getName() + ".yml");
		}
		Configuration config = new Configuration(saveFile);
		config.load();
		config.setProperty("Size", sizeAfter);
		config.save();
		plugin.loadInventory(player, player.getWorld());
		notificationsAndMoneyPlayer.sendMessage(plugin.li.getMessage("your")
				+ ChatColor.RED + plugin.inventoryName + ChatColor.WHITE
				+ plugin.li.getMessage("hasbeenupgraded"));
		notificationsAndMoneyPlayer.sendMessage(plugin.li
				.getMessage("ithasnow")
				+ ChatColor.RED
				+ sizeAfter
				+ ChatColor.WHITE + plugin.li.getMessage("slots"));
	}

	public boolean userHasPermission(Player player, String permission) {
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

	public void showHelp(Player player) {
		if (userHasPermission(player, "backpack.reload")) {
			player.sendMessage(plugin.li.getMessage("reloadcommand"));
		}
		player.sendMessage(plugin.li.getMessage("infocommand") + ChatColor.RED
				+ plugin.inventoryName + ChatColor.WHITE + ".");
		if (BIT.allowedSize(player.getWorld(), player, true) < upgradeAllowedSize(
				player.getWorld(), player)
				&& method != null
				&& (permissionHandler != null || permissionsBukkitPlugin != null)
				|| permissionsEx != null || groupManager != null) {
			player.sendMessage(plugin.li.getMessage("upgradecommand")
					+ ChatColor.RED + plugin.inventoryName + ChatColor.WHITE + ".");
		}
	}

	public double calculateCost(int size) {
		double cost = plugin.price54;
		if (size == 9) {
			cost = plugin.price18;
		} else if (size == 18) {
			cost = plugin.price27;
		} else if (size == 27) {
			cost = plugin.price36;
		} else if (size == 36) {
			cost = plugin.price45;
		} else if (size == 45) {
			cost = plugin.price54;
		}
		return cost;
	}

	private int upgradeAllowedSize(World world, Player player) {
		int size = 9;
		if (userHasPermission(player, "backpack.upgrade54")) {
			size = 54;
		} else if (userHasPermission(player, "backpack.upgrade45")) {
			size = 45;
		} else if (userHasPermission(player, "backpack.upgrade36")) {
			size = 36;
		} else if (userHasPermission(player, "backpack.upgrade27")) {
			size = 27;
		} else if (userHasPermission(player, "backpack.upgrade18")) {
			size = 18;
		}
		return size;
	}


	public void onDisableXXX() {
		method = null;
		Bukkit.getServer().getScheduler().cancelTask(plugin.saveTaskId);
		logger.info(plugin.logTag + plugin.li.getMessage("savingallinventories"));
		SBInventorySaveTask.saveAll();
		logger.info(plugin.logTag + plugin.li.getMessage("isnowdisabled"));
	}
}