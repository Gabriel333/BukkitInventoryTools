package dk.gabriel333.BukkitInventoryTools;

import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.event.input.KeyReleasedEvent;

public class BITKeyboardListener extends InputListener {

	@Override
	public void onKeyPressedEvent(KeyPressedEvent event) {
		// SpoutPlayer sPlayer = event.getPlayer();
		// int id = event.getPlayer().getEntityId();
		// String keypressed = event.getKey().name();
		BIT.holdingKey.put(event.getPlayer().getEntityId(), event.getKey()
				.name());
		// sPlayer.sendMessage("sPlayer:" + sPlayer.getName() + "Pressed key:"+
		// keypressed);
	}

	@Override
	public void onKeyReleasedEvent(KeyReleasedEvent event) {
		// SpoutPlayer sPlayer = event.getPlayer();
		// int id = sPlayer.getEntityId();
		// Keyboard keyreleased = event.getKey();
		BIT.holdingKey.put(event.getPlayer().getEntityId(), "");
		// sPlayer.sendMessage("sPlayer:" + sPlayer.getName() + "Released key:"+
		// keyreleased);
	}

}
