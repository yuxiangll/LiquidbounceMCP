package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

@ModuleInfo(name = "NoJumpDelay", description = "Removes delay between jumps.", category = ModuleCategory.MOVEMENT)
public class NoJumpDelay extends Module {
	private static NoJumpDelay instance;

	public NoJumpDelay(){
		instance = this;
	}

	public static NoJumpDelay getInstance(){
		return instance;
	}
}
