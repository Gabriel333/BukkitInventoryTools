package dk.rocologo.BukkitInventoryTools;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.rocologo.Library.RLMessages;
import dk.rocologo.Library.RLPermissions;
import dk.rocologo.BukkitInventoryTools.BITDigiLock;

public class BITCommandDigiLock implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		SpoutPlayer sPlayer = (SpoutPlayer) sender;
		Block block = sPlayer.getTargetBlock(null, 4);
		String pincode = "0000";
		String coowners="";
		String shared="";
		String owner = sPlayer.getName();
		Integer closetimer = 0; // never closes automatically
		if (!BIT.isPlayer(sPlayer)) {
			RLMessages.showError("You cant use this command in the console.");
			return false;
		} else if (RLPermissions.hasPerm(sPlayer, "digilock.use",
				RLPermissions.NOT_QUIET)
				|| RLPermissions.hasPerm(sPlayer, "digilock.admin",
						RLPermissions.NOT_QUIET)
				|| RLPermissions.hasPerm(sPlayer, "digilock.*",
						RLPermissions.NOT_QUIET)
				|| RLPermissions.hasPerm(sPlayer, "*", RLPermissions.NOT_QUIET)) {
			if (args.length == 0) {
				BITDigiLock.showDigiLockStatus();
				sPlayer.sendMessage("Usage: /digilock [lock,unlock,reset,help]");
				return true;
			}
			sPlayer.sendMessage("args: " + args[0].toString());
			sPlayer.sendMessage("args.length: " + args.length);
			String action = args[0];
			if (action.equalsIgnoreCase("lock")) {
				if (BITDigiLock.isLockable(block)) {
					sPlayer.sendMessage("You want to lock :"
							+ block.getType());
					// if USEGUI then
					if (sPlayer.isSpoutCraftEnabled()) {
						pincode = "0000";
						owner = sPlayer.getName();
						closetimer = 0;
						BITGui.openMenu(sPlayer,block, ScreenType.GAME_SCREEN);
						pincode = BITGui.pincode.toString();
						sPlayer.sendMessage("the code is:" + pincode);
						BITDigiLock.SaveDigiLock(sPlayer, block, pincode, owner,
								closetimer,coowners,shared);
					} else {
						if (args[1] != null) {
							pincode = args[1];
							sPlayer.sendMessage("the code is:" + pincode);
						}
						if (args[2] != null) {
							owner = args[2];
							sPlayer.sendMessage("Owner:" + owner);
						}
						BITDigiLock.SaveDigiLock(sPlayer, block, pincode, owner,
								closetimer,coowners,shared);
						// syntax is /safetylock lock pincode owner
						// example /safetylock lock 0000 Gabriel333
					}
				} else {
					sPlayer.sendMessage("You can't lock a "
							+ block.getType() + " block.");
					return true;
				}
			} else if (action.equalsIgnoreCase("unlock")) {
				sPlayer.sendMessage("You want to unlock :"
						+ block.getType());
				BITDigiLock.unlockDigiLock();
			} else if (action.equalsIgnoreCase("reset")) {
				sPlayer.sendMessage("You want to reset lock at:"
						+ block.getType());
				BITDigiLock.resetDigiLock();
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
