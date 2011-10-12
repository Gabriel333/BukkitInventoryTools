package dk.gabriel333.BukkitInventoryTools;

import java.util.HashMap;
import java.util.UUID;

import org.getspout.spoutapi.gui.GenericTextField;
import org.getspout.spoutapi.gui.PopupScreen;

public class BITPlayerData {
	
	private PopupScreen popupGetPincode;
	private PopupScreen popupSetPincode;
	private GenericTextField pincode;
	private GenericTextField owner;
	private GenericTextField closetimer;
	private GenericTextField coOwners;
	private GenericTextField useCost;
	private GenericTextField connectedTo;
	private GenericTextField shared;

	public BITPlayerData(PopupScreen popupGetPincode,
			PopupScreen popupSetPincode, GenericTextField pincode,
			GenericTextField owner,
			GenericTextField closetimer,
			GenericTextField coOwners,
			GenericTextField useCost,
			GenericTextField connectedTo,
			GenericTextField shared) {
		this.popupGetPincode = popupGetPincode;
		this.popupSetPincode = popupSetPincode;
		this.pincode = pincode;
		this.owner = owner;
		this.closetimer = closetimer;
		this.coOwners = coOwners;
		this.useCost = useCost;
		this.shared = shared;
	}

	public void setUserno(Integer userno) {
		this.userno = userno;
	}

	public void setPopupGetPincode(PopupScreen popupGetPincode) {
		this.popupGetPincode = popupGetPincode;
	}

}