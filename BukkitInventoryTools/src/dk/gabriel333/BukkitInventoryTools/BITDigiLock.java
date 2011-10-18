package dk.gabriel333.BukkitInventoryTools;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Dispenser;

import org.bukkit.material.Button;
import org.bukkit.material.Door;
import org.bukkit.material.Lever;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;

public class BITDigiLock {

	private BIT plugin;

	public BITDigiLock(Plugin plugin) {
		plugin = this.plugin;
	}

	protected SpoutBlock sBlock;
	protected String pincode;
	protected String owner;
	protected int closetimer;
	protected String coOwners;
	protected int typeId;
	protected String connectedTo;
	protected int useCost;

	/**
	 * Constructs a new BITDigiLock
	 * 
	 * @param block
	 * @param pincode
	 * @param owner
	 * @param closetimer
	 * @param coowners
	 * @param typeId
	 * @param connectedTo
	 * @param useCost
	 */
	BITDigiLock(SpoutBlock block, String pincode, String owner, int closetimer,
			String coowners, int typeId, String connectedTo, int useCost) {
		this.sBlock = block;
		this.pincode = pincode;
		this.owner = owner;
		this.closetimer = closetimer;
		this.coOwners = coowners;
		this.typeId = typeId;
		this.connectedTo = connectedTo;
		this.useCost = useCost;
	}
	
	//public static Map<Integer, Boolean> isLocked = new HashMap<Integer, Boolean>();


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
	 * @param typeId
	 *            is the type of the block.
	 * @param connectedTo
	 *            - not used yet.
	 * @param useCost
	 *            is the cost to use the block.
	 */
	public static void SaveDigiLock(SpoutPlayer sPlayer, SpoutBlock block,
			String pincode, String owner, Integer closetimer, String coowners,
			int typeId, String connectedTo, int useCost) {
		String query;
		boolean createlock = true;
		boolean newLock = true;
		int cost = G333Config.DIGILOCK_COST;
		block = getDigiLockBlock(block);
		if (isLocked(block)) {
			newLock = false;
			query = "UPDATE " + BIT.digilockTable + " SET pincode='" + pincode
					+ "', owner='" + owner + "', closetimer=" + closetimer
					+ " , coowners='" + coowners + "', typeid=" + typeId
					+ ", connectedto='" + connectedTo + "', usecost=" + useCost

					+ " WHERE x = " + block.getX() + " AND y = " + block.getY()
					+ " AND z = " + block.getZ() + " AND world='"
					+ block.getWorld().getName() + "';";
		} else {
			// NEW DIGILOCK
			query = "INSERT INTO " + BIT.digilockTable
					+ " (pincode, owner, closetimer, "
					+ "x, y, z, world, coowners, "
					+ "typeid, connectedto, usecost) VALUES ('" + pincode
					+ "', '" + owner + "', " + closetimer + ", " + block.getX()
					+ ", " + block.getY() + ", " + block.getZ() + ", '"
					+ block.getWorld().getName() + "', '" + coowners + "', "
					+ block.getTypeId() + ", '" + connectedTo + "', " + useCost
					+ " );";
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
			if (G333Config.DEBUG_SQL)
				sPlayer.sendMessage(ChatColor.YELLOW + "Updating lock: "
						+ query);
			if (G333Config.STORAGE_TYPE.equals("MYSQL")) {
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
		// TODO: Implement a HASHMAP for testing if the block is locked.
		// G333Messages.showInfo("isLocked was called");
		if (isLockable(block)) {
			block = getDigiLockBlock(block);
			String query = "SELECT * FROM " + BIT.digilockTable
					+ " WHERE (x = " + block.getX() + " AND y = "
					+ block.getY() + " AND z = " + block.getZ()
					+ " AND world='" + block.getWorld().getName() + "');";
			ResultSet result = null;
			if (G333Config.STORAGE_TYPE.equals("MYSQL")) {
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
		}
		return false;
	}

	/**
	 * Method to find the the next block which is connected to a lever or a
	 * stonebutton.
	 * 
	 * @param sBlock
	 *            SpoutBlock
	 * @return SpoutBlock
	 */
	public static SpoutBlock getDigiLockBlock(SpoutBlock sBlock) {
		if (isDoor(sBlock)) {
			if (isDoubleDoor(sBlock)) {
				sBlock = getLeftDoubleDoor(sBlock);
			}
			Door door = (Door) sBlock.getState().getData();
			if (door.isTopHalf()) {
				sBlock = sBlock.getRelative(BlockFace.DOWN);
			}
		} else if (isChest(sBlock)) {
			SpoutChest sChest1 = (SpoutChest) sBlock.getState();
			if (sChest1.isDoubleChest()) {
				SpoutChest sChest2 = sChest1.getOtherSide();
				SpoutBlock sBlock2 = (SpoutBlock) sChest2.getBlock();
				if (sChest1.getX() == sChest2.getX()) {
					if (sChest1.getZ() < sChest2.getZ()) {
						return sBlock;
					} else {
						return sBlock2;
					}
				} else {
					if (sChest1.getX() < sChest2.getX()) {
						return sBlock;
					} else {
						return sBlock2;
					}
				}
			}
		}
		return sBlock;
	}

	/**
	 * Checks if sPlayer is co Owner of the DigiLock.
	 * 
	 * @param sPlayer
	 * @return true or false
	 */
	public boolean isCoowner(SpoutPlayer sPlayer) {
		if (coOwners.toLowerCase().contains(sPlayer.getName().toLowerCase())
				|| coOwners.toLowerCase().contains("everyone"))
			return true;
		return false;
	}

	/**
	 * Checks if sPlayer is Owner of the DigiLock.
	 * 
	 * @param sPlayer
	 * @return true or false
	 */
	public boolean isOwner(SpoutPlayer sPlayer) {
		if (owner.toLowerCase().equals(sPlayer.getName().toLowerCase()))
			return true;
		return false;
	}

	/**
	 * Checks if sPlayer is Owner of the DigiLock placed on sBlock.
	 * 
	 * @param sPlayer
	 * @return true or false
	 */
	public static boolean isOwner(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		if (BITDigiLock.loadDigiLock(sBlock).getOwner().toLowerCase()
				.equals(sPlayer.getName().toLowerCase()))
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

	// check if material is a lockable block
	/**
	 * Check if the Block is made of a lockable material.
	 * 
	 * @param block
	 * @return true or false
	 */
	public static boolean isLockable(Block block) {
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
		return sBlock;
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
		this.sBlock = block;
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

	public void setUseCost(int useCost) {
		this.useCost = useCost;
	}

	public void setConnectedTo(String connectedTo) {
		this.connectedTo = connectedTo;
	}

	public void setDigiLock(SpoutBlock block, String pincode, String owner,
			int closetimer, String coowners, String connectedTo, int useCost) {
		this.sBlock = block;
		this.pincode = pincode;
		this.owner = owner;
		this.closetimer = closetimer;
		this.coOwners = coowners;
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
		if (G333Config.STORAGE_TYPE.equals("MYSQL")) {
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
				int typeId = result.getInt("typeId");
				String connectedTo = result.getString("connectedto");
				int useCost = result.getInt("usecost");
				BITDigiLock digilock = new BITDigiLock(block, pincode, owner,
						closetimer, coowners, typeId, connectedTo, useCost);
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
				+ sBlock.getX() + " AND y = " + sBlock.getY() + " AND z = "
				+ sBlock.getZ() + " AND world='" + sBlock.getWorld().getName()
				+ "');";
		if (deletelock) {
			if (G333Config.DEBUG_SQL)
				sPlayer.sendMessage(ChatColor.YELLOW + "Removeing lock: "
						+ query);
			if (G333Config.STORAGE_TYPE.equals("MYSQL")) {
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
		if (block.getType().equals(Material.CHEST)
				|| block.getType().equals(Material.LOCKED_CHEST))
			return true;
		else
			return false;
	}

	public static boolean isSign(Block block) {
		if (block.getType().equals(Material.SIGN)
				|| block.getType().equals(Material.WALL_SIGN)
				|| block.getType().equals(Material.SIGN_POST))
			return true;
		else
			return false;
	}

	public static boolean isBookshelf(SpoutBlock sBlock) {
		if (sBlock.getType().equals(Material.BOOKSHELF)) {
			return true;
		} else {
			return false;
		}
	}

	public static void playDigiLockSound(SpoutBlock sBlock) {
		SpoutManager
				.getSoundManager()
				.playGlobalCustomSoundEffect(
						BIT.plugin,
						"http://dl.dropbox.com/u/36067670/BukkitInventoryTools/Sounds/Digilock.wav",
						true, sBlock.getLocation(), 5);
	}

	public static boolean isNeighbourLocked(SpoutBlock block) {
		for (BlockFace bf : BlockFace.values()) {
			if (isLocked(block.getRelative(bf))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isNeighbourSameOwner(SpoutBlock block, String owner) {
		for (BlockFace bf : BlockFace.values()) {
			if (isLocked(block.getRelative(bf))) {
				BITDigiLock digilock = BITDigiLock.loadDigiLock(block
						.getFace(bf));
				if (digilock.getOwner().equalsIgnoreCase(owner)) {
					return true;
				}
			}
		}
		return false;
	}

	public SpoutBlock getNextLockedBlock(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		for (int i = -1; i < 1 + 1; i++) {
			for (int j = -1; j < +1; j++) {
				for (int k = -1; k < +1; k++) {
					if (!(i == 0 && j == 0 && k == 0)) {
						SpoutBlock sb = sBlock.getRelative(i, j, k);
						if (BITDigiLock.isLocked(sb)
								&& (BITDigiLock.isDoubleDoor(sb)
										|| BITDigiLock.isDoor(sb)
										|| BITDigiLock.isTrapdoor(sb) || BITDigiLock
											.isDispenser(sb))) {
							return sb;
						}
					}
				}
			}
		}
		return null;
	}

	// *******************************************************
	//
	// STONE_BUTTON
	//
	// *******************************************************
	/**
	 * Check if sBlock is a STONE_BUTTON
	 * 
	 * @param sBlock
	 * @return true or false
	 */
	public static boolean isButton(SpoutBlock sBlock) {
		if (sBlock.getType().equals(Material.STONE_BUTTON)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check if the STONE_BUTTON is on.
	 * 
	 * @param block
	 * @return
	 */
	public static boolean isButtonOn(SpoutBlock block) {
		Button button = (Button) block.getState().getData();
		return button.isPowered();
	}

	/**
	 * Handle the actions when a player presses the STONE_BUTTON
	 * 
	 * @param sPlayer SpoutPlayer
	 * @param sBlock SpoutBlock
	 * @param cost the cost the player is charged when the button is pressed.
	 */
	public static void pressButtonOn(SpoutPlayer sPlayer, SpoutBlock sBlock,
			int cost) {
		boolean doTheWork = false;
		BITDigiLock digilock = BITDigiLock.loadDigiLock(sBlock);
		SpoutBlock nextBlock = digilock.getNextLockedBlock(sPlayer, sBlock);
		if (nextBlock != null) {
			BITDigiLock nextDigilock = BITDigiLock.loadDigiLock(nextBlock);
			if (digilock.getOwner().equalsIgnoreCase(nextDigilock.getOwner())) {
				doTheWork = true;
			} else {
				sPlayer.sendMessage("You are not the owner of the "
						+ nextBlock.getType());
			}
			boolean pressButton = true;
			if (BIT.useEconomy && cost > 0 && doTheWork
					&& !isOwner(sPlayer, sBlock)) {
				if (BIT.plugin.Method.hasAccount(sPlayer.getName())) {
					if (BIT.plugin.Method.getAccount(sPlayer.getName())
							.hasEnough(cost)) {
						BIT.plugin.Method.getAccount(sPlayer.getName())
								.subtract(cost);
						BIT.plugin.Method.getAccount(nextDigilock.getOwner())
								.add(cost);
						sPlayer.sendMessage("Your account ("
								+ BIT.plugin.Method.getAccount(
										sPlayer.getName()).balance()
								+ ") has been deducted " + cost + " bucks");
					} else {
						sPlayer.sendMessage("You dont have enough money ("
								+ BIT.plugin.Method.getAccount(
										sPlayer.getName()).balance()
								+ "). Cost is:" + cost);
						pressButton = false;
					}
				}
			}
			if (pressButton && doTheWork) {
				Button button = (Button) sBlock.getState().getData();
				button.setPowered(true);
				// x | 8 ^ 8 = 0
				// sBlock.setData((byte) ((lever.getData() | 8) ^ 8));
				if (BITDigiLock.isDoubleDoor(nextBlock)) {
					BITDigiLock.openDoubleDoor(sPlayer, nextBlock, 0);
					BITDigiLock.scheduleCloseDoubleDoor(sPlayer, nextBlock, 5, 0);
				} else if (BITDigiLock.isDoor(nextBlock)) {
					BITDigiLock.openDoor(sPlayer, nextBlock, 0);
					BITDigiLock.scheduleCloseDoor(sPlayer, nextBlock, 5, 0);
				} else if (BITDigiLock.isTrapdoor(nextBlock)) {
					BITDigiLock.openTrapdoor(sPlayer, nextBlock, 0);
					BITDigiLock.scheduleCloseTrapdoor(sPlayer, nextBlock,5);
				} else if (BITDigiLock.isDispenser(nextBlock)) {
					Dispenser dispenser = (Dispenser) nextBlock.getState();
					dispenser.dispense();
				}

			}
		}
	}


	/**
	 * Check if sBlock is a DISPENSER.
	 * 
	 * @param sBlock
	 * @return
	 */
	public static boolean isDispenser(SpoutBlock sBlock) {
		if (sBlock.getType().equals(Material.DISPENSER)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check if sBlock is a LEVER.
	 * 
	 * @param sBlock
	 * @return
	 */
	public static boolean isLever(SpoutBlock sBlock) {
		if (sBlock.getType().equals(Material.LEVER)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Set the LEVER on - on sBlock.
	 * 
	 * @param sBlock
	 * @return
	 */
	public static boolean isLeverOn(SpoutBlock sBlock) {
		Lever lever = (Lever) sBlock.getState().getData();
		return lever.isPowered();
	}

	public static void leverOn(SpoutPlayer sPlayer, SpoutBlock sBlock, int cost) {
		boolean doTheWork = false;
		BITDigiLock digilock = BITDigiLock.loadDigiLock(sBlock);
		SpoutBlock nextBlock = digilock.getNextLockedBlock(sPlayer, sBlock);
		if (nextBlock != null) {
			BITDigiLock nextDigilock = BITDigiLock.loadDigiLock(nextBlock);

			if (digilock.getOwner().equalsIgnoreCase(nextDigilock.getOwner())) {
				doTheWork = true;
			} else {
				sPlayer.sendMessage("You are not the owner of the "
						+ nextBlock.getType());
			}
			boolean setleveron = true;
			if (BIT.useEconomy && cost > 0 && doTheWork
					&& !isOwner(sPlayer, sBlock)) {
				if (BIT.plugin.Method.hasAccount(sPlayer.getName())) {
					if (BIT.plugin.Method.getAccount(sPlayer.getName())
							.hasEnough(cost)) {
						BIT.plugin.Method.getAccount(sPlayer.getName())
								.subtract(cost);
						BIT.plugin.Method.getAccount(nextDigilock.getOwner())
								.add(cost);
						sPlayer.sendMessage("Your account ("
								+ BIT.plugin.Method.getAccount(
										sPlayer.getName()).balance()
								+ ") has been deducted " + cost + " bucks");
					} else {
						sPlayer.sendMessage("You dont have enough money ("
								+ BIT.plugin.Method.getAccount(
										sPlayer.getName()).balance()
								+ "). Cost is:" + cost);
						setleveron = false;
					}
				}
			}
			if (setleveron && doTheWork) {
				playDigiLockSound(sBlock);
				Lever lever = (Lever) sBlock.getState().getData();
				lever.setPowered(true);
				// x | 8 ^ 8 = 0
				// sBlock.setData((byte) ((lever.getData() | 8) ^ 8));

				if (BITDigiLock.isDoubleDoor(nextBlock)) {
					BITDigiLock.openDoubleDoor(sPlayer, nextBlock, 0);
				} else if (BITDigiLock.isDoor(nextBlock)) {
					BITDigiLock.openDoor(sPlayer, nextBlock, 0);
				} else if (BITDigiLock.isTrapdoor(nextBlock)) {
					BITDigiLock.openTrapdoor(sPlayer, nextBlock, 0);
				} else if (nextBlock.getType().equals(Material.DISPENSER)) {
					Dispenser dispenser = (Dispenser) nextBlock.getState();
					dispenser.dispense();
				}
				if (digilock.getClosetimer() > 0) {
					scheduleLeverOff(sPlayer, sBlock, digilock.getClosetimer());
				}
			}
		}
	}

	public static void leverOff(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		if (BITDigiLock.isLeverOn(sBlock)) {
			BITDigiLock digilock = BITDigiLock.loadDigiLock(sBlock);
			SpoutBlock nextBlock = digilock.getNextLockedBlock(sPlayer, sBlock);
			if (nextBlock != null) {
				BITDigiLock nextDigilock = BITDigiLock.loadDigiLock(nextBlock);
				if (digilock.getOwner().equalsIgnoreCase(
						nextDigilock.getOwner())) {
					Lever lever = (Lever) sBlock.getState().getData();
					lever.setPowered(false);
					// sBlock.setData((byte) (lever.getData() |8));
					if (BITDigiLock.isDoubleDoor(nextBlock)) {
						BITDigiLock.closeDoubleDoor(sPlayer, nextBlock, 0);
					} else if (BITDigiLock.isDoor(nextBlock)) {
						BITDigiLock.closeDoor(sPlayer, nextBlock, 0);
					} else if (BITDigiLock.isTrapdoor(nextBlock)) {
						BITDigiLock.closeTrapdoor(sPlayer, nextBlock);
					}
				} else {
					sPlayer.sendMessage("You are not the owner of the "
							+ nextBlock.getType());
				}
			}
		}
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
						if (G333Config.DEBUG_DOOR)
							sp.sendMessage("Turning lever off in " + closetimer
									+ " seconds");
						if (BITDigiLock.isLeverOn(sBlock))
							BITDigiLock.leverOff(sp, sb);
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

	public static boolean isDoorOpen(SpoutBlock sBlock) {
		if ((sBlock.getState().getData().getData() & 4) == 4) {
			return true;
		} else {
			return false;
		}
	}

	public static void openDoor(SpoutPlayer sPlayer, SpoutBlock sBlock, int cost) {
		boolean opendoor = true;
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
		Door door = (Door) sBlock.getState().getData();
		SpoutBlock nextBlock;
		if (opendoor) {
			if (!isDoorOpen(sBlock)) {
				playDigiLockSound(sBlock);
				sBlock.setData((byte) (sBlock.getState().getData().getData() | 4));
				if (door.isTopHalf()) {
					nextBlock = sBlock.getRelative(BlockFace.DOWN);
					nextBlock.setData((byte) (nextBlock.getState().getData()
							.getData() | 4));
				} else {
					nextBlock = sBlock.getRelative(BlockFace.UP);
					nextBlock.setData((byte) (nextBlock.getState().getData()
							.getData() | 4));
				}
				BITDigiLock digilock = loadDigiLock(sBlock);
				if (digilock != null) {
					if (digilock.getClosetimer() > 0 && !isDoubleDoor(sBlock)) {
						scheduleCloseDoor(sPlayer, sBlock,
								digilock.getClosetimer(), 0);
					}
				}
			}
		}
	}

	public static void closeDoor(SpoutPlayer sPlayer, SpoutBlock sBlock,
			int cost) {
		boolean closedoor = true;
		if (BIT.useEconomy && cost > 0 && !isOwner(sPlayer, sBlock)) {
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
			if (isDoorOpen(sBlock)) {
				playDigiLockSound(sBlock);
				Door door = (Door) sBlock.getState().getData();
				SpoutBlock nextBlock;
				sBlock.setData((byte) ((sBlock.getState().getData().getData() | 4) ^ 4));
				if (door.isTopHalf()) {
					nextBlock = sBlock.getRelative(BlockFace.DOWN);
					nextBlock.setData((byte) ((nextBlock.getState().getData()
							.getData() | 4) ^ 4));
				} else {
					nextBlock = sBlock.getRelative(BlockFace.UP);
					nextBlock.setData((byte) ((nextBlock.getState().getData()
							.getData() | 4) ^ 4));
				}
			}
		}
	}

	public static void closeDoor(SpoutBlock sBlock) {
		if (isDoorOpen(sBlock)) {
			playDigiLockSound(sBlock);
			Door door = (Door) sBlock.getState().getData();
			SpoutBlock nextBlock;
			sBlock.setData((byte) ((sBlock.getState().getData().getData() | 4) ^ 4));
			if (door.isTopHalf()) {
				nextBlock = sBlock.getRelative(BlockFace.DOWN);
				nextBlock.setData((byte) ((nextBlock.getState().getData()
						.getData() | 4) ^ 4));
			} else {
				nextBlock = sBlock.getRelative(BlockFace.UP);
				nextBlock.setData((byte) ((nextBlock.getState().getData()
						.getData() | 4) ^ 4));
			}
		}
	}

	public static void toggleDoor(SpoutBlock sBlock) {
		playDigiLockSound(sBlock);
		Door door = (Door) sBlock.getState().getData();
		SpoutBlock nextBlock;
		sBlock.setData((byte) (sBlock.getState().getData().getData() ^ 4));
		if (door.isTopHalf()) {
			nextBlock = sBlock.getRelative(BlockFace.DOWN);
			nextBlock
					.setData((byte) (nextBlock.getState().getData().getData() ^ 4));
		} else {
			nextBlock = sBlock.getRelative(BlockFace.UP);
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
						if (G333Config.DEBUG_DOOR)
							sp.sendMessage("Autoclosing the door in "
									+ closetimer + " seconds");
						if (isDoor(sb) && !isDoubleDoor(sb)) {
							if (isDoorOpen(sb)) {
								closeDoor(sp, sb, c);
								playDigiLockSound(sBlock);
							}
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

	public static boolean isTrapdoorOpen(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		if ((sBlock.getState().getData().getData() & 4) == 4) {
			return true;
		} else {
			return false;
		}
	}

	public static void openTrapdoor(SpoutPlayer sPlayer, SpoutBlock sBlock,
			int cost) {
		boolean opentrapdoor = true;
		if (BIT.useEconomy && cost > 0 && !isOwner(sPlayer, sBlock)) {
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
				sBlock.setData((byte) (sBlock.getState().getData().getData() | 4));
				// if (BITDigiLock.isLocked(sBlock)) {
				BITDigiLock digilock = loadDigiLock(sBlock);
				if (digilock != null) {
					playDigiLockSound(sBlock);
					if (digilock.getClosetimer() > 0) {
						scheduleCloseTrapdoor(sPlayer, sBlock,
								digilock.getClosetimer());
					}
				}
				// }
			}
		}
	}

	public static void closeTrapdoor(SpoutPlayer sPlayer, SpoutBlock sBlock) {
		sBlock.setData((byte) ((sBlock.getState().getData().getData() | 4) ^ 4));
		if (G333Config.DEBUG_DOOR)
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
						if (G333Config.DEBUG_DOOR)
							sp.sendMessage("Autoclosing the trapdoor in "
									+ closetimer + " seconds");
						if (sBlock.getType() == Material.TRAP_DOOR) {
							if (isTrapdoorOpen(sp, sb)) {
								closeTrapdoor(sp, sb);
								playDigiLockSound(sBlock);
							}
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
	public static boolean isDoubleDoor(SpoutBlock sBlock) {
		if (isDoor(sBlock)) {
			if (isDoor(sBlock.getFace(BlockFace.EAST))
					|| isDoor(sBlock.getFace(BlockFace.NORTH))
					|| isDoor(sBlock.getFace(BlockFace.SOUTH))
					|| isDoor(sBlock.getFace(BlockFace.WEST))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isDoubleDoorOpen(SpoutBlock sBlock) {
		return (isDoorOpen(getLeftDoubleDoor(sBlock)) || !isDoorOpen(getRightDoubleDoor(sBlock)));
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
		if (digilock != null) {
			if (digilock.getClosetimer() > 0) {
				scheduleCloseDoubleDoor(sPlayer, sBlock,
						digilock.getClosetimer(), 0);
			}
		}
	}

	public static boolean isLeftDoubleDoor(SpoutBlock sBlock) {
		if (isDoubleDoor(sBlock)) {
			Door door = (Door) sBlock.getState().getData();
			// left door:NORTH,NORTH_EAST Right door:WEST,NORTH_WEST
			// left door:WEST,NORTH_WEST Right door:SOUTH,SOUTH_WEST
			// left door:SOUTH,SOUTH_WEST Right door:EAST,SOUTH_EAST
			// left door:EAST,SOUTH_EAST Right door:NORTH,NORTH_EAST
			if (door.getFacing() == BlockFace.NORTH
					&& door.getHingeCorner() == BlockFace.NORTH_EAST) {
				if (isDoor(sBlock.getRelative(BlockFace.WEST))) {
					return true;
				} else {
					return false;
				}
			} else if (door.getFacing() == BlockFace.WEST
					&& door.getHingeCorner() == BlockFace.NORTH_WEST) {
				if (isDoor(sBlock.getRelative(BlockFace.SOUTH))) {
					return true;
				} else {
					return false;
				}
			} else if (door.getFacing() == BlockFace.EAST
					&& door.getHingeCorner() == BlockFace.SOUTH_EAST) {
				if (isDoor(sBlock.getRelative(BlockFace.NORTH))) {
					return true;
				} else {
					return false;
				}
			} else if (door.getFacing() == BlockFace.SOUTH
					&& door.getHingeCorner() == BlockFace.SOUTH_WEST) {
				if (isDoor(sBlock.getRelative(BlockFace.EAST))) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}

	public static SpoutBlock getLeftDoubleDoor(SpoutBlock sBlock) {
		if (isLeftDoubleDoor(sBlock)) {
			return sBlock;
		} else {
			Door door = (Door) sBlock.getState().getData();
			// left door:NORTH,NORTH_EAST Right door:WEST,NORTH_WEST
			// left door:WEST,NORTH_WEST Right door:SOUTH,SOUTH_WEST
			// left door:EAST,SOUTH_EAST Right door:NORTH,NORTH_EAST
			// left door:SOUTH,SOUTH_WEST Right door:EAST,SOUTH_EAST
			if (door.getFacing() == BlockFace.NORTH
					&& door.getHingeCorner() == BlockFace.NORTH_EAST) {
				return sBlock.getRelative(BlockFace.SOUTH);
			} else if (door.getFacing() == BlockFace.WEST
					&& door.getHingeCorner() == BlockFace.NORTH_WEST) {
				return sBlock.getRelative(BlockFace.EAST);
			} else if (door.getFacing() == BlockFace.EAST
					&& door.getHingeCorner() == BlockFace.SOUTH_EAST) {
				return sBlock.getRelative(BlockFace.WEST);
			} else {
				// if (door.getFacing() == BlockFace.SOUTH
				// && door.getHingeCorner() == BlockFace.SOUTH_WEST) {
				return sBlock.getRelative(BlockFace.NORTH);
			}
		}
	}

	public static boolean isRightDoubleDoor(SpoutBlock sBlock) {
		if (isDoubleDoor(sBlock)) {
			if (isLeftDoubleDoor(sBlock)) {
				return false;
			} else {
				return true;
			}
		}
		return false;
	}

	public static SpoutBlock getRightDoubleDoor(SpoutBlock sBlock) {
		if (isRightDoubleDoor(sBlock)) {
			return sBlock;
		} else {
			Door door = (Door) sBlock.getState().getData();
			// left door:NORTH,NORTH_EAST Right door:WEST,NORTH_WEST
			// left door:WEST,NORTH_WEST Right door:SOUTH,SOUTH_WEST
			// left door:EAST,SOUTH_EAST Right door:NORTH,NORTH_EAST
			// left door:SOUTH,SOUTH_WEST Right door:EAST,SOUTH_EAST
			if (door.getFacing() == BlockFace.NORTH
					&& door.getHingeCorner() == BlockFace.NORTH_EAST) {
				return sBlock.getRelative(BlockFace.WEST);
			} else if (door.getFacing() == BlockFace.WEST
					&& door.getHingeCorner() == BlockFace.NORTH_WEST) {
				return sBlock.getRelative(BlockFace.SOUTH);
			} else if (door.getFacing() == BlockFace.EAST
					&& door.getHingeCorner() == BlockFace.SOUTH_EAST) {
				return sBlock.getRelative(BlockFace.NORTH);
			} else {
				// (door.getFacing() == BlockFace.SOUTH
				// && door.getHingeCorner() == BlockFace.SOUTH_WEST) {
				return sBlock.getRelative(BlockFace.EAST);
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
						if (G333Config.DEBUG_DOOR)
							sp.sendMessage("Autoclosing the DoubleDoor in "
									+ closetimer + " seconds");
						if (isDoubleDoor(sBlock)) {
							if (isDoubleDoorOpen(sb)) {
								closeDoubleDoor(sp, sb, c);
								playDigiLockSound(sBlock);
							}
						}
					}
				}, fs);
		return taskID;
	}

}
