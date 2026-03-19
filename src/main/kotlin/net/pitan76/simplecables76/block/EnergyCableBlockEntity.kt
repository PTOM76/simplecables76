package net.pitan76.simplecables76.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent
import net.pitan76.mcpitanlib.midohra.util.math.Direction
import net.pitan76.mcpitanlib.midohra.world.World

class EnergyCableBlockEntity : BaseEnergyTile {
    constructor(type: BlockEntityType<*>, e: TileCreateEvent) : super(type, e)

    constructor(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : super(type, pos, state)

    constructor(e: TileCreateEvent) : this(BlockEntities.ENERGY_CABLE.get(), e)

    override val maxEnergy: Long
        get() = 1024
    override val maxOutput: Long
        get() = 64
    override val maxInput: Long
        get() = 64

    override fun writeNbt(args: WriteNbtArgs) {
        super.writeNbt(args)
    }

    override fun readNbt(args: ReadNbtArgs) {
        super.readNbt(args)
    }

    override fun tick(e: TileTickEvent<BaseEnergyTile>) {
//        val world: World? = e.midohraWorld
//        val pos = e.midohraPos
//        if (world == null) return
//
//        if (this.energy > 0) {
//            val dirs = listOf(Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)
//            for (dir in dirs) {
//                val neighborPos = pos.offset(dir)
//                val neighborBlockEntity = world.getBlockEntity(neighborPos)
//                if (neighborBlockEntity is BaseEnergyTile) {
//                    val energyToTransfer = minOf(this.energy, this.maxOutput)
//                    if (energyToTransfer > 0) {
//                        val inserted = neighborBlockEntity.insertEnergy(energyToTransfer);
//                    }
//                }
//            }
//        }

    }
}
