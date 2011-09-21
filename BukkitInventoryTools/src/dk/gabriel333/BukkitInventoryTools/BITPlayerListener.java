package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.Inventory;
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
				BITDigiLock digilock = BITDigiLock.loadDigiLock(sPlayer, block);
				if (G333Permissions.hasPerm(sPlayer, "digilock.use",
						G333Permissions.NOT_QUIET)) {
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
									G333Messages.sendNotification(sPlayer,
											"Opened by fingerprint");
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
					}
					// HANDLING THE DOOR
					else if (BITDigiLock.isDoor(block)) {
						if (sPlayer.isSpoutCraftEnabled()) {
							if (digilock.getPincode().equals("")) {
								// TOGGLE DOOR BY FINGERPRINT / NAME
								if (digilock.isOwner(sPlayer)
										|| digilock.isCoowner(sPlayer)) {
									G333Messages.sendNotification(sPlayer,
											"Used with fingerprint");
									// BITDigiLock.toggleDoor(sPlayer, block);
								} else {
									sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
									BITDigiLock.closeDoor(sPlayer, block);
									event.setCancelled(true);
								}
							} else {
								// ASK FOR PINCODE
								event.setCancelled(true);
								if (BITDigiLock.isDoorOpen(sPlayer, block)) {
									BITDigiLock.closeDoor(sPlayer, block);
								} else {
									BITGui.getPincode(sPlayer, block);
									if (digilock.getPincode().equals(
											BITGui.pincode2.getText())) {
										BITDigiLock.openDoor(sPlayer, block);
									} else {
										BITDigiLock.closeDoor(sPlayer, block);
									}
								}
							}
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ sPlayer.getName());
							event.setCancelled(true);
						}
					}
					// HANDLING STONE_BUTTON
					else if (block.getType().equals(Material.STONE_BUTTON)) {
						if (sPlayer.isSpoutCraftEnabled()) {
							if (digilock.getPincode().equals("")) {
								// PRESS STONE_BUTTON BY FINGERPRINT
								// (playername)
								if (digilock.isOwner(sPlayer)
										|| digilock.isCoowner(sPlayer)) {
									G333Messages.sendNotification(sPlayer,
											"Used with fingerprint");
								} else {
									sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
									event.setCancelled(true);
								}
							} else {
								BITGui.getPincode(sPlayer, block);
								if (digilock.getPincode().equals(
										BITGui.pincode2.getText())) {
									// okay - go on
									if (G333Config.g333Config.DEBUG_DOOR)
										sPlayer.sendMessage("The button was pressed.");
								} else {
									if (G333Config.g333Config.DEBUG_DOOR)
										sPlayer.sendMessage("Wrong pincode for the button.");
									event.setCancelled(true);
								}
							}
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ sPlayer.getName());
							event.setCancelled(true);
						}
					}
					// HANDLING LEVER
					else if (block.getType().equals(Material.LEVER)) {
						if (sPlayer.isSpoutCraftEnabled()) {
							if (digilock.getPincode().equals("")) {
								// USE LEVER/STONE_BUTTON BY FINGERPRINT
								// (playername)
								if (digilock.isOwner(sPlayer)
										|| digilock.isCoowner(sPlayer)) {
									G333Messages.sendNotification(sPlayer,
											"Used with fingerprint");
								} else {
									sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
									event.setCancelled(true);
								}
							} else {
								BITGui.getPincode(sPlayer, block);
								if (digilock.getPincode().equals(
										BITGui.pincode2.getText())) {
									// okay - go on
									if (G333Config.g333Config.DEBUG_DOOR)
										sPlayer.sendMessage("Lever flipped/Button pressed.");
								} else {
									if (G333Config.g333Config.DEBUG_DOOR)
										sPlayer.sendMessage("Wrong pincode for the lever/button.");
									event.setCancelled(true);
								}
							}
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ sPlayer.getName());
							event.setCancelled(true);
						}
					}
					// BOOKSHELF
					else if ((block.getType().equals(Material.BOOKSHELF))) {

					}
					// HANDLING FURNACE
					else if (block.getType().equals(Material.FURNACE)) {
						if (sPlayer.isSpoutCraftEnabled()) {
							if (digilock.getPincode().equals("")) {
								// USE FURNACE BY FINGERPRINT (playername)
								if (digilock.isOwner(sPlayer)
										|| digilock.isCoowner(sPlayer)) {
									G333Messages.sendNotification(sPlayer,
											"Used with fingerprint");
								} else {
									sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
									event.setCancelled(true);
								}
							} else {
								BITGui.getPincode(sPlayer, block);
								if (digilock.getPincode().equals(
										BITGui.pincode2.getText())) {
									// okay - go on
									if (G333Config.g333Config.DEBUG_DOOR)
										sPlayer.sendMessage("Furnace opened.");
								} else {
									if (G333Config.g333Config.DEBUG_DOOR)
										sPlayer.sendMessage("Wrong pincode!");
									event.setCancelled(true);
								}
							}
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ sPlayer.getName());
							event.setCancelled(true);
						}
					}
					// HANDLING DISPENCER
					else if (block.getType().equals(Material.DISPENSER)) {
						if (sPlayer.isSpoutCraftEnabled()) {
							if (digilock.getPincode().equals("")) {
								// USE DSIPENSER BY FINGERPRINT (playername)
								if (digilock.isOwner(sPlayer)
										|| digilock.isCoowner(sPlayer)) {
									G333Messages.sendNotification(sPlayer,
											"Used with fingerprint");
								} else {
									sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
									event.setCancelled(true);
								}
							} else {
								BITGui.getPincode(sPlayer, block);
								if (digilock.getPincode().equals(
										BITGui.pincode2.getText())) {
									// okay - go on
									if (G333Config.g333Config.DEBUG_DOOR)
										sPlayer.sendMessage("Dispencer opened.");
								} else {
									if (G333Config.g333Config.DEBUG_DOOR)
										sPlayer.sendMessage("Wrong pincode!");
									event.setCancelled(true);
								}
							}
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ sPlayer.getName());
							event.setCancelled(true);
						}

					} else {
						sPlayer.sendMessage("ERROR: BITPlayerListener. Cant handle block:"
								+ block.getType());
					}
				} else {
					// the player has not digilock.use permission.

					G333Messages.sendNotification(sPlayer,
							"Locked with Digilock.");
					event.setCancelled(true);
				}
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
