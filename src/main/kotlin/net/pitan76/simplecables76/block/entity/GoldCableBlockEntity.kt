package net.pitan76.simplecables76.block.entity

import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.simplecables76.Config

class GoldCableBlockEntity : EnergyCableBlockEntity {
    constructor(e: TileCreateEvent, speed: Int): super(BlockEntities.GOLD_CABLE.get(), e, speed)

    constructor(e: TileCreateEvent): super(e, Config.goldCableTransferRate)
}
