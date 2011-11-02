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
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;

import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.block.SpoutChest;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.BIT;
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
		// mousebuttons, the rest is only with RIGHT_CLICK
		if (!(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event
				.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
			return;
		}
		SpoutBlock sBlock = (SpoutBlock) event.getClickedBlock();
		// for faster handling
		if (!BITDigiLock.isLockable(sBlock)) {
			return;
		}
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		ItemStack itemInHand = sPlayer.getInventory().getItemInHand();
		if (itemInHand.getType().equals(sBlock.getType())) {
			// This allows the user to place a new Bookshelf on a Bookshelf
			// where the Inventory is created.
			return;
		}
		int id = sPlayer.getEntityId();

		if (G333Config.DEBUG_GUI)
			sPlayer.sendMessage("BITPlayerListener:" + " Your action was:"
					+ event.getAction() + " on sBlock:" + sBlock.getType()
					+ " with:" + itemInHand.getType() + " while holding:"
					+ BIT.holdingKey.get(id));

		// Call setPincode
		if (sPlayer.isSpoutCraftEnabled()
				&& BITDigiLock.isLockable(sBlock)
				&& BIT.holdingKey.get(id).equals("KEY_LCONTROL")
				&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
				&& (G333Permissions.hasPerm(sPlayer, "digilock.create",
						G333Permissions.NOT_QUIET) || G333Permissions.hasPerm(
						sPlayer, "digilock.admin", G333Permissions.NOT_QUIET))) {
			event.setCancelled(true);
			BITDigiLock.setPincode(sPlayer, sBlock);

			// Call openEditSignGUI
		} else

		// HANDLING THAT PLAYER CLICK ON A BLOCK WITH A DIGILOCK
		if (BITDigiLock.isLocked(sBlock)) {
			BITDigiLock digilock = BITDigiLock.loadDigiLock(sBlock);

			// HANDLING A LOCKED CHEST AND DOUBLECHEST
			if (BITDigiLock.isChest(sBlock)) {
				if ((digilock.getPincode().equals("") || digilock.getPincode()
						.equalsIgnoreCase("fingerprint"))
						&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
						&& G333Permissions.hasPerm(sPlayer, "digilock.use",
								G333Permissions.NOT_QUIET)) {
					// OPEN CHEST BY FINGERPRINT / NAME
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						SpoutChest sChest = (SpoutChest) sBlock.getState();
						Inventory inv = sChest.getLargestInventory();
						G333Messages.sendNotification(sPlayer,
								"Opened by fingerprint");
						BITDigiLock.playDigiLockSound(sBlock);
						sPlayer.openInventoryWindow(inv);
					} else {
						event.setCancelled(true);
						sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
					}
				} else {
					event.setCancelled(true);
					if (sPlayer.isSpoutCraftEnabled()
							&& event.getAction().equals(
									Action.RIGHT_CLICK_BLOCK)
							&& G333Permissions.hasPerm(sPlayer, "digilock.use",
									G333Permissions.NOT_QUIET)) {
						BITDigiLock.getPincode(sPlayer, sBlock);
					} else {
						sPlayer.sendMessage("Locked with Digilock.");
					}
				}
			}

			// HANDLING A LOCKED DOUBLEDOOR
			else if (BITDigiLock.isDoubleDoor(sBlock)) {
				event.setCancelled(true);
				if (digilock.getPincode().equals("")
						|| digilock.getPincode()
								.equalsIgnoreCase("fingerprint")
						&& G333Permissions.hasPerm(sPlayer, "digilock.use",
								G333Permissions.NOT_QUIET)) {
					// TOGGLE DOOR BY FINGERPRINT / NAME
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						BITDigiLock.playDigiLockSound(sBlock);
						if (BITDigiLock.isDoubleDoorOpen(sBlock)) {
							BITDigiLock.closeDoubleDoor(sPlayer, sBlock, 0);
						} else {
							BITDigiLock.openDoubleDoor(sPlayer, sBlock,
									digilock.getUseCost());
						}
						G333Messages.sendNotification(sPlayer,
								"Used with fingerprint");
					} else {
						sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
						if (BITDigiLock.isDoubleDoorOpen(sBlock)) {
							BITDigiLock.playDigiLockSound(sBlock);
							BITDigiLock.closeDoubleDoor(sPlayer, sBlock, 0);
						}
					}
				} else {
					// ASK FOR PINCODE
					if (!BITDigiLock.isDoubleDoorOpen(sBlock)
							&& G333Permissions.hasPerm(sPlayer, "digilock.use",
									G333Permissions.NOT_QUIET)) {
						if (sPlayer.isSpoutCraftEnabled()) {
							BITDigiLock.getPincode(sPlayer,
									BITDigiLock.getLeftDoubleDoor(sBlock));
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ sPlayer.getName());
						}
					} else {
						BITDigiLock.closeDoubleDoor(sPlayer, sBlock, 0);
						BITDigiLock.playDigiLockSound(sBlock);
					}
				}
			}

			// HANDLING A LOCKED DOOR
			else if (BITDigiLock.isDoor(sBlock)) {
				event.setCancelled(true);
				if (digilock.getPincode().equals("")
						|| digilock.getPincode()
								.equalsIgnoreCase("fingerprint")
						&& G333Permissions.hasPerm(sPlayer, "digilock.use",
								G333Permissions.NOT_QUIET)) {
					// TOGGLE DOOR BY FINGERPRINT / NAME
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						BITDigiLock.playDigiLockSound(sBlock);
						if (BITDigiLock.isDoorOpen(sBlock)) {
							BITDigiLock.closeDoor(sPlayer, sBlock, 0);
						} else {
							BITDigiLock.openDoor(sPlayer, sBlock,
									digilock.getUseCost());
						}
						G333Messages.sendNotification(sPlayer,
								"Used with fingerprint");
					} else {
						sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
						if (BITDigiLock.isDoorOpen(sBlock)) {
							BITDigiLock.playDigiLockSound(sBlock);
							BITDigiLock.closeDoor(sPlayer, sBlock, 0);
						}
					}
				} else {
					// ASK FOR PINCODE
					if (!BITDigiLock.isDoorOpen(sBlock)) {
						if (sPlayer.isSpoutCraftEnabled()
								&& G333Permissions.hasPerm(sPlayer,
										"digilock.use",
										G333Permissions.NOT_QUIET)) {
							BITDigiLock.getPincode(sPlayer, sBlock);
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ sPlayer.getName());
						}
					} else {
						BITDigiLock.closeDoor(sPlayer, sBlock, 0);
						BITDigiLock.playDigiLockSound(sBlock);
					}
				}
			}

			// HANDLING A LOCKED TRAP_DOOR
			else if (sBlock.getType().equals(Material.TRAP_DOOR)) {
				event.setCancelled(true);
				if (digilock.getPincode().equals("")
						|| digilock.getPincode()
								.equalsIgnoreCase("fingerprint")
						&& G333Permissions.hasPerm(sPlayer, "digilock.use",
								G333Permissions.NOT_QUIET)) {
					// TOGGLE DOOR BY FINGERPRINT / NAME
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						BITDigiLock.playDigiLockSound(sBlock);
						if (BITDigiLock.isTrapdoorOpen(sPlayer, sBlock)) {
							BITDigiLock.closeTrapdoor(sPlayer, sBlock);
						} else {
							BITDigiLock.openTrapdoor(sPlayer, sBlock,
									digilock.getUseCost());
						}
						G333Messages.sendNotification(sPlayer,
								"Used with fingerprint");
					} else {
						sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
						if (BITDigiLock.isTrapdoorOpen(sPlayer, sBlock)) {
							BITDigiLock.playDigiLockSound(sBlock);
							BITDigiLock.closeTrapdoor(sPlayer, sBlock);
						}
					}
				} else {
					// ASK FOR PINCODE
					if (!BITDigiLock.isTrapdoorOpen(sPlayer, sBlock)) {
						if (sPlayer.isSpoutCraftEnabled()
								&& G333Permissions.hasPerm(sPlayer,
										"digilock.use",
										G333Permissions.NOT_QUIET)) {
							BITDigiLock.getPincode(sPlayer, sBlock);
						} else {
							sPlayer.sendMessage("Digilock'ed by "
									+ sPlayer.getName());
						}
					} else {
						BITDigiLock.closeTrapdoor(sPlayer, sBlock);
						BITDigiLock.playDigiLockSound(sBlock);
					}
				}
			}

			// HANDLING A FENCE GATE
						else if (sBlock.getType().equals(Material.FENCE_GATE)) {
							event.setCancelled(true);
							if (digilock.getPincode().equals("")
									|| digilock.getPincode()
											.equalsIgnoreCase("fingerprint")
									&& G333Permissions.hasPerm(sPlayer, "digilock.use",
											G333Permissions.NOT_QUIET)) {
								// TOGGLE DOOR BY FINGERPRINT / NAME
								if (digilock.isOwner(sPlayer)
										|| digilock.isCoowner(sPlayer)
										|| digilock.isUser(sPlayer)) {
									BITDigiLock.playDigiLockSound(sBlock);
									if (BITDigiLock.isFenceGateOpen(sPlayer, sBlock)) {
										BITDigiLock.closeFenceGate(sPlayer, sBlock);
									} else {
										BITDigiLock.openFenceGate(sPlayer, sBlock,
												digilock.getUseCost());
									}
									G333Messages.sendNotification(sPlayer,
											"Used with fingerprint");
								} else {
									sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
									if (BITDigiLock.isFenceGateOpen(sPlayer, sBlock)) {
										BITDigiLock.playDigiLockSound(sBlock);
										BITDigiLock.closeFenceGate(sPlayer, sBlock);
									}
								}
							} else {
								// ASK FOR PINCODE
								if (!BITDigiLock.isFenceGateOpen(sPlayer, sBlock)) {
									if (sPlayer.isSpoutCraftEnabled()
											&& G333Permissions.hasPerm(sPlayer,
													"digilock.use",
													G333Permissions.NOT_QUIET)) {
										BITDigiLock.getPincode(sPlayer, sBlock);
									} else {
										sPlayer.sendMessage("Digilock'ed by "
												+ sPlayer.getName());
									}
								} else {
									BITDigiLock.closeFenceGate(sPlayer, sBlock);
									BITDigiLock.playDigiLockSound(sBlock);
								}
							}
						}
			
			// HANDLING A LOCKED DISPENCER
			else if (sBlock.getType().equals(Material.DISPENSER)) {
				if ((digilock.getPincode().equals("") || digilock.getPincode()
						.equalsIgnoreCase("fingerprint"))
						&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
						&& G333Permissions.hasPerm(sPlayer, "digilock.use",
								G333Permissions.NOT_QUIET)) {
					// USE DISPENSER BY FINGERPRINT (playername)
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						G333Messages.sendNotification(sPlayer,
								"Opened with fingerprint");
						BITDigiLock.playDigiLockSound(digilock.getBlock());
						Dispenser dispenser = (Dispenser) sBlock.getState();
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
									Action.RIGHT_CLICK_BLOCK)
							&& G333Permissions.hasPerm(sPlayer, "digilock.use",
									G333Permissions.NOT_QUIET)) {
						BITDigiLock.getPincode(sPlayer, sBlock);
					} else {
						sPlayer.sendMessage("Digilock'ed by "
								+ sPlayer.getName());
					}
				}
			}

			// HANDLING FURNACE
			else if (sBlock.getType().equals(Material.FURNACE)) {
				if ((digilock.getPincode().equals("") || digilock.getPincode()
						.equalsIgnoreCase("fingerprint")
						&& G333Permissions.hasPerm(sPlayer, "digilock.use",
								G333Permissions.NOT_QUIET))
						&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					// USE FURNACE BY FINGERPRINT (playername)
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						G333Messages.sendNotification(sPlayer,
								"Used with fingerprint");
						BITDigiLock.playDigiLockSound(digilock.getBlock());
						Furnace furnace = (Furnace) sBlock.getState();
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
									Action.RIGHT_CLICK_BLOCK)
							&& G333Permissions.hasPerm(sPlayer, "digilock.use",
									G333Permissions.NOT_QUIET)) {
						BITDigiLock.getPincode(sPlayer, sBlock);
					} else {
						sPlayer.sendMessage("Digilock'ed by "
								+ sPlayer.getName());
					}
				}
			}

			// HANDLING LEVER
			else if (sBlock.getType().equals(Material.LEVER)) {
				Lever lever = (Lever) sBlock.getState().getData();
				SpoutBlock nextLockableBlock = digilock.getNextLockableBlock(
						sPlayer, sBlock);
				if (digilock.getPincode().equals("")
						|| digilock.getPincode()
								.equalsIgnoreCase("fingerprint")
						&& G333Permissions.hasPerm(sPlayer, "digilock.use",
								G333Permissions.NOT_QUIET)) {
					// USE LEVER BY FINGERPRINT
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						if (nextLockableBlock != null) {
							if (BITDigiLock.isLocked(nextLockableBlock)) {
								G333Messages.sendNotification(sPlayer,
										"Used with fingerprint");
								BITDigiLock.playDigiLockSound(sBlock);
								if (lever.isPowered()) {
									BITDigiLock.leverOff(sPlayer, sBlock);
								} else {
									BITDigiLock.leverOn(sPlayer, sBlock,
											digilock.getUseCost());
								}
								BITDigiLock.playDigiLockSound(sBlock);
							} else {
								sPlayer.sendMessage("The connected block "
										+ nextLockableBlock.getType()
										+ " is not locked. Please lock it.");
								if (BITDigiLock.isDoubleDoor(nextLockableBlock)) {
									BITDigiLock.closeDoubleDoor(sPlayer,
											nextLockableBlock, 0);
								} else if (BITDigiLock
										.isDoor(nextLockableBlock)) {
									BITDigiLock.closeDoor(sPlayer,
											nextLockableBlock, 0);
								} else if (BITDigiLock
										.isTrapdoor(nextLockableBlock)) {
									BITDigiLock.closeTrapdoor(sPlayer,
											nextLockableBlock);
								}
								BITDigiLock.leverOff(sPlayer, sBlock);
								event.setCancelled(true);

							}
						} else {
							sPlayer.sendMessage("The lever is not connected to anything.");
						}
					} else {
						sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
						event.setCancelled(true);
						BITDigiLock.leverOff(sPlayer, sBlock);
					}
				} else { // LEVER with pincode
					if (nextLockableBlock != null) {
						if (BITDigiLock.isLocked(nextLockableBlock)) {
							if (!BITDigiLock.isLeverOn(sBlock)) {
								if (sPlayer.isSpoutCraftEnabled()
										&& G333Permissions.hasPerm(sPlayer,
												"digilock.use",
												G333Permissions.NOT_QUIET)) {
									BITDigiLock.getPincode(sPlayer, sBlock);
								} else {
									sPlayer.sendMessage("Digilock'ed by "
											+ sPlayer.getName());
									event.setCancelled(true);
								}
							} else {
								if (BITDigiLock.isDoubleDoor(nextLockableBlock)) {
									BITDigiLock.closeDoubleDoor(sPlayer,
											nextLockableBlock, 0);
								} else if (BITDigiLock
										.isDoor(nextLockableBlock)) {
									BITDigiLock.closeDoor(sPlayer,
											nextLockableBlock, 0);
								} else if (BITDigiLock
										.isTrapdoor(nextLockableBlock)) {
									BITDigiLock.closeTrapdoor(sPlayer,
											nextLockableBlock);
								}
								BITDigiLock.leverOff(sPlayer, sBlock);
							}
						} else {
							sPlayer.sendMessage("The connected block "
									+ nextLockableBlock.getType()
									+ " is not locked. Please lock it.");
							if (BITDigiLock.isDoubleDoor(nextLockableBlock)) {
								BITDigiLock.closeDoubleDoor(sPlayer,
										nextLockableBlock, 0);
							} else if (BITDigiLock.isDoor(nextLockableBlock)) {
								BITDigiLock.closeDoor(sPlayer,
										nextLockableBlock, 0);
							} else if (BITDigiLock
									.isTrapdoor(nextLockableBlock)) {
								BITDigiLock.closeTrapdoor(sPlayer,
										nextLockableBlock);
							}
							BITDigiLock.leverOff(sPlayer, sBlock);
							event.setCancelled(true);

						}
					} else {
						sPlayer.sendMessage("The lever is not connected to anything.");
					}
				}
			}

			// HANDLING STONE_BUTTON
			else if (sBlock.getType().equals(Material.STONE_BUTTON)) {
				if (digilock.getPincode().equals("")
						|| digilock.getPincode()
								.equalsIgnoreCase("fingerprint")
						&& G333Permissions.hasPerm(sPlayer, "digilock.use",
								G333Permissions.NOT_QUIET)) {
					// PRESS STONE_BUTTON BY FINGERPRINT
					// (playername)
					Button button = (Button) sBlock.getState().getData();
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						G333Messages.sendNotification(sPlayer,
								"Used with fingerprint");
						BITDigiLock.playDigiLockSound(sBlock);
						if (!button.isPowered()) {
							BITDigiLock.pressButtonOn(sPlayer, sBlock,
									digilock.getUseCost());
						}
					} else {
						event.setCancelled(true);
						sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
					}
				} else {
					event.setCancelled(true);
					if (sPlayer.isSpoutCraftEnabled()
							&& G333Permissions.hasPerm(sPlayer, "digilock.use",
									G333Permissions.NOT_QUIET)) {
						BITDigiLock.getPincode(sPlayer, sBlock);
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
			else if (BITDigiLock.isSign(sBlock)) {
				if ((digilock.getPincode().equals("") || digilock.getPincode()
						.equalsIgnoreCase("fingerprint"))
						&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
						&& G333Permissions.hasPerm(sPlayer, "digilock.use",
								G333Permissions.NOT_QUIET)) {
					// USE SIGN BY FINGERPRINT (playername)
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						G333Messages.sendNotification(sPlayer,
								"Used with fingerprint");
						if (sPlayer.isSpoutCraftEnabled()
								&& G333Config.LIBRARY_USESIGNEDITGUI
								&& BIT.holdingKey.get(id).equals("KEY_LSHIFT")
								&& G333Permissions.hasPerm(sPlayer,
										"digilock.use",
										G333Permissions.NOT_QUIET)) {
							Sign sign = (Sign) sBlock.getState();
							sPlayer.openSignEditGUI(sign);
						}
					} else {
						event.setCancelled(true);
						sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
					}
				} else {
					if (sPlayer.isSpoutCraftEnabled()
							&& event.getAction().equals(
									Action.RIGHT_CLICK_BLOCK)
							&& G333Permissions.hasPerm(sPlayer, "digilock.use",
									G333Permissions.NOT_QUIET)) {
						BITDigiLock.getPincode(sPlayer, sBlock);
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
			else if ((sBlock.getType().equals(Material.BOOKSHELF))) {
				if ((digilock.getPincode().equals("") || digilock.getPincode()
						.equalsIgnoreCase("fingerprint"))
						&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
						&& G333Permissions.hasPerm(sPlayer, "digilock.use",
								G333Permissions.NOT_QUIET)
						&& G333Permissions.hasPerm(sPlayer, "bookshelf.use",
								G333Permissions.NOT_QUIET)) {
					// USE SIGN BY FINGERPRINT (playername)
					if (digilock.isOwner(sPlayer)
							|| digilock.isCoowner(sPlayer)
							|| digilock.isUser(sPlayer)) {
						G333Messages.sendNotification(sPlayer,
								"Used with fingerprint");
						// if (sPlayer.isSpoutCraftEnabled()) {
						BITInventory bitInventory = BITInventory
								.loadBitInventory(sPlayer, sBlock);
						bitInventory.openBitInventory(sPlayer, bitInventory);
						// } else {

						// }
					} else {
						event.setCancelled(true);
						sPlayer.sendMessage("Your fingerprint does not match the DigiLock");
					}
				} else {
					if (sPlayer.isSpoutCraftEnabled()
							&& event.getAction().equals(
									Action.RIGHT_CLICK_BLOCK)
							&& G333Permissions.hasPerm(sPlayer, "digilock.use",
									G333Permissions.NOT_QUIET)) {
						BITDigiLock.getPincode(sPlayer, sBlock);
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
						+ sBlock.getType());
			}

			// } else {
			// // the player has not digilock.use permission.
			// G333Messages.sendNotification(sPlayer, "Locked with Digilock.");
			// event.setCancelled(true);
			// }

			// ELSE - IT WAS NOT A LOCKED BLOCK
		} else {
			if (G333Config.DEBUG_GUI) {
				sPlayer.sendMessage("There is no digilock on this block");

			}
			// HANDLING THE DOUBLEDOOR
			else if (BITDigiLock.isDoubleDoor(sBlock)) {
				// if LEFT_CLICK_BLOCK is canceled the double door cant be
				// broken. 
				if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					event.setCancelled(true);
					if (BITDigiLock.isDoubleDoorOpen(sBlock)) {
						BITDigiLock.closeDoubleDoor(sPlayer, sBlock, 0);
					} else {
						BITDigiLock.openDoubleDoor(sPlayer, sBlock, 0);
					}
				}

			}
			// HANDLING THE DOOR
			else if (BITDigiLock.isDoor(sBlock)) {

			}
			// HANDLING TRAP_DOOR
			else if (sBlock.getType().equals(Material.TRAP_DOOR)) {

			}
			// HANDLING DISPENCER
			else if (sBlock.getType().equals(Material.DISPENSER)) {

			}
			// HANDLING FURNACE
			else if (sBlock.getType().equals(Material.FURNACE)) {

			}
			// HANDLING LEVER
			else if (sBlock.getType().equals(Material.LEVER)) {
				Lever lever = (Lever) sBlock.getState().getData();
				if (lever.isPowered()) {

					BITDigiLock.leverOff(sPlayer, sBlock);
				} else {
					BITDigiLock.leverOn(sPlayer, sBlock, 0);
				}

			}
			// HANDLING STONE_BUTTON
			else if (sBlock.getType().equals(Material.STONE_BUTTON)) {

			}

			// HANDLING SIGN and SIGN_POST
			else if (BITDigiLock.isSign(sBlock)) {
				if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)
						&& G333Config.LIBRARY_USESIGNEDITGUI
						&& BIT.holdingKey.get(id).equals("KEY_LSHIFT")) {
					Sign sign = (Sign) sBlock.getState();
					sPlayer.openSignEditGUI(sign);
				}
			}

			// BOOKSHELF
			else if (sBlock.getType().equals(Material.BOOKSHELF)
					&& event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if (BITInventory.isBitInventoryCreated(sBlock)
						&& G333Permissions.hasPerm(sPlayer, "bookshelf.use",
								G333Permissions.NOT_QUIET)) {
					BITInventory bitInventory = BITInventory.loadBitInventory(
							sPlayer, sBlock);
					bitInventory.openBitInventory(sPlayer, bitInventory);
				} else if (!BITInventory.isBitInventoryCreated(sBlock)
						&& G333Permissions.hasPerm(sPlayer, "bookshelf.create",
								G333Permissions.NOT_QUIET)) {
					BITInventory.setBookshelfInventory(sPlayer, sBlock);
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

		// ItemStack item =
		// event.getPlayer().getInventory().getItem(event.getNewSlot());
		// if (item != null && item.getType() == Material.BOOK &&
		// item.getDurability() != 0) {
		// sBook book = plugin.getBookById(item.getDurability());
		// if (book != null) {
		// event.getPlayer().sendMessage(BookWorm.TEXT_COLOR +
		// BookWorm.S_READ_BOOK + ": " + BookWorm.TEXT_COLOR_2 +
		// book.getTitle());
		// }
		// }
	}

	public void onPlayerLogin(PlayerLoginEvent event) {
		int id = event.getPlayer().getEntityId();
		BIT.addUserData(id);
	}

	public void onPlayerKick(PlayerKickEvent event) {
		int id = event.getPlayer().getEntityId();
		BIT.removeUserData(id);
	}

	public void onPlayerJoin(PlayerJoinEvent event) {
		int id = event.getPlayer().getEntityId();
		BIT.addUserData(id);
	}

	public void onPlayerQuit(PlayerQuitEvent event) {
		int id = event.getPlayer().getEntityId();
		BIT.removeUserData(id);
	}

}
