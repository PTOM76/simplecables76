package net.pitan76.simplecables76.block

import net.pitan76.mcpitanlib.api.block.CompatWaterloggable
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity
import net.pitan76.mcpitanlib.api.util.CompatActionResult
import net.pitan76.mcpitanlib.api.util.TextUtil

class EnergyCable(settings: CompatibleBlockSettings) : AbstractCable(settings), CompatWaterloggable {

    override fun onRightClick(e: BlockUseEvent): CompatActionResult {
        val blockEntity = e.blockEntity
        if (blockEntity is BaseEnergyTile) {
            e.player.sendMessage(TextUtil.of("Energy: ${blockEntity.energy} / ${blockEntity.maxEnergy}"))
        }

        return super.onRightClick(e)
    }

    override fun createBlockEntity(e: TileCreateEvent): CompatBlockEntity {
        return EnergyCableBlockEntity(e)
    }
}
