package dk.gabriel333.BukkitInventoryTools;

public class BITPlayerData {

    private boolean registered;
    private boolean authenticated;

    public BITPlayerData(boolean registered, boolean authenticated) {
        this.registered = registered;
        this.authenticated = authenticated;
    }

    public boolean isRegistered() {
        return registered;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setRegistered(boolean newvalue) {
        registered = newvalue;
    }

    public void setAuthenticated(boolean newvalue) {
        authenticated = newvalue;
    }
}