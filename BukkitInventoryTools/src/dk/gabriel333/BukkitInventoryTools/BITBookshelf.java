package dk.gabriel333.BukkitInventoryTools;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.inventory.InventoryBuilder;

import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;

public class BITBookshelf {

	private BIT plugin;

	public BITBookshelf(Plugin plugin) {
		plugin = this.plugin;
	}

	protected SpoutBlock block;
	protected String owner;
	protected String coOwners;
	protected InventoryBuilder inventory;
	protected int useCost;

	/**
	 * Constructs a new BITDigiLock
	 * 
	 */
	BITBookshelf(SpoutBlock block, String owner, String coowners,
			InventoryBuilder inventory, int useCost) {
		this.block = block;
		this.owner = owner;
		this.coOwners = coowners;
		this.inventory = inventory;
		this.useCost = useCost;
	}
	

	public void setBookshelf(SpoutBlock block, String owner,
			String coowners, InventoryBuilder inventory, 
			int useCost) {
		this.block = block;
		this.owner = owner;
		this.coOwners = coowners;
		this.inventory = inventory;
		this.useCost = useCost;
	}

	/**
	 * Saves the bookshelf to the database.
	 * 
	 * @param block - block of the DigiLock.
	 * @param owner - is the owner
	 * @param coowners - list of coowners
	 * @param Inventory
	 * @param useCost
	 *            is the cost to use the block.
	 */
	
	public static void SaveBookshelf(SpoutPlayer sPlayer, SpoutBlock block,
			String pincode, String owner, Integer closetimer, String coowners,
			int typeId, String connectedTo, int useCost) {
		String query;
		boolean createlock = true;
		boolean newLock = true;
		int cost = G333Config.DIGILOCK_COST;
		if (isLocked(block)) {
			newLock = false;
			query = "UPDATE " + BIT.digilockTable + " SET pincode='" + pincode
					+ "', owner='" + owner + "', closetimer=" + closetimer
					+ " , coowners='" + coowners 
					+ "', typeid=" + typeId + ", connectedto='" + connectedTo
					+ "', usecost=" + useCost

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
					+ block.getWorld().getName() + "', '" + coowners 
					+ "', " + block.getTypeId() + ", '" + connectedTo
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



	public static BITDigiLock loadDigiLock(SpoutBlock block) {
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
				int typeId = result.getInt("typeId");
				String connectedTo = result.getString("connectedto");
				int useCost = result.getInt("usecost");
				BITDigiLock digilock = new BITDigiLock(block, pincode, owner,
						closetimer, coowners, typeId, connectedTo,
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


	// *******************************************************
	//
	// DISPENSER
	//
	// *******************************************************
	public static boolean isDispenser(SpoutBlock sBlock) {
		if (sBlock.getType().equals(Material.DISPENSER)) {
			return true;
		} else {
			return false;
		}
	}


}
