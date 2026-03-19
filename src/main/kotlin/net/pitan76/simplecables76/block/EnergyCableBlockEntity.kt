package net.pitan76.simplecables76.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker
import net.pitan76.mcpitanlib.midohra.util.math.Direction
import net.pitan76.simplecables76.compat.TREnergyStorage
import team.reborn.energy.api.EnergyStorage

class EnergyCableBlockEntity : BaseEnergyTile, ExtendBlockEntityTicker<EnergyCableBlockEntity> {
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

    override fun tick(e: TileTickEvent<EnergyCableBlockEntity>) {
        val world = e.midohraWorld
        val pos = e.midohraPos

        val dirs = listOf(Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)
        for (dir in dirs) {
            val neighborPos = pos.offset(dir)
            if (e.blockEntity is BaseEnergyTile) {
                val sendAmount = minOf(this.maxOutput, this.energy)
                if (sendAmount > 0) {
                    val inserted = e.blockEntity.insertEnergy(sendAmount)
                    this.energy -= inserted
                }
            } else {
                val storage = EnergyStorage.SIDED.find(world.toMinecraft(), neighborPos.toMinecraft(), dir.opposite.toMinecraft())
                if (storage != null && storage !is TREnergyStorage) {
                    val sendAmount = minOf(this.maxOutput.toLong(), this.energy.toLong())
                    if (sendAmount > 0) {
                        val inserted = storage.insert(sendAmount, null)
                        this.energy -= inserted.toInt()
                    }
                }
            }
        }
    }
}
