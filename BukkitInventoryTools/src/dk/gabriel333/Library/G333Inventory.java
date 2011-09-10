package dk.gabriel333.Library;

import org.bukkit.Material;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.getspout.spoutapi.player.SpoutPlayer;

public class G333Inventory {

	public static void sortInventoryItems(SpoutPlayer sPlayer, Inventory inventory) {
		stackInventoryItems(sPlayer, inventory);
		orderInventoryItems(inventory, 0);
	}

	public static void stackPlayerInventoryItems(SpoutPlayer sPlayer) {
		Inventory inventory = sPlayer.getInventory();
		int i, j;
		for (i = 0; i < inventory.getSize(); i++) {
			ItemStack item1 = inventory.getItem(i);
			if ((item1.getAmount() == 64)
					|| (i < 9 && (item1.getAmount() == 0
							|| G333Inventory.isTool(item1)
							|| G333Inventory.isWeapon(item1)
							|| G333Inventory.isArmor(item1)
							|| G333Inventory.isFood(item1) ||
					// Food must be alone in slot 0-8 so you can eat it.
					G333Inventory.isVehicle(item1)))) {
				continue;
			} else {
				for (j = i + 1; j < inventory.getSize(); j++) {
					G333Inventory.moveitemInventory(sPlayer, inventory, j, i);
				}
			}
		}
		G333Inventory.orderInventoryItems(inventory, 9);
	}
	
	private static void stackInventoryItems(SpoutPlayer sPlayer,
			Inventory inventory) {
		int i, j;
		for (i = 0; i < inventory.getSize(); i++) {
			for (j = i + 1; j < inventory.getSize(); j++) {
				moveitemInventory(sPlayer, inventory, j, i);
			}
		}
	}

	public static void moveitemInventory(SpoutPlayer p, Inventory inventory, int fromslot,
			int toslot) {
		int from_amt, to_amt, total_amt;
		ItemStack fromitem, toitem;
		fromitem = inventory.getItem(fromslot);
		toitem = inventory.getItem(toslot);
		from_amt = fromitem.getAmount();
		to_amt = toitem.getAmount();
		total_amt = from_amt + to_amt;
		if ((from_amt == 0 && to_amt == 0) || from_amt == 0) {
			// Dont do anything
			return;
		} else if (to_amt == 0 && from_amt > 0) {
			to_amt = total_amt;
			from_amt = 0;
			if (G333Config.g333Config.DEBUG_SORTINVENTORY) {
				G333Messages.showInfo("1) (from,to)=(" + fromslot + ">"
						+ toslot + ") To_amt=" + to_amt + " from_amt="
						+ from_amt + " total_amt=" + total_amt);
			}
			inventory.setItem(toslot, fromitem);
			inventory.getItem(toslot).setAmount(to_amt);
			inventory.clear(fromslot);
			return;
		} else {
			// Here is to_amt > and from_amt>0 so move all what's possible if
			// it is the same kind of item.
			if (G333Permissions.hasPerm(p, "stack.*",G333Permissions.QUIET)) {
				// okay...
			} else if ((isTool(fromitem) && !G333Permissions.hasPerm(p,
					"stack.tools",G333Permissions.QUIET))
					|| (isWeapon(fromitem) && !G333Permissions.hasPerm(p,
							"stack.weapons",G333Permissions.QUIET))
					|| (isArmor(fromitem) && !G333Permissions.hasPerm(p,
							"stack.armor",G333Permissions.QUIET))
					|| (isFood(fromitem) && !G333Permissions.hasPerm(p,
							"stack.food",G333Permissions.QUIET))
					|| (isVehicle(fromitem) && !G333Permissions.hasPerm(p,
							"stack.vehicles",G333Permissions.QUIET))) {
				return;
			}
			if (fromitem.getTypeId() == toitem.getTypeId()
					&& fromitem.getDurability() == toitem.getDurability()) {
				if (fromitem.getData() != null && toitem.getData() != null) {
					if (!fromitem.getData().equals(toitem.getData())) {
						// DONT MOVE ANYTHING
						return;
					}
				}
				if (total_amt > 64) {
					to_amt = 64;
					from_amt = total_amt - 64;
					if (G333Config.g333Config.DEBUG_SORTINVENTORY) {
						G333Messages.showInfo("4) To_amt=" + to_amt
								+ " from_amt=" + from_amt + " total_amt="
								+ total_amt);
					}
					fromitem.setAmount(from_amt);
					toitem.setAmount(to_amt);
					return;
				} else {
					// total_amt is <= 64 so everything goes to toslot
					if (G333Config.g333Config.DEBUG_SORTINVENTORY) {
						G333Messages.showInfo("5) To_amt=" + to_amt
								+ " from_amt=" + from_amt + " total_amt="
								+ total_amt);
					}
					inventory.setItem(toslot, fromitem);
					inventory.getItem(toslot).setAmount(total_amt);
					inventory.clear(fromslot);
					return;
				}
			}
		}
	}

	public static void orderInventoryItems(Inventory inventory, int startslot) {
		int n = startslot;
		for (int m = 0; m < G333Config.g333Config.SORTSEQ.length; m++) {
			Material mat = Material
					.matchMaterial(G333Config.g333Config.SORTSEQ[m]);
			if (mat == null) {
				G333Messages.showError("Configuration error i config.yml.");
				G333Messages.showError(" Unknown material in SORTSEQ:"
						+ G333Config.g333Config.SORTSEQ[m]);
			} else if (inventory.contains(mat)) {
				for (int i = n; i < inventory.getSize(); i++) {
					if (inventory.getItem(i).getType() == mat) {
						n++;
						continue;
					} else {
						for (int j = i + 1; j < inventory.getSize(); j++) {
							if (inventory.getItem(j).getType() == mat) {
								switchInventoryItems(inventory, i, j);
								n++;
								break;
							}
						}
					}
				}

			}
		}
	}

	private static void switchInventoryItems(Inventory inventory, int slot1,
			int slot2) {
		ItemStack item = inventory.getItem(slot1);
		inventory.setItem(slot1, inventory.getItem(slot2));
		inventory.setItem(slot2, item);
	}


	// ********************************************************************
	// ********************************************************************
	// *****************INVENTORY TOOLS************************************
	// ********************************************************************
	// ********************************************************************

	// Contains the list of tools
//	protected static final int tools[] = { 256, 257, 258, 269, 270, 271, 273,
//			274, 275, 277, 278, 279, 284, 285, 286, 290, 291, 292, 293, 294,
//			346 };

	// Check if it is a tool
	public static boolean isTool(ItemStack item) {
		for (String i : G333Config.vehicles) {
			if (item.getTypeId() == Integer.valueOf(i)) {
				return true;
			}
		}
		return false;
	}

	// Contains the list of weapons
//	protected static final int weapons[] = { 267, 268, 272, 276, 283, 261 };

	// Check if it is a weapon
	public static boolean isWeapon(ItemStack item) {
		for (String i : G333Config.weapons) {
			if (item.getTypeId() == Integer.valueOf(i)) {
				return true;
			}
		}
		return false;
	}

	// Contains the list of armors
//	protected static final int armors[] = { 298, 299, 300, 301, 302, 303, 304,
//			305, 306, 307, 308, 309, 310, 311, 312, 313, 314, 315, 316, 317 };

	// Check if it is a armor
	public static boolean isArmor(ItemStack item) {
		for (String i : G333Config.armors) {
			if (item.getTypeId() == Integer.valueOf(i)) {
				return true;
			}
		}
		return false;
	}

	// Contains the list of foods
//	protected static final int foods[] = { 260, 282, 297, 319, 320, 322, 349,
//			350, 354, 357 };

	// Check if it is food
	public static boolean isFood(ItemStack item) {
		for (String i : G333Config.foods) {
			if (item.getTypeId() == Integer.valueOf(i)) {
				return true;
			}
		}
		return false;
	}

	// Contains the list of vehicles
//	protected static final int vehicles[] = { 328, 333, 342, 343 };

	// Check if it is a vehicles
	public static boolean isVehicle(ItemStack item) {
		for (String i : G333Config.vehicles) {
			if (item.getTypeId() == Integer.valueOf(i)) {
				return true;
			}
		}
		return false;
	}

	// ********************************************************************
	// ********************************************************************
	// *****************UNUSED*********************************************
	// ********************************************************************
	// ********************************************************************

}
