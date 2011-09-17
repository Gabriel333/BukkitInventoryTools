package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BITDigiLock;
import dk.gabriel333.Library.G333Messages;
import dk.gabriel333.Library.G333Permissions;

public class BITCommandDigiLock implements CommandExecutor {

	public BITCommandDigiLock(BIT plugin) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		SpoutPlayer sPlayer = (SpoutPlayer) sender;
		SpoutBlock block = (SpoutBlock) sPlayer.getTargetBlock(null, 4);
		String pincode = "0000";
		String coowners = "";
		String shared = "";
		String owner = sPlayer.getName();
		Integer closetimer = 0; // never closes automatically
		if (!BIT.isPlayer(sPlayer)) {
			G333Messages.showError("You cant use this command in the console.");
			return false;
		} else if (G333Permissions.hasPerm(sPlayer, "digilock.use",
				G333Permissions.NOT_QUIET)
				|| G333Permissions.hasPerm(sPlayer, "digilock.admin",
						G333Permissions.NOT_QUIET)
				|| G333Permissions.hasPerm(sPlayer, "digilock.*",
						G333Permissions.NOT_QUIET)
				|| G333Permissions.hasPerm(sPlayer, "*",
						G333Permissions.NOT_QUIET)) {
			// sPlayer.sendMessage("args: " + args[0].toString());
			// sPlayer.sendMessage("args.length: " + args.length);
			if (!BITDigiLock.isLocked(block)) {
				if (args.length == 0) {
					sPlayer.sendMessage("Usage: /digilock [pincode]|[unlock pincode]"
							+ "|[lock pincode]|[owner playername]|[closetimer seconds]|[remove]");
					return true;
				} else {
					String action;
					// LOCK
					// ***********************************************************
					for (int n = 0; n < args.length; n++) {
						action = args[n];
						if (action.equalsIgnoreCase("lock")) {
							pincode = args[n + 1];
							n++;
						} else if (action.equalsIgnoreCase("owner")) {
							owner = args[n + 1];
							n++;
						} else if (action.equalsIgnoreCase("closetimer")) {
							closetimer = Integer.getInteger(args[n + 1]);
							n++;
						} else if (action.equalsIgnoreCase("remove")) {

						}
					}
					// sPlayer.sendMessage("save pincode:" + pincode + " owner:"
					// + owner + " closetimer:" + closetimer
					// + " coowners:" + coowners + " shared:" + shared);
					BITDigiLock.SaveDigiLock(sPlayer, block, pincode, owner,
							closetimer, coowners, shared);
					return true;
				}
			} else { // digilock is locked
				BITDigiLock digilock = BITDigiLock.loadDigiLock(sPlayer, block);
				String action = args[0];

				// UNLOCK *************************************************
				if (action.equalsIgnoreCase("unlock") && args.length == 2) {
					if (digilock.getPincode().equalsIgnoreCase(args[1])) {
						if (BITDigiLock.isChest(digilock.getBlock())) {
							SpoutChest sChest = (SpoutChest) block.getState();
							Inventory inv = sChest.getLargestInventory();
							sPlayer.openInventoryWindow(inv);
						} else if (BITDigiLock.isDoor(digilock.getBlock())) {
							digilock.openDoor(sPlayer);
						}
					} else {
						sPlayer.sendMessage("wrong pincode!");
						sPlayer.damage(10);
						// TODO: damage player
					}
				} else if (action.equalsIgnoreCase("remove")
						&& (digilock.getOwner().equalsIgnoreCase(
								sPlayer.getName()) || G333Permissions.hasPerm(
								sPlayer, "digilock.admin",
								G333Permissions.NOT_QUIET)) && args.length == 1) {
					digilock.RemoveDigiLock(sPlayer);
				} else if (digilock.getPincode().equalsIgnoreCase(args[0])
						&& args.length == 1) {
					if (BITDigiLock.isChest(digilock.getBlock())) {
						SpoutChest sChest = (SpoutChest) block.getState();
						Inventory inv = sChest.getLargestInventory();
						sPlayer.openInventoryWindow(inv);
					} else if (BITDigiLock.isDoor(digilock.getBlock())) {
						digilock.openDoor(sPlayer);
					}
				} else {
					if (args.length == 1)
						sPlayer.damage(5);
					sPlayer.sendMessage("Wrong pincode!");
					sPlayer.sendMessage("Usage: /digilock [pincode]|[unlock pincode]"
							+ "|[lock pincode]|[owner playername]|[closetimer seconds]|[remove]");
				}
			}
			return true;
		}
		return false;
	}
}
