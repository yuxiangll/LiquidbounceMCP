package net.ccbluex.liquidbounce.features.module.modules.movement;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventState;
import meteordevelopment.orbit.EventHandler;
import net.ccbluex.liquidbounce.event.MotionEvent;
import net.ccbluex.liquidbounce.event.SlowDownEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(name = "NoSlow", description = "Cancels slowness effects caused by soulsand and using items.", category = ModuleCategory.MOVEMENT)
public class NoSlow extends Module {
    private static NoSlow instance;

    public static NoSlow getInstance(){
        return instance;
    }

    public NoSlow(){
        instance = this;
    }

    private FloatValue blockForwardMultiplier = new FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F);
    private FloatValue blockStrafeMultiplier = new FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F);

    private FloatValue consumeForwardMultiplier = new FloatValue("ConsumeForwardMultiplier", 1.0F, 0.2F, 1.0F);
    private FloatValue consumeStrafeMultiplier = new FloatValue("ConsumeStrafeMultiplier", 1.0F, 0.2F, 1.0F);

    private FloatValue bowForwardMultiplier = new FloatValue("BowForwardMultiplier", 1.0F, 0.2F, 1.0F);
    private FloatValue bowStrafeMultiplier = new FloatValue("BowStrafeMultiplier", 1.0F, 0.2F, 1.0F);

    private BoolValue packet = new BoolValue("Packet", true);

    /**
     * Soulsand
     */
    // Soulsand TODO: Soulsand
    public BoolValue soulSandValue = new BoolValue("Soulsand", true); // Soulsand

    @EventHandler
    public void onMotion(MotionEvent event) {
        ItemStack heldItem = mc.thePlayer.getHeldItem();
        if (heldItem == null || !(heldItem.getItem() instanceof ItemSword) || !MovementUtils.isMoving()) {
            return;
        }

        KillAura killAura = KillAura.Companion.getInstance();
        if (!mc.thePlayer.isBlocking() && !killAura.getBlockingStatus()) {
            return;
        }
        if (this.packet.get()) {
            switch (event.getEventState()) {
                case PRE:
                    C07PacketPlayerDigging digging = new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN);
                    mc.getNetHandler().addToSendQueue(digging);
                    break;
                case POST:
                    C08PacketPlayerBlockPlacement blockPlace = new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem());
                    mc.getNetHandler().addToSendQueue(blockPlace);
                    break;
            }
        }
    }

    @EventHandler
    public void onSlowDown(SlowDownEvent event) {
        if(mc.thePlayer.getHeldItem() == null)
            return;

        Item heldItem = mc.thePlayer.getHeldItem().getItem();

        event.setForward(getMultiplier(heldItem, true));
        event.setStrafe(getMultiplier(heldItem, false));
    }

    private float getMultiplier(Item item, boolean isForward) {
        if(item instanceof ItemFood || item instanceof ItemPotion || item instanceof ItemBucketMilk){
            return isForward ? this.consumeForwardMultiplier.get() : this.consumeStrafeMultiplier.get();
        }else if(item instanceof ItemSword){
            return isForward ? this.blockForwardMultiplier.get() : this.blockStrafeMultiplier.get();
        }else if(item instanceof ItemBow){
            return isForward ? this.bowForwardMultiplier.get() : this.bowStrafeMultiplier.get();
        }else{
            return 0.2f;
        }
    }

}
