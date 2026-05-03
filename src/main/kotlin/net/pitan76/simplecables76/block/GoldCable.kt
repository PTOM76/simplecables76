package net.pitan76.simplecables76.block

import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil
import net.pitan76.simplecables76.Config
import net.pitan76.simplecables76.block.entity.GoldCableBlockEntity

class GoldCable(settings: CompatibleBlockSettings, speed: Int) : EnergyCable(settings, speed) {

    override val CODEC: CompatMapCodec<out GoldCable> =
        CompatBlockMapCodecUtil.createCodec<GoldCable> { settings: CompatibleBlockSettings ->
            GoldCable(settings, Config.goldCableTransferRate)
        }

    override fun getCompatCodec(): CompatMapCodec<out GoldCable> {
        return CODEC
    }

    override fun createBlockEntity(e: TileCreateEvent): CompatBlockEntity {
        return GoldCableBlockEntity(e, speed)
    }
}
