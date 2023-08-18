package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

@ModuleInfo(name = "AirJump", description = "Allows you to jump in the mid air", category = ModuleCategory.MOVEMENT)
public class AirJump extends Module {
	// once i get every module to java, i'll replace this with public access
	private static AirJump instance = null;

	public AirJump(){
		instance = this;
	}

	public static AirJump getInstance(){
		return instance;
	}
}
