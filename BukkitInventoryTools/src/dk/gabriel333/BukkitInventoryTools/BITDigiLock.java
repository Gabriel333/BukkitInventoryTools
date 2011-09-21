package dk.gabriel333.BukkitInventoryTools;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import org.bukkit.material.Door;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;

public class BITDigiLock {

	private Plugin plugin;

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

	// protected boolean locked = true;

	/**
	 * Constructs a new BITDigiLock
	 * 
	 */
	BITDigiLock(SpoutBlock block, String pincode, String owner, int closetimer,
			String coowners, String shared, 
			int typeId, String connectedTo) {
		this.block = block;
		this.pincode = pincode;
		this.owner = owner;
		this.closetimer = closetimer;
		this.coOwners = coowners;
		this.shared = shared;
		this.typeId = typeId;
		this.connectedTo = connectedTo;
	}

	public static void SaveDigiLock(SpoutPlayer sPlayer, SpoutBlock block,
			String pincode, String owner, Integer closetimer, String coowners,
			String shared, int typeId,
			String connectedTo) {
		String query = null;
		if (isLocked(block)) {
			query = "UPDATE "+BIT.digilockTable+" SET pincode='" + pincode
					+ "', owner='" + owner + "', closetimer=" + closetimer
					+ " , coowners='" + coowners + "', shared='" + shared
					+ "', typeid=" + typeId + ", connectedto='" + connectedTo
					+ "'"

					+ " WHERE x = " + block.getX() + " AND y = " + block.getY()
					+ " AND z = " + block.getZ() + " AND world='"
					+ block.getWorld().getName() + "';";
			if (isDoor(block)) {
				Door door = (Door) block.getState().getData();
				if (door.isTopHalf()) {
					query = "UPDATE "+BIT.digilockTable+" SET pincode='"
							+ pincode + "', owner='" + owner + "', closetimer="
							+ closetimer + " , coowners='" + coowners
							+ "', shared='" + shared + "', typeid="
							+ typeId + ", connectedto='" + connectedTo + "'"
							+ " WHERE x = " + block.getX() + " AND y = "
							+ (block.getY() - 1) + " AND z = " + block.getZ()
							+ " AND world='" + block.getWorld().getName()
							+ "';";
				}
			}
			G333Messages.sendNotification(sPlayer, "DigiLock updated.");
		} else {
			query = "INSERT INTO "+BIT.digilockTable+" (pincode, owner, closetimer, "
					+ "x, y, z, world, coowners, shared, "
					+ "typeid, connectedto) VALUES ('"
					+ pincode
					+ "', '"
					+ owner
					+ "', "
					+ closetimer
					+ ", "
					+ block.getX()
					+ ", "
					+ block.getY()
					+ ", "
					+ block.getZ()
					+ ", '"
					+ block.getWorld().getName()
					+ "', '"
					+ coowners
					+ "', '"
					+ shared
					+ "', " + block.getTypeId() + ", '" + connectedTo + "');";

			if (isDoor(block)) {
				Door door = (Door) block.getState().getData();
				if (door.isTopHalf()) {
					query = "INSERT INTO "+BIT.digilockTable+" (pincode, owner, closetimer,"
							+ " x, y, z, world, coowners, shared, typeid, connectedto) VALUES ('"
							+ pincode
							+ "', '"
							+ owner
							+ "', "
							+ closetimer
							+ ", "
							+ block.getX()
							+ ", "
							+ (block.getY() - 1)
							+ ", "
							+ block.getZ()
							+ ", '"
							+ block.getWorld().getName()
							+ "', '"
							+ coowners
							+ "', '"
							+ shared
							+ "', "
							+ block.getTypeId()
							+ ", '"
							+ connectedTo + "');";
				}
			}
			G333Messages.sendNotification(sPlayer, "DigiLock created.");
		}
		if (G333Config.g333Config.DEBUG_SQL)
			sPlayer.sendMessage(ChatColor.YELLOW + "Updating lock: " + query);
		if (G333Config.g333Config.STORAGE_TYPE.equals("MYSQL")) {
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
	}

	public static Boolean isLocked(SpoutBlock block) {
		String query = "SELECT * FROM "+BIT.digilockTable+" WHERE (x = "
				+ block.getX() + " AND y = " + block.getY() + " AND z = "
				+ block.getZ() + " AND world='" + block.getWorld().getName()
				+ "');";
		if (isChest(block)) {
			SpoutChest sChest1 = (SpoutChest) block.getState();
			if (sChest1.isDoubleChest()) {
				SpoutChest sChest2 = sChest1.getOtherSide();
				query = "SELECT * FROM "+BIT.digilockTable+" WHERE (x = "
						+ block.getX() + " AND y = " + block.getY()
						+ " AND z = " + block.getZ() + " AND world='"
						+ block.getWorld().getName() + "') OR (x = "
						+ sChest2.getBlock().getX() + " AND y = "
						+ sChest2.getBlock().getY() + " AND z = "
						+ sChest2.getBlock().getZ() + " AND world='"
						+ sChest2.getBlock().getWorld().getName() + "');";
			}
		} else if (isDoor(block)) {
			Door door = (Door) block.getState().getData();
			if (door.isTopHalf()) {
				query = "SELECT * FROM "+BIT.digilockTable+" WHERE (x = "
						+ block.getX() + " AND y = " + (block.getY() - 1)
						+ " AND z = " + block.getZ() + " AND world='"
						+ block.getWorld().getName() + "');";
			}
		}
		if (query != "") {
			ResultSet result = null;
			if (G333Config.g333Config.STORAGE_TYPE.equals("MYSQL")) {
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
					// sPlayer.sendMessage("This block is locked with a DigiLock!");
					return true;
				} else {
					// sPlayer.sendMessage("This block is NOT locked with a DigiLock!");
					return false;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	public boolean isCoowner(SpoutPlayer sPlayer) {
		if (coOwners.contains(sPlayer.getName()))
			return true;
		return false;
	}

	public boolean isOwner(SpoutPlayer sPlayer) {
		if (owner.equals(sPlayer.getName()))
			return true;
		return false;
	}

	public void addCoowner(SpoutPlayer sPlayer) {
		coOwners = coOwners.concat("," + sPlayer.getName());
	}

	public boolean removeCoowner(SpoutPlayer sPlayer) {
		if (coOwners.contains(sPlayer.getName())) {
			coOwners = coOwners.replace("," + sPlayer.getName(), "");
			return true;
		}
		return false;
	}

	private static final Material lockablematerials[] = { Material.CHEST,
			Material.LOCKED_CHEST, Material.IRON_DOOR,
			Material.IRON_DOOR_BLOCK, Material.WOODEN_DOOR, Material.WOOD_DOOR,
			Material.FURNACE, Material.DISPENSER, Material.LEVER,
			Material.STONE_BUTTON, Material.BOOKSHELF };

	// Material.STORAGE_MINECART,
	// Material.WALL_SIGN, Material.SIGN, Material.SIGN_POST,

	// check if material is a lockable block
	static boolean isLockable(Block block) {
		for (Material i : lockablematerials) {
			if (i == block.getType())
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

	public void setDigiLock(SpoutBlock block, String pincode, String owner,
			int closetimer, String coowners, String shared, String connectedTo) {
		this.block = block;
		this.pincode = pincode;
		this.owner = owner;
		this.closetimer = closetimer;
		this.coOwners = coowners;
		this.shared = shared;
		this.typeId = block.getTypeId();
		this.connectedTo = connectedTo;
	}

	public static BITDigiLock loadDigiLock(SpoutPlayer sPlayer, SpoutBlock block) {
		String query = "SELECT * FROM "+BIT.digilockTable+" WHERE (x = "
				+ block.getX() + " AND y = " + block.getY() + " AND z = "
				+ block.getZ() + " AND world='" + block.getWorld().getName()
				+ "');";
		if (isChest(block)) {
			SpoutChest sChest1 = (SpoutChest) block.getState();
			if (sChest1.isDoubleChest()) {
				SpoutChest sChest2 = sChest1.getOtherSide();
				query = "SELECT * FROM "+BIT.digilockTable+" WHERE (x = "
						+ block.getX() + " AND y = " + block.getY()
						+ " AND z = " + block.getZ() + " AND world='"
						+ block.getWorld().getName() + "') OR (x = "
						+ sChest2.getBlock().getX() + " AND y = "
						+ sChest2.getBlock().getY() + " AND z = "
						+ sChest2.getBlock().getZ() + " AND world='"
						+ sChest2.getBlock().getWorld().getName() + "');";
			}
		} else if (isDoor(block)) {
			Door door = (Door) block.getState().getData();
			if (door.isTopHalf()) {
				query = "SELECT * FROM "+BIT.digilockTable+" WHERE (x = "
						+ block.getX() + " AND y = " + (block.getY() - 1)
						+ " AND z = " + block.getZ() + " AND world='"
						+ block.getWorld().getName() + "');";
			}
		}
		ResultSet result = null;
		if (G333Config.g333Config.STORAGE_TYPE.equals("MYSQL")) {
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
				BITDigiLock digilock = new BITDigiLock(block, pincode, owner,
						closetimer, coowners, shared, 
						typeId, connectedTo);
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
		String query = "DELETE FROM "+BIT.digilockTable+" WHERE (x = "
				+ block.getX() + " AND y = " + block.getY() + " AND z = "
				+ block.getZ() + " AND world='" + block.getWorld().getName()
				+ "');";
		if (isChest(getBlock())) {
			SpoutChest sChest1 = (SpoutChest) getBlock().getState();
			if (sChest1.isDoubleChest()) {
				SpoutChest sChest2 = sChest1.getOtherSide();
				query = "DELETE FROM "+BIT.digilockTable+" WHERE (x = "
						+ block.getX() + " AND y = " + block.getY()
						+ " AND z = " + block.getZ() + " AND world='"
						+ block.getWorld().getName() + "') OR (x = "
						+ sChest2.getBlock().getX() + " AND y = "
						+ sChest2.getBlock().getY() + " AND z = "
						+ sChest2.getBlock().getZ() + " AND world='"
						+ sChest2.getBlock().getWorld().getName() + "');";
			}
		} else if (isDoor(getBlock())) {
			Door door = (Door) getBlock().getState().getData();
			if (door.isTopHalf()) {
				query = "DELETE FROM "+BIT.digilockTable+" WHERE (x = "
						+ block.getX() + " AND y = " + (block.getY() - 1)
						+ " AND z = " + block.getZ() + " AND world='"
						+ block.getWorld().getName() + "');";
			}
		}
		if (G333Config.g333Config.DEBUG_SQL)
			sPlayer.sendMessage(ChatColor.YELLOW + "Removeing lock: " + query);
		if (G333Config.g333Config.STORAGE_TYPE.equals("MYSQL")) {
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
	}

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

	public static boolean isChest(Block block) {
		if (block.getType().equals(Material.CHEST))
			return true;
		else if (block.getType().equals(Material.LOCKED_CHEST))
			return true;
		return false;
	}

	public static boolean isDoorOpen(SpoutPlayer sPlayer, SpoutBlock block) {
		if ((block.getState().getData().getData() & 4) == 4) {
			return true;
		} else {
			return false;
		}
	}

	public static void openDoor(SpoutPlayer sPlayer, SpoutBlock block) {
		Door door = (Door) block.getState().getData();
		SpoutBlock nextBlock;
		if (G333Config.g333Config.DEBUG_DOOR)
			sPlayer.sendMessage("The door is closed. OpenDoor");
		block.setData((byte) (block.getState().getData().getData() | 4));
		if (door.isTopHalf()) {
			nextBlock = block.getRelative(BlockFace.DOWN);
			nextBlock
					.setData((byte) (nextBlock.getState().getData().getData() | 4));
		} else {
			nextBlock = block.getRelative(BlockFace.UP);
			nextBlock
					.setData((byte) (nextBlock.getState().getData().getData() | 4));
		}
	}

	public static void closeDoor(SpoutPlayer sPlayer, SpoutBlock block) {
		Door door = (Door) block.getState().getData();
		SpoutBlock nextBlock;
		block.setData((byte) ((block.getState().getData().getData() | 4) ^ 4));
		if (door.isTopHalf()) {
			nextBlock = block.getRelative(BlockFace.DOWN);
			nextBlock
					.setData((byte) ((nextBlock.getState().getData().getData() | 4) ^ 4));
		} else {
			nextBlock = block.getRelative(BlockFace.UP);
			nextBlock
					.setData((byte) ((nextBlock.getState().getData().getData() | 4) ^ 4));
		}
		if (G333Config.g333Config.DEBUG_DOOR)
			sPlayer.sendMessage("Close the door.");
	}

	public static void toggleDoor(SpoutPlayer sPlayer, SpoutBlock block) {
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

}
