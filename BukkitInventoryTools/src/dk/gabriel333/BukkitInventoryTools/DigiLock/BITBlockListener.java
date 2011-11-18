package dk.gabriel333.BukkitInventoryTools.DigiLock;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.BukkitInventoryTools.Inventory.BITInventory;
import dk.gabriel333.Library.BITConfig;
import dk.gabriel333.Library.BITMessages;

public class BITBlockListener extends BlockListener {

	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		// TODO: THERE IS AN MEMORY LEAK HERE!!!
		/*
		 * SpoutBlock sBlock = (SpoutBlock) event.getBlock(); if
		 * (!BITDigiLock.isLockable(sBlock)) return; if
		 * (BITDigiLock.isLocked(sBlock)) { if (G333Config.DEBUG_EVENTS)
		 * G333Messages.showInfo("BlockRedstoneEvt:" +
		 * event.getBlock().getType() + " getOC:" + event.getOldCurrent() +
		 * " getNC:" + event.getNewCurrent()); if
		 * (BITDigiLock.isDoubleDoor(sBlock)) {
		 * 
		 * } else if (BITDigiLock.isDoor(sBlock)) { Door door = (Door)
		 * sBlock.getState().getData(); if (!door.isOpen()) {
		 * event.setNewCurrent(event.getOldCurrent()); } } }
		 */
	}

	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.isCancelled())
			return;
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (sBlock != null) {
			if (!BITDigiLock.isLockable(sBlock))
				return;
			if (BITDigiLock.isLocked(sBlock)) {
				event.setCancelled(true);
				if (BITConfig.DEBUG_EVENTS) {
					BITMessages.showInfo("BlockPhysicsEvt:"
							+ event.getBlock().getType() + " getCT:"
							+ event.getChangedType());
				}
			}
		}
	}

	@Override
	public void onBlockFromTo(BlockFromToEvent event) {
		super.onBlockFromTo(event);
		if (event.isCancelled())
			return;
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BITDigiLock.isLockable(sBlock))
			return;
		if (BITDigiLock.isLocked(sBlock)) {
			if (BITDigiLock.isDoubleDoor(sBlock)) {
				BITMessages.showInfo("Tried to break doubledoor");
			}
			event.setCancelled(true);
			if (BITConfig.DEBUG_EVENTS) {
				BITMessages.showInfo("BlockFromTo:"
						+ event.getBlock().getType() + " ToBlk:"
						+ event.getToBlock().getType());
			}
		}
	}

	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		SpoutBlock blockOnTop = sBlock.getRelative(BlockFace.UP);
		if (BITDigiLock.isBookshelf(sBlock)&&!BITDigiLock.isLocked(sBlock)) {
			if (BITInventory.isBitInventoryCreated(sBlock)) {
				BITInventory.removeBookshelfAndDropItems(sPlayer, sBlock);
			}
		} else
		if (BITDigiLock.isLocked(sBlock) || BITDigiLock.isLocked(blockOnTop)) {
			sPlayer.damage(5);
			event.setCancelled(true);
		}
	}

	public void onBlockDamage(BlockDamageEvent event) {
		if (event.isCancelled())
			return;
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BITDigiLock.isLockable(sBlock))
			return;
		if (BITDigiLock.isLocked(sBlock)) {
			// sPlayer.damage(1);
			event.setCancelled(true);
		}
	}

	public void onBlockBurn(BlockBurnEvent event) {
		if (event.isCancelled())
			return;
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (BITDigiLock.isLocked(sBlock)) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockFade(BlockFadeEvent event) {
		super.onBlockFade(event);
		if (event.isCancelled())
			return;
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BITDigiLock.isLockable(sBlock))
			return;
		if (BITDigiLock.isLocked(sBlock)) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockForm(BlockFormEvent event) {
		super.onBlockForm(event);
		if (event.isCancelled())
			return;
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BITDigiLock.isLockable(sBlock))
			return;
		if (BITDigiLock.isLocked(sBlock)) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockSpread(BlockSpreadEvent event) {
		super.onBlockSpread(event);
		if (event.isCancelled())
			return;
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BITDigiLock.isLockable(sBlock))
			return;
		if (BITDigiLock.isLocked(sBlock)) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockIgnite(BlockIgniteEvent event) {
		super.onBlockIgnite(event);
		if (event.isCancelled())
			return;
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BITDigiLock.isLockable(sBlock))
			return;
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		if (BITDigiLock.isLocked(sBlock)) {
			sPlayer.damage(10);
			event.setCancelled(true);
		}
	}

	@Override
	public void onSignChange(SignChangeEvent event) {
		super.onSignChange(event);
		if (event.isCancelled())
			return;
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BITDigiLock.isLockable(sBlock))
			return;
		if (BITDigiLock.isLocked(sBlock)) {
			if (!BITConfig.LIBRARY_USESIGNEDITGUI) {
				event.setCancelled(true);
			}
		}
	}

	@Override
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		super.onBlockPistonExtend(event);
		if (event.isCancelled())
			return;
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BITDigiLock.isLockable(sBlock))
			return;
		if (BITDigiLock.isLocked(sBlock)) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		super.onBlockPistonRetract(event);
		if (event.isCancelled())
			return;
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		if (!BITDigiLock.isLockable(sBlock))
			return;
		if (BITDigiLock.isLocked(sBlock)) {
			event.setCancelled(true);
		}
	}

}
