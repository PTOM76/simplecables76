package net.pitan76.simplecables76.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity

class EnergyCableBlockEntity : CompatBlockEntity {
    constructor(type: BlockEntityType<*>?, event: TileCreateEvent) : super(type, event)

    constructor(type: BlockEntityType<*>?, pos: BlockPos, state: BlockState?) : super(type, pos, state)

    constructor(e: TileCreateEvent) : this(BlockEntities.ENERGY_CABLE.getOrNull(), e)

    override fun writeNbt(args: WriteNbtArgs) {
        super.writeNbt(args)
    }

    override fun readNbt(args: ReadNbtArgs) {
        super.readNbt(args)
    }
}
