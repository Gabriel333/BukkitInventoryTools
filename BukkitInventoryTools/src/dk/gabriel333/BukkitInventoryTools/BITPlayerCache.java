package dk.gabriel333.BukkitInventoryTools;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.PopupScreen;

import dk.gabriel333.BukkitInventoryTools.BITPlayerData;

public class BITPlayerCache {

	private Map<Player, BITPlayerData> playerDatatable = new HashMap<Player, BITPlayerData>();

	public BITPlayerCache() {
	}

	public void createCache(Player player, 
    	    PopupScreen popupGetPincode,
    		PopupScreen popupSetPincode,
    		GenericTextField pincode,
    		GenericTextField owner,
    		GenericTextField closetimer,
    		GenericTextField coOwners,
    		GenericTextField useCost,
    		GenericTextField connectedTo,
    		GenericTextField shared) {
   	        if(!playerDatatable.containsKey(player)) {
    	            BITPlayerData data = new BITPlayerData(popupGetPincode,
    		popupSetPincode,pincode,
    		owner,
    		closetimer,
    		coOwners,
    		useCost,
    		connectedTo,
    		shared);
    	            playerDatatable.put(player, data);
    	        }}

	public void removeCache(Player player) {
		if (playerDatatable.containsKey(player)) {
			playerDatatable.remove(player);
		}
	}

	public void recreateCache(Player player) {
		removeCache(player);
		createCache(player, new PopupScreen(), new PopupScreen(), new GenericTextField(), new GenericTextField(),new GenericTextField(),new GenericTextField(),new GenericTextField(),new GenericTextField(),new GenericTextField());
	}

	private BITPlayerData getPlayerData(Player player) {
		if (playerDatatable.containsKey(player)) {
			return playerDatatable.get(player);
		}
		return null;
	}

	public boolean isPlayerInCache(Player player) {
		if (playerDatatable.containsKey(player)) {
			return true;
		}
		return false;
	}

	public void setPlayerRegistered(Player player, boolean newvalue) {
		getPlayerData(player).setRegistered(newvalue);
	}

	public boolean isPlayerRegistered(Player player) {
		return isPlayerInCache(player) ? getPlayerData(player).isRegistered()
				: false;
	}

	public void setPlayerAuthenticated(Player player, boolean newvalue) {
		getPlayerData(player).setAuthenticated(newvalue);
	}

	public boolean isPlayerAuthenticated(Player player) {
		return isPlayerInCache(player) ? getPlayerData(player)
				.isAuthenticated() : false;
	}
}
