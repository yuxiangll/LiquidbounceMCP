package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.event.ClickWindowEvent;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;

@ModuleInfo(name = "InventoryMove", description = "Allows you to walk while an inventory is opened.", category = ModuleCategory.MOVEMENT)
public class InventoryMove extends Module {
	private static InventoryMove instance;

	public static InventoryMove getInstance(){
		return instance;
	}

	public InventoryMove(){
		instance = this;
	}

	private BoolValue noDetectableValue = new BoolValue("NoDetectable", false);
	public BoolValue aacAdditionProValue = new BoolValue("AACAdditionPro", false);
	private BoolValue noMoveClicksValue = new BoolValue("NoMoveClicks", false);

	@EventTarget
	public void onUpdate(UpdateEvent event) {
		if (!(mc.currentScreen instanceof GuiChat) && !(mc.currentScreen instanceof GuiIngameMenu) && (!noDetectableValue.get() || !(mc.currentScreen instanceof GuiContainer))) {
			mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
			mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
			mc.gameSettings.keyBindRight.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindRight);
			mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
			mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
			mc.gameSettings.keyBindSprint.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSprint);
		}
	}

	@EventTarget
	public void onClick(ClickWindowEvent event) {
		if (noMoveClicksValue.get() && MovementUtils.isMoving()){
			event.cancelEvent();
		}
	}

	@Override
	public void onDisable() {
		if (!GameSettings.isKeyDown(mc.gameSettings.keyBindForward) || mc.currentScreen != null)
			mc.gameSettings.keyBindForward.pressed = false;
		if (!GameSettings.isKeyDown(mc.gameSettings.keyBindBack) || mc.currentScreen != null)
			mc.gameSettings.keyBindBack.pressed = false;
		if (!GameSettings.isKeyDown(mc.gameSettings.keyBindRight) || mc.currentScreen != null)
			mc.gameSettings.keyBindRight.pressed = false;
		if (!GameSettings.isKeyDown(mc.gameSettings.keyBindLeft) || mc.currentScreen != null)
			mc.gameSettings.keyBindLeft.pressed = false;
		if (!GameSettings.isKeyDown(mc.gameSettings.keyBindJump) || mc.currentScreen != null)
			mc.gameSettings.keyBindJump.pressed = false;
		if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSprint) || mc.currentScreen != null)
			mc.gameSettings.keyBindSprint.pressed = false;
	}

	public String getTag(){
		if(aacAdditionProValue.get())
			return "AACAdditionPro";

		return null;
	}
}
