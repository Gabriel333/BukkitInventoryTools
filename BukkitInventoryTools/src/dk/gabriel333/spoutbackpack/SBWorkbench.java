package dk.gabriel333.spoutbackpack;

import net.minecraft.server.ContainerWorkbench;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ICrafting;

public class SBWorkbench extends ContainerWorkbench {

	public SBWorkbench(EntityPlayer entityPlayer, int windowNumber) {
		super(entityPlayer.inventory, entityPlayer.world, 0, 0, 0);
		super.windowId = windowNumber;
		super.a((ICrafting) entityPlayer);
	}

	@Override
	public boolean b(EntityHuman entityhuman) {
		return true;
	}
}