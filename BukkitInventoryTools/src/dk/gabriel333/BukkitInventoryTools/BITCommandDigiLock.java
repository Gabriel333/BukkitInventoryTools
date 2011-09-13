package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BITDigiLock;
import dk.gabriel333.Library.G333Messages;
import dk.gabriel333.Library.G333Permissions;

public class BITCommandDigiLock implements CommandExecutor {

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
				|| G333Permissions.hasPerm(sPlayer, "*", G333Permissions.NOT_QUIET)) {
			if (args.length == 0) {
				sPlayer.sendMessage("Usage: /digilock [lock,unlock,reset,help]");
				return true;
			}
			sPlayer.sendMessage("args: " + args[0].toString());
			sPlayer.sendMessage("args.length: " + args.length);
			String action = args[0];
			if (action.equalsIgnoreCase("lock")) {
				if (BITDigiLock.isLockable(block)) {
					if (sPlayer.isSpoutCraftEnabled()) {
						pincode = "0000";
						owner = sPlayer.getName();
						closetimer = 0;
						BITGui.setPincode(sPlayer, block);
						pincode = BITGui.pincode.toString();
						sPlayer.sendMessage("the code is:" + pincode);
						BITDigiLock.SaveDigiLock(sPlayer, block, pincode,
								owner, closetimer, coowners, shared);
					} else {
						if (args[1] != null) {
							pincode = args[1];
							sPlayer.sendMessage("the code is:" + pincode);
						}
						if (args[2] != null) {
							owner = args[2];
							sPlayer.sendMessage("Owner:" + owner);
						}
						BITDigiLock.SaveDigiLock(sPlayer, block, pincode,
								owner, closetimer, coowners, shared);
						// syntax is /safetylock lock pincode owner
						// example /safetylock lock 0000 Gabriel333
					}
				} else {
					sPlayer.sendMessage("You can't lock a " + block.getType()
							+ " block.");
					return true;
				}
			} else if (action.equalsIgnoreCase("unlock")) {
				sPlayer.sendMessage("You want to unlock :" + block.getType());
			} else if (action.equalsIgnoreCase("reset")) {
				sPlayer.sendMessage("You want to reset lock at:"
						+ block.getType());
				sPlayer.sendMessage("The widget was closed");
			} else {
				sPlayer.sendMessage("You did something wrong....");
				sPlayer.sendMessage("Action: " + args[0] + " No. arguments is "
						+ args.length);
			}

		}
		return true;
	}
}
