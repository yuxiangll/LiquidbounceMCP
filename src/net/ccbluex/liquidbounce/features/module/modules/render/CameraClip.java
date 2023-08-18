package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

@ModuleInfo(name = "CameraClip", description = "Allows you to see through walls in third person view.", category = ModuleCategory.RENDER)
public class CameraClip extends Module {
	private static CameraClip instance;

	public static CameraClip getInstance(){
		return instance;
	}

	public CameraClip(){
		instance = this;
	}
}