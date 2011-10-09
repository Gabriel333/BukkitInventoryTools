package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.event.block.BlockListener;

import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockBreakEvent;
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
import org.bukkit.material.Door;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;

public class BITBlockListener extends BlockListener {

	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (BITDigiLock.isLocked(block)) {

			if (G333Config.config.DEBUG_EVENTS)
				G333Messages.showInfo("BlockRedstoneEvt:"
						+ event.getBlock().getType() + " getOC:"
						+ event.getOldCurrent() + " getNC:"
						+ event.getNewCurrent());

			if (BITDigiLock.isDoubleDoor(block)) {
				if (BITDigiLock.isDoubleDoorOpen(block)) {
					if (G333Config.config.DEBUG_EVENTS)
						G333Messages
								.showInfo("BlockRedstoneEvt-doubledoor is open");
					
					if (BITDigiLock.isLeftDoubleDoor(block)) {
						Door door = (Door) block.getState().getData();
						if (door.isOpen()) {
							BITDigiLock.closeDoor(block);
							//event.setNewCurrent(event.getOldCurrent());
						}
					} else {
						SpoutBlock otherblock = BITDigiLock.getLeftDoubleDoor(block);
						Door door = (Door) otherblock.getState().getData();
						if (!door.isOpen()) {
							BITDigiLock.openDoor(otherblock);
							//event.setNewCurrent(event.getOldCurrent());
						}
					}
				} else {
					if (G333Config.config.DEBUG_EVENTS)
						G333Messages
								.showInfo("BlockRedstoneEvt-doubledoor is closed");

				}
			} else if (BITDigiLock.isDoor(block)) {

				if (G333Config.config.DEBUG_EVENTS)
					G333Messages.showInfo("BlockRedstoneEvt-door");

				Door door = (Door) block.getState().getData();
				if (!door.isOpen()) {
					event.setNewCurrent(event.getOldCurrent());

				}
			}
		}

	}

	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		SpoutBlock blockOnTop = block.getRelative(BlockFace.UP);
		if (BITDigiLock.isLocked(block) || BITDigiLock.isLocked(blockOnTop)) {
			sPlayer.damage(10);
			event.setCancelled(true);
		}
	}

	public void onBlockDamage(BlockDamageEvent event) {
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (BITDigiLock.isLocked(block)) {
			event.setCancelled(true);
		}
	}

	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (BITDigiLock.isLocked(block)) {
			if (BITDigiLock.isLockable(block)
					&& BITDigiLock.isLockable(event.getChangedType())) {

				if (G333Config.config.DEBUG_EVENTS) {
					G333Messages.showInfo("BlockPhysicsEvt:"
							+ event.getBlock().getType() + " getCT:"
							+ event.getChangedType());

				}
			} else {
				event.setCancelled(true);
			}
		}
	}

	public void onBlockBurn(BlockBurnEvent event) {
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (BITDigiLock.isLocked(block)) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockFade(BlockFadeEvent event) {
		super.onBlockFade(event);
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (BITDigiLock.isLocked(block)) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockForm(BlockFormEvent event) {
		super.onBlockForm(event);
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (BITDigiLock.isLocked(block)) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockFromTo(BlockFromToEvent event) {
		super.onBlockFromTo(event);
		if (event.isCancelled())
			return;
		if (G333Config.config.DEBUG_EVENTS) {
			G333Messages.showInfo("BlockFromTo:" + event.getBlock().getType()
					+ " ToBlk:" + event.getToBlock().getType());
		}
		SpoutBlock block = (SpoutBlock) event.getBlock();
		SpoutBlock toBlock = (SpoutBlock) event.getToBlock();
		if (BITDigiLock.isLocked(block) || BITDigiLock.isLocked(toBlock)) {
			BITDigiLock digilock = BITDigiLock.loadDigiLock(block);
			BITDigiLock toDigilock = BITDigiLock.loadDigiLock(toBlock);
			if (digilock.getOwner().equalsIgnoreCase(toDigilock.getOwner())) {
				if (G333Config.config.DEBUG_EVENTS) {
					G333Messages.showInfo("BlockFromTo:"
							+ event.getBlock().getType() + " ToBlk:"
							+ event.getToBlock().getType());
				}
			} else {
				event.setCancelled(true);
			}
		}
	}

	@Override
	public void onBlockSpread(BlockSpreadEvent event) {
		super.onBlockSpread(event);
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (BITDigiLock.isLocked(block)) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockIgnite(BlockIgniteEvent event) {
		super.onBlockIgnite(event);
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		if (BITDigiLock.isLocked(block)) {
			sPlayer.damage(10);
			event.setCancelled(true);
		}
	}

	@Override
	public void onSignChange(SignChangeEvent event) {
		super.onSignChange(event);
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		// SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		if (BITDigiLock.isLocked(block)) {
			// sPlayer.damage(1);
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		super.onBlockPistonExtend(event);
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (BITDigiLock.isLocked(block)) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		super.onBlockPistonRetract(event);
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (BITDigiLock.isLocked(block)) {
			event.setCancelled(true);
		}
	}

}
