package net.pitan76.simplecables76.block

import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil
import net.pitan76.simplecables76.Config
import net.pitan76.simplecables76.block.entity.IronCableBlockEntity

class IronCable(settings: CompatibleBlockSettings, speed: Int) : EnergyCable(settings, speed) {

    override val CODEC: CompatMapCodec<out IronCable> =
        CompatBlockMapCodecUtil.createCodec<IronCable> { settings: CompatibleBlockSettings ->
            IronCable(settings, Config.ironCableTransferRate)
        }

    override fun getCompatCodec(): CompatMapCodec<out IronCable> {
        return CODEC
    }

    override fun createBlockEntity(e: TileCreateEvent): CompatBlockEntity {
        return IronCableBlockEntity(e, speed)
    }
}
