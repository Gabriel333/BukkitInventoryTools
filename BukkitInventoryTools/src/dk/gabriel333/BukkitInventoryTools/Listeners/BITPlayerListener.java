package dk.gabriel333.BukkitInventoryTools.Listeners;

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
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.DigiLock.BITDigiLock;
import dk.gabriel333.BukkitInventoryTools.Inventory.BITInventory;
import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;
import dk.gabriel333.Library.G333Permissions;

public class BITPlayerListener extends PlayerListener {

	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled())
			return;
		// DOORS, DOUBLEDOORS, TRAPDOORS LEVERS, BUTTON can be handled with both
		// mousebuttons, the
		// rest is only with RIGHT_CLICK
		if (!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event
				.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
			return;
		}
		SpoutBlock block = (SpoutBlock) event.getClickedBlock();
		// for faster handling
		if (!BITDigiLock.isLockable(block)) {
			return;
		}
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		int id = sPlayer.getEntityId();
		if (G333Config.DEBUG_GUI)
			sPlayer.sendMessage("BITPlayerListener:Event:"
					+ event.getEventName() + " action:" + event.getAction()
					+ " Block:" + event.getClickedBlock());
		// HANDLING THAT PLAYER CLICK ON A BLOCK WITH A DIGILOCK
		if (BITDigiLock.isLocked(block)) {
			BITDigiLock digilock = BITDigiLock.loadDigiLock(block);
			if (G333Permissions.hasPerm(sPlayer, "digilock.use",
					G333Permissions.NOT_QUIET)) {

				// HANDLING A LOCKED CHEST AND DOUBLECHEST
				if (BITDigiLock.isChest(block)) {
					if ((digilock.getPincode().equals("") || digilock
							.getPincode().equalsIgnoreCase("fingerprint"))
							&& event.getAction().equals(
									Action.RIGHT_CLICK_BLOCK)) {
						// OPEN CHEST BY FINGERPRINT / NAME
						if (digilock.isOwner(sPlayer)
								|| digilock.isCoowner(sPlayer)) {
							SpoutChest sChest = (SpoutChest) block.getState();
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
						if (sPlayer.isSpoutCraftEnabled()
								&& event.getAction().equals(
										Action.RIGHT_CLICK_BLOCK)) {
							BITDigiLock.getPincode(sPlayer, block);
						} else {
							sPlayer.sendMessage("Locked with Digilock.");
						}
					}
				}

				// HANDLING A LOCKED DOUBLEDOOR
				else if (BITDigiLock.isDoubleDoor(block)) {
					event.setCancelled(true);
					if (digilock.getPincode().equals("")
							|| digilock.getPincode().equalsIgnoreCase(
									"fingerprint")) {
						// TOGGLE DOOR BY FINGERPRINT / NAME
						if (digilock.isOwner(sPlayer)
								|| digilock.isCoowner(sPlayer)) {
							BITDigiLock.playDigiLockSound(block);
							if (BITDigiLock.isDoubleDoorOpen(block)) {
								BITDigiLock.closeDoubleDoor(sPlayer, block, 0);
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
								BITDigiLock.closeDoubleDoor(sPlayer, block, 0);
							}
						}
					} else {
						// ASK FOR PINCODE
						if (!BITDigiLock.isDoubleDoorOpen(block)) {
							if (sPlayer.isSpoutCraftEnabled()) {
								BITDigiLock.getPincode(sPlayer,
										BITDigiLock.getLeftDoubleDoor(block));
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

				// HANDLING A LOCKED DOOR
				else if (BITDigiLock.isDoor(block)) {
					event.setCancelled(true);
					if (digilock.getPincode().equals("")
							|| digilock.getPincode().equalsIgnoreCase(
									"fingerprint")) {
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
								BITDigiLock.getPincode(sPlayer, block);
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

				// HANDLING A LOCKED TRAP_DOOR
				else if (block.getType().equals(Material.TRAP_DOOR)) {
					event.setCancelled(true);
					if (digilock.getPincode().equals("")
							|| digilock.getPincode().equalsIgnoreCase(
									"fingerprint")) {
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
								BITDigiLock.getPincode(sPlayer, block);
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

				// HANDLING A LOCKED DISPENCER
				else if (block.getType().equals(Material.DISPENSER)) {
					if ((digilock.getPincode().equals("") || digilock
							.getPincode().equalsIgnoreCase("fingerprint"))
							&& event.getAction().equals(
									Action.RIGHT_CLICK_BLOCK)) {
						// USE DISPENSER BY FINGERPRINT (playername)
						if (digilock.isOwner(sPlayer)
								|| digilock.isCoowner(sPlayer)) {
							G333Messages.sendNotification(sPlayer,
									"Opened with fingerprint");
							BITDigiLock.playDigiLockSound(digilock.getBlock());
							Dispenser dispenser = (Dispenser) block.getState();
							Inventory inv = dispenser.getInventory();
							sPlayer.openInventoryWindow(inv);
						} else {
							event.setCancelled(true);
							sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
						}
					} else {
						event.setCancelled(true);
						if (sPlayer.isSpoutCraftEnabled()
								&& event.getAction().equals(
										Action.RIGHT_CLICK_BLOCK)) {
							BITDigiLock.getPincode(sPlayer, block);
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ sPlayer.getName());
						}
					}
				}

				// HANDLING FURNACE
				else if (block.getType().equals(Material.FURNACE)) {
					if ((digilock.getPincode().equals("") || digilock
							.getPincode().equalsIgnoreCase("fingerprint"))
							&& event.getAction().equals(
									Action.RIGHT_CLICK_BLOCK)) {
						// USE FURNACE BY FINGERPRINT (playername)
						if (digilock.isOwner(sPlayer)
								|| digilock.isCoowner(sPlayer)) {
							G333Messages.sendNotification(sPlayer,
									"Used with fingerprint");
							BITDigiLock.playDigiLockSound(digilock.getBlock());
							Furnace furnace = (Furnace) block.getState();
							Inventory inv = furnace.getInventory();
							sPlayer.openInventoryWindow(inv);
						} else {
							event.setCancelled(true);
							sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
						}
					} else {
						event.setCancelled(true);
						if (sPlayer.isSpoutCraftEnabled()
								&& event.getAction().equals(
										Action.RIGHT_CLICK_BLOCK)) {
							BITDigiLock.getPincode(sPlayer, block);
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
							|| digilock.getPincode().equalsIgnoreCase(
									"fingerprint")) {
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
								BITDigiLock.getPincode(sPlayer, block);
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
							|| digilock.getPincode().equalsIgnoreCase(
									"fingerprint")) {
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
							BITDigiLock.getPincode(sPlayer, block);
							if (digilock.getPincode().equals(
									BITDigiLock.pincodeGUI.get(id).getText())) {
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
					if ((digilock.getPincode().equals("") || digilock
							.getPincode().equalsIgnoreCase("fingerprint"))
							&& event.getAction().equals(
									Action.RIGHT_CLICK_BLOCK)) {
						// USE SIGN BY FINGERPRINT (playername)
						if (digilock.isOwner(sPlayer)
								|| digilock.isCoowner(sPlayer)) {
							G333Messages.sendNotification(sPlayer,
									"Used with fingerprint");
							if (sPlayer.isSpoutCraftEnabled()) {
								Sign sign = (Sign) block.getState();
								sPlayer.openSignEditGUI(sign);
							}
						} else {
							event.setCancelled(true);
							sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
						}
					} else {
						if (sPlayer.isSpoutCraftEnabled()
								&& event.getAction().equals(
										Action.RIGHT_CLICK_BLOCK)) {
							BITDigiLock.getPincode(sPlayer, block);
							if (digilock.getPincode().equals(
									BITDigiLock.pincodeGUI.get(id).getText())) {
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
					if ((digilock.getPincode().equals("") || digilock
							.getPincode().equalsIgnoreCase("fingerprint"))
							&& event.getAction().equals(
									Action.RIGHT_CLICK_BLOCK)) {
						// USE SIGN BY FINGERPRINT (playername)
						if (digilock.isOwner(sPlayer)
								|| digilock.isCoowner(sPlayer)) {
							G333Messages.sendNotification(sPlayer,
									"Used with fingerprint");
							// if (sPlayer.isSpoutCraftEnabled()) {
							BITInventory bitInventory = BITInventory
									.loadBitInventory(sPlayer, block);
							bitInventory
									.openBitInventory(sPlayer, bitInventory);
							// } else {

							// }
						} else {
							event.setCancelled(true);
							sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
						}
					} else {
						if (sPlayer.isSpoutCraftEnabled()
								&& event.getAction().equals(
										Action.RIGHT_CLICK_BLOCK)) {
							BITDigiLock.getPincode(sPlayer, block);
							if (digilock.getPincode().equals(
									BITDigiLock.pincodeGUI.get(id).getText())) {
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
				G333Messages.sendNotification(sPlayer, "Locked with Digilock.");
				event.setCancelled(true);
			}
			// ELSE - IT WAS NOT A LOCKED BLOCK
		} else {
			if (G333Config.DEBUG_GUI) {
				sPlayer.sendMessage("There is no digilock on this block");
			}
			if (BITDigiLock.isChest(block)) {

			}
			// HANDLING THE DOUBLEDOOR
			else if (BITDigiLock.isDoubleDoor(block)) {
				event.setCancelled(true);
				if (BITDigiLock.isDoubleDoorOpen(block)) {
					BITDigiLock.closeDoubleDoor(sPlayer, block, 0);
				} else {
					BITDigiLock.openDoubleDoor(sPlayer, block, 0);
				}

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
				if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					Sign sign = (Sign) block.getState();
					sPlayer.openSignEditGUI(sign);
				}

			}
			// BOOKSHELF
			else if (block.getType().equals(Material.BOOKSHELF)
					&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if (BITInventory.isBitInventoryCreated(block)
						&& G333Permissions.hasPerm(sPlayer, "bookshelf.use",
								G333Permissions.NOT_QUIET)) {
					BITInventory bitInventory = BITInventory.loadBitInventory(
							sPlayer, block);
					bitInventory.openBitInventory(sPlayer, bitInventory);
				} else if (!BITInventory.isBitInventoryCreated(block)
						&& G333Permissions.hasPerm(sPlayer, "bookshelf.create",
								G333Permissions.NOT_QUIET)) {
					
					//BITInventory bitInventory = new BITInventory(BIT.plugin);

					BITInventory.setBookshelfInventory(sPlayer, block);

				}

			}
		}
	}

	public void onItemHeldChange(PlayerItemHeldEvent event) {
		if (G333Config.DEBUG_GUI) {
			SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
			sPlayer.sendMessage("Event:" + event.getEventName() + " type:"
					+ event.getType());
		}
	}

	public void onPlayerLogin(PlayerLoginEvent event) {
		//int id = event.getPlayer().getEntityId();
		//BITPlayer.addUserData(id);
	}

	public void onPlayerKick(PlayerKickEvent event) {
		//int id = event.getPlayer().getEntityId();
		//BITPlayer.removeUserData(id);
	}

	public void onPlayerJoin(PlayerJoinEvent event) {
		//int id = event.getPlayer().getEntityId();
		//BITPlayer.addUserData(id);
	}

	public void onPlayerQuit(PlayerQuitEvent event) {
		//int id = event.getPlayer().getEntityId();
		//BITPlayer.removeUserData(id);
	}



}
