package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

@ModuleInfo(name = "SwingAnimation", description = "Changes swing animation.", category = ModuleCategory.RENDER)
public class SwingAnimation extends Module {
	private static SwingAnimation instance;

	public static SwingAnimation getInstance() {
		return instance;
	}

	public SwingAnimation(){
		instance = this;
	}
}