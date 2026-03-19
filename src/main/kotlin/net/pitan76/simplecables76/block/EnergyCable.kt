package net.pitan76.simplecables76.block

import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity
import net.pitan76.mcpitanlib.api.util.CompatActionResult

class EnergyCable(settings: CompatibleBlockSettings) : AbstractCable(settings) {

    override fun onRightClick(e: BlockUseEvent): CompatActionResult {
        return super.onRightClick(e)
    }

    override fun createBlockEntity(e: TileCreateEvent): CompatBlockEntity {
        return EnergyCableBlockEntity(e)
    }

    override fun isTick(): Boolean {
        return true
    }
}
