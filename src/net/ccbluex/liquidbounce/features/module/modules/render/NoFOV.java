package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.FloatValue;

@ModuleInfo(name = "NoFOV", description = "Disables FOV changes caused by speed effect, etc.", category = ModuleCategory.RENDER)
public class NoFOV extends Module {
	private static NoFOV instance;

	public static NoFOV getInstance(){
		return instance;
	}

    public NoFOV() {
        instance = this;
    }
	public FloatValue fovValue = new FloatValue("FOV", 1f, 0f, 1.5f);
}
