package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;

@ModuleInfo(name = "AntiBlind", description = "Cancels blindness effects.", category = ModuleCategory.RENDER)
public class AntiBlind extends Module {
	private static AntiBlind instance;

	public static AntiBlind getInstance(){
		return instance;
	}

	public AntiBlind(){
		instance = this;
	}


	public BoolValue confusionEffect = new BoolValue("Confusion", true);
	public BoolValue pumpkinEffect = new BoolValue("Pumpkin", true);
	public FloatValue fireEffect = new FloatValue("FireOpacity", 0.5f, 0, 1);
}
