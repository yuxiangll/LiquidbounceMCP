package net.ccbluex.liquidbounce.features.module.modules.misc;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;

@ModuleInfo(name = "ComponentOnHover", description = "Allows you to see onclick action and value of chat message components when hovered.", category = ModuleCategory.MISC)
public class ComponentOnHover extends Module {
	private static ComponentOnHover instance;

	public static ComponentOnHover getInstance(){
		return instance;
	}

	public ComponentOnHover(){
		instance = this;
	}
}