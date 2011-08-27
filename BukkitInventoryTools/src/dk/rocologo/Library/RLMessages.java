package dk.rocologo.Library;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.getspout.spoutapi.player.SpoutPlayer;

import dk.rocologo.Library.RLPlugin;

public class RLMessages {
	
    public static final Logger l = Logger.getLogger("Minecraft");
    
    private static String PLUGIN_NAME=RLPlugin.PLUGIN_NAME;
      	   	
    public static void showInfo(String message) {
    	l.log(Level.INFO, "["+PLUGIN_NAME+"] " + message);
    }

    public static void showError(String message) {
    	l.log(Level.SEVERE, "["+PLUGIN_NAME+"] " + message);
    }

    public static void showWarning(String message) {
    	l.log(Level.WARNING, "["+PLUGIN_NAME+"] " + message);
    }

    public static void showStackTrace(Throwable t) {
    	l.log(Level.SEVERE, t.getMessage(), t);
    }
    
    public static void sendNotification(SpoutPlayer sPlayer, String string) {
    	//SpoutPlayer sPlayer = (SpoutPlayer) player;
    	if (sPlayer.isSpoutCraftEnabled()  && (sPlayer instanceof SpoutPlayer)) {
    	//if(Safety.spout && (sPlayer instanceof SpoutPlayer)) { 
			sPlayer.sendNotification(sPlayer.getName(), string, Material.DIAMOND);
		} else {
			sPlayer.sendMessage(string);
		}
    }
}
