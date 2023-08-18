package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

@ModuleInfo(name = "NoHurtCam", description = "Disables hurt cam effect when getting hurt.", category = ModuleCategory.RENDER)
public class NoHurtCam extends Module {
	private static NoHurtCam instance;

	public static NoHurtCam getInstance(){
		return instance;
	}

	public NoHurtCam(){
		instance = this;
	}
}
