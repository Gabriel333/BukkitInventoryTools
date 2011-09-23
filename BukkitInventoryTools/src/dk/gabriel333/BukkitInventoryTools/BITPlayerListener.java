package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.Lever;
//import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.player.SpoutPlayer;
//import org.getspout.spoutapi.sound.SoundManager;

import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;
import dk.gabriel333.Library.G333Permissions;

public class BITPlayerListener extends PlayerListener {

	//private static BIT plugin;

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

						if (digilock.getPincode().equals("")||digilock.getPincode().equals("fingerprint")) {
							// OPEN CHEST BY FINGERPRINT / NAME
							if (digilock.isOwner(sPlayer)
									|| digilock.isCoowner(sPlayer)) {
								SpoutChest sChest = (SpoutChest) block
										.getState();
								Inventory inv = sChest.getLargestInventory();
								G333Messages.sendNotification(sPlayer,
										"Opened by fingerprint");
								
								sPlayer.openInventoryWindow(inv);
							} else {
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
							}
						} else {
							if (sPlayer.isSpoutCraftEnabled()) {
								BITGui.getPincode(sPlayer, block);
							} else {
								sPlayer.sendMessage("Locked with Digilock.");
							}
						}

					}
					// HANDLING THE DOOR
					else if (BITDigiLock.isDoor(block)) {
						if (digilock.getPincode().equals("")||digilock.getPincode().equals("fingerprint")) {
							// TOGGLE DOOR BY FINGERPRINT / NAME
							if (digilock.isOwner(sPlayer)
									|| digilock.isCoowner(sPlayer)) {
								BITDigiLock.openDigiLockSound(block);
								G333Messages.sendNotification(sPlayer,
										"Used with fingerprint");
							} else {
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
								BITDigiLock.closeDoor(sPlayer, block);
								event.setCancelled(true);
							}
						} else {
							// ASK FOR PINCODE
							if (!BITDigiLock.isDoorOpen(sPlayer, block)) {
								event.setCancelled(true);
								if (sPlayer.isSpoutCraftEnabled()) {
									BITGui.getPincode(sPlayer, block);
									if (digilock.getPincode().equals(
											BITGui.pincode2.getText())) {
										BITDigiLock.openDoor(sPlayer, block);
									}
								} else {
									sPlayer.sendMessage("Digilock'ed by "
											+ sPlayer.getName());
									event.setCancelled(true);
								}
							}
						}
					}
					// HANDLING TRAP_DOOR
					else if (block.getType().equals(Material.TRAP_DOOR)) {
						if (digilock.getPincode().equals("")||digilock.getPincode().equals("fingerprint")) {
							// TOGGLE DOOR BY FINGERPRINT / NAME
							if (digilock.isOwner(sPlayer)
									|| digilock.isCoowner(sPlayer)) {
								BITDigiLock.openDigiLockSound(block);
								G333Messages.sendNotification(sPlayer,
										"Used with fingerprint");
							} else {
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
								BITDigiLock.closeDoor(sPlayer, block);
								event.setCancelled(true);
							}
						} else {
							// ASK FOR PINCODE
							if (!BITDigiLock.isTrapdoorOpen(sPlayer, block)) {
								event.setCancelled(true);
								if (sPlayer.isSpoutCraftEnabled()) {
									BITGui.getPincode(sPlayer, block);
									if (digilock.getPincode().equals(
											BITGui.pincode2.getText())) {
										BITDigiLock
												.openTrapdoor(sPlayer, block);
									}
								} else {
									sPlayer.sendMessage("Digilock'ed by "
											+ sPlayer.getName());
									event.setCancelled(true);
								}
							}
						}
					}
					// HANDLING DISPENCER
					else if (block.getType().equals(Material.DISPENSER)) {
						if (digilock.getPincode().equals("")||digilock.getPincode().equals("fingerprint")) {
							// USE DSIPENSER BY FINGERPRINT (playername)
							if (digilock.isOwner(sPlayer)
									|| digilock.isCoowner(sPlayer)) {
								G333Messages.sendNotification(sPlayer,
										"Opened with fingerprint");
							} else {
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
								event.setCancelled(true);
							}
						} else {
							if (sPlayer.isSpoutCraftEnabled()) {
								BITGui.getPincode(sPlayer, block);
								if (digilock.getPincode().equals(
										BITGui.pincode2.getText())) {

								} else {
									// if (G333Config.g333Config.DEBUG_DOOR)
									event.setCancelled(true);
								}
							} else {
								sPlayer.sendMessage("Digilock'ed by "
										+ sPlayer.getName());
								event.setCancelled(true);
							}
						}

					}
					// HANDLING FURNACE
					else if (block.getType().equals(Material.FURNACE)) {
						if (sPlayer.isSpoutCraftEnabled()) {
							if (digilock.getPincode().equals("")||digilock.getPincode().equals("fingerprint")) {
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

								} else {
									// if (G333Config.g333Config.DEBUG_DOOR)
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
						Lever lever = (Lever) block.getState().getData();
						if (digilock.getPincode().equals("")||digilock.getPincode().equals("fingerprint")) {
							// USE LEVER/STONE_BUTTON BY FINGERPRINT
							// (playername)
							if (digilock.isOwner(sPlayer)
									|| digilock.isCoowner(sPlayer)) {
								G333Messages.sendNotification(sPlayer,
										"Used with fingerprint");
								if (lever.isPowered()) {
									lever.setPowered(false);
								} else {
									lever.setPowered(true);
								}
							} else {
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
								lever.setPowered(false);
								event.setCancelled(true);
							}
						} else {
							if (lever.isPowered()) {
								// DO IT (TURN LEVER OFF)
								lever.setPowered(false);
							} else {
								if (sPlayer.isSpoutCraftEnabled()) {
									BITGui.getPincode(sPlayer, block);
									if (digilock.getPincode().equals(
											BITGui.pincode2.getText())) {
										// DO IT
										lever.setPowered(true);
									} else {
										lever.setPowered(false);
										event.setCancelled(true);
									}
								} else {
									sPlayer.sendMessage("Digilock'ed by "
											+ sPlayer.getName());
									event.setCancelled(true);
								}
							}
						}
					}
					// HANDLING STONE_BUTTON
					else if (block.getType().equals(Material.STONE_BUTTON)) {

						if (digilock.getPincode().equals("")||digilock.getPincode().equals("fingerprint")) {
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
							if (sPlayer.isSpoutCraftEnabled()) {
								BITGui.getPincode(sPlayer, block);
								if (digilock.getPincode().equals(
										BITGui.pincode2.getText())) {
									// okay - go on

								} else {

									event.setCancelled(true);
								}
							} else {
								sPlayer.sendMessage("Digilock'ed by "
										+ sPlayer.getName());
								event.setCancelled(true);
							}
						}

					}
					// BOOKSHELF
					else if ((block.getType().equals(Material.BOOKSHELF))) {

					}
					// HANDLING SIGN and SIGN_POST
					else if (BITDigiLock.isSign(block)) {
						if (digilock.getPincode().equals("")||digilock.getPincode().equals("fingerprint")) {
							// USE SIGN BY FINGERPRINT (playername)
							if (digilock.isOwner(sPlayer)
									|| digilock.isCoowner(sPlayer)) {
								G333Messages.sendNotification(sPlayer,
										"Used with fingerprint");
							} else {
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
								event.setCancelled(true);
							}
						} else {
							if (sPlayer.isSpoutCraftEnabled()) {
								BITGui.getPincode(sPlayer, block);
								if (digilock.getPincode().equals(
										BITGui.pincode2.getText())) {
									// okay - go on
								} else {
									// if (G333Config.g333Config.DEBUG_DOOR)
									event.setCancelled(true);
								}
							} else {
								sPlayer.sendMessage("Digilock'ed by "
										+ sPlayer.getName());
								event.setCancelled(true);
							}
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
