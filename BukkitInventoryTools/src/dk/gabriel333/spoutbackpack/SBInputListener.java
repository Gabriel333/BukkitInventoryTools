package dk.gabriel333.spoutbackpack;

import java.util.logging.Logger;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet100OpenWindow;
import net.minecraft.server.Packet101CloseWindow;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.inventory.Inventory;
import org.getspout.spout.inventory.CustomInventory;
import org.getspout.spoutapi.event.input.InputListener;
import org.getspout.spoutapi.event.input.KeyPressedEvent;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.ScreenType;
import org.getspout.spoutapi.keyboard.Keyboard;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SBInputListener extends InputListener {
	private SpoutBackpack	plugin;
	public static Logger	logger	= Logger.getLogger("minecraft");

	public SBInputListener(SpoutBackpack plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onKeyPressedEvent(KeyPressedEvent event) {
		if (event.getKey() == getKeyInConfig("Backpack.Key", "B")) {
			ScreenType screentype = event.getScreenType();
			if (screentype == ScreenType.WORKBENCH_INVENTORY) { return; }
			if (plugin.canOpenBackpack(event.getPlayer().getWorld(), event.getPlayer()) == false) { return; }
			if (screentype == ScreenType.CHEST_INVENTORY) {
				CraftPlayer player = (CraftPlayer) event.getPlayer();
				if (!plugin.openedInventoriesOthers.containsKey(player.getName())) {
					if (plugin.openedInventories.containsKey(player.getName())) {
						if (plugin.widgets.containsKey(player.getName()) && plugin.useWidget == true) {
							plugin.widgets.get(player.getName()).setVisible(false).setDirty(true);
						}
						Inventory inv = plugin.openedInventories.get(player.getName());
						plugin.inventories.put(player.getName(), inv.getContents());
						event.getPlayer().closeActiveWindow();
					}
				} else {
					if (plugin.openedInventories.containsKey(plugin.openedInventoriesOthers.get(player.getName()))) {
						Inventory inv = plugin.openedInventories.get(plugin.openedInventoriesOthers.get(player.getName()));
						plugin.inventories.put(plugin.openedInventoriesOthers.get(player.getName()), inv.getContents());
						plugin.openedInventoriesOthers.remove(player.getName());
						event.getPlayer().closeActiveWindow();
					}
				}
			}
			if (!plugin.openedInventoriesOthers.containsValue(event.getPlayer().getName())) {
				if (screentype != null
						&& (screentype == ScreenType.GAME_SCREEN || screentype == ScreenType.PLAYER_INVENTORY
								|| screentype == ScreenType.DISPENSER_INVENTORY || screentype == ScreenType.FURNACE_INVENTORY || screentype == ScreenType.WORKBENCH_INVENTORY)) {
					SpoutPlayer player = event.getPlayer();
					if (plugin.method != null && plugin.useWidget == true) {
						if (plugin.widgets.containsKey(player.getName())) {
							plugin.widgets.get(player.getName()).setVisible(true).setDirty(true);
						} else {
							GenericLabel widget = new GenericLabel("");
							if (!plugin.method.hasAccount(player.getName())) { return; }
							widget.setText(
									plugin.li.getMessage("money")
											+ String.format(plugin.method.format(plugin.method.getAccount(player.getName()).balance())))
									.setTextColor(new Color(1.0F, 1.0F, 1.0F, 1.0F)).setX(plugin.widgetX).setY(plugin.widgetY);
							player.getMainScreen().attachWidget(plugin, widget);
							plugin.widgets.put(player.getName(), widget);
						}
					}
					CustomInventory inv = new CustomInventory(plugin.allowedSize(player.getWorld(), player, true), plugin.inventoryName);
					plugin.openedInventories.put(player.getName(), inv);
					if (plugin.inventories.containsKey(player.getName())) {
						inv.setContents(plugin.inventories.get(player.getName()));
					}
					player.openInventoryWindow((Inventory) inv);
				}
			} else {
				event.getPlayer().sendMessage(
						plugin.li.getMessage("someoneisusingyour") + ChatColor.RED + plugin.inventoryName + ChatColor.WHITE
								+ plugin.li.getMessage("!"));
			}
		} else if (event.getKey() == getKeyInConfig("Workbench.Key", "W") && plugin.workbenchEnabled == true) {
			if (!plugin.openedInventoriesOthers.containsKey(event.getPlayer().getName())) {
				if (!plugin.openedInventories.containsKey(event.getPlayer().getName())) {
					if (!plugin.hasWorkbench(event.getPlayer())) { return; }
					if (plugin.workbenchInventory == true && !event.getPlayer().getInventory().contains(Material.WORKBENCH)) { return; }
					final int windowNumber = 1;
					ScreenType screentype = event.getScreenType();
					if (screentype == ScreenType.WORKBENCH_INVENTORY) {
						SpoutPlayer player = event.getPlayer();
						final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
						entityPlayer.netServerHandler.sendPacket(new Packet101CloseWindow(windowNumber));
					} else if (screentype != null
							&& (screentype == ScreenType.GAME_SCREEN || screentype == ScreenType.PLAYER_INVENTORY
									|| screentype == ScreenType.DISPENSER_INVENTORY || screentype == ScreenType.FURNACE_INVENTORY || screentype == ScreenType.CHEST_INVENTORY)) {
						SpoutPlayer player = event.getPlayer();
						final EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
						entityPlayer.netServerHandler.sendPacket(new Packet100OpenWindow(windowNumber, 1, "Crafting", 9));
						entityPlayer.activeContainer = new SBWorkbench(entityPlayer, windowNumber);
					}
				}
			}
		}
	}

	private Keyboard getKeyInConfig(String configProperty, String configDefault) {
		String keyInConfig = plugin.config.getString(configProperty, configDefault);
		plugin.saveConfig();
		Keyboard key = Keyboard.KEY_B;
		if (keyInConfig.equalsIgnoreCase("A")) {
			key = Keyboard.KEY_A;
		} else if (keyInConfig.equalsIgnoreCase("B")) {
			key = Keyboard.KEY_B;
		} else if (keyInConfig.equalsIgnoreCase("C")) {
			key = Keyboard.KEY_C;
		} else if (keyInConfig.equalsIgnoreCase("D")) {
			key = Keyboard.KEY_D;
		} else if (keyInConfig.equalsIgnoreCase("E")) {
			key = Keyboard.KEY_E;
		} else if (keyInConfig.equalsIgnoreCase("F")) {
			key = Keyboard.KEY_F;
		} else if (keyInConfig.equalsIgnoreCase("G")) {
			key = Keyboard.KEY_G;
		} else if (keyInConfig.equalsIgnoreCase("H")) {
			key = Keyboard.KEY_H;
		} else if (keyInConfig.equalsIgnoreCase("I")) {
			key = Keyboard.KEY_I;
		} else if (keyInConfig.equalsIgnoreCase("J")) {
			key = Keyboard.KEY_J;
		} else if (keyInConfig.equalsIgnoreCase("K")) {
			key = Keyboard.KEY_K;
		} else if (keyInConfig.equalsIgnoreCase("L")) {
			key = Keyboard.KEY_L;
		} else if (keyInConfig.equalsIgnoreCase("M")) {
			key = Keyboard.KEY_M;
		} else if (keyInConfig.equalsIgnoreCase("N")) {
			key = Keyboard.KEY_N;
		} else if (keyInConfig.equalsIgnoreCase("O")) {
			key = Keyboard.KEY_O;
		} else if (keyInConfig.equalsIgnoreCase("P")) {
			key = Keyboard.KEY_P;
		} else if (keyInConfig.equalsIgnoreCase("Q")) {
			key = Keyboard.KEY_Q;
		} else if (keyInConfig.equalsIgnoreCase("R")) {
			key = Keyboard.KEY_R;
		} else if (keyInConfig.equalsIgnoreCase("S")) {
			key = Keyboard.KEY_S;
		} else if (keyInConfig.equalsIgnoreCase("T")) {
			key = Keyboard.KEY_T;
		} else if (keyInConfig.equalsIgnoreCase("U")) {
			key = Keyboard.KEY_U;
		} else if (keyInConfig.equalsIgnoreCase("V")) {
			key = Keyboard.KEY_V;
		} else if (keyInConfig.equalsIgnoreCase("W")) {
			key = Keyboard.KEY_W;
		} else if (keyInConfig.equalsIgnoreCase("X")) {
			key = Keyboard.KEY_X;
		} else if (keyInConfig.equalsIgnoreCase("Y")) {
			key = Keyboard.KEY_Y;
		} else if (keyInConfig.equalsIgnoreCase("Z")) {
			key = Keyboard.KEY_Z;
		} else if (keyInConfig.equalsIgnoreCase("0")) {
			key = Keyboard.KEY_0;
		} else if (keyInConfig.equalsIgnoreCase("1")) {
			key = Keyboard.KEY_1;
		} else if (keyInConfig.equalsIgnoreCase("2")) {
			key = Keyboard.KEY_2;
		} else if (keyInConfig.equalsIgnoreCase("3")) {
			key = Keyboard.KEY_3;
		} else if (keyInConfig.equalsIgnoreCase("4")) {
			key = Keyboard.KEY_4;
		} else if (keyInConfig.equalsIgnoreCase("5")) {
			key = Keyboard.KEY_5;
		} else if (keyInConfig.equalsIgnoreCase("6")) {
			key = Keyboard.KEY_6;
		} else if (keyInConfig.equalsIgnoreCase("7")) {
			key = Keyboard.KEY_7;
		} else if (keyInConfig.equalsIgnoreCase("8")) {
			key = Keyboard.KEY_8;
		} else if (keyInConfig.equalsIgnoreCase("9")) {
			key = Keyboard.KEY_9;
		} else if (keyInConfig.equalsIgnoreCase("NONE")) {
			key = Keyboard.KEY_NONE;
		} else if (keyInConfig.equalsIgnoreCase("ESCAPE")) {
			key = Keyboard.KEY_ESCAPE;
		} else if (keyInConfig.equalsIgnoreCase("MINUS")) {
			key = Keyboard.KEY_MINUS;
		} else if (keyInConfig.equalsIgnoreCase("EQUALS")) {
			key = Keyboard.KEY_EQUALS;
		} else if (keyInConfig.equalsIgnoreCase("BACK")) {
			key = Keyboard.KEY_BACK;
		} else if (keyInConfig.equalsIgnoreCase("TAB")) {
			key = Keyboard.KEY_TAB;
		} else if (keyInConfig.equalsIgnoreCase("LBRACKET")) {
			key = Keyboard.KEY_LBRACKET;
		} else if (keyInConfig.equalsIgnoreCase("RBRACKET")) {
			key = Keyboard.KEY_RBRACKET;
		} else if (keyInConfig.equalsIgnoreCase("RETURN")) {
			key = Keyboard.KEY_RETURN;
		} else if (keyInConfig.equalsIgnoreCase("LCONTROL")) {
			key = Keyboard.KEY_LCONTROL;
		} else if (keyInConfig.equalsIgnoreCase("SEMICOLON")) {
			key = Keyboard.KEY_SEMICOLON;
		} else if (keyInConfig.equalsIgnoreCase("APOSTROPHE")) {
			key = Keyboard.KEY_APOSTROPHE;
		} else if (keyInConfig.equalsIgnoreCase("GRAVE")) {
			key = Keyboard.KEY_GRAVE;
		} else if (keyInConfig.equalsIgnoreCase("LSHIFT")) {
			key = Keyboard.KEY_LSHIFT;
		} else if (keyInConfig.equalsIgnoreCase("BACKSLASH")) {
			key = Keyboard.KEY_BACKSLASH;
		} else if (keyInConfig.equalsIgnoreCase("COMMA")) {
			key = Keyboard.KEY_COMMA;
		} else if (keyInConfig.equalsIgnoreCase("PERIOD")) {
			key = Keyboard.KEY_PERIOD;
		} else if (keyInConfig.equalsIgnoreCase("SLASH")) {
			key = Keyboard.KEY_SLASH;
		} else if (keyInConfig.equalsIgnoreCase("RSHIFT")) {
			key = Keyboard.KEY_RSHIFT;
		} else if (keyInConfig.equalsIgnoreCase("MULTIPLY")) {
			key = Keyboard.KEY_MULTIPLY;
		} else if (keyInConfig.equalsIgnoreCase("LMENU")) {
			key = Keyboard.KEY_LMENU;
		} else if (keyInConfig.equalsIgnoreCase("SPACE")) {
			key = Keyboard.KEY_SPACE;
		} else if (keyInConfig.equalsIgnoreCase("F1")) {
			key = Keyboard.KEY_F1;
		} else if (keyInConfig.equalsIgnoreCase("F2")) {
			key = Keyboard.KEY_F2;
		} else if (keyInConfig.equalsIgnoreCase("F3")) {
			key = Keyboard.KEY_F3;
		} else if (keyInConfig.equalsIgnoreCase("F4")) {
			key = Keyboard.KEY_F4;
		} else if (keyInConfig.equalsIgnoreCase("F5")) {
			key = Keyboard.KEY_F5;
		} else if (keyInConfig.equalsIgnoreCase("F6")) {
			key = Keyboard.KEY_F6;
		} else if (keyInConfig.equalsIgnoreCase("F7")) {
			key = Keyboard.KEY_F7;
		} else if (keyInConfig.equalsIgnoreCase("F8")) {
			key = Keyboard.KEY_F8;
		} else if (keyInConfig.equalsIgnoreCase("F9")) {
			key = Keyboard.KEY_F9;
		} else if (keyInConfig.equalsIgnoreCase("F10")) {
			key = Keyboard.KEY_F10;
		} else if (keyInConfig.equalsIgnoreCase("NUMLOCK")) {
			key = Keyboard.KEY_NUMLOCK;
		} else if (keyInConfig.equalsIgnoreCase("SCROLL")) {
			key = Keyboard.KEY_SCROLL;
		} else if (keyInConfig.equalsIgnoreCase("SUBTRACT")) {
			key = Keyboard.KEY_SUBTRACT;
		} else if (keyInConfig.equalsIgnoreCase("ADD")) {
			key = Keyboard.KEY_ADD;
		} else if (keyInConfig.equalsIgnoreCase("DECIMAL")) {
			key = Keyboard.KEY_DECIMAL;
		} else if (keyInConfig.equalsIgnoreCase("F11")) {
			key = Keyboard.KEY_F11;
		} else if (keyInConfig.equalsIgnoreCase("F12")) {
			key = Keyboard.KEY_F12;
		} else if (keyInConfig.equalsIgnoreCase("F13")) {
			key = Keyboard.KEY_F13;
		} else if (keyInConfig.equalsIgnoreCase("F14")) {
			key = Keyboard.KEY_F14;
		} else if (keyInConfig.equalsIgnoreCase("F15")) {
			key = Keyboard.KEY_F15;
		} else if (keyInConfig.equalsIgnoreCase("NUMPAD0")) {
			key = Keyboard.KEY_NUMPAD0;
		} else if (keyInConfig.equalsIgnoreCase("NUMPAD1")) {
			key = Keyboard.KEY_NUMPAD1;
		} else if (keyInConfig.equalsIgnoreCase("NUMPAD2")) {
			key = Keyboard.KEY_NUMPAD2;
		} else if (keyInConfig.equalsIgnoreCase("NUMPAD3")) {
			key = Keyboard.KEY_NUMPAD3;
		} else if (keyInConfig.equalsIgnoreCase("NUMPAD4")) {
			key = Keyboard.KEY_NUMPAD4;
		} else if (keyInConfig.equalsIgnoreCase("NUMPAD5")) {
			key = Keyboard.KEY_NUMPAD5;
		} else if (keyInConfig.equalsIgnoreCase("NUMPAD6")) {
			key = Keyboard.KEY_NUMPAD6;
		} else if (keyInConfig.equalsIgnoreCase("NUMPAD7")) {
			key = Keyboard.KEY_NUMPAD7;
		} else if (keyInConfig.equalsIgnoreCase("NUMPAD8")) {
			key = Keyboard.KEY_NUMPAD8;
		} else if (keyInConfig.equalsIgnoreCase("NUMPAD9")) {
			key = Keyboard.KEY_NUMPAD9;
		} else if (keyInConfig.equalsIgnoreCase("KANA")) {
			key = Keyboard.KEY_KANA;
		} else if (keyInConfig.equalsIgnoreCase("CONVERT")) {
			key = Keyboard.KEY_CONVERT;
		} else if (keyInConfig.equalsIgnoreCase("NOCONVERT")) {
			key = Keyboard.KEY_NOCONVERT;
		} else if (keyInConfig.equalsIgnoreCase("YEN")) {
			key = Keyboard.KEY_YEN;
		} else if (keyInConfig.equalsIgnoreCase("NUMPADEQUALS")) {
			key = Keyboard.KEY_NUMPADEQUALS;
		} else if (keyInConfig.equalsIgnoreCase("CIRCUMFLEX")) {
			key = Keyboard.KEY_CIRCUMFLEX;
		} else if (keyInConfig.equalsIgnoreCase("AT")) {
			key = Keyboard.KEY_AT;
		} else if (keyInConfig.equalsIgnoreCase("COLON")) {
			key = Keyboard.KEY_COLON;
		} else if (keyInConfig.equalsIgnoreCase("UNDERLINE")) {
			key = Keyboard.KEY_UNDERLINE;
		} else if (keyInConfig.equalsIgnoreCase("KANJI")) {
			key = Keyboard.KEY_KANJI;
		} else if (keyInConfig.equalsIgnoreCase("STOP")) {
			key = Keyboard.KEY_STOP;
		} else if (keyInConfig.equalsIgnoreCase("AX")) {
			key = Keyboard.KEY_AX;
		} else if (keyInConfig.equalsIgnoreCase("UNLABELED")) {
			key = Keyboard.KEY_UNLABELED;
		} else if (keyInConfig.equalsIgnoreCase("NUMPADENTER")) {
			key = Keyboard.KEY_NUMPADENTER;
		} else if (keyInConfig.equalsIgnoreCase("RCONTROL")) {
			key = Keyboard.KEY_RCONTROL;
		} else if (keyInConfig.equalsIgnoreCase("NUMPADCOMMA")) {
			key = Keyboard.KEY_NUMPADCOMMA;
		} else if (keyInConfig.equalsIgnoreCase("DIVIDE")) {
			key = Keyboard.KEY_DIVIDE;
		} else if (keyInConfig.equalsIgnoreCase("SYSRQ")) {
			key = Keyboard.KEY_SYSRQ;
		} else if (keyInConfig.equalsIgnoreCase("RMENU")) {
			key = Keyboard.KEY_RMENU;
		} else if (keyInConfig.equalsIgnoreCase("PAUSE")) {
			key = Keyboard.KEY_PAUSE;
		} else if (keyInConfig.equalsIgnoreCase("HOME")) {
			key = Keyboard.KEY_HOME;
		} else if (keyInConfig.equalsIgnoreCase("UP")) {
			key = Keyboard.KEY_UP;
		} else if (keyInConfig.equalsIgnoreCase("PRIOR")) {
			key = Keyboard.KEY_PRIOR;
		} else if (keyInConfig.equalsIgnoreCase("LEFT")) {
			key = Keyboard.KEY_LEFT;
		} else if (keyInConfig.equalsIgnoreCase("RIGHT")) {
			key = Keyboard.KEY_RIGHT;
		} else if (keyInConfig.equalsIgnoreCase("END")) {
			key = Keyboard.KEY_END;
		} else if (keyInConfig.equalsIgnoreCase("DOWN")) {
			key = Keyboard.KEY_DOWN;
		} else if (keyInConfig.equalsIgnoreCase("NEXT")) {
			key = Keyboard.KEY_NEXT;
		} else if (keyInConfig.equalsIgnoreCase("INSERT")) {
			key = Keyboard.KEY_INSERT;
		} else if (keyInConfig.equalsIgnoreCase("DELETE")) {
			key = Keyboard.KEY_DELETE;
		} else if (keyInConfig.equalsIgnoreCase("LMETA")) {
			key = Keyboard.KEY_LMETA;
		} else if (keyInConfig.equalsIgnoreCase("LWIN")) {
			key = Keyboard.KEY_LWIN;
		} else if (keyInConfig.equalsIgnoreCase("RMETA")) {
			key = Keyboard.KEY_RMETA;
		} else if (keyInConfig.equalsIgnoreCase("RWIN")) {
			key = Keyboard.KEY_RWIN;
		} else if (keyInConfig.equalsIgnoreCase("APPS")) {
			key = Keyboard.KEY_APPS;
		} else if (keyInConfig.equalsIgnoreCase("POWER")) {
			key = Keyboard.KEY_POWER;
		} else if (keyInConfig.equalsIgnoreCase("SLEEP")) {
			key = Keyboard.KEY_SLEEP;
		}
		return key;
	}
}