package net.pitan76.simplecables76.block

import net.minecraft.world.level.block.Block
import net.pitan76.mcpitanlib.api.block.CompatibleMaterial
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings
import net.pitan76.mcpitanlib.api.registry.result.RegistryResult
import net.pitan76.simplecables76.SimpleCables.Companion._id
import net.pitan76.simplecables76.SimpleCables.Companion.registry

object Blocks {
    lateinit var ENERGY_CABLE: RegistryResult<Block>
    lateinit var COPPER_CABLE: RegistryResult<Block>
    lateinit var IRON_CABLE: RegistryResult<Block>
    lateinit var GOLD_CABLE: RegistryResult<Block>

    @JvmStatic
    fun init() {
        ENERGY_CABLE = registry.registerBlock(_id("energy_cable")) {
            EnergyCable(
                CompatibleBlockSettings.of(_id("energy_cable"), CompatibleMaterial.STONE).strength(1.0f, 6.0f),
                512
            )
        }

        COPPER_CABLE = registry.registerBlock(_id("copper_cable")) {
            EnergyCable(
                CompatibleBlockSettings.of(_id("copper_cable"), CompatibleMaterial.METAL).strength(1.0f, 6.0f),
                256
            )
        }

        IRON_CABLE = registry.registerBlock(_id("iron_cable")) {
            EnergyCable(
                CompatibleBlockSettings.of(_id("iron_cable"), CompatibleMaterial.METAL).strength(1.0f, 6.0f),
                512
            )
        }

        GOLD_CABLE = registry.registerBlock(_id("gold_cable")) {
            EnergyCable(
                CompatibleBlockSettings.of(_id("gold_cable"), CompatibleMaterial.METAL).strength(1.0f, 6.0f),
                1024
            )
        }
    }
}