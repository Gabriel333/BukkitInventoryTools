package dk.rocologo.BukkitInventoryTools;

import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.getspout.spoutapi.player.SpoutPlayer;

public class BITPlayerListener extends PlayerListener {
	
	protected static String pincodeFromSQL;
	protected static String enteredPincode;

	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!(event.getAction().equals(Action.LEFT_CLICK_BLOCK) || event
				.getAction().equals(Action.RIGHT_CLICK_BLOCK)))
			return;
		Block block = event.getClickedBlock();
		SpoutPlayer sPlayer = (SpoutPlayer) event.getPlayer();
		if (BITDigiLock.isLocked(block)) {
			BITGui.openPincodeWindow(sPlayer);
			pincodeFromSQL=BITDigiLock.getPincodeFromSQL(sPlayer, block);
			enteredPincode=BITGui.pincode2.getText();
			if (pincodeFromSQL.equals(enteredPincode)) {
				event.setCancelled(true);
			}
			
		}

	}

}
