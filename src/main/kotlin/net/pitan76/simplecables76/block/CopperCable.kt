package net.pitan76.simplecables76.block

import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity
import net.pitan76.simplecables76.block.entity.CopperCableBlockEntity

class CopperCable(settings: CompatibleBlockSettings, speed: Int) : EnergyCable(settings, speed) {
    override fun createBlockEntity(e: TileCreateEvent): CompatBlockEntity {
        return CopperCableBlockEntity(e, speed)
    }
}
