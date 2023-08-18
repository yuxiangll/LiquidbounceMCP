package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;

@ModuleInfo(name = "NoSwing", description = "Disabled swing effect when hitting an entity/mining a block.", category = ModuleCategory.RENDER)
public class NoSwing extends Module {
	private static NoSwing instance;

	public static NoSwing getInstance(){
		return instance;
	}

	public NoSwing(){
		instance = this;
	}
	public final BoolValue serverSideValue = new BoolValue("ServerSide", true);
}