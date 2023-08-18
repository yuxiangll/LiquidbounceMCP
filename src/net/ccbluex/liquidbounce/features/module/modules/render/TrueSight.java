package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;

@ModuleInfo(name = "TrueSight", description = "Allows you to see invisible entities and barriers.", category = ModuleCategory.RENDER)
public class TrueSight extends Module {
	private static TrueSight instance;

	public static TrueSight getInstance(){
		return instance;
	}

	public TrueSight(){
		instance = this;
	}

    public BoolValue barriersValue = new BoolValue("Barriers", true);
	public BoolValue entitiesValue = new BoolValue("Entities", true);
}