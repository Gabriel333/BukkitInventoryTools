package dk.gabriel333.BukkitInventoryTools;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import org.bukkit.plugin.Plugin;
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

	protected Block block;
	protected String pincode;
	protected String owner;
	protected int closetimer;
	protected String coowners;
	protected String shared;

	// protected boolean locked = true;

	/**
	 * Constructs a new BITDigiLock
	 * 
	 */
	BITDigiLock(Block block, String pincode, String owner, int closetimer,
			String coowners, String shared) {
		this.block = block;
		this.pincode = pincode;
		this.owner = owner;
		this.closetimer = closetimer;
		this.coowners = coowners;
		this.shared = shared;

	}

	static void resetDigiLock() {
		// TODO Auto-generated method stub
	}

	static void unlockDigiLock() {
		// TODO Auto-generated method stub
	}

	static void showDigiLockStatus() {
		// TODO Auto-generated method stub

	}

	static void SaveDigiLock(SpoutPlayer sPlayer, Block block, String pincode,
			String owner, Integer closetimer, String coowners, String shared) {
		String query = null;
		if (isLocked(sPlayer, block)) {
			query = "UPDATE BukkitInventoryTools SET pincode='" + pincode
					+ "', owner='" + owner + "', closetimer=" + closetimer
					+ " , coowners='" + coowners + "', shared='" + shared + "'"
					+ " WHERE x = " + block.getX() + " AND y = " + block.getY()
					+ " AND z = " + block.getZ() + " AND world='"
					+ block.getWorld().getName() + "';";
			G333Messages.sendNotification(sPlayer, "DigiLock updated.");
		} else {
			query = "INSERT INTO BukkitInventoryTools (pincode, owner, closetimer,"
					+ " x, y, z, world, coowners, shared) VALUES ('"
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
					+ shared + "');";
			G333Messages.sendNotification(sPlayer, "DigiLock created.");
		}
		if (G333Config.g333Config.DEBUG_SQL)
			sPlayer.sendMessage(ChatColor.YELLOW + "Updating lock: " + query);
		if (G333Config.g333Config.STORAGE_TYPE.equals("MySQL")) {
			try {
				BIT.manageMySQL.insertQuery(query);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			BIT.manageSQLite.insertQuery(query);
		}
	}

	void SaveDigiLock(SpoutPlayer sPlayer, BITDigiLock digilock) {
		String query = null;
		if (isLocked(sPlayer, digilock.getBlock())) {
			query = "UPDATE BukkitInventoryTools SET pincode='"
					+ digilock.getPincode() + "', owner='"
					+ digilock.getOwner() + "', closetimer="
					+ digilock.getClosetimer() + " , coowners='"
					+ digilock.getClosetimer() + "', shared='"
					+ digilock.getShared() + "'" + " WHERE x = "
					+ digilock.getBlock().getX() + " AND y = "
					+ digilock.getBlock().getY() + " AND z = "
					+ digilock.getBlock().getZ() + " AND world='"
					+ digilock.getBlock().getWorld().getName() + "';";
			G333Messages.sendNotification(sPlayer, "DigiLock updated.");
		} else {
			query = "INSERT INTO BukkitInventoryTools (pincode, owner, closetimer,"
					+ " x, y, z, world, coowners, shared) VALUES ('"
					+ digilock.getPincode()
					+ "', '"
					+ digilock.getOwner()
					+ "', "
					+ digilock.getClosetimer()
					+ ", "
					+ digilock.getBlock().getX()
					+ ", "
					+ digilock.getBlock().getY()
					+ ", "
					+ digilock.getBlock().getZ()
					+ ", '"
					+ digilock.getBlock().getWorld().getName()
					+ "', '"
					+ digilock.getCoowners()
					+ "', '"
					+ digilock.getShared()
					+ "');";
			G333Messages.sendNotification(sPlayer, "DigiLock created.");
		}
		if (G333Config.g333Config.DEBUG_SQL)
			G333Messages.showInfo("SQL: " + query);
		if (G333Config.g333Config.STORAGE_TYPE.equals("MySQL")) {
			try {
				BIT.manageMySQL.insertQuery(query);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// sPlayer.sendMessage(ChatColor.GREEN +
			// "This block is now owned by alta189");
		} else {
			BIT.manageSQLite.insertQuery(query);
		}
	}

	public static Boolean isLocked(SpoutPlayer sPlayer, Block block) {
		if (block.getType() == Material.CHEST) {
			SpoutChest sChest1 = (SpoutChest) block.getState();
			SpoutChest sChest2 = sChest1.getOtherSide();
			String query = "SELECT * FROM BukkitInventoryTools WHERE (x = "
					+ block.getX() + " AND y = " + block.getY() + " AND z = "
					+ block.getZ() + " AND world='"
					+ block.getWorld().getName() + "') OR (x = "
					+ sChest2.getBlock().getX() + " AND y = "
					+ sChest2.getBlock().getY() + " AND z = "
					+ sChest2.getBlock().getZ() + " AND world='"
					+ sChest2.getBlock().getWorld().getName() + "');";
			ResultSet result = null;
			if (G333Config.g333Config.STORAGE_TYPE.equals("MySQL")) {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean isCoowner(SpoutPlayer sPlayer) {
		if (coowners.contains(sPlayer.getName()))
			return true;
		return false;
	}

	public void addCoowner(SpoutPlayer sPlayer) {
		coowners = coowners.concat("," + sPlayer.getName());
	}

	public boolean removeCoowner(SpoutPlayer sPlayer) {
		if (coowners.contains(sPlayer.getName())) {
			coowners = coowners.replace("," + sPlayer.getName(), "");
			return true;
		}
		return false;
	}

	private static final Material lockablematerials[] = { Material.CHEST,
			Material.IRON_DOOR, Material.WOOD_DOOR, Material.FURNACE,
			Material.IRON_DOOR_BLOCK, Material.LOCKED_CHEST, Material.LEVER,
			Material.SIGN, Material.SIGN_POST, Material.STONE_BUTTON,
			Material.STORAGE_MINECART, Material.WALL_SIGN, Material.WOODEN_DOOR };

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

	public String getCoowners() {
		return coowners;
	}

	public Block getBlock() {
		return block;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setClosetimer(int closetimer) {
		this.closetimer = closetimer;
	}

	public void setCoowners(String coowners) {
		this.coowners = coowners;
	}

	public void setShared(String shared) {
		this.shared = shared;
	}

	public void setDigiLock(Block block, String pincode, String owner,
			int closetimer, String coowners, String shared) {
		this.block = block;
		this.pincode = pincode;
		this.owner = owner;
		this.closetimer = closetimer;
		this.coowners = coowners;
		this.shared = shared;
	}

	public static String getPincodeFromSQL(SpoutPlayer sPlayer, Block block) {
		String query = "SELECT * FROM BukkitInventoryTools WHERE x = "
				+ block.getX() + " AND y = " + block.getY() + " AND z = "
				+ block.getZ() + " AND world='" + block.getWorld().getName()
				+ "';";
		// TODO: add and rownum<2 to select only one row
		ResultSet result = null;

		if (G333Config.g333Config.STORAGE_TYPE.equals("MySQL")) {
			try {
				result = BIT.manageMySQL.sqlQuery(query);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else { // SQLLITE
			result = BIT.manageSQLite.sqlQuery(query);
		}

		try {
			if (result != null && result.next()) {
				String pincode = result.getString("pincode");
				// sPlayer.sendMessage("Pincode stored in SQL is:" + pincode);
				return pincode;
			} else {
				// sPlayer.sendMessage("This block is not owned - open inventory");
				return "";
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "ERR1";
	}

	public static String getOwnerFromSQL(SpoutPlayer sPlayer, Block block) {
		SpoutChest sChest1 = (SpoutChest) block.getState();
		SpoutChest sChest2 = sChest1.getOtherSide();
		String query = "SELECT * FROM BukkitInventoryTools WHERE (x = "
				+ block.getX() + " AND y = " + block.getY() + " AND z = "
				+ block.getZ() + " AND world='" + block.getWorld().getName()
				+ "') OR (x = " + sChest2.getBlock().getX() + " AND y = "
				+ sChest2.getBlock().getY() + " AND z = "
				+ sChest2.getBlock().getZ() + " AND world='"
				+ sChest2.getBlock().getWorld().getName() + "');";
		ResultSet result = null;

		if (G333Config.g333Config.STORAGE_TYPE.equals("MySQL")) {
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
				String owner = result.getString("owner");
				return owner;
			} else {
				return sPlayer.getName();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "ERR2";

	}

	public static BITDigiLock loadDigiLock(SpoutPlayer sPlayer, Block block) {
		SpoutChest sChest1 = (SpoutChest) block.getState();
		SpoutChest sChest2 = sChest1.getOtherSide();
		String query = "SELECT * FROM BukkitInventoryTools WHERE (x = "
				+ block.getX() + " AND y = " + block.getY() + " AND z = "
				+ block.getZ() + " AND world='" + block.getWorld().getName()
				+ "') OR (x = " + sChest2.getBlock().getX() + " AND y = "
				+ sChest2.getBlock().getY() + " AND z = "
				+ sChest2.getBlock().getZ() + " AND world='"
				+ sChest2.getBlock().getWorld().getName() + "');";
		ResultSet result = null;
		if (G333Config.g333Config.STORAGE_TYPE.equals("MySQL")) {
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
				BITDigiLock digilock = new BITDigiLock(block, pincode, owner,
						closetimer, coowners, shared);
				return digilock;
			} else {
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static void RemoveDigiLock(SpoutPlayer sPlayer, BITDigiLock digilock) {
		SpoutChest sChest1 = (SpoutChest) digilock.getBlock().getState();
		SpoutChest sChest2 = sChest1.getOtherSide();
		String query = "DELETE FROM BukkitInventoryTools WHERE (x = "
				+ digilock.block.getX() + " AND y = " + digilock.block.getY()
				+ " AND z = " + digilock.block.getZ() + " AND world='"
				+ digilock.block.getWorld().getName() + "') OR (x = "
				+ sChest2.getBlock().getX() + " AND y = "
				+ sChest2.getBlock().getY() + " AND z = "
				+ sChest2.getBlock().getZ() + " AND world='"
				+ sChest2.getBlock().getWorld().getName() + "');";
		if (G333Config.g333Config.STORAGE_TYPE.equals("MySQL")) {
			try {
				BIT.manageMySQL.deleteQuery(query);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else { // SQLLITE
			BIT.manageSQLite.deleteQuery(query);

		}

	}

}
