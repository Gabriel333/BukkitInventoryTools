package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import com.nijikokun.register.payment.Methods;

import dk.gabriel333.Library.G333Messages;

public class BITServerListener extends ServerListener {
	private BIT plugin;
	private Methods Methods = null;

	public BITServerListener(BIT plugin) {
		this.plugin = plugin;
		this.Methods = new Methods();
	}

	public void onPluginDisable(PluginDisableEvent event) {
		if ((this.Methods != null) && (Methods.hasMethod())) {
			if (Methods.checkDisabled(event.getPlugin())) {
				this.plugin.Method = null;
				G333Messages.showInfo("Economy plugin is disabled");
				// Message when payment method is disabled.
			}
		}
	}

	public void onPluginEnable(PluginEnableEvent event) {
		if ((!Methods.hasMethod()) && (Methods.setMethod(this.plugin))) {
			this.plugin.Method = Methods.getMethod();
			if (Methods.getMethod() != null) {
				G333Messages.showInfo("Register enabled: "
						+ this.plugin.Method.getName() + " v"
						+ this.plugin.Method.getVersion() + ").");
			} else {
				G333Messages.showWarning("Register could not find a economy plugin.");
			}
		}
	}
}