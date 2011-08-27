package dk.rocologo.BukkitInventoryTools;

import org.bukkit.Material;
import org.bukkit.block.Block;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.rocologo.Library.*;

public class BITCommandSort implements CommandExecutor {
	public BITCommandSort(BIT instance) {
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		SpoutPlayer sPlayer = (SpoutPlayer) sender;
		Block targetblock = sPlayer.getTargetBlock(null, 4);
		if (BIT.isPlayer(sender)) {
			if (RLPermissions.hasPerm(sender, "sortinventory.use",
					RLPermissions.NOT_QUIET)) {
				// G333Messages.showInfo("Target is:"+targetblock.getType());
				if (targetblock.getType() == Material.CHEST) {
					SpoutChest sChest = (SpoutChest) targetblock.getState();
					RLInventory.sortInventoryItems(sPlayer,
							sChest.getLargestInventory());
					RLMessages.sendNotification(sPlayer, "Chest sorted.");
				} else {
					BITPlayer.sortinventory(sPlayer,
							ScreenType.CHAT_SCREEN);
					RLMessages.sendNotification(sPlayer, "Items sorted.");
				}
			}
			return true;
		} else {
			RLMessages.showWarning("You can't use /sort in the console.");
			return false;
		}
	}

}
