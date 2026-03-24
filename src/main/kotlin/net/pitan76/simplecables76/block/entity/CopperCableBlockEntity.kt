package net.pitan76.simplecables76.block.entity

import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.simplecables76.Config

class CopperCableBlockEntity : EnergyCableBlockEntity {
    constructor(e: TileCreateEvent, speed: Int): super(BlockEntities.COPPER_CABLE.get(), e, speed)

    constructor(e: TileCreateEvent): this(e, Config.copperCableTransferRate)
}
