package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.Material;
import org.bukkit.block.Block;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.*;

public class BITCommandBookshelf implements CommandExecutor{

	public BITCommandBookshelf(BIT instance) {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		SpoutPlayer sPlayer =  (SpoutPlayer) sender;
		Block targetblock = sPlayer.getTargetBlock(null, 5);
		if (BIT.isPlayer(sender)) {
			if (G333Permissions.hasPerm(sender, "bookshelf.use",
					G333Permissions.NOT_QUIET)) {
				
				if (targetblock.getType() == Material.BOOKSHELF) {
					
					G333Messages.sendNotification(sPlayer, "Handle bookshelf.");
					
				                
				} else {
				
				}
			}
			return true;
		} else {
			G333Messages.showWarning("You can't use /bookshelf in the console.");
			return false;
		}
	}

}
