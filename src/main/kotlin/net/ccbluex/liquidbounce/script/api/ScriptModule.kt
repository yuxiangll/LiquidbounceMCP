package net.ccbluex.liquidbounce.script.api

import meteordevelopment.orbit.EventHandler
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.Value
import org.openjdk.nashorn.api.scripting.JSObject

@Suppress("unused")
@ModuleInfo(name = "ScriptModule", description = "Empty", category = ModuleCategory.MISC)
class ScriptModule(private val moduleObject: JSObject) : Module() {

    private val events = HashMap<String, JSObject>()
    private val _values = LinkedHashMap<String, Value<*>>()
    private var _tag: String? = null

    /**
     * Allows the user to access values by typing module.settings.<valuename>
     */
    val settings by lazy { _values }

    init {
        name = moduleObject.getMember("name") as String
        description = moduleObject.getMember("description") as String

        val categoryString = moduleObject.getMember("category") as String
        for (category in ModuleCategory.values())
            if (categoryString.equals(category.displayName, true))
                this.category = category

        if (moduleObject.hasMember("settings")) {
            val settings = moduleObject.getMember("settings") as JSObject

            for (settingName in settings.keySet())
                _values[settingName] = settings.getMember(settingName) as Value<*>
        }

        if (moduleObject.hasMember("tag"))
            _tag = moduleObject.getMember("tag") as String
    }

    override val values: List<Value<*>>
        get() {
            return _values.values.toList()
        }

    override var tag: String?
        get() = _tag
        set(value) {
            _tag = value
        }

    /**
     * Called from inside the script to register a new event handler.
     * @param eventName Name of the event.
     * @param handler JavaScript function used to handle the event.
     */
    fun on(eventName: String, handler: JSObject) {
        events[eventName] = handler
    }

    override fun onEnable() = callEvent("enable")

    override fun onDisable() = callEvent("disable")

    @EventHandler
    fun onUpdate(updateEvent: UpdateEvent) = callEvent("update")

    @EventHandler
    fun onMotion(motionEvent: MotionEvent) = callEvent("motion", motionEvent)

    @EventHandler
    fun onRender2D(render2DEvent: Render2DEvent) = callEvent("render2D", render2DEvent)

    @EventHandler
    fun onRender3D(render3DEvent: Render3DEvent) = callEvent("render3D", render3DEvent)

    @EventHandler
    fun onPacket(packetEvent: PacketEvent) = callEvent("packet", packetEvent)

    @EventHandler
    fun onJump(jumpEvent: JumpEvent) = callEvent("jump", jumpEvent)

    @EventHandler
    fun onAttack(attackEvent: AttackEvent) = callEvent("attack", attackEvent)

    @EventHandler
    fun onKey(keyEvent: KeyEvent) = callEvent("key", keyEvent)

    @EventHandler
    fun onMove(moveEvent: MoveEvent) = callEvent("move", moveEvent)

    @EventHandler
    fun onStep(stepEvent: StepEvent) = callEvent("step", stepEvent)

    @EventHandler
    fun onStepConfirm(stepConfirmEvent: StepConfirmEvent) = callEvent("stepConfirm")

    @EventHandler
    fun onWorld(worldEvent: WorldEvent) = callEvent("world", worldEvent)

    @EventHandler
    fun onSession(sessionEvent: SessionEvent) = callEvent("session")

    @EventHandler
    fun onClickBlock(clickBlockEvent: ClickBlockEvent) = callEvent("clickBlock", clickBlockEvent)

    @EventHandler
    fun onStrafe(strafeEvent: StrafeEvent) = callEvent("strafe", strafeEvent)

    @EventHandler
    fun onSlowDown(slowDownEvent: SlowDownEvent) = callEvent("slowDown", slowDownEvent)

    /**
     * Calls the handler of a registered event.
     * @param eventName Name of the event to be called.
     * @param payload Event data passed to the handler function.
     */
    private fun callEvent(eventName: String, payload: Any? = null) {
        try {
            events[eventName]?.call(moduleObject, payload)
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("[ScriptAPI] Exception in module '$name'!", throwable)
        }
    }
}