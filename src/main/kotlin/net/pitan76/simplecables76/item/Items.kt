package net.pitan76.simplecables76.item

import net.minecraft.world.item.Item
import net.pitan76.mcpitanlib.api.item.DefaultItemGroups
import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings
import net.pitan76.mcpitanlib.api.registry.result.RegistryResult
import net.pitan76.mcpitanlib.api.util.item.ItemUtil
import net.pitan76.simplecables76.SimpleCables.Companion._id
import net.pitan76.simplecables76.SimpleCables.Companion.registry
import net.pitan76.simplecables76.block.Blocks

object Items {
    lateinit var ENERGY_CABLE : RegistryResult<Item>

    @JvmStatic
    fun init() {
        ENERGY_CABLE = registry.registerItem(_id("energy_cable")) {
            ItemUtil.create(Blocks.ENERGY_CABLE.orNull, CompatibleItemSettings.of(_id("energy_cable")).addGroup(
                DefaultItemGroups.TRANSPORTATION))
        }
    }
}