package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.Material;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;
import dk.gabriel333.Library.G333Permissions;

public class BITPlayerListener extends PlayerListener {

	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled())
			return;
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				|| event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
			BITPlayer bPlayer = new BITPlayer(sPlayer);
			SpoutBlock block = (SpoutBlock) event.getClickedBlock();
			int id = sPlayer.getEntityId();
			if (G333Config.config.DEBUG_GUI)
				sPlayer.sendMessage("BITPlayerListener:Event:"
						+ event.getEventName() + " action:" + event.getAction()
						+ " Block:" + event.getClickedBlock());
			// HANDLING THAT PLAYER CLICK ON A BLOCK WITH A DIGILOCK
			if (BITDigiLock.isLocked(block)) {
				BITDigiLock digilock = BITDigiLock.loadDigiLock(block);
				if (G333Permissions.hasPerm(sPlayer, "digilock.use",
						G333Permissions.NOT_QUIET)) {

					// HANDLING A CHEST AND DOUBLECHEST
					if (BITDigiLock.isChest(block)) {
						if (digilock.getPincode().equals("")
								|| digilock.getPincode().equals("fingerprint")) {
							// OPEN CHEST BY FINGERPRINT / NAME
							if (digilock.isOwner(sPlayer)
									|| digilock.isCoowner(sPlayer)) {
								SpoutChest sChest = (SpoutChest) block
										.getState();
								Inventory inv = sChest.getLargestInventory();
								G333Messages.sendNotification(sPlayer,
										"Opened by fingerprint");
								BITDigiLock.playDigiLockSound(block);
								sPlayer.openInventoryWindow(inv);
							} else {
								event.setCancelled(true);
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
							}
						} else {
							event.setCancelled(true);
							if (sPlayer.isSpoutCraftEnabled()) {
								bPlayer.getPincode(sPlayer, block);
							} else {
								sPlayer.sendMessage("Locked with Digilock.");
							}
						}
					}

					// HANDLING THE DOUBLEDOOR
					else if (BITDigiLock.isDoubleDoor(block)) {
						event.setCancelled(true);
						if (digilock.getPincode().equals("")
								|| digilock.getPincode().equals("fingerprint")) {
							// TOGGLE DOOR BY FINGERPRINT / NAME
							if (digilock.isOwner(sPlayer)
									|| digilock.isCoowner(sPlayer)) {
								BITDigiLock.playDigiLockSound(block);
								if (BITDigiLock.isDoubleDoorOpen(block)) {
									BITDigiLock.closeDoubleDoor(sPlayer, block,
											0);
								} else {
									BITDigiLock.openDoubleDoor(sPlayer, block,
											digilock.getUseCost());
								}
								G333Messages.sendNotification(sPlayer,
										"Used with fingerprint");
							} else {
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
								if (BITDigiLock.isDoubleDoorOpen(block)) {
									BITDigiLock.playDigiLockSound(block);
									BITDigiLock.closeDoubleDoor(sPlayer, block,
											0);
								}
							}
						} else {
							// ASK FOR PINCODE
							if (!BITDigiLock.isDoubleDoorOpen(block)) {
								if (sPlayer.isSpoutCraftEnabled()) {
									bPlayer.getPincode(sPlayer, BITDigiLock
											.getLeftDoubleDoor(block));
								} else {
									sPlayer.sendMessage("Digilock'ed by "
											+ sPlayer.getName());
								}
							} else {
								BITDigiLock.closeDoubleDoor(sPlayer, block, 0);
								BITDigiLock.playDigiLockSound(block);
							}
						}
					}

					// HANDLING THE DOOR
					else if (BITDigiLock.isDoor(block)) {
						event.setCancelled(true);
						if (digilock.getPincode().equals("")
								|| digilock.getPincode().equals("fingerprint")) {
							// TOGGLE DOOR BY FINGERPRINT / NAME
							if (digilock.isOwner(sPlayer)
									|| digilock.isCoowner(sPlayer)) {
								BITDigiLock.playDigiLockSound(block);
								if (BITDigiLock.isDoorOpen(block)) {
									BITDigiLock.closeDoor(sPlayer, block, 0);
								} else {
									BITDigiLock.openDoor(sPlayer, block,
											digilock.getUseCost());
								}
								G333Messages.sendNotification(sPlayer,
										"Used with fingerprint");
							} else {
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
								if (BITDigiLock.isDoorOpen(block)) {
									BITDigiLock.playDigiLockSound(block);
									BITDigiLock.closeDoor(sPlayer, block, 0);
								}
							}
						} else {
							// ASK FOR PINCODE
							if (!BITDigiLock.isDoorOpen(block)) {
								if (sPlayer.isSpoutCraftEnabled()) {
									bPlayer.getPincode(sPlayer, block);
								} else {
									sPlayer.sendMessage("Digilock'ed by "
											+ sPlayer.getName());
								}
							} else {
								BITDigiLock.closeDoor(sPlayer, block, 0);
								BITDigiLock.playDigiLockSound(block);
							}
						}
					}

					// HANDLING TRAP_DOOR
					else if (block.getType().equals(Material.TRAP_DOOR)) {
						event.setCancelled(true);
						if (digilock.getPincode().equals("")
								|| digilock.getPincode().equals("fingerprint")) {
							// TOGGLE DOOR BY FINGERPRINT / NAME
							if (digilock.isOwner(sPlayer)
									|| digilock.isCoowner(sPlayer)) {
								BITDigiLock.playDigiLockSound(block);
								if (BITDigiLock.isTrapdoorOpen(sPlayer, block)) {
									BITDigiLock.closeTrapdoor(sPlayer, block);
								} else {
									BITDigiLock.openTrapdoor(sPlayer, block,
											digilock.getUseCost());
								}
								G333Messages.sendNotification(sPlayer,
										"Used with fingerprint");
							} else {
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
								if (BITDigiLock.isTrapdoorOpen(sPlayer, block)) {
									BITDigiLock.playDigiLockSound(block);
									BITDigiLock.closeDoor(sPlayer, block, 0);
								}
							}
						} else {
							// ASK FOR PINCODE
							if (!BITDigiLock.isTrapdoorOpen(sPlayer, block)) {
								if (sPlayer.isSpoutCraftEnabled()) {
									bPlayer.getPincode(sPlayer, block);
								} else {
									sPlayer.sendMessage("Digilock'ed by "
											+ sPlayer.getName());
								}
							} else {
								BITDigiLock.closeTrapdoor(sPlayer, block);
								BITDigiLock.playDigiLockSound(block);
							}
						}
					}

					// HANDLING DISPENCER
					else if (block.getType().equals(Material.DISPENSER)) {
						if (digilock.getPincode().equals("")
								|| digilock.getPincode().equals("fingerprint")) {
							// USE DISPENSER BY FINGERPRINT (playername)
							if (digilock.isOwner(sPlayer)
									|| digilock.isCoowner(sPlayer)) {
								G333Messages.sendNotification(sPlayer,
										"Opened with fingerprint");
								BITDigiLock.playDigiLockSound(digilock
										.getBlock());
								Dispenser dispenser = (Dispenser) block
										.getState();
								Inventory inv = dispenser.getInventory();
								sPlayer.openInventoryWindow(inv);
							} else {
								event.setCancelled(true);
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
							}
						} else {
							event.setCancelled(true);
							if (sPlayer.isSpoutCraftEnabled()) {
								bPlayer.getPincode(sPlayer, block);

							} else {
								sPlayer.sendMessage("Digilock'ed by "
										+ sPlayer.getName());
							}
						}
					}

					// HANDLING FURNACE
					else if (block.getType().equals(Material.FURNACE)) {
						if (digilock.getPincode().equals("")
								|| digilock.getPincode().equals("fingerprint")) {
							// USE FURNACE BY FINGERPRINT (playername)
							if (digilock.isOwner(sPlayer)
									|| digilock.isCoowner(sPlayer)) {
								G333Messages.sendNotification(sPlayer,
										"Used with fingerprint");
								BITDigiLock.playDigiLockSound(digilock
										.getBlock());
								Furnace furnace = (Furnace) block.getState();
								Inventory inv = furnace.getInventory();
								sPlayer.openInventoryWindow(inv);
							} else {
								event.setCancelled(true);
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
							}
						} else {
							event.setCancelled(true);
							if (sPlayer.isSpoutCraftEnabled()) {
								bPlayer.getPincode(sPlayer, block);
							} else {
								sPlayer.sendMessage("Digilock'ed by "
										+ sPlayer.getName());
							}
						}
					}

					// HANDLING LEVER
					else if (block.getType().equals(Material.LEVER)) {
						Lever lever = (Lever) block.getState().getData();
						if (digilock.getPincode().equals("")
								|| digilock.getPincode().equals("fingerprint")) {
							// USE LEVER BY FINGERPRINT
							if (digilock.isOwner(sPlayer)
									|| digilock.isCoowner(sPlayer)) {
								G333Messages.sendNotification(sPlayer,
										"Used with fingerprint");
								BITDigiLock.playDigiLockSound(block);
								if (lever.isPowered()) {
									BITDigiLock.leverOff(sPlayer, block);
								} else {
									BITDigiLock.leverOn(sPlayer, block,
											digilock.getUseCost());
								}
								BITDigiLock.playDigiLockSound(block);
							} else {
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
								event.setCancelled(true);
								BITDigiLock.leverOff(sPlayer, block);
							}
						} else {
							if (lever.isPowered()) {
								BITDigiLock.leverOff(sPlayer, block);
							} else {
								if (sPlayer.isSpoutCraftEnabled()) {
									bPlayer.getPincode(sPlayer, block);
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
						if (digilock.getPincode().equals("")
								|| digilock.getPincode().equals("fingerprint")) {
							// PRESS STONE_BUTTON BY FINGERPRINT
							// (playername)
							Button button = (Button) block.getState().getData();
							if (digilock.isOwner(sPlayer)
									|| digilock.isCoowner(sPlayer)) {
								G333Messages.sendNotification(sPlayer,
										"Used with fingerprint");
								BITDigiLock.playDigiLockSound(block);
								if (!button.isPowered()) {
									BITDigiLock.pressButtonOn(sPlayer, block,
											digilock.getUseCost());
								}
							} else {
								event.setCancelled(true);
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
							}
						} else {
							event.setCancelled(true);
							if (sPlayer.isSpoutCraftEnabled()) {
								bPlayer.getPincode(sPlayer, block);
								if (digilock.getPincode().equals(
										BITPlayer.pincode.get(id).getText())) {
									// okay - go on
								} else {
									// event.setCancelled(true);
								}
							} else {
								sPlayer.sendMessage("Digilock'ed by "
										+ sPlayer.getName());
								// event.setCancelled(true);
							}
						}
					}

					// HANDLING SIGN and SIGN_POST
					else if (BITDigiLock.isSign(block)) {
						if (digilock.getPincode().equals("")
								|| digilock.getPincode().equals("fingerprint")) {
							// USE SIGN BY FINGERPRINT (playername)
							if (digilock.isOwner(sPlayer)
									|| digilock.isCoowner(sPlayer)) {
								G333Messages.sendNotification(sPlayer,
										"Used with fingerprint");
								if (sPlayer.isSpoutCraftEnabled()) {
									Sign sign = (Sign) block.getState();
									sPlayer.openSignEditGUI(sign);
									sign.update();
								} else {

								}
							} else {
								event.setCancelled(true);
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
							}
						} else {
							if (sPlayer.isSpoutCraftEnabled()) {
								bPlayer.getPincode(sPlayer, block);
								if (digilock.getPincode().equals(
										BITPlayer.pincode.get(id).getText())) {
									// okay - go on
								} else {
									// if (G333Config.config.DEBUG_DOOR)
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
						if (digilock.getPincode().equals("")
								|| digilock.getPincode().equals("fingerprint")) {
							// USE SIGN BY FINGERPRINT (playername)
							if (digilock.isOwner(sPlayer)
									|| digilock.isCoowner(sPlayer)) {
								G333Messages.sendNotification(sPlayer,
										"Used with fingerprint");
								if (sPlayer.isSpoutCraftEnabled()) {
									BITInventory bitInventory = BITInventory
											.loadBitInventory(sPlayer, block);
									bitInventory.openBitInventory(sPlayer, bitInventory);
									//sPlayer.openInventoryWindow(bitInventory.getInventory());
									//sPlayer.sendMessage("inv:"+bitInventory.toString());
								} else {

								}
							} else {
								event.setCancelled(true);
								sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
							}
						} else {
							if (sPlayer.isSpoutCraftEnabled()) {
								bPlayer.getPincode(sPlayer, block);
								if (digilock.getPincode().equals(
										BITPlayer.pincode.get(id).getText())) {
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
				if (G333Config.config.DEBUG_GUI) {
					sPlayer.sendMessage("There is no digilock on this block");
					if (BITDigiLock.isChest(block)) {

					}
					// HANDLING THE DOUBLEDOOR
					else if (BITDigiLock.isDoubleDoor(block)) {

					}
					// HANDLING THE DOOR
					else if (BITDigiLock.isDoor(block)) {

					}
					// HANDLING TRAP_DOOR
					else if (block.getType().equals(Material.TRAP_DOOR)) {

					}
					// HANDLING DISPENCER
					else if (block.getType().equals(Material.DISPENSER)) {

					}
					// HANDLING FURNACE
					else if (block.getType().equals(Material.FURNACE)) {

					}
					// HANDLING LEVER
					else if (block.getType().equals(Material.LEVER)) {

					}
					// HANDLING STONE_BUTTON
					else if (block.getType().equals(Material.STONE_BUTTON)) {

					}

					// HANDLING SIGN and SIGN_POST
					else if (BITDigiLock.isSign(block)) {

					}
					// BOOKSHELF
					else if ((block.getType().equals(Material.BOOKSHELF))) {

					}
				}

			}
		} // else the action was LEFT_CLICK_AIR or RIGHT_CLICK_AIR
	}

	public void onItemHeldChange(PlayerItemHeldEvent event) {
		if (G333Config.config.DEBUG_GUI) {
			SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
			sPlayer.sendMessage("Event:" + event.getEventName() + " type:"
					+ event.getType());
		}
	}

	public void onPlayerLogin(PlayerLoginEvent event) {
		int id = event.getPlayer().getEntityId();
			addUserData(id);
	}

	public void onPlayerKick(PlayerKickEvent event) {
		int id = event.getPlayer().getEntityId();
			removeUserData(id);
	}

	public void onPlayerJoin(PlayerJoinEvent event) {
		int id = event.getPlayer().getEntityId();
			addUserData(id);
	}

	public void onPlayerQuit(PlayerQuitEvent event) {
		int id = event.getPlayer().getEntityId();
		removeUserData(id);
	}

	private void removeUserData(int id) {
		if (BITPlayer.userno.containsKey(id)) {
		BITPlayer.userno.remove(id);
		BITPlayer.pincodePopupScreen.remove(id);
		BITPlayer.pincode.remove(id);
		BITPlayer.owner.remove(id);
		BITPlayer.coOwners.remove(id);
		BITPlayer.closetimer.remove(id);
		BITPlayer.useCost.remove(id);
		BITPlayer.connectedTo.remove(id);
		}
	}

	private void addUserData(int id) {
		if (!BITPlayer.userno.containsKey(id)) {
			BITPlayer.userno.put(id, new Integer(0));
			BITPlayer.pincodePopupScreen.put(id, new GenericPopup());
			BITPlayer.pincode.put(id, new GenericTextField());
			BITPlayer.owner.put(id, new GenericTextField());
			BITPlayer.coOwners.put(id, new GenericTextField());
			BITPlayer.closetimer.put(id, new GenericTextField());
			BITPlayer.useCost.put(id, new GenericTextField());
			BITPlayer.connectedTo.put(id, new GenericTextField());
		}
	}

}
