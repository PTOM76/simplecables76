package net.pitan76.simplecables76.item

import net.pitan76.mcpitanlib.api.item.v2.CompatibleItemSettings
import net.pitan76.mcpitanlib.api.util.item.ItemUtil
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
        ENERGY_CABLE = registry.registerRawItem(_id("energy_cable")) {
            ItemUtil.create(Blocks.ENERGY_CABLE.get(), CompatibleItemSettings.of(_id("energy_cable"))
                .addGroup(ItemGroups.TRANSPORTATION))
        }

        COPPER_CABLE = registry.registerRawItem(_id("copper_cable")) {
            ItemUtil.create(Blocks.COPPER_CABLE.get(), CompatibleItemSettings.of(_id("copper_cable"))
                .addGroup(ItemGroups.TRANSPORTATION))
        }

        IRON_CABLE = registry.registerRawItem(_id("iron_cable")) {
            ItemUtil.create(Blocks.IRON_CABLE.get(), CompatibleItemSettings.of(_id("iron_cable"))
                .addGroup(ItemGroups.TRANSPORTATION))
        }

        GOLD_CABLE = registry.registerRawItem(_id("gold_cable")) {
            ItemUtil.create(Blocks.GOLD_CABLE.get(), CompatibleItemSettings.of(_id("gold_cable"))
                .addGroup(ItemGroups.TRANSPORTATION))
        }
    }
}