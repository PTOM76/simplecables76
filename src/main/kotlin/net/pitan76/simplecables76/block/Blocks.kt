package net.pitan76.simplecables76.block

import net.pitan76.mcpitanlib.api.block.CompatibleMaterial
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings
import net.pitan76.mcpitanlib.midohra.block.TypedBlockWrapper
import net.pitan76.simplecables76.Config
import net.pitan76.simplecables76.SimpleCables.Companion._id
import net.pitan76.simplecables76.SimpleCables.Companion.registry

object Blocks {
    lateinit var ENERGY_CABLE: TypedBlockWrapper<EnergyCable>
    lateinit var COPPER_CABLE: TypedBlockWrapper<CopperCable>
    lateinit var IRON_CABLE: TypedBlockWrapper<IronCable>
    lateinit var GOLD_CABLE: TypedBlockWrapper<GoldCable>

    @JvmStatic
    fun init() {
        ENERGY_CABLE = registry.registerBlock(_id("energy_cable")) {
            EnergyCable(
                CompatibleBlockSettings.of(_id("energy_cable"), CompatibleMaterial.STONE)
                    .strength(0.5f, 6.0f),
                Config.energyCableTransferRate
            )
        }

        COPPER_CABLE = registry.registerBlock(_id("copper_cable")) {
            CopperCable(
                CompatibleBlockSettings.of(_id("copper_cable"), CompatibleMaterial.METAL)
                    .strength(0.5f, 6.0f),
                Config.copperCableTransferRate
            )
        }

        IRON_CABLE = registry.registerBlock(_id("iron_cable")) {
            IronCable(
                CompatibleBlockSettings.of(_id("iron_cable"), CompatibleMaterial.METAL)
                    .strength(0.5f, 6.0f),
                Config.ironCableTransferRate
            )
        }

        GOLD_CABLE = registry.registerBlock(_id("gold_cable")) {
            GoldCable(
                CompatibleBlockSettings.of(_id("gold_cable"), CompatibleMaterial.METAL)
                    .strength(0.5f, 6.0f),
                Config.goldCableTransferRate
            )
        }
    }
}