package dk.gabriel333.spoutbackpack;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class SBPlayerListener extends PlayerListener {
	private SpoutBackpack	plugin;

	public SBPlayerListener(SpoutBackpack plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		try {
			Player player = event.getPlayer();
			plugin.loadInventory(player, player.getWorld());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (((event.getFrom().getWorld().getName() != event.getTo().getWorld().getName()) || plugin.portals.contains(event.getPlayer())
				&& plugin.config.getBoolean("Backpack." + event.getTo().getWorld().getName() + ".InventoriesShare?", true) == false)) {
			try {
				Player player = event.getPlayer();
				SBInventorySaveTask.saveInventory(player, event.getFrom().getWorld());
				plugin.inventories.remove(player.getName());
				plugin.loadInventory(player, event.getTo().getWorld());
			} catch (Exception e) {
				e.printStackTrace();
			}
			plugin.portals.remove(event.getPlayer());
		}
	}

	@Override
	public void onPlayerPortal(PlayerPortalEvent event) {
		plugin.portals.add(event.getPlayer());
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event) {
		try {
			Player player = event.getPlayer();
			SBInventorySaveTask.saveInventory(player, player.getWorld());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		try {
			Player player = event.getPlayer();
			SBInventorySaveTask.saveInventory(player, player.getWorld());
			if (plugin.inventories.containsKey(player)) {
				plugin.inventories.remove(player);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
