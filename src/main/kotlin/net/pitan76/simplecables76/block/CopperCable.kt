package net.pitan76.simplecables76.block

import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil
import net.pitan76.simplecables76.Config
import net.pitan76.simplecables76.block.entity.CopperCableBlockEntity

class CopperCable(settings: CompatibleBlockSettings, speed: Int) : EnergyCable(settings, speed) {

    override val CODEC: CompatMapCodec<out CopperCable> =
        CompatBlockMapCodecUtil.createCodec<CopperCable> { settings: CompatibleBlockSettings ->
            CopperCable(settings, Config.copperCableTransferRate)
        }

    override fun getCompatCodec(): CompatMapCodec<out CopperCable> {
        return CODEC
    }

    override fun createBlockEntity(e: TileCreateEvent): CompatBlockEntity {
        return CopperCableBlockEntity(e, speed)
    }
}
