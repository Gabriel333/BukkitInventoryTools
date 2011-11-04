package dk.gabriel333.spoutbackpack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.getspout.spout.inventory.CustomInventory;
import org.getspout.spoutapi.gui.GenericLabel;

import register.Method;
import register.Method.MethodAccount;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.garbagemule.MobArena.MobArenaHandler;
//CLJimport com.matejdro.bukkit.jail.Jail;
//CLJimport com.matejdro.bukkit.jail.JailAPI;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import dk.gabriel333.spoutbackpack.SBLanguageInterface.Language;

@SuppressWarnings({ "deprecation"})
public class SpoutBackpack extends JavaPlugin {
	public Configuration					config;
	public boolean							workbenchEnabled;
	public boolean							workbenchInventory;
	public boolean							workbenchBuyable;
	public int								blackOrWhiteList;
	public List<Integer>					whitelist				= new ArrayList<Integer>();
	public List<Integer>					blacklist				= new ArrayList<Integer>();
	protected List<String>					noBackpackRegions		= new ArrayList<String>();
	public List<Player>						portals					= new ArrayList<Player>();
	public String							inventoryName;
	public boolean							useWidget;
	public int								widgetX;
	public int								widgetY;
	public double							price18;
	public double							price27;
	public double							price36;
	public double							price45;
	public double							price54;
	public int								saveTime;
	private int								saveTaskId;
	public boolean							logSaves;
	public HashMap<String, ItemStack[]>		inventories				= new HashMap<String, ItemStack[]>();
	public HashMap<String, Inventory>		openedInventories		= new HashMap<String, Inventory>();
	public HashMap<String, String>			openedInventoriesOthers	= new HashMap<String, String>();
	public HashMap<String, GenericLabel>	widgets					= new HashMap<String, GenericLabel>();
	public static Logger					logger					= Logger.getLogger("minecraft");
	public String							logTag					= "[SpoutBackpack]";
	public SBLanguageInterface				li;
	public PermissionHandler				permissionHandler;
	public Plugin							permissionsBukkitPlugin;
	public PermissionManager				permissionsEx;
	public static GroupManager				groupManager;
	public Method							method					= null;
	public static MobArenaHandler			mobArenaHandler;
	//CLJpublic static JailAPI					jail;

	@Override
	public void onEnable() {
		loadOrReloadConfiguration();
		li = new SBLanguageInterface(loadLanguage());
		if (config.getBoolean("Permissions.UsePermissions?", true) == false) {
			if (config.getBoolean("Permissions.UsePermissionsBukkit?", false) == false) {
				if (config.getBoolean("Permissions.UsePermissionsEx?", false) == false) {
					if (config.getBoolean("Permissions.UseGroupManager?", false) == false) {
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
		setupMobArena();
		//CLJsetupJail();
		PluginManager pm = this.getServer().getPluginManager();
		//CKJpm.registerEvent(Type.PLUGIN_ENABLE, new SBRegister(this), Priority.Monitor, this);
		//CLJpm.registerEvent(Type.PLUGIN_DISABLE, new SBRegister(this), Priority.Monitor, this);
		pm.registerEvent(Type.CUSTOM_EVENT, new SBInputListener(this), Priority.Normal, this);
		pm.registerEvent(Type.CUSTOM_EVENT, new SBInventoryListener(this), Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_JOIN, new SBPlayerListener(this), Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_TELEPORT, new SBPlayerListener(this), Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_PORTAL, new SBPlayerListener(this), Priority.Monitor, this);
		pm.registerEvent(Type.PLAYER_KICK, new SBPlayerListener(this), Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_QUIT, new SBPlayerListener(this), Priority.Normal, this);
		pm.registerEvent(Type.ENTITY_DEATH, new SBEntityListener(this), Priority.Normal, this);
		long delay = 20L * 60 * saveTime;
		saveTaskId = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new SBInventorySaveTask(this), delay, delay);
		logger.info(logTag + " Version " + getDescription().getVersion() + " is now enabled.");
		for (Player player : this.getServer().getOnlinePlayers()) {
			this.loadInventory(player, player.getWorld());
		}
		logger.info(logTag + li.getMessage("inventoriesloaded"));
	}

	private void loadOrReloadConfiguration() {
		this.config = this.getConfiguration();
		config.load();
		config.getString("Language", "English");
		config.getBoolean("Permissions.UsePermissions?", true);
		config.getBoolean("Permissions.UsePermissionsBukkit?", false);
		config.getBoolean("Permissions.UsePermissionsEx?", false);
		config.getBoolean("Permissions.UseGroupManager?", false);
		config.getString("Backpack.Key", "B");
		inventoryName = config.getString("Backpack.Name", "Backpack");
		config.getBoolean("Backpack.world_name.InventoriesShare?", true);
		noBackpackRegions = config.getStringList("Backpack.RegionWhereBackpacksAreDisabled", null);
		if (noBackpackRegions.size() == 0) {
			noBackpackRegions.add("region1");
			noBackpackRegions.add("region2");
			config.setProperty("Backpack.RegionWhereBackpacksAreDisabled", noBackpackRegions);
		}
		price18 = config.getDouble("Backpack.Price.18", 10.00);
		price27 = config.getDouble("Backpack.Price.27", 20.00);
		price36 = config.getDouble("Backpack.Price.36", 30.00);
		price45 = config.getDouble("Backpack.Price.45", 40.00);
		price54 = config.getDouble("Backpack.Price.54", 50.00);
		blackOrWhiteList = config.getInt("Backpack.NoneBlackOrWhiteList?", 0);
		if (blackOrWhiteList != 1 && blackOrWhiteList != 2) {
			blackOrWhiteList = 0;
		}
		whitelist = config.getIntList("Backpack.Whitelist", null);
		if (whitelist.size() == 0) {
			whitelist.add(262);
			config.setProperty("Backpack.Whitelist", whitelist);
		}
		blacklist = config.getIntList("Backpack.Blacklist", null);
		if (blacklist.size() == 0) {
			blacklist.add(264);
			config.setProperty("Backpack.Blacklist", blacklist);
		}
		workbenchEnabled = config.getBoolean("Workbench.Enabled?", true);
		config.getString("Workbench.Key", "N");
		workbenchInventory = config.getBoolean("Workbench.NeededInInventory?", false);
		useWidget = config.getBoolean("Widget.Enabled?", true);
		widgetX = config.getInt("Widget.PositionX", 3);
		widgetY = config.getInt("Widget.PositionY", 5);
		logSaves = config.getBoolean("Saves.Log?", false);
		saveTime = config.getInt("Saves.Interval(InMinutes)", 5);
		config.save();
	}

	private Language loadLanguage() {
		this.config = this.getConfiguration();
		config.load();
		String languageInConfig = config.getString("Language", "English");
		if (languageInConfig.equalsIgnoreCase("English")) {
			return Language.ENGLISH;
		} else if (languageInConfig.equalsIgnoreCase("French")) {
			return Language.FRENCH;
		} else {
			logger.warning(logTag + " Language set to ENGLISH by default.");
			logger.warning(logTag + " Invalid property in configuration file!");
			return Language.ENGLISH;
		}
	}

	private boolean setupPermissions() {
		Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
		if (permissionsPlugin == null) {
			return false;
		} else {
			permissionHandler = ((Permissions) permissionsPlugin).getHandler();
			logger.info(logTag + li.getMessage("permissionsfound"));
			return true;
		}
	}

	private boolean setupPermissionsBukkit() {
		permissionsBukkitPlugin = this.getServer().getPluginManager().getPlugin("PermissionsBukkit");
		if (permissionsBukkitPlugin == null) {
			return false;
		} else {
			logger.info(logTag + li.getMessage("permissionsbukkitfound"));
			return true;
		}
	}

	private boolean setupPermissionsEx() {
		if (getServer().getPluginManager().isPluginEnabled("PermissionsEx")) {
			permissionsEx = PermissionsEx.getPermissionManager();
			logger.info(logTag + li.getMessage("permissionsexfound"));
			return true;
		} else {
			return false;
		}
	}

	private boolean setupGroupManager() {
		Plugin groupManagerPlugin = this.getServer().getPluginManager().getPlugin("GroupManager");
		if (groupManagerPlugin == null) {
			return false;
		} else {
			groupManager = (GroupManager) groupManagerPlugin;
			logger.info(logTag + li.getMessage("groupmanagerfound"));
			return true;
		}
	}

	private void setupOPSystem() {
		if (permissionHandler == null && permissionsBukkitPlugin == null) {
			logger.info(logTag + li.getMessage("opsystemfound"));
		}
	}

	WorldGuardPlugin getWorldGuard() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) { return null; }
		return (WorldGuardPlugin) plugin;
	}

	private void setupMobArena() {
		if (mobArenaHandler != null) { return; }
		Plugin mobArenaPlugin = this.getServer().getPluginManager().getPlugin("MobArena");
		if (mobArenaPlugin == null) { return; }
		mobArenaHandler = new MobArenaHandler();
		logger.info(logTag + li.getMessage("mobarenafound"));
		return;
	}

	//CLJprivate void setupJail() {
	//CLJ	Plugin jailPlugin = getServer().getPluginManager().getPlugin("Jail");
	//CLJ	if (jailPlugin != null) {
	//CLJ		jail = ((Jail) jailPlugin).API;
	//CLJ		logger.info(logTag + li.getMessage("jailfound"));
	//CLJ		return;
	//CLJ	} else {
	//CLJ		return;
	//CLJ	}
	//CLJ}

	@SuppressWarnings({ })
	public void loadInventory(Player player, World world) {
		if (inventories.get(player.getName()) != null) { return; }
		File saveFile;
		if (config.getBoolean("Backpack." + world.getName() + ".InventoriesShare?", true)) {
			saveFile = new File(this.getDataFolder() + File.separator + "inventories", player.getName() + ".yml");
		} else {
			saveFile = new File(this.getDataFolder() + File.separator + "inventories", player.getName() + "_" + world.getName() + ".yml");
		}
		@SuppressWarnings({})
		Configuration config = new Configuration(saveFile);
		config.load();
		int size = allowedSize(world, player, true);
		CustomInventory inv = new CustomInventory(size, this.inventoryName);
		if (saveFile.exists()) {
			Integer i = 0;
			for (i = 0; i < size; i++) {
				ItemStack item = new ItemStack(0, 0);
				item.setAmount(config.getInt(i.toString() + ".amount", 0));
				item.setTypeId(config.getInt(i.toString() + ".type", 0));
				Integer durability = config.getInt(i.toString() + ".durability", 0);
				item.setDurability(Short.parseShort(durability.toString()));
				inv.setItem(i, item);
			}
		}
		this.inventories.put(player.getName(), inv.getContents());
	}

	public void updateInventory(Player player, ItemStack[] is) {
		this.inventories.put(player.getName(), is);
	}

	public int allowedSize(World world, Player player, boolean configurationCheck) {
		int size = 9;
		if (permissionHandler != null) {
			if (permissionHandler.has(world.getName(), player.getName(), "backpack.size54")) {
				size = 54;
			} else if (permissionHandler.has(world.getName(), player.getName(), "backpack.size45")) {
				size = 45;
			} else if (permissionHandler.has(world.getName(), player.getName(), "backpack.size36")) {
				size = 36;
			} else if (permissionHandler.has(world.getName(), player.getName(), "backpack.size27")) {
				size = 27;
			} else if (permissionHandler.has(world.getName(), player.getName(), "backpack.size18")) {
				size = 18;
			} else if (permissionHandler.has(world.getName(), player.getName(), "backpack.size9")) {
				size = 9;
			}
		} else if (permissionsBukkitPlugin != null) {
			if (player.hasPermission("backpack.size54")) {
				size = 54;
			} else if (player.hasPermission("backpack.size45")) {
				size = 45;
			} else if (player.hasPermission("backpack.size36")) {
				size = 36;
			} else if (player.hasPermission("backpack.size27")) {
				size = 27;
			} else if (player.hasPermission("backpack.size18")) {
				size = 18;
			} else if (player.hasPermission("backpack.size9")) {
				size = 9;
			}
		} else if (permissionsEx != null) {
			if (permissionsEx.has(player.getName(), "backpack.size54", player.getWorld().getName())) {
				size = 54;
			} else if (permissionsEx.has(player.getName(), "backpack.size45", player.getWorld().getName())) {
				size = 45;
			} else if (permissionsEx.has(player.getName(), "backpack.size36", player.getWorld().getName())) {
				size = 36;
			} else if (permissionsEx.has(player.getName(), "backpack.size27", player.getWorld().getName())) {
				size = 27;
			} else if (permissionsEx.has(player.getName(), "backpack.size18", player.getWorld().getName())) {
				size = 18;
			} else if (permissionsEx.has(player.getName(), "backpack.size9", player.getWorld().getName())) {
				size = 9;
			}
		} else if (groupManager != null) {
				if (groupManager.getWorldsHolder().getWorldPermissions(player).has(player, "backpack.size54")) {
					size = 54;
				} else if (groupManager.getWorldsHolder().getWorldPermissions(player).has(player, "backpack.size45")) {
					size = 45;
				} else if (groupManager.getWorldsHolder().getWorldPermissions(player).has(player, "backpack.size36")) {
					size = 36;
				} else if (groupManager.getWorldsHolder().getWorldPermissions(player).has(player, "backpack.size27")) {
					size = 27;
				} else if (groupManager.getWorldsHolder().getWorldPermissions(player).has(player, "backpack.size18")) {
					size = 18;
				} else if (groupManager.getWorldsHolder().getWorldPermissions(player).has(player, "backpack.size9")) {
					size = 9;
				}
		} else {
			if (player.isOp() == true) {
				size = 54;
			} else {
				size = 9;
			}
		}
		if (configurationCheck) {
			if (allowedSizeInConfig(world, player) > size) {
				size = allowedSizeInConfig(world, player);
			}
		}
		return size;
	}

	public int allowedSizeInConfig(World world, Player player) {
		File saveFile;
		if (config.getBoolean("Backpack." + world.getName() + ".InventoriesShare?", true)) {
			saveFile = new File(this.getDataFolder() + File.separator + "inventories", player.getName() + ".yml");
		} else {
			saveFile = new File(this.getDataFolder() + File.separator + "inventories", player.getName() + "_" + world.getName() + ".yml");
		}
		Configuration config = new Configuration(saveFile);
		config.load();
		return config.getInt("Size", 0);
	}

	public boolean canOpenBackpack(World world, Player player) {
		boolean canOpenBackpack = false;
		if (permissionHandler != null) {
			if ((permissionHandler.has(world.getName(), player.getName(), "backpack.size54"))
					|| (permissionHandler.has(world.getName(), player.getName(), "backpack.size45"))
					|| (permissionHandler.has(world.getName(), player.getName(), "backpack.size36"))
					|| (permissionHandler.has(world.getName(), player.getName(), "backpack.size27"))
					|| (permissionHandler.has(world.getName(), player.getName(), "backpack.size18"))
					|| (permissionHandler.has(world.getName(), player.getName(), "backpack.size9"))) {
				canOpenBackpack = true;
			} else {
				canOpenBackpack = false;
			}
		} else if (permissionsBukkitPlugin != null) {
			if ((player.hasPermission("backpack.size54")) || (player.hasPermission("backpack.size45"))
					|| (player.hasPermission("backpack.size36")) || (player.hasPermission("backpack.size27"))
					|| (player.hasPermission("backpack.size18")) || (player.hasPermission("backpack.size9"))) {
				canOpenBackpack = true;
			} else {
				canOpenBackpack = false;
			}
		} else if (permissionsEx != null) {
			if ((permissionsEx.has(player.getName(), "backpack.size54", world.getName()))
					|| (permissionsEx.has(player.getName(), "backpack.size45", world.getName()))
					|| (permissionsEx.has(player.getName(), "backpack.size36", world.getName()))
					|| (permissionsEx.has(player.getName(), "backpack.size27", world.getName()))
					|| (permissionsEx.has(player.getName(), "backpack.size18", world.getName()))
					|| (permissionsEx.has(player.getName(), "backpack.size9", world.getName()))) {
				canOpenBackpack = true;
			} else {
				canOpenBackpack = false;
			}
			} else if (groupManager != null) {
				if ((groupManager.getWorldsHolder().getWorldPermissions(player).has(player, "backpack.size54"))
						|| (groupManager.getWorldsHolder().getWorldPermissions(player).has(player, "backpack.size45"))
						|| (groupManager.getWorldsHolder().getWorldPermissions(player).has(player, "backpack.size36"))
						|| (groupManager.getWorldsHolder().getWorldPermissions(player).has(player, "backpack.size27"))
						|| (groupManager.getWorldsHolder().getWorldPermissions(player).has(player, "backpack.size18"))
						|| (groupManager.getWorldsHolder().getWorldPermissions(player).has(player, "backpack.size9"))) {
					canOpenBackpack = true;
				} else {
					canOpenBackpack = false;
				}
		} else {
			canOpenBackpack = true;
		}
		if (getWorldGuard() != null) {
			Location location = player.getLocation();
			com.sk89q.worldedit.Vector vector = new com.sk89q.worldedit.Vector(location.getX(), location.getY(), location.getZ());
			Map<String, ProtectedRegion> regions = getWorldGuard().getGlobalRegionManager().get(location.getWorld()).getRegions();
			List<String> inRegions = new ArrayList<String>();
			for (String key_ : regions.keySet()) {
				ProtectedRegion region = regions.get(key_);
				if (region.contains(vector)) {
					inRegions.add(key_);
				}
			}
			for (String region : noBackpackRegions) {
				if (inRegions.contains(region)) {
					canOpenBackpack = false;
				}
			}
		}
		if (mobArenaHandler != null) {
			if (mobArenaHandler.inRegion(player.getLocation())) {
				canOpenBackpack = false;
			}
		}
		//CLJif (jail != null) {
		//CLJ	if (jail.isPlayerJailed(player.getName()) == true) {
		//CLJ		canOpenBackpack = false;
		//CLJ	}
		//CLJ}
		return canOpenBackpack;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (commandLabel.equalsIgnoreCase("backpack") || commandLabel.equalsIgnoreCase("bp")) {
				if (canOpenBackpack(player.getWorld(), player) == true) {
					if (args.length == 0) {
						showHelp(player);
					} else if (args.length == 1) {
						String argument = args[0];
						if (argument.equalsIgnoreCase("reload")) {
							if (userHasPermission(player, "backpack.reload")) {
								loadOrReloadConfiguration();
								player.sendMessage(li.getMessage("configreloaded1"));
								player.sendMessage(li.getMessage("configreloaded2"));
								player.sendMessage(li.getMessage("configreloaded3"));
							}
						} else if (argument.equalsIgnoreCase("help") || argument.equalsIgnoreCase("?")) {
							showHelp(player);
						} else if (argument.equalsIgnoreCase("info")) {
							int size = allowedSize(player.getWorld(), player, true);
							if (size == 54) {
								player.sendMessage(li.getMessage("youvegotthebiggest") + ChatColor.RED + inventoryName + ChatColor.WHITE
										+ li.getMessage("!"));
							} else {
								player.sendMessage(li.getMessage("your") + ChatColor.RED + inventoryName + ChatColor.WHITE
										+ li.getMessage("has") + ChatColor.RED + size + ChatColor.WHITE + li.getMessage("slots"));
								if (size < upgradeAllowedSize(player.getWorld(), player) && method != null
										&& (permissionHandler != null || permissionsBukkitPlugin != null) || permissionsEx != null
										|| groupManager != null) {
									double cost = calculateCost(size);
									player.sendMessage(li.getMessage("nextupgradecost") + ChatColor.RED + method.format(cost)
											+ ChatColor.WHITE + ".");
								}
							}
						} else if (argument.equalsIgnoreCase("upgrade")) {
							if (allowedSize(player.getWorld(), player, true) < upgradeAllowedSize(player.getWorld(), player)) {
								if (method != null && (permissionHandler != null || permissionsBukkitPlugin != null)
										|| permissionsEx != null || groupManager != null) {
									startUpgradeProcedure(allowedSize(player.getWorld(), player, true), player, player);
								}
							} else if (allowedSize(player.getWorld(), player, true) == 54) {
								player.sendMessage(li.getMessage("youvegotthebiggest") + ChatColor.RED + inventoryName + ChatColor.WHITE
										+ li.getMessage("!"));
							} else {
								player.sendMessage(li.getMessage("youvegotthebiggest") + ChatColor.RED + inventoryName + ChatColor.WHITE
										+ li.getMessage("foryourpermissions"));
							}
						} else if (argument.equalsIgnoreCase("clear")) {
							if (userHasPermission(player, "backpack.clear")) {
								if (inventories.containsKey(player.getName())) {
									inventories.remove(player.getName());
									player.sendMessage(li.getMessage("your") + ChatColor.RED + inventoryName + ChatColor.WHITE
											+ li.getMessage("hasbeencleared"));
								} else {
									player.sendMessage(li.getMessage("youdonthavearegistred") + ChatColor.RED + inventoryName
											+ ChatColor.WHITE + li.getMessage("!"));
								}
							}
						} else if (argument.equalsIgnoreCase("debug")) {
							if (permissionHandler != null) {
								logger.info(li.getMessage("usingpermissions"));
							} else if (permissionsBukkitPlugin != null) {
								logger.info(li.getMessage("usingpermissionsbukkit"));
							} else if (permissionsEx != null) {
								logger.info(li.getMessage("usingpermissionsex"));
							} else if (groupManager != null) {
								logger.info(li.getMessage("usinggroupmanager"));
							}
							if (method != null) {
								logger.info(li.getMessage("usingeconomy"));
							}
							logger.info(li.getMessage("yourpermissionsgiveyoua") + allowedSize(player.getWorld(), player, false)
									+ li.getMessage("slotsbis") + inventoryName + ".");
							logger.info(li.getMessage("yourpersonalfilegivesyoua") + allowedSizeInConfig(player.getWorld(), player)
									+ li.getMessage("slotsbis") + inventoryName + ".");
							logger.info(li.getMessage("yourpermissionsallowyoutoupgradetoa")
									+ upgradeAllowedSize(player.getWorld(), player) + li.getMessage("slotsbis") + inventoryName + ".");
						} else {
							showHelp(player);
						}
					} else if (args.length == 2) {
						String firstArgument = args[0];
						String playerName = args[1];
						if (firstArgument.equalsIgnoreCase("info")) {
							if (userHasPermission(player, "backpack.info.other")) {
								if (inventories.containsKey(playerName)) {
									Player playerCmd = getServer().getPlayer(playerName);
									int size = allowedSize(playerCmd.getWorld(), playerCmd, true);
									if (size == 54) {
										player.sendMessage(li.getMessage("playerhasgotthebiggest") + ChatColor.RED + inventoryName
												+ ChatColor.WHITE + li.getMessage("!"));
									} else {
										player.sendMessage(li.getMessage("players") + ChatColor.RED + inventoryName + ChatColor.WHITE
												+ li.getMessage("hasbis") + size + li.getMessage("slots"));
										if (size < upgradeAllowedSize(player.getWorld(), player) && method != null
												&& (permissionHandler != null || permissionsBukkitPlugin != null) || permissionsEx != null
												|| groupManager != null) {
											double cost = calculateCost(size);
											player.sendMessage(li.getMessage("nextupgradecost") + ChatColor.RED + method.format(cost)
													+ ChatColor.WHITE + ".");
										}
									}
								} else {
									player.sendMessage(ChatColor.RED + li.getMessage("playernotfound"));
								}
							}
						} else if (firstArgument.equalsIgnoreCase("upgrade")) {
							if (playerName.equalsIgnoreCase("workbench")) {
								if (!hasWorkbench(player) && workbenchBuyable) {
									setWorkbench(player, true);
								} else
									player.sendMessage(ChatColor.RED + li.getMessage("youalreadyhaveaccesstotheworkbench"));
							} else {
								if (userHasPermission(player, "backpack.upgrade.other")) {
									if (inventories.containsKey(playerName)) {
										Player playerCmd = getServer().getPlayer(playerName);
										if (allowedSize(playerCmd.getWorld(), playerCmd, true) < upgradeAllowedSize(playerCmd.getWorld(),
												playerCmd)) {
											if (method != null && (permissionHandler != null || permissionsBukkitPlugin != null)
													|| permissionsEx != null || groupManager != null) {
												startUpgradeProcedure(allowedSize(playerCmd.getWorld(), playerCmd, true), playerCmd, player);
											}
										} else if (allowedSize(playerCmd.getWorld(), playerCmd, true) == 54) {
											player.sendMessage(li.getMessage("playerhasgotthebiggest") + ChatColor.RED + inventoryName
													+ ChatColor.WHITE + li.getMessage("!"));
										} else {
											player.sendMessage(li.getMessage("playerhasgotthebiggest") + ChatColor.RED + inventoryName
													+ ChatColor.WHITE + li.getMessage("forhispermissions"));
										}
									} else {
										player.sendMessage(ChatColor.RED + li.getMessage("playernotfound"));
									}
								}
							}
						} else if (firstArgument.equalsIgnoreCase("clear")) {
							if (userHasPermission(player, "backpack.clear.other")) {
								if (inventories.containsKey(playerName)) {
									inventories.remove(playerName);
									player.sendMessage(li.getMessage("frenchonly") + playerName + li.getMessage("'s") + ChatColor.RED
											+ inventoryName + ChatColor.WHITE + li.getMessage("hasbeencleared"));
								} else {
									player.sendMessage(ChatColor.RED + li.getMessage("playernotfound"));
								}
							}
						} else if (firstArgument.equalsIgnoreCase("open")) {
							if (userHasPermission(player, "backpack.open.other")) {
								if (inventories.containsKey(playerName)) {
									if (!openedInventories.containsKey(playerName)) {
										CustomInventory inv = new CustomInventory(allowedSize(player.getWorld(), player, true),
												inventoryName);
										openedInventories.put(playerName, inv);
										openedInventoriesOthers.put(player.getName(), playerName);
										inv.setContents(inventories.get(playerName));
										((org.getspout.spoutapi.player.SpoutPlayer) player).openInventoryWindow((Inventory) inv);
									} else {
										player.sendMessage(li.getMessage("playerhasalreadyhis") + ChatColor.RED + inventoryName
												+ ChatColor.WHITE + li.getMessage("opened"));
									}
								} else {
									player.sendMessage(ChatColor.RED + li.getMessage("playernotfound"));
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

	private void startUpgradeProcedure(int sizeBefore, Player player, Player notificationsAndMoneyPlayer) {
		int sizeAfter = sizeBefore + 9;
		double cost = calculateCost(sizeBefore);
		if (method.hasAccount(notificationsAndMoneyPlayer.getName())) {
			MethodAccount account = method.getAccount(notificationsAndMoneyPlayer.getName());
			if (account != null) {
				if (account.hasEnough(cost)) {
					account.subtract(cost);
				} else {
					if (player.equals(notificationsAndMoneyPlayer)) {
						notificationsAndMoneyPlayer.sendMessage(li.getMessage("notenoughmoneyyour") + ChatColor.RED + inventoryName
								+ ChatColor.WHITE + ".");
					} else {
						notificationsAndMoneyPlayer.sendMessage(li.getMessage("notenoughmoneyplayer") + ChatColor.RED + inventoryName
								+ ChatColor.WHITE + ".");
					}
					return;
				}
			} else {
				notificationsAndMoneyPlayer.sendMessage(ChatColor.RED + li.getMessage("noaccount"));
				return;
			}
		}
		SBInventorySaveTask.saveInventory(player, player.getWorld());
		inventories.remove(player.getName());
		File saveFile;
		if (config.getBoolean("Backpack." + player.getWorld().getName() + ".InventoriesShare?", true)) {
			saveFile = new File(this.getDataFolder() + File.separator + "inventories", player.getName() + ".yml");
		} else {
			saveFile = new File(this.getDataFolder() + File.separator + "inventories", player.getName() + "_" + player.getWorld().getName()
					+ ".yml");
		}
		Configuration config = new Configuration(saveFile);
		config.load();
		config.setProperty("Size", sizeAfter);
		config.save();
		loadInventory(player, player.getWorld());
		notificationsAndMoneyPlayer.sendMessage(li.getMessage("your") + ChatColor.RED + inventoryName + ChatColor.WHITE
				+ li.getMessage("hasbeenupgraded"));
		notificationsAndMoneyPlayer.sendMessage(li.getMessage("ithasnow") + ChatColor.RED + sizeAfter + ChatColor.WHITE
				+ li.getMessage("slots"));
	}

	public boolean userHasPermission(Player player, String permission) {
		if (permissionHandler != null) {
			return permissionHandler.has(player.getWorld().getName(), player.getName(), permission);
		} else if (permissionsBukkitPlugin != null) {
			return player.hasPermission(permission);
		} else if (permissionsEx != null) {
			return permissionsEx.has(player, permission);
		} else if (groupManager != null) {
			return groupManager.getWorldsHolder().getWorldPermissions(player).has(player, permission);
		} else {
			return player.isOp();
		}
	}

	public void showHelp(Player player) {
		if (userHasPermission(player, "backpack.reload")) {
			player.sendMessage(li.getMessage("reloadcommand"));
		}
		player.sendMessage(li.getMessage("infocommand") + ChatColor.RED + inventoryName + ChatColor.WHITE + ".");
		if (allowedSize(player.getWorld(), player, true) < upgradeAllowedSize(player.getWorld(), player) && method != null
				&& (permissionHandler != null || permissionsBukkitPlugin != null) || permissionsEx != null || groupManager != null) {
			player.sendMessage(li.getMessage("upgradecommand") + ChatColor.RED + inventoryName + ChatColor.WHITE + ".");
		}
	}

	public double calculateCost(int size) {
		double cost = price54;
		if (size == 9) {
			cost = price18;
		} else if (size == 18) {
			cost = price27;
		} else if (size == 27) {
			cost = price36;
		} else if (size == 36) {
			cost = price45;
		} else if (size == 45) {
			cost = price54;
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

	public boolean isOpenBackpack(Player player) {
		return openedInventories.containsKey(player.getName());
	}

	public Inventory getOpenedBackpack(Player player) {
		return openedInventories.get(player.getName());
	}

	public Inventory getClosedBackpack(Player player) {
		CustomInventory inventory = new CustomInventory(allowedSize(player.getWorld(), player, true), inventoryName);
		if (inventories.containsKey(player.getName())) {
			inventory.setContents(inventories.get(player.getName()));
		}
		return inventory;
	}

	public void setClosedBackpack(Player player, Inventory inventory) {
		inventories.put(player.getName(), inventory.getContents());
		return;
	}

	public boolean hasWorkbench(Player player) {
		if (userHasPermission(player, "backpack.workbench"))
			return true;
		File saveFile;
		if (config.getBoolean("Backpack." + player.getWorld().getName() + ".InventoriesShare?", true)) {
			saveFile = new File(this.getDataFolder() + File.separator + "inventories", player.getName() + ".yml");
		} else {
			saveFile = new File(this.getDataFolder() + File.separator + "inventories", player.getName() + "_" + player.getWorld().getName()
					+ ".yml");
		}
		Configuration config = new Configuration(saveFile);
		config.load();
		boolean enabled = config.getBoolean("Workbench", false);
		config.save();
		if (enabled)
			return true;
		return false;
	}

	public void setWorkbench(Player player, boolean enabled) {
		File saveFile;
		if (config.getBoolean("Backpack." + player.getWorld().getName() + ".InventoriesShare?", true)) {
			saveFile = new File(this.getDataFolder() + File.separator + "inventories", player.getName() + ".yml");
		} else {
			saveFile = new File(this.getDataFolder() + File.separator + "inventories", player.getName() + "_" + player.getWorld().getName()
					+ ".yml");
		}
		Configuration config = new Configuration(saveFile);
		config.load();
		config.setProperty("Workbench", enabled);
		config.save();
	}

	@Override
	public void onDisable() {
		method = null;
		getServer().getScheduler().cancelTask(saveTaskId);
		logger.info(logTag + li.getMessage("savingallinventories"));
		SBInventorySaveTask.saveAll();
		logger.info(logTag + li.getMessage("isnowdisabled"));
	}
}