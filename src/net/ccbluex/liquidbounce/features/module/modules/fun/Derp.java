package net.ccbluex.liquidbounce.features.module.modules.fun;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;

@ModuleInfo(name = "Derp", description = "Makes it look like you were derping around.", category = ModuleCategory.FUN)
public class Derp extends Module {
	private static Derp instance;

	public static Derp getInstance(){
		return instance;
	}

	public Derp(){
		instance = this;
	}

	private BoolValue headlessValue = new BoolValue("Headless", false);
	private BoolValue spinnyValue = new BoolValue("Spinny", false);
	private FloatValue incrementValue = new FloatValue("Increment", 1F, 0F, 50F);

	private float currentSpin = 0F;

	public float[] getRotation() {
		float[] derpRotations = new float[]{(float) (mc.thePlayer.rotationYaw + (Math.random() * 360 - 180)), (float) (Math.random() * 180 - 90)};

		if (headlessValue.get())
			derpRotations[1] = 180F;

		if (spinnyValue.get()) {
			derpRotations[0] = currentSpin + incrementValue.get();
			currentSpin = derpRotations[0];
		}

		return derpRotations;
	}
}