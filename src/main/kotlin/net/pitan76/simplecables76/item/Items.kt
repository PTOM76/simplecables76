package net.pitan76.simplecables76.item

import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings
import net.pitan76.mcpitanlib.midohra.item.ItemGroups
import net.pitan76.mcpitanlib.midohra.item.ItemWrapper
import net.pitan76.simplecables76.SimpleCables.Companion._id
import net.pitan76.simplecables76.SimpleCables.Companion.registry
import net.pitan76.simplecables76.block.Blocks

object Items {
    lateinit var ENERGY_CABLE : ItemWrapper
    lateinit var COPPER_CABLE : ItemWrapper
    lateinit var IRON_CABLE : ItemWrapper
    lateinit var GOLD_CABLE : ItemWrapper

    @JvmStatic
    fun init() {
        ENERGY_CABLE = registry.registerBlockItem(_id("energy_cable"), Blocks.ENERGY_CABLE,
            CompatibleItemSettings.of(_id("energy_cable"))
            .addGroup(ItemGroups.TRANSPORTATION))

        COPPER_CABLE = registry.registerBlockItem(_id("copper_cable"), Blocks.COPPER_CABLE,
            CompatibleItemSettings.of(_id("copper_cable"))
                .addGroup(ItemGroups.TRANSPORTATION))

        IRON_CABLE = registry.registerBlockItem(_id("iron_cable"), Blocks.IRON_CABLE,
                CompatibleItemSettings.of(_id("iron_cable"))
                .addGroup(ItemGroups.TRANSPORTATION))

        GOLD_CABLE = registry.registerBlockItem(_id("gold_cable"), Blocks.GOLD_CABLE,
            CompatibleItemSettings.of(_id("gold_cable"))
                .addGroup(ItemGroups.TRANSPORTATION))
    }
}