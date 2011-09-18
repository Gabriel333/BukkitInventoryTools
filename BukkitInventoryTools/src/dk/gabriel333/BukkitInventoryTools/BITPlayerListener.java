package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.Door;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;
import dk.gabriel333.Library.G333Permissions;

public class BITPlayerListener extends PlayerListener {

	public static BIT plugin;

	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled())
			return;
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				|| event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
			SpoutBlock block = (SpoutBlock) event.getClickedBlock();
			if (G333Config.g333Config.DEBUG_GUI)
				sPlayer.sendMessage("BITPlayerListener:Event:"
						+ event.getEventName() + " action:" + event.getAction()
						+ " Block:" + event.getClickedBlock());
			// HANDLING THAT PLAYER CLICK ON A BLOCK WITH A DIGILOCK
			if (BITDigiLock.isLocked(block)) {
				if (G333Permissions.hasPerm(sPlayer, "digilock.use",
						G333Permissions.NOT_QUIET)) {
					BITDigiLock digilock = BITDigiLock.loadDigiLock(sPlayer,
							block);
					if (BITDigiLock.isChest(block)
							&& event.getAction().equals(
									Action.RIGHT_CLICK_BLOCK)) {
						if (sPlayer.isSpoutCraftEnabled()) {
							if (digilock.getPincode().equals("")) {
								// OPEN CHEST BY FINGERPRINT / NAME
								if (digilock.isOwner(sPlayer)
										|| digilock.isCoowner(sPlayer)) {
									SpoutChest sChest = (SpoutChest) block
											.getState();
									Inventory inv = sChest
											.getLargestInventory();
									G333Messages.sendNotification(sPlayer, "Opened by fingerprint");
									sPlayer.openInventoryWindow(inv);
								} else {
									sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
								}
							} else {
								BITGui.getPincode(sPlayer, block);
							}
						} else {
							sPlayer.sendMessage("Locked with Digilock.");
						}
					} else if (BITDigiLock.isDoor(block)) {
						Door door = (Door) block.getState().getData();
						if (door.isOpen()) {
							digilock.closeDoor(sPlayer);
						} else {
							if (sPlayer.isSpoutCraftEnabled()) {
								if (digilock.getPincode().equals("")) {
									// OPEN DOOR BY FINGERPRINT / NAME
									if (digilock.isOwner(sPlayer)
											|| digilock.isCoowner(sPlayer)) {
										G333Messages.sendNotification(sPlayer, "Opened by fingerprint");
										digilock.openDoor(sPlayer);
									} else {
										sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
									}
								} else {
									BITGui.getPincode(sPlayer, block);
								}
							} else {
								sPlayer.sendMessage("Locked with Digilock.");
							}
						} // TODO: else if furnace, dispencer....
					}
				}
				event.setCancelled(true);
				// ELSE - IT WAS NOT A LOCKED BLOCK
			} else {

				if (G333Config.g333Config.DEBUG_GUI) {
					sPlayer.sendMessage("There is no digilock on this block");
					if (event.getMaterial().equals(Material.APPLE)) {
						sPlayer.sendMessage("Item used:"
								+ event.getItem().getType());

					}
				}

			}
		} // else the action was LEFT_CLICK_AIR or RIGHT_CLICK_AIR
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
