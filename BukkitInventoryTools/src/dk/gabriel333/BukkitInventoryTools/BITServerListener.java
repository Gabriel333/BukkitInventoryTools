package dk.gabriel333.BukkitInventoryTools;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import dk.gabriel333.register.payment.Methods;

import dk.gabriel333.Library.G333Messages;

public class BITServerListener extends ServerListener {
	private BIT plugin;
	private Methods Methods = null;

	public BITServerListener(BIT plugin) {
		this.plugin = plugin;
		this.Methods = new Methods();
	}

	@SuppressWarnings("static-access")
	public void onPluginDisable(PluginDisableEvent event) {
		if ((this.Methods != null) && (Methods.hasMethod())) {
			if (dk.gabriel333.register.payment.Methods.checkDisabled(event
					.getPlugin())) {
				this.plugin.Method = null;
				G333Messages.showInfo("Economy plugin is disabled");
				BIT.useEconomy=false;
				// Message when payment method is disabled.
			}
		}
	}

	@SuppressWarnings("static-access")
	public void onPluginEnable(PluginEnableEvent event) {
		if ((!dk.gabriel333.register.payment.Methods.hasMethod())
				&& (Methods.setMethod(this.plugin.getServer()
						.getPluginManager()))) {
			this.plugin.Method = dk.gabriel333.register.payment.Methods
					.getMethod();
			if (dk.gabriel333.register.payment.Methods.getMethod() != null) {
				G333Messages.showInfo("Register enabled: "
						+ this.plugin.Method.getName() + " v"
						+ this.plugin.Method.getVersion() + ").");
				BIT.useEconomy=true;
			} else {
				G333Messages
						.showWarning("Register could not find a economy plugin.");
				BIT.useEconomy=false;
			}
		}
	}
}