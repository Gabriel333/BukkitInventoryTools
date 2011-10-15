package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.Inventory;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.*;

public class BITCommandBookshelf implements CommandExecutor {

	public BITCommandBookshelf(BIT instance) {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		SpoutPlayer sPlayer = (SpoutPlayer) sender;
		if (!BIT.isPlayer(sender)) {
			G333Messages
					.showWarning("You can't use /bookshelf in the console.");
			return false;
		} else {
			SpoutBlock sBlock = (SpoutBlock) sPlayer.getTargetBlock(null, 5);
			String coowners = "";
			String name = "";
			String owner = sPlayer.getName();
			int usecost = 0;
			if (BITDigiLock.isBookshelf((SpoutBlock) sBlock)) {
				if (G333Permissions.hasPerm(sPlayer, "bookshelf.create",
						G333Permissions.NOT_QUIET)
						|| G333Permissions.hasPerm(sPlayer, "bookshelf.use",
								G333Permissions.NOT_QUIET)
						|| G333Permissions.hasPerm(sPlayer, "bookshelf.admin",
								G333Permissions.NOT_QUIET)
						|| G333Permissions.hasPerm(sPlayer, "bookshelf.*",
								G333Permissions.NOT_QUIET)
						|| G333Permissions.hasPerm(sPlayer, "*",
								G333Permissions.NOT_QUIET)) {

					if (!BITInventory.isBitInventoryCreated(sBlock)) {
						if (args.length == 0) {
							return false;
						} else {
							String action;
							// Create Bookshelf
							for (int n = 0; n < args.length; n++) {
								action = args[n];
								if (action.equalsIgnoreCase("create")) {
									if (n + 1 <= args.length) {
										name = args[n + 1];
										sPlayer.sendMessage("create " + name);
									}
									n++;
								} else if (action.equalsIgnoreCase("owner")) {
									if (n + 1 <= args.length) {
										owner = args[n + 1];
									}
									n++;
								} else if (action.equalsIgnoreCase("usecost")) {
									if (n + 1 <= args.length)
										usecost = Integer
												.getInteger(args[n + 1]);
									n++;
								} else if (action.equalsIgnoreCase("coowners")) {
									if (n + 1 <= args.length)
										coowners = args[n + 1];
									n++;
								} else if (action.equalsIgnoreCase("remove")) {
									sPlayer.sendMessage("No inventory is created on this bookshelf");
									return false;
								} else if (action.equalsIgnoreCase("info")) {
									sPlayer.sendMessage("No inventory is created on this bookshelf");
									return false;
								}
							}
							if (G333Permissions.hasPerm(sPlayer,
									"bookshelf.create",
									G333Permissions.NOT_QUIET)
									&& args[0].equalsIgnoreCase("create")) {
								Inventory inventory = SpoutManager
										.getInventoryBuilder()
										.construct(G333Config.BOOKSHELF_SIZE,
												name);
								BITInventory.SaveBitInventory(sPlayer, sBlock,
										owner, name, coowners, inventory,
										usecost);
								sPlayer.sendMessage("Created bookshelf:" + name);
								return true;
							} else {
								return false;
							}
						}
					} else {
						// bitInventory exists

						BITInventory bitInventory = BITInventory
								.loadBitInventory(sPlayer, sBlock);
						sPlayer.sendMessage("bitInventory:"
								+ bitInventory.getInventory().toString());
						if ((bitInventory.isOwner(sPlayer) || bitInventory
								.isCoowner(sPlayer)) && args.length == 0) {
							sPlayer.sendMessage("(1) open the inv name:"
									+ bitInventory.getName() + " Size:"
									+ bitInventory.getSize());
							for (int i = 0; i < 9; i++) {
								G333Messages.showInfo("i:"
										+ i
										+ " id:"
										+ bitInventory.getInventory()
												.getItem(i).getTypeId());
							}
							// sPlayer.openInventoryWindow(bitInventory);
							// bitInventory.
							return true;
						}
						String action = args[0];
						// OPEN
						// *************************************************
						if (action.equalsIgnoreCase("open")) {
							//Inventory inv = bitInventory.getInventory();
							if ((bitInventory.isOwner(sPlayer) || bitInventory
									.isCoowner(sPlayer)) && args.length == 1) {
								sPlayer.sendMessage("(2) open the inv name:"
										+ bitInventory.getName() + " Size:"
										+ bitInventory.getSize());
								bitInventory.openInventory(sPlayer);
								return true;
							}
						}
						// REMOVE
						// *************************************************
						else if (action.equalsIgnoreCase("remove")
								&& (bitInventory.getOwner().equalsIgnoreCase(
										sPlayer.getName()) || G333Permissions
										.hasPerm(sPlayer, "bookshelf.admin",
												G333Permissions.NOT_QUIET))
								&& args.length == 1) {
							bitInventory.RemoveBitInventory(sPlayer,
									G333Config.BOOKSHELF_DESTROYCOST);
							return true;
						}
						// addcoowner
						// ***************************************
						else if (action.equalsIgnoreCase("addcoowner")
								&& (bitInventory.getOwner().equalsIgnoreCase(
										sPlayer.getName()) || G333Permissions
										.hasPerm(sPlayer, "bookshelf.admin",
												G333Permissions.NOT_QUIET))
								&& args.length == 2) {
							bitInventory.addCoowner(args[1]);
							BITInventory.SaveBitInventory(sPlayer, sBlock,
									owner, name, coowners,
									bitInventory.getInventory(), usecost);
							return true;
						}
						// remcoowner
						// ***************************************
						else if (action.equalsIgnoreCase("remcoowner")
								&& (bitInventory.getOwner().equalsIgnoreCase(
										sPlayer.getName()) || G333Permissions
										.hasPerm(sPlayer, "bookshelf.admin",
												G333Permissions.NOT_QUIET))
								&& args.length == 2) {
							bitInventory.removeCoowner(args[1]);
							BITInventory.SaveBitInventory(sPlayer, sBlock,
									owner, name, coowners,
									bitInventory.getInventory(), usecost);
							return true;
						}
						// usecost ***************************************
						else if (action.equalsIgnoreCase("usecost")
								&& (bitInventory.getOwner().equalsIgnoreCase(
										sPlayer.getName()) || G333Permissions
										.hasPerm(sPlayer, "bookshelf.admin",
												G333Permissions.NOT_QUIET))
								&& args.length == 2) {
							bitInventory.setUseCost(Integer.parseInt(args[1]));
							BITInventory.SaveBitInventory(sPlayer, sBlock,
									owner, name, coowners,
									bitInventory.getInventory(), usecost);
							return true;
						}
						// INFO ***************************************
						else if (action.equalsIgnoreCase("info")) {
							sPlayer.sendMessage("The owner of this bookself is: "
									+ bitInventory.getOwner());
							sPlayer.sendMessage("The coOwner of this lock is: "
									+ bitInventory.getCoOwners());
							sPlayer.sendMessage("useCost is: "
									+ bitInventory.getUseCost());
							return true;
						} else if ((bitInventory.isOwner(sPlayer) || bitInventory
								.isCoowner(sPlayer)) && args.length == 1) {
							bitInventory.openInventory(sPlayer);
							return true;
						}

					}
				}

			}

		}
		return false;
	}
}
