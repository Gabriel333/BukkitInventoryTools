package dk.rocologo.BukkitInventoryTools;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import dk.rocologo.Library.RLConfig;
import dk.rocologo.Library.RLMessages;
import dk.rocologo.BukkitInventoryTools.BIT;

public class BITDigiLock {

	private Plugin plugin;

	public BITDigiLock(Plugin plugin) {
		plugin = this.plugin;
	}

	protected Block block;
	protected String owner;
	protected boolean locked = true;

	// private String coOwners = ",";

	/**
	 * Constructs a new Safe
	 * 
	 * @param owner
	 *            The name of the owner of the safe
	 * @param safe
	 *            The Block of the safe
	 * @param coOwners
	 *            The list of the coowners of the safe
	 */
	BITDigiLock(String owner, Block block, String coOwners) {
		this.owner = owner;
		this.block = block;
		// this.coOwners = coOwners;
	}

	static void SaveDigiLock(Block block, String pincode, String owner,
			Integer closetimer) {
		String query = null;
		// insert/update targetblock, code, owner, closetime into safetylocks
		// table
		if (!checkForLock(block)) {
			// TODO: Block exists - update data.
			query = "UPDATE BukkitInventoryTools SET pincode='" + pincode
					+ "', owner='" + owner + "', closetimer=" + closetimer
					+ " WHERE x = " + block.getX() + " AND y = " + block.getY()
					+ " AND z = " + block.getZ() + ";";

			return;
		} else {
			query = "INSERT INTO BukkitInventoryTools (pincode, owner, closetimer, x, y, z) VALUES ('"
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
					+ ");";
		}
		if (RLConfig.rLConfig.DEBUG_SQL)
			RLMessages.showInfo("SQL: " + query);
		if (RLConfig.rLConfig.STORAGE_TYPE.equals("MySQL")) {
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

	public static Boolean checkForLock(Block block) {
		String query = "SELECT * FROM BukkitInventoryTools WHERE x = "
				+ block.getX() + " AND y = " + block.getY() + " AND z = "
				+ block.getZ() + ";";
		// TODO: add and rownum<2 to select only one row
		ResultSet result = null;

		if (RLConfig.rLConfig.STORAGE_TYPE.equals("MySQL")) {
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
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
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

	public static final Material lockablematerials[] = { Material.CHEST,
			Material.IRON_DOOR, Material.WOOD_DOOR, Material.FURNACE,
			Material.IRON_DOOR_BLOCK, Material.LOCKED_CHEST, Material.LEVER,
			Material.SIGN, Material.SIGN_POST, Material.STONE_BUTTON,
			Material.STORAGE_MINECART, Material.WALL_SIGN, Material.WOODEN_DOOR };

	// check if material is a lockable block
	static boolean isLockable(Material m) {
		if (m == null)
			return false;
		for (Material i : lockablematerials) {
			if (i == m)
				return true;
		}
		return false;
	}

}
