package dk.gabriel333.BukkitInventoryTools.Inventory;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;

public class BITInventory {
	
	private BIT plugin;
	
	public BITInventory(Plugin plugin) {
		plugin = this.plugin;
	}

	protected SpoutBlock sBlock;
	protected String name;
	protected String owner;
	protected String coOwners;
	protected Inventory inventory;
	protected int useCost;

	/**
	 * Constructs a new BITInventory
	 * 
	 */
	BITInventory(SpoutBlock sBlock, String owner, String name, String coowners,
			Inventory inventory, int useCost) {
		// super();
		this.sBlock = sBlock;
		this.name = name;
		this.owner = owner;
		this.coOwners = coowners;
		this.inventory = inventory;
		this.useCost = useCost;
	}

	public void setInventory(SpoutBlock sBlock, String owner, String name,
			String coOwners, Inventory inventory, int useCost) {
		this.sBlock = sBlock;
		this.owner = owner;
		this.name = name;
		this.coOwners = coOwners;
		this.inventory = inventory;
		this.useCost = useCost;
	}

	public static Map<Integer, BITInventory> openedInventories = new HashMap<Integer, BITInventory>();
	//public static Map<Integer, Boolean> isBookshelfCreated = new HashMap<Integer, Boolean>();

	public Inventory getInventory() {
		return this.inventory;
	}

	public String getOwner() {
		return this.owner;
	}

	public String getCoOwners() {
		return this.coOwners;
	}

	public int getUseCost() {
		return this.useCost;
	}

	public SpoutBlock getBlock() {
		return this.sBlock;
	}
	
	public void openBitInventory(SpoutPlayer sPlayer, BITInventory bitInventory) {
		int id = sPlayer.getEntityId();
		openedInventories.put(id, bitInventory);
		sPlayer.openInventoryWindow(bitInventory.getInventory());
	}

	public static void closeBitInventory(SpoutPlayer sPlayer) {
		int id = sPlayer.getEntityId();
		BITInventory bitInventory = openedInventories.get(id);
		saveBitInventory(sPlayer, bitInventory);
		openedInventories.remove(id);
	}

	public static void saveBitInventory(SpoutPlayer sPlayer, BITInventory inv) {
		// sPlayer.sendMessage("block:"+inv.getBlock().getType()+"own:"+
		// " name:"+inv.getName()+" coo:"+inv.getCoOwners()+" usecost:"+inv.getUseCost());
		if (inv != null) {
			saveBitInventory(sPlayer, inv.getBlock(), inv.getOwner(),
					inv.getName(), inv.getCoOwners(), inv.getInventory(),
					inv.getUseCost());
		} else {
			sPlayer.sendMessage("BITInventory error: Inventory is null in SaveBitInventory!");
		}
	}

	public static void saveBitInventory(SpoutPlayer sPlayer, SpoutBlock block,
			String owner, String name, String coowners, Inventory inventory,
			int useCost) {
		String query;
		boolean createBookshelf = true;
		int cost = G333Config.BOOKSHELF_COST;
		if (isBitInventoryCreated(block)) {
			for (int i = 0; i < inventory.getSize(); i++) {
				query = "UPDATE " + BIT.bitInventoryTable + " SET owner='"
						+ owner + "', coowners='" + coowners + "', usecost="
						+ useCost + ", slotNo=" + i + ", itemstack_type="
						+ inventory.getItem(i).getTypeId()
						+ ", itemstack_amount="
						+ inventory.getItem(i).getAmount()
						+ ", itemstack_durability="
						+ inventory.getItem(i).getDurability() + " WHERE x = "
						+ block.getX() + " AND y = " + block.getY()
						+ " AND z = " + block.getZ() + " AND world='"
						+ block.getWorld().getName() + "' AND slotno=" + i
						+ ";";
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
			}
			G333Messages.sendNotification(sPlayer, "Bookshelf updated.");
		} else {
			// NEW BOOKSHELF
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
						createBookshelf = false;
					}
				}
			}
			if (createBookshelf) {
				// sPlayer.sendMessage("Inventorysize:" + inventory.getSize());
				for (int i = 0; i < inventory.getSize(); i++) {
					query = "INSERT INTO "
							+ BIT.bitInventoryTable
							+ " (x, y, z, world, owner, name, coowners, usecost, "
							+ "slotno, itemstack_type, itemstack_amount, itemstack_durability "
							+ ") VALUES (" + block.getX() + ", " + block.getY()
							+ ", " + block.getZ() + ", '"
							+ block.getWorld().getName() + "', '" + owner
							+ "', '" + name + "', '" + coowners + "', "
							+ useCost + ", " + i + ", "
							+ inventory.getItem(i).getTypeId() + ","
							+ inventory.getItem(i).getAmount() + ","
							+ inventory.getItem(i).getDurability() + " );";
					// sPlayer.sendMessage("Insert:" + query);
					if (G333Config.DEBUG_SQL)
						sPlayer.sendMessage(ChatColor.YELLOW
								+ "Insert to bookshelf: " + query);
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
				}
				G333Messages.sendNotification(sPlayer, "Bookshelf created.");
			} else {
				sPlayer.sendMessage("You dont have enough money. Cost is:"
						+ cost);
			}
		}
	}

	public static Boolean isBitInventoryCreated(SpoutBlock block) {
		// TODO: Implement a HASHMAP for testing if the Inventory is created.
		// G333Messages.showInfo("isLocked was called");
		String query = "SELECT * FROM " + BIT.bitInventoryTable
				+ " WHERE (x = " + block.getX() + " AND y = " + block.getY()
				+ " AND z = " + block.getZ() + " AND world='"
				+ block.getWorld().getName() + "');";
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
		return false;
	}

	public static BITInventory loadBitInventory(SpoutPlayer sPlayer,
			SpoutBlock sBlock) {
		int size = loadBitInventorySize(sBlock);
		String name = "Bookshelf";
		Inventory inventory = SpoutManager.getInventoryBuilder().construct(
				size, name);
		String owner = sPlayer.getName();
		String coOwners = "";
		int useCost = 0;
		String query = "SELECT * FROM " + BIT.bitInventoryTable
				+ " WHERE (x = " + sBlock.getX() + " AND y = " + sBlock.getY()
				+ " AND z = " + sBlock.getZ() + " AND world='"
				+ sBlock.getWorld().getName() + "');";
		// sPlayer.sendMessage("select:" + query);
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
			// sPlayer.sendMessage("Result:" + result.toString());
		}
		int i = 0;
		ItemStack itemstack;
		int itemstack_typeId;
		int itemstack_amount;
		short itemstack_durability;
		try {
			while (result != null && result.next() && i < size) {
				itemstack_typeId = result.getInt("itemstack_type");
				itemstack_amount = result.getInt("itemstack_amount");
				itemstack_durability = (short) result
						.getInt("itemstack_durability");
				// sPlayer.sendMessage("i:" + i + " tp:" + itemstack_typeId
				// + " amt:" + itemstack_amount + "du:"
				// + itemstack_durability);
				if (itemstack_amount == 0) {
					inventory.clear(i);
				} else {

					itemstack = new ItemStack(itemstack_typeId,
							itemstack_amount, itemstack_durability);
					inventory.setItem(i, itemstack);
					inventory.setItem(i, itemstack);

				}
				// G333Messages.showInfo("i:" + i + " " + "tp:"
				// + inventory.getItem(i).getType() + " amt:"
				// + inventory.getItem(i).getAmount() + " du:"
				// + inventory.getItem(i).getDurability());
				name = result.getString("name");
				owner = result.getString("owner");
				coOwners = result.getString("coowners");
				useCost = result.getInt("usecost");
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// G333Messages.showInfo("name:" + name + " owner:" + owner +
		// " coowners:"
		// + coOwners + " usecost:" + useCost);
		// G333Messages.showInfo("inv:" + inventory);
		BITInventory inv = new BITInventory(sBlock, owner, name, coOwners,
				inventory, useCost);
		return inv;
	}

	public static String loadBitInventoryName(SpoutPlayer sPlayer,
			SpoutBlock block) {
		String name = "";
		String query = "SELECT * FROM " + BIT.bitInventoryTable
				+ " WHERE (x = " + block.getX() + " AND y = " + block.getY()
				+ " AND z = " + block.getZ() + " AND world='"
				+ block.getWorld().getName() + "');";
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
				name = result.getString("name");
				return name;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int loadBitInventorySize(SpoutBlock block) {
		int i = 0;
		String query = "SELECT * FROM " + BIT.bitInventoryTable
				+ " WHERE (x = " + block.getX() + " AND y = " + block.getY()
				+ " AND z = " + block.getZ() + " AND world='"
				+ block.getWorld().getName() + "');";
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

			while (result != null && result.next()) {
				i++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ((((i - 1) / 9) * 9) + 9);
	}

	public void RemoveBitInventory(SpoutPlayer sPlayer, int destroycost) {
		boolean deletelock = true;
		if (BIT.useEconomy) {
			if (BIT.plugin.Method.hasAccount(sPlayer.getName())) {
				if (BIT.plugin.Method.getAccount(sPlayer.getName()).hasEnough(
						destroycost)
						|| destroycost < 0) {
					BIT.plugin.Method.getAccount(sPlayer.getName()).subtract(
							destroycost);
					sPlayer.sendMessage("Your account ("
							+ BIT.plugin.Method.getAccount(sPlayer.getName())
									.balance() + ") has been deducted "
							+ destroycost + " bucks");
				} else {
					sPlayer.sendMessage("You dont have enough money ("
							+ BIT.plugin.Method.getAccount(sPlayer.getName())
									.balance() + "). Cost is:" + destroycost);
					deletelock = false;
				}
			}
		}
		String query = "DELETE FROM " + BIT.bitInventoryTable + " WHERE (x = "
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
					+ G333Config.BOOKSHELF_DESTROYCOST + ")");
		}

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

	public void setUseCost(int useCost) {
		this.useCost = useCost;
	}

	public String getName() {
		return name;
	}

	public int getSize() {
		return inventory.getSize();
	}
	
	public static void removeBookshelfAndDropItems(SpoutPlayer sPlayer, SpoutBlock sBlock){
		//World world = sBlock.getBlock().getWorld();
		World world = sBlock.getWorld();
		Location location = sBlock.getLocation();
		BITInventory bitInventory = BITInventory.loadBitInventory(
				sPlayer, sBlock);
		if (bitInventory != null) {
			for (int i = 0; i < bitInventory.getSize(); i++) {
				ItemStack itemstack = bitInventory.getInventory()
						.getItem(i);
				if (itemstack.getAmount() != 0) {
					world.dropItemNaturally(location, itemstack);
				}
			}
			bitInventory.RemoveBitInventory(sPlayer,
					G333Config.BOOKSHELF_DESTROYCOST);
			
		}
	}

}