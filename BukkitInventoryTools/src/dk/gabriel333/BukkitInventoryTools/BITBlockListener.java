package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.inventory.ItemStack;
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
import org.bukkit.material.Door;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.gabriel333.Library.G333Config;
import dk.gabriel333.Library.G333Messages;

public class BITBlockListener extends BlockListener {

	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		// TODO: MAYBE THERE IS AN MEMORY LEAK HERE???
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (!BITDigiLock.isLockable(block))
			return;
		if (BITDigiLock.isLocked(block)) {
			if (G333Config.DEBUG_EVENTS)
				G333Messages.showInfo("BlockRedstoneEvt:"
						+ event.getBlock().getType() + " getOC:"
						+ event.getOldCurrent() + " getNC:"
						+ event.getNewCurrent());
			if (BITDigiLock.isDoubleDoor(block)) {

			} else if (BITDigiLock.isDoor(block)) {
				Door door = (Door) block.getState().getData();
				if (!door.isOpen()) {
					event.setNewCurrent(event.getOldCurrent());
				}
			}
		}
	}

	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (!BITDigiLock.isLockable(block))
			return;
		if (BITDigiLock.isLocked(block)) {
			event.setCancelled(true);
			if (G333Config.DEBUG_EVENTS) {
				G333Messages.showInfo("BlockPhysicsEvt:"
						+ event.getBlock().getType() + " getCT:"
						+ event.getChangedType());
			}
		}
	}

	@Override
	public void onBlockFromTo(BlockFromToEvent event) {
		super.onBlockFromTo(event);
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (!BITDigiLock.isLockable(block))
			return;
		if (BITDigiLock.isLocked(block)) {
			event.setCancelled(true);
			if (G333Config.DEBUG_EVENTS) {
				G333Messages.showInfo("BlockFromTo:"
						+ event.getBlock().getType() + " ToBlk:"
						+ event.getToBlock().getType());
			}
		}
	}

	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		SpoutBlock sBlock = (SpoutBlock) event.getBlock();
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		SpoutBlock blockOnTop = sBlock.getRelative(BlockFace.UP);
		if (!BITDigiLock.isLockable(sBlock))
			return;
		if (BITDigiLock.isLocked(sBlock) || BITDigiLock.isLocked(blockOnTop)) {
			sPlayer.damage(5);
			event.setCancelled(true);
		} else {
			Location location = event.getBlock().getLocation();
			if (BITDigiLock.isBookshelf(sBlock)) {
				if (BITInventory.isBitInventoryCreated(sBlock)) {
					World world = event.getBlock().getWorld();
					BITInventory bitInventory = BITInventory.loadBitInventory(
							sPlayer, sBlock);
					if (bitInventory != null) {
						for (int i = 0; i < bitInventory.getSize(); i++) {
							ItemStack itemstack = bitInventory.getInventory()
									.getItem(i);
							if (itemstack.getAmount() != 0) {
								world.dropItemNaturally(location, itemstack);
							}
						}
						bitInventory.RemoveBitInventory(sPlayer,
								G333Config.BOOKSHELF_DESTROYCOST);
					}
				}
			}
		}

	}

	public void onBlockDamage(BlockDamageEvent event) {
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		// SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		if (!BITDigiLock.isLockable(block))
			return;
		if (BITDigiLock.isLocked(block)) {
			// sPlayer.damage(1);
			event.setCancelled(true);
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
		if (!BITDigiLock.isLockable(block))
			return;
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
		if (!BITDigiLock.isLockable(block))
			return;
		if (BITDigiLock.isLocked(block)) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockSpread(BlockSpreadEvent event) {
		super.onBlockSpread(event);
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (!BITDigiLock.isLockable(block))
			return;
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
		if (!BITDigiLock.isLockable(block))
			return;
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
		if (!BITDigiLock.isLockable(block))
			return;
		if (BITDigiLock.isLocked(block)) {
			// event.setCancelled(true);
		}
	}

	@Override
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		super.onBlockPistonExtend(event);
		if (event.isCancelled())
			return;
		SpoutBlock block = (SpoutBlock) event.getBlock();
		if (!BITDigiLock.isLockable(block))
			return;
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
		if (!BITDigiLock.isLockable(block))
			return;
		if (BITDigiLock.isLocked(block)) {
			event.setCancelled(true);
		}
	}

}
