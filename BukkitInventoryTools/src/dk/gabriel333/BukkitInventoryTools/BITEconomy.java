package dk.gabriel333.BukkitInventoryTools;

import dk.gabriel333.register.payment.Method;
import dk.gabriel333.Library.G333Messages;

public class BITEconomy {

	public Method economy;

	public boolean hasAccount(String name){
		return economy.hasAccount(name);
	}

	public boolean chargePlayer(String name, float amount){
		if(hasAccount(name)) {
			economy.getAccount(name).subtract(amount);
			return true;
		} else {
			G333Messages.showWarning("Could not fetch economy details for " + name);
			return false;
		}
	}

	public boolean hasEnough(String name, float amount) {
		return economy.getAccount(name).hasEnough(amount);
	}

	public double balance(String name){
		return economy.getAccount(name).balance();
	}

	public String formattedBalance(double amount){
		return economy.format(amount);
	}
}