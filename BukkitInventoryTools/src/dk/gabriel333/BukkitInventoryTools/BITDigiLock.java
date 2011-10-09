package dk.gabriel333.BukkitInventoryTools;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import org.bukkit.material.Door;
import org.bukkit.material.Lever;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;

public class BITDigiLock {

	private BIT plugin;

	public BITDigiLock(Plugin plugin) {
		plugin = this.plugin;
	}

	protected SpoutBlock block;
	protected String pincode;
	protected String owner;
	protected int closetimer;
	protected String coOwners;
	protected String shared;
	protected int typeId;
	protected String connectedTo;
	protected int useCost;

	/**
	 * Constructs a new BITDigiLock
	 * 
	 */
	BITDigiLock(SpoutBlock block, String pincode, String owner, int closetimer,
			String coowners, String shared, int typeId, String connectedTo,
			int useCost) {
		this.block = block;
		this.pincode = pincode;
		this.owner = owner;
		this.closetimer = closetimer;
		this.coOwners = coowners;
		this.shared = shared;
		this.typeId = typeId;
		this.connectedTo = connectedTo;
		this.useCost = useCost;
	}

	/**
	 * Saves the DigiLock to the database.
	 * 
	 * @param sPlayer
	 *            the player who is interactions with the lock.
	 * @param block
	 *            The block of the DigiLock.
	 * @param pincode
	 *            of the DigiLock. "fingerprint" or "" or a 10 digits code.
	 * @param owner
	 *            is the owner of the DigiLock
	 * @param closetimer
	 *            is number of seconds before the door closes.
	 * @param coowners
	 *            is the list of co - owners of the DigiLock.
	 * @param shared
	 *            - not used.
	 * @param typeId
	 *            is the type of the block.
	 * @param connectedTo
	 *            - not used yet.
	 * @param useCost
	 *            is the cost to use the block.
	 */
	public static void SaveDigiLock(SpoutPlayer sPlayer, SpoutBlock block,
			String pincode, String owner, Integer closetimer, String coowners,
			String shared, int typeId, String connectedTo, int useCost) {
		String query;
		boolean createlock = true;
		boolean newLock = true;
		int cost = G333Config.DIGILOCK_COST;
		block = getDigiLockBlock(block);
		if (isLocked(block)) {
			newLock = false;
			query = "UPDATE " + BIT.digilockTable + " SET pincode='" + pincode
					+ "', owner='" + owner + "', closetimer=" + closetimer
					+ " , coowners='" + coowners + "', shared='" + shared
					+ "', typeid=" + typeId + ", connectedto='" + connectedTo
					+ "', usecost=" + useCost

					+ " WHERE x = " + block.getX() + " AND y = " + block.getY()
					+ " AND z = " + block.getZ() + " AND world='"
					+ block.getWorld().getName() + "';";
		} else {
			// NEW DIGILOCK
			query = "INSERT INTO " + BIT.digilockTable
					+ " (pincode, owner, closetimer, "
					+ "x, y, z, world, coowners, shared, "
					+ "typeid, connectedto, usecost) VALUES ('" + pincode
					+ "', '" + owner + "', " + closetimer + ", " + block.getX()
					+ ", " + block.getY() + ", " + block.getZ() + ", '"
					+ block.getWorld().getName() + "', '" + coowners + "', '"
					+ shared + "', " + block.getTypeId() + ", '" + connectedTo
					+ "', " + useCost + " );";
			if (BIT.useEconomy) {
				if (BIT.plugin.Method.hasAccount(sPlayer.getName()) && cost > 0) {
					if (BIT.plugin.Method.getAccount(sPlayer.getName())
							.hasEnough(cost)) {
						BIT.plugin.Method.getAccount(sPlayer.getName())
								.subtract(cost);
						sPlayer.sendMessage("Your account ("
								+ BIT.plugin.Method.getAccount(
										sPlayer.getName()).balance()
								+ ") has been deducted " + cost + " bucks");
					} else {
						sPlayer.sendMessage("You dont have enough money ("
								+ BIT.plugin.Method.getAccount(
										sPlayer.getName()).balance()
								+ "). Cost is:" + cost);
						createlock = false;
					}
				}
			}
		}
		if (createlock) {
			if (G333Config.config.DEBUG_SQL)
				sPlayer.sendMessage(ChatColor.YELLOW + "Updating lock: "
						+ query);
			if (G333Config.config.STORAGE_TYPE.equals("MYSQL")) {
				try {
					BIT.manageMySQL.insertQuery(query);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} else {
				BIT.manageSQLite.insertQuery(query);
			}
			if (newLock) {
				G333Messages.sendNotification(sPlayer, "DigiLock created.");
			} else {
				G333Messages.sendNotification(sPlayer, "DigiLock updated.");
			}
		} else {
			sPlayer.sendMessage("You dont have enough money. Cost is:" + cost);
		}
	}

	public static Boolean isLocked(SpoutBlock block) {
		block = getDigiLockBlock(block);
		String query = "SELECT * FROM " + BIT.digilockTable + " WHERE (x = "
				+ block.getX() + " AND y = " + block.getY() + " AND z = "
				+ block.getZ() + " AND world='" + block.getWorld().getName()
				+ "');";
		ResultSet result = null;
		if (G333Config.config.STORAGE_TYPE.equals("MYSQL")) {
			try {
				result = BIT.manageMySQL.sqlQuery(query);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else { // SQLLITE
			result = BIT.manageSQLite.sqlQuery(query);
		}
		try {
			if (result != null && result.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static SpoutBlock getDigiLockBlock(SpoutBlock block) {
		if (isDoor(block)) {
			if (isDoubleDoor(block)) {
				block = getLeftDoubleDoor(block);
			}
			Door door = (Door) block.getState().getData();
			if (door.isTopHalf()) {
				block = block.getRelative(BlockFace.DOWN);
			}
		} else if (isChest(block)) {
			SpoutChest sChest1 = (SpoutChest) block.getState();
			if (sChest1.isDoubleChest()) {
				SpoutChest sChest2 = sChest1.getOtherSide();
				SpoutBlock block2 = (SpoutBlock) sChest2.getBlock();
				if (sChest1.getX() == sChest2.getX()) {
					if (sChest1.getZ() < sChest2.getZ()) {
						return block;
					} else {
						return block2;
					}
				} else {
					if (sChest1.getX() < sChest2.getX()) {
						return block;
					} else {
						return block2;
					}
				}
			}
		}
		return block;
	}

	public boolean isCoowner(SpoutPlayer sPlayer) {
		if (coOwners.toLowerCase().contains(sPlayer.getName().toLowerCase())
				|| coOwners.toLowerCase().contains("everyone"))
			return true;
		return false;
	}

	public boolean isOwner(SpoutPlayer sPlayer) {
		if (owner.toLowerCase().equals(sPlayer.getName().toLowerCase()))
			return true;
		return false;
	}

	public void addCoowner(String name) {
		this.coOwners = coOwners.concat("," + name);
	}

	public boolean removeCoowner(String name) {
		if (coOwners.toLowerCase().contains(name.toLowerCase())) {
			this.coOwners = coOwners.replace(name, "");
			this.coOwners = coOwners.replace(",,", ",");
			return true;
		}
		return false;
	}

	private static final Material lockablematerials[] = { Material.CHEST,
			Material.LOCKED_CHEST, Material.IRON_DOOR,
			Material.IRON_DOOR_BLOCK, Material.WOODEN_DOOR, Material.WOOD_DOOR,
			Material.FURNACE, Material.DISPENSER, Material.LEVER,
			Material.STONE_BUTTON, Material.BOOKSHELF, Material.TRAP_DOOR,
			Material.SIGN, Material.SIGN_POST, Material.WALL_SIGN };

	// Material.STORAGE_MINECART, , Material.REDSTONE_WIRE

	// check if material is a lockable block
	static boolean isLockable(Block block) {
		for (Material i : lockablematerials) {
			if (i == block.getType())
				return true;
		}
		return false;
	}

	static boolean isLockable(Material material) {
		for (Material i : lockablematerials) {
			if (i == material)
				return true;
		}
		return false;
	}

	public String getPincode() {
		return pincode;
	}

	public String getShared() {
		return shared;
	}

	public String getOwner() {
		return owner;
	}

	public int getClosetimer() {
		return closetimer;
	}

	public String getCoOwners() {
		return coOwners;
	}

	public SpoutBlock getBlock() {
		return block;
	}

	public int getUseCost() {
		return useCost;
	}

	public String getConnectedTo() {
		return connectedTo;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public void setBlock(SpoutBlock block) {
		this.block = block;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setClosetimer(int closetimer) {
		this.closetimer = closetimer;
	}

	public void setCoowners(String coowners) {
		this.coOwners = coowners;
	}

	public void setShared(String shared) {
		this.shared = shared;
	}

	public void setUseCost(int useCost) {
		this.useCost = useCost;
	}

	public void setConnectedTo(String connectedTo) {
		this.connectedTo = connectedTo;
	}

	public void setDigiLock(SpoutBlock block, String pincode, String owner,
			int closetimer, String coowners, String shared, String connectedTo,
			int useCost) {
		this.block = block;
		this.pincode = pincode;
		this.owner = owner;
		this.closetimer = closetimer;
		this.coOwners = coowners;
		this.shared = shared;
		this.typeId = block.getTypeId();
		this.connectedTo = connectedTo;
		this.useCost = useCost;
	}

	public static BITDigiLock loadDigiLock(SpoutBlock block) {
		block = getDigiLockBlock(block);
		String query = "SELECT * FROM " + BIT.digilockTable + " WHERE (x = "
				+ block.getX() + " AND y = " + block.getY() + " AND z = "
				+ block.getZ() + " AND world='" + block.getWorld().getName()
				+ "');";
		ResultSet result = null;
		if (G333Config.config.STORAGE_TYPE.equals("MYSQL")) {
			try {
				result = BIT.manageMySQL.sqlQuery(query);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else { // SQLLITE
			result = BIT.manageSQLite.sqlQuery(query);
		}

		try {
			if (result != null && result.next()) {
				String pincode = result.getString("pincode");
				String owner = result.getString("owner");
				int closetimer = result.getInt("closetimer");
				String coowners = result.getString("coowners");
				String shared = result.getString("shared");
				int typeId = result.getInt("typeId");
				String connectedTo = result.getString("connectedto");
				int useCost = result.getInt("usecost");
				BITDigiLock digilock = new BITDigiLock(block, pincode, owner,
						closetimer, coowners, shared, typeId, connectedTo,
						useCost);
				return digilock;
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void RemoveDigiLock(SpoutPlayer sPlayer) {
		boolean deletelock = true;
		if (BIT.useEconomy) {
			if (BIT.plugin.Method.hasAccount(sPlayer.getName())) {
				if (BIT.plugin.Method.getAccount(sPlayer.getName()).hasEnough(
						G333Config.DIGILOCK_DESTROYCOST)
						|| G333Config.DIGILOCK_DESTROYCOST < 0) {
					BIT.plugin.Method.getAccount(sPlayer.getName()).subtract(
							G333Config.DIGILOCK_DESTROYCOST);
					sPlayer.sendMessage("Your account ("
							+ BIT.plugin.Method.getAccount(sPlayer.getName())
									.balance() + ") has been deducted "
							+ G333Config.DIGILOCK_DESTROYCOST + " bucks");
				} else {
					sPlayer.sendMessage("You dont have enough money ("
							+ BIT.plugin.Method.getAccount(sPlayer.getName())
									.balance() + "). Cost is:"
							+ G333Config.DIGILOCK_DESTROYCOST);
					deletelock = false;
				}
			}
		}
		String query = "DELETE FROM " + BIT.digilockTable + " WHERE (x = "
				+ block.getX() + " AND y = " + block.getY() + " AND z = "
				+ block.getZ() + " AND world='" + block.getWorld().getName()
				+ "');";
		if (deletelock) {
			if (G333Config.config.DEBUG_SQL)
				sPlayer.sendMessage(ChatColor.YELLOW + "Removeing lock: "
						+ query);
			if (G333Config.config.STORAGE_TYPE.equals("MYSQL")) {
				try {
					BIT.manageMySQL.deleteQuery(query);
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} else { // SQLLITE
				BIT.manageSQLite.deleteQuery(query);
			}
			G333Messages.sendNotification(sPlayer, "DigiLock removed.");
		} else {
			G333Messages.sendNotification(sPlayer, "You need more money ("
					+ G333Config.DIGILOCK_DESTROYCOST + ")");
		}

	}

	public static boolean isChest(Block block) {
		if (block.getType().equals(Material.CHEST))
			return true;
		else if (block.getType().equals(Material.LOCKED_CHEST))
			return true;
		return false;
	}

	public static boolean isSign(Block block) {
		if (block.getType().equals(Material.SIGN))
			return true;
		else if (block.getType().equals(Material.SIGN_POST))
			return true;
		return false;
	}

	public static void playDigiLockSound(SpoutBlock sBlock) {
		SpoutManager
				.getSoundManager()
				.playGlobalCustomSoundEffect(
						BIT.plugin,
						"http://dl.dropbox.com/u/36067670/BukkitInventoryTools/Sounds/Digilock.wav",
						true, sBlock.getLocation(), 5);
	}

	// *******************************************************
	//
	// LEVERS
	//
	// *******************************************************
	public static boolean isLever(SpoutBlock sBlock) {
		if (sBlock.getType().equals(Material.LEVER)) {
			return true;
		} else {
			return false;
		}
	}

	public static void leverOn(SpoutPlayer sPlayer, SpoutBlock block, int cost) {
		boolean setleveron = true;
		if (BIT.useEconomy && cost > 0) {
			if (BIT.plugin.Method.hasAccount(sPlayer.getName())) {
				if (BIT.plugin.Method.getAccount(sPlayer.getName()).hasEnough(
						cost)) {
					BIT.plugin.Method.getAccount(sPlayer.getName()).subtract(
							cost);
					sPlayer.sendMessage("Your account ("
							+ BIT.plugin.Method.getAccount(sPlayer.getName())
									.balance() + ") has been deducted " + cost
							+ " bucks");
				} else {
					sPlayer.sendMessage("You dont have enough money ("
							+ BIT.plugin.Method.getAccount(sPlayer.getName())
									.balance() + "). Cost is:" + cost);
					setleveron = false;
				}
			}
		}
		if (setleveron) {
			BITDigiLock digilock = loadDigiLock(block);
			playDigiLockSound(block);
			Lever lever = (Lever) block.getState().getData();
			lever.setPowered(true);
			// block.setData((byte) (block.getState().getData().getData() | 8));
			if (digilock.getClosetimer() > 0) {
				scheduleLeverOff(sPlayer, block, digilock.getClosetimer());
			}
		}
	}

	public static void leverOff(SpoutPlayer sPlayer, SpoutBlock block) {
		playDigiLockSound(block);
		Lever lever = (Lever) block.getState().getData();
		lever.setPowered(false);
		// block.setData((byte) ((block.getState().getData().getData() | 8) ^
		// 8));
	}

	//
	public static int scheduleLeverOff(final SpoutPlayer sPlayer,
			final SpoutBlock sBlock, final int closetimer) {
		int fs = closetimer * 20;
		// 20 ticks / second
		int taskID = BIT.plugin.getServer().getScheduler()
				.scheduleSyncDelayedTask(BIT.plugin, new Runnable() {
					public void run() {
						SpoutBlock sb = sBlock;
						SpoutPlayer sp = sPlayer;
						Lever lever = (Lever) sb.getState().getData();
						if (G333Config.config.DEBUG_DOOR)
							sp.sendMessage("Turning lever off in " + closetimer
									+ " seconds");
						lever.setPowered(false);
					}
				}, fs);
		return taskID;
	}

	// *******************************************************
	//
	// DOORS
	//
	// *******************************************************

	public static boolean isDoor(Block block) {
		if (block.getType().equals(Material.WOOD_DOOR))
			return true;
		else if (block.getType().equals(Material.WOODEN_DOOR))
			return true;
		else if (block.getType().equals(Material.IRON_DOOR))
			return true;
		else if (block.getType().equals(Material.IRON_DOOR_BLOCK))
			return true;
		return false;
	}

	public static boolean isDoorOpen(SpoutBlock block) {
		if ((block.getState().getData().getData() & 4) == 4) {
			return true;
		} else {
			return false;
		}
	}

	public static void openDoor(SpoutPlayer sPlayer, SpoutBlock block, int cost) {
		boolean opendoor = true;
		BITDigiLock digilock = loadDigiLock(block);
		if (BIT.useEconomy && cost > 0) {
			if (BIT.plugin.Method.hasAccount(sPlayer.getName())) {
				if (BIT.plugin.Method.getAccount(sPlayer.getName()).hasEnough(
						cost)) {
					BIT.plugin.Method.getAccount(sPlayer.getName()).subtract(
							cost);
					sPlayer.sendMessage("Your account ("
							+ BIT.plugin.Method.getAccount(sPlayer.getName())
									.balance() + ") has been deducted " + cost
							+ " bucks");
				} else {
					sPlayer.sendMessage("You dont have enough money ("
							+ BIT.plugin.Method.getAccount(sPlayer.getName())
									.balance() + "). Cost is:" + cost);
					opendoor = false;
				}
			}
		}
		Door door = (Door) block.getState().getData();
		SpoutBlock nextBlock;
		if (opendoor) {
			if (!isDoorOpen(block)) {
				playDigiLockSound(block);
				block.setData((byte) (block.getState().getData().getData() | 4));
				if (door.isTopHalf()) {
					nextBlock = block.getRelative(BlockFace.DOWN);
					nextBlock.setData((byte) (nextBlock.getState().getData()
							.getData() | 4));
				} else {
					nextBlock = block.getRelative(BlockFace.UP);
					nextBlock.setData((byte) (nextBlock.getState().getData()
							.getData() | 4));
				}
				if (digilock.getClosetimer() > 0 && !isDoubleDoor(block)) {
					scheduleCloseDoor(sPlayer, block, digilock.getClosetimer(),
							0);
				}
			}
		}
	}

	public static void openDoor(SpoutBlock block) {
		// BITDigiLock digilock = loadDigiLock(block);
		Door door = (Door) block.getState().getData();
		SpoutBlock nextBlock;
		if (!isDoorOpen(block)) {
			playDigiLockSound(block);
			block.setData((byte) (block.getState().getData().getData() | 4));
			if (door.isTopHalf()) {
				nextBlock = block.getRelative(BlockFace.DOWN);
				nextBlock.setData((byte) (nextBlock.getState().getData()
						.getData() | 4));
			} else {
				nextBlock = block.getRelative(BlockFace.UP);
				nextBlock.setData((byte) (nextBlock.getState().getData()
						.getData() | 4));
			}
			// if (digilock.getClosetimer() > 0 && !isDoubleDoor(block)) {
			// scheduleCloseDoor(sPlayer, block, digilock.getClosetimer(),
			// 0);
			// }
		}

	}

	public static void closeDoor(SpoutPlayer sPlayer, SpoutBlock block, int cost) {
		boolean closedoor = true;
		if (BIT.useEconomy && cost > 0) {
			if (BIT.plugin.Method.hasAccount(sPlayer.getName())) {
				if (BIT.plugin.Method.getAccount(sPlayer.getName()).hasEnough(
						cost)) {
					BIT.plugin.Method.getAccount(sPlayer.getName()).subtract(
							cost);
					sPlayer.sendMessage("Your account ("
							+ BIT.plugin.Method.getAccount(sPlayer.getName())
									.balance() + ") has been deducted " + cost
							+ " bucks");
				} else {
					sPlayer.sendMessage("You dont have enough money ("
							+ BIT.plugin.Method.getAccount(sPlayer.getName())
									.balance() + "). Cost is:" + cost);
					closedoor = false;
				}
			}
		}
		if (closedoor) {
			if (isDoorOpen(block)) {
				playDigiLockSound(block);
				Door door = (Door) block.getState().getData();
				SpoutBlock nextBlock;
				block.setData((byte) ((block.getState().getData().getData() | 4) ^ 4));
				if (door.isTopHalf()) {
					nextBlock = block.getRelative(BlockFace.DOWN);
					nextBlock.setData((byte) ((nextBlock.getState().getData()
							.getData() | 4) ^ 4));
				} else {
					nextBlock = block.getRelative(BlockFace.UP);
					nextBlock.setData((byte) ((nextBlock.getState().getData()
							.getData() | 4) ^ 4));
				}
			}
		}
	}

	public static void closeDoor(SpoutBlock block) {
		if (isDoorOpen(block)) {
			playDigiLockSound(block);
			Door door = (Door) block.getState().getData();
			SpoutBlock nextBlock;
			block.setData((byte) ((block.getState().getData().getData() | 4) ^ 4));
			if (door.isTopHalf()) {
				nextBlock = block.getRelative(BlockFace.DOWN);
				nextBlock.setData((byte) ((nextBlock.getState().getData()
						.getData() | 4) ^ 4));
			} else {
				nextBlock = block.getRelative(BlockFace.UP);
				nextBlock.setData((byte) ((nextBlock.getState().getData()
						.getData() | 4) ^ 4));
			}
		}
	}

	public static void toggleDoor(SpoutBlock block) {
		playDigiLockSound(block);
		Door door = (Door) block.getState().getData();
		SpoutBlock nextBlock;
		block.setData((byte) (block.getState().getData().getData() ^ 4));
		if (door.isTopHalf()) {
			nextBlock = block.getRelative(BlockFace.DOWN);
			nextBlock
					.setData((byte) (nextBlock.getState().getData().getData() ^ 4));
		} else {
			nextBlock = block.getRelative(BlockFace.UP);
			nextBlock
					.setData((byte) (nextBlock.getState().getData().getData() ^ 4));
		}
	}

	public static int scheduleCloseDoor(final SpoutPlayer sPlayer,
			final SpoutBlock sBlock, final int closetimer, final int cost) {
		int fs = closetimer * 20;
		// 20 ticks / second
		int taskID = BIT.plugin.getServer().getScheduler()
				.scheduleSyncDelayedTask(BIT.plugin, new Runnable() {
					public void run() {
						SpoutBlock sb = sBlock;
						SpoutPlayer sp = sPlayer;
						int c = cost;
						if (G333Config.config.DEBUG_DOOR)
							sp.sendMessage("Autoclosing the door in "
									+ closetimer + " seconds");
						if (isDoor(sb) && !isDoubleDoor(sb)) {
							if (isDoorOpen(sb))
								closeDoor(sp, sb, c);
						}
					}
				}, fs);
		return taskID;
	}

	// *******************************************************
	//
	// TRAPDOORS
	//
	// *******************************************************

	public static boolean isTrapdoor(Block block) {
		if (block.getType().equals(Material.TRAP_DOOR)) {
			return true;
		}
		return false;
	}

	public static boolean isTrapdoorOpen(SpoutPlayer sPlayer, SpoutBlock block) {
		if ((block.getState().getData().getData() & 4) == 4) {
			return true;
		} else {
			return false;
		}
	}

	public static void openTrapdoor(SpoutPlayer sPlayer, SpoutBlock sBlock,
			int cost) {
		boolean opentrapdoor = true;
		BITDigiLock digilock = loadDigiLock(sBlock);
		if (BIT.useEconomy && cost > 0) {
			if (BIT.plugin.Method.hasAccount(sPlayer.getName())) {
				if (BIT.plugin.Method.getAccount(sPlayer.getName()).hasEnough(
						cost)) {
					BIT.plugin.Method.getAccount(sPlayer.getName()).subtract(
							cost);
					sPlayer.sendMessage("Your account ("
							+ BIT.plugin.Method.getAccount(sPlayer.getName())
									.balance() + ") has been deducted " + cost
							+ " bucks");
				} else {
					sPlayer.sendMessage("You dont have enough money ("
							+ BIT.plugin.Method.getAccount(sPlayer.getName())
									.balance() + "). Cost is:" + cost);
					opentrapdoor = false;
				}
			}
		}
		if (opentrapdoor) {
			if (!isTrapdoorOpen(sPlayer, sBlock)) {
				playDigiLockSound(sBlock);
				sBlock.setData((byte) (sBlock.getState().getData().getData() | 4));
				if (digilock.getClosetimer() > 0) {
					scheduleCloseTrapdoor(sPlayer, sBlock,
							digilock.getClosetimer());
				}
			}
		}
	}

	public static void closeTrapdoor(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		sBlock.setData((byte) ((sBlock.getState().getData().getData() | 4) ^ 4));
		if (G333Config.config.DEBUG_DOOR)
			sPlayer.sendMessage("Close the door.");
	}

	public static void toggleTrapdoor(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		sBlock.setData((byte) (sBlock.getState().getData().getData() ^ 4));
	}

	public static int scheduleCloseTrapdoor(final SpoutPlayer sPlayer,
			final SpoutBlock sBlock, final int closetimer) {
		int fs = closetimer * 20;
		// 20 ticks / second
		int taskID = BIT.plugin.getServer().getScheduler()
				.scheduleSyncDelayedTask(BIT.plugin, new Runnable() {
					public void run() {
						SpoutBlock sb = sBlock;
						SpoutPlayer sp = sPlayer;
						if (G333Config.config.DEBUG_DOOR)
							sp.sendMessage("Autoclosing the trapdoor in "
									+ closetimer + " seconds");
						if (sBlock.getType() == Material.TRAP_DOOR) {
							if (isTrapdoorOpen(sp, sb))
								closeTrapdoor(sp, sb);
						}
					}
				}, fs);
		return taskID;
	}

	// *******************************************************
	//
	// DOUBLEDOORS
	//
	// *******************************************************
	public static boolean isDoubleDoor(SpoutBlock block) {
		if (isDoor(block)) {
			if (isDoor(block.getFace(BlockFace.EAST))
					|| isDoor(block.getFace(BlockFace.NORTH))
					|| isDoor(block.getFace(BlockFace.SOUTH))
					|| isDoor(block.getFace(BlockFace.WEST))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isDoubleDoorOpen(SpoutBlock block) {
		return (isDoorOpen(getLeftDoubleDoor(block)) || !isDoorOpen(getRightDoubleDoor(block)));
	}

	public static void closeDoubleDoor(SpoutPlayer sPlayer, SpoutBlock sBlock,
			int cost) {
		if (isDoubleDoor(sBlock)) {
			if (isLeftDoubleDoor(sBlock)) {
				closeDoor(sPlayer, sBlock, 0);
				openDoor(sPlayer, getRightDoubleDoor(sBlock), 0);
			} else {
				openDoor(sPlayer, sBlock, cost);
				closeDoor(sPlayer, getLeftDoubleDoor(sBlock), cost);
			}
		}
	}

	public static void openDoubleDoor(SpoutPlayer sPlayer, SpoutBlock sBlock,
			int cost) {
		if (isDoubleDoor(sBlock)) {
			if (isLeftDoubleDoor(sBlock)) {
				openDoor(sPlayer, sBlock, cost);
				closeDoor(sPlayer, getRightDoubleDoor(sBlock), 0);
			} else {
				closeDoor(sPlayer, sBlock, 0);
				openDoor(sPlayer, getLeftDoubleDoor(sBlock), cost);
			}
		}
		BITDigiLock digilock = loadDigiLock(sBlock);
		if (digilock.getClosetimer() > 0) {
			scheduleCloseDoubleDoor(sPlayer, sBlock, digilock.getClosetimer(),
					0);
		}
	}

	public static boolean isLeftDoubleDoor(SpoutBlock block) {
		if (isDoubleDoor(block)) {
			Door door = (Door) block.getState().getData();
			// left door:NORTH,NORTH_EAST Right door:WEST,NORTH_WEST
			// left door:WEST,NORTH_WEST Right door:SOUTH,SOUTH_WEST
			// left door:SOUTH,SOUTH_WEST Right door:EAST,SOUTH_EAST
			// left door:EAST,SOUTH_EAST Right door:NORTH,NORTH_EAST
			if (door.getFacing() == BlockFace.NORTH
					&& door.getHingeCorner() == BlockFace.NORTH_EAST) {
				if (isDoor(block.getRelative(BlockFace.WEST))) {
					return true;
				} else {
					return false;
				}
			} else if (door.getFacing() == BlockFace.WEST
					&& door.getHingeCorner() == BlockFace.NORTH_WEST) {
				if (isDoor(block.getRelative(BlockFace.SOUTH))) {
					return true;
				} else {
					return false;
				}
			} else if (door.getFacing() == BlockFace.EAST
					&& door.getHingeCorner() == BlockFace.SOUTH_EAST) {
				if (isDoor(block.getRelative(BlockFace.NORTH))) {
					return true;
				} else {
					return false;
				}
			} else if (door.getFacing() == BlockFace.SOUTH
					&& door.getHingeCorner() == BlockFace.SOUTH_WEST) {
				if (isDoor(block.getRelative(BlockFace.EAST))) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	public static SpoutBlock getLeftDoubleDoor(SpoutBlock block) {
		if (isLeftDoubleDoor(block)) {
			return block;
		} else {
			Door door = (Door) block.getState().getData();
			// left door:NORTH,NORTH_EAST Right door:WEST,NORTH_WEST
			// left door:WEST,NORTH_WEST Right door:SOUTH,SOUTH_WEST
			// left door:EAST,SOUTH_EAST Right door:NORTH,NORTH_EAST
			// left door:SOUTH,SOUTH_WEST Right door:EAST,SOUTH_EAST
			if (door.getFacing() == BlockFace.NORTH
					&& door.getHingeCorner() == BlockFace.NORTH_EAST) {
				return block.getRelative(BlockFace.SOUTH);
			} else if (door.getFacing() == BlockFace.WEST
					&& door.getHingeCorner() == BlockFace.NORTH_WEST) {
				return block.getRelative(BlockFace.EAST);
			} else if (door.getFacing() == BlockFace.EAST
					&& door.getHingeCorner() == BlockFace.SOUTH_EAST) {
				return block.getRelative(BlockFace.WEST);
			} else {
				// if (door.getFacing() == BlockFace.SOUTH
				// && door.getHingeCorner() == BlockFace.SOUTH_WEST) {
				return block.getRelative(BlockFace.NORTH);
			}
		}
	}

	public static boolean isRightDoubleDoor(SpoutBlock block) {
		if (isDoubleDoor(block)) {
			if (isLeftDoubleDoor(block)) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public static SpoutBlock getRightDoubleDoor(SpoutBlock block) {
		if (isRightDoubleDoor(block)) {
			return block;
		} else {
			Door door = (Door) block.getState().getData();
			// left door:NORTH,NORTH_EAST Right door:WEST,NORTH_WEST
			// left door:WEST,NORTH_WEST Right door:SOUTH,SOUTH_WEST
			// left door:EAST,SOUTH_EAST Right door:NORTH,NORTH_EAST
			// left door:SOUTH,SOUTH_WEST Right door:EAST,SOUTH_EAST
			if (door.getFacing() == BlockFace.NORTH
					&& door.getHingeCorner() == BlockFace.NORTH_EAST) {
				return block.getRelative(BlockFace.WEST);
			} else if (door.getFacing() == BlockFace.WEST
					&& door.getHingeCorner() == BlockFace.NORTH_WEST) {
				return block.getRelative(BlockFace.SOUTH);
			} else if (door.getFacing() == BlockFace.EAST
					&& door.getHingeCorner() == BlockFace.SOUTH_EAST) {
				return block.getRelative(BlockFace.NORTH);
			} else {
				// (door.getFacing() == BlockFace.SOUTH
				// && door.getHingeCorner() == BlockFace.SOUTH_WEST) {
				return block.getRelative(BlockFace.EAST);
			}
		}
	}

	public static int scheduleCloseDoubleDoor(final SpoutPlayer sPlayer,
			final SpoutBlock sBlock, final int closetimer, final int cost) {
		int fs = closetimer * 20;
		// 20 ticks / second
		int taskID = BIT.plugin.getServer().getScheduler()
				.scheduleSyncDelayedTask(BIT.plugin, new Runnable() {
					public void run() {
						SpoutBlock sb = sBlock;
						SpoutPlayer sp = sPlayer;
						int c = cost;
						if (G333Config.config.DEBUG_DOOR)
							sp.sendMessage("Autoclosing the DoubleDoor in "
									+ closetimer + " seconds");
						if (isDoubleDoor(sBlock)) {
							if (isDoubleDoorOpen(sb))
								closeDoubleDoor(sp, sb, c);
						}
					}
				}, fs);
		return taskID;
	}

}
