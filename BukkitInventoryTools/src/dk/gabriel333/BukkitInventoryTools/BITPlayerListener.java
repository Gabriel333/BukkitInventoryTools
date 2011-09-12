package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.block.Block;

import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.material.Door;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;
import dk.gabriel333.Library.G333Permissions;

public class BITPlayerListener extends PlayerListener {

	public static BIT plugin;

	public void onPlayerInteract(PlayerInteractEvent event) {
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		Block block = event.getClickedBlock();
		if (G333Config.g333Config.DEBUG_GUI)
			sPlayer.sendMessage("BITPlayerListener:Event:"
					+ event.getEventName() + " type:" + event.getType()
					+ " Block:" + event.getClickedBlock().getType());
		if (block != null) {
			if (BITDigiLock.isLocked(sPlayer, block)) {
				if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					G333Messages.sendNotification(sPlayer,
							"Locked with DigiLock");
				} else {
					if (G333Permissions.hasPerm(sPlayer, "digilock.use",
							G333Permissions.NOT_QUIET)
							|| G333Permissions
									.hasPerm(sPlayer, "digilock.admin",
											G333Permissions.NOT_QUIET)) {
						if (BITDigiLock.isChest(block)) {
							BITGui.getPincode(sPlayer);
						} else if (BITDigiLock.isDoor(block)) {
							Door door = (Door) block.getState();
							if (door.isOpen()) {
								door.setOpen(false);
							} else {
								BITGui.getPincode(sPlayer);
							}
						} // TODO: else if furnace, dispencer....

					}
				}
				event.setCancelled(true);
			} else {
				if (G333Config.g333Config.DEBUG_GUI)
					sPlayer.sendMessage("There is no digilock on this block");
			}
		} else {
			// block is null or it is a wolf???
		}
	}

	public void onItemHeldChange(PlayerItemHeldEvent event) {

		if (G333Config.g333Config.DEBUG_GUI) {
			SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
			sPlayer.sendMessage("Event:" + event.getEventName() + " type:"
					+ event.getType());
		}
		// BackpackPlayer player =
		// BackpackManager.getBackpackPlayer(event.getPlayer());
		// player.updateDialogWindow();
		// if (player.isBackpackDisabled()) {
		// return;
		// }
		// int page = Math.min(event.getNewSlot(), player.getMaxInventoryPages()
		// - 1);
		// int current = player.getCurrentInventoryPage();
		// if (player.getPlayer().isSneaking() && page != current && page <
		// player.getMaxInventoryPages()) {
		// while (page > current) {
		// player.nextPage();
		// current++;
		// }
		// while (page < current) {
		// player.previousPage();
		// current--;
		// }
		// player.getPlayer().updateInventory();
		// }
	}

}
