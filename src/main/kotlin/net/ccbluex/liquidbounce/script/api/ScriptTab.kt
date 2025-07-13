package net.ccbluex.liquidbounce.script.api

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import org.openjdk.nashorn.api.scripting.JSObject
import org.openjdk.nashorn.api.scripting.ScriptUtils

// TODO: Restore this
@Suppress("UNCHECKED_CAST", "unused")
class ScriptTab(private val tabObject: JSObject) : CreativeTabs(-1, tabObject.getMember("name").toString()) {

    val items = ScriptUtils.convert(tabObject.getMember("items"), Array<ItemStack>::class.java) as Array<ItemStack>

    override fun getTabIconItem() = Items::class.java.getField(tabObject.getMember("icon") as String).get(null) as Item

    override fun getTranslatedTabLabel() = tabObject.getMember("name") as String

    override fun displayAllReleventItems(p_78018_1_: MutableList<ItemStack>?) {
        items.forEach { p_78018_1_?.add(it) }
    }
}