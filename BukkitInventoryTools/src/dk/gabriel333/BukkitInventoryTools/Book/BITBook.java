package dk.gabriel333.BukkitInventoryTools.Book;

import org.bukkit.plugin.Plugin;
import org.getspout.spoutapi.block.SpoutBlock;
import org.getspout.spoutapi.gui.GenericTextField;

import dk.gabriel333.BukkitInventoryTools.BIT;

public class BITBook {

	private BIT plugin;

	public BITBook(Plugin plugin) {
		plugin = this.plugin;
	}

	protected SpoutBlock sBlock;
	protected int slotNo;
	protected String title;
	protected String owner;
	protected String coOwners;
	protected int numberOfPages;
	protected GenericTextField[] page;
	protected int useCost;

	/**
	 * Contructs a new BITBook
	 * 
	 * @param sBlock
	 * @param slotNo
	 * @param title
	 * @param owner
	 * @param coOwners
	 * @param numberOfPages
	 * @param page
	 * @param useCost
	 */
	BITBook(SpoutBlock sBlock, int slotNo, String title, String owner,
			String coOwners, int numberOfPages, GenericTextField[] page, int useCost) {
		this.sBlock = sBlock;
		this.slotNo = slotNo;
		this.title = title;
		this.owner = owner;
		this.coOwners = coOwners;
		this.numberOfPages = numberOfPages;
		this.page = page;
		this.useCost = useCost;
	}
}