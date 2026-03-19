package net.pitan76.simplecables76.block

import net.minecraft.world.level.block.Block
import net.pitan76.mcpitanlib.api.block.CompatibleMaterial
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings
import net.pitan76.mcpitanlib.api.registry.result.RegistryResult
import net.pitan76.simplecables76.SimpleCables.Companion._id
import net.pitan76.simplecables76.SimpleCables.Companion.registry

object Blocks {
    lateinit var ENERGY_CABLE : RegistryResult<Block>

    @JvmStatic
    fun init() {
        ENERGY_CABLE = registry.registerBlock(_id("energy_cable")) {
            EnergyCable(CompatibleBlockSettings.of(_id("energy_cable"), CompatibleMaterial.STONE))
        }
    }
}