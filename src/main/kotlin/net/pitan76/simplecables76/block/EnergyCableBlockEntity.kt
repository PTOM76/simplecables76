package net.pitan76.simplecables76.block

import java.util.UUID
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent
import net.pitan76.mcpitanlib.api.lookup.block.BlockApiLookupWithDirection
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker
import net.pitan76.mcpitanlib.midohra.util.math.Direction
import net.pitan76.simplecables76.CableNetworkManager
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

    var networkId: UUID = UUID.randomUUID()

    override fun writeNbt(args: WriteNbtArgs) {
        super.writeNbt(args)
    }

    override fun readNbt(args: ReadNbtArgs) {
        super.readNbt(args)
    }

    override fun tick(e: TileTickEvent<EnergyCableBlockEntity>) {
        if (e.isClient) return

        val world = e.midohraWorld
        val pos = e.midohraPos

        // CableNetworkManagerでネットワーク取得
        val network = CableNetworkManager.getOrCreateNetwork(world, pos)
        if (network.cables.firstOrNull()?.first != this) return

        val cables = network.cables
        val tiles = network.tiles

        // 供給元/消費先
        val sources = (cables + tiles).filter { (_, storage) -> storage.energy > 0 && storage.maxOutput > 0 }
        val sinks = (cables + tiles)
            .filter { (_, storage) -> storage.energy < storage.maxEnergy && storage.maxInput > 0 }
            .filterNot { it.first in sources.map { s -> s.first } }

        // 分配アルゴリズム
        val totalEnergy = sources.sumOf { it.second.energy }
        val totalCapacity = sinks.sumOf { it.second.maxEnergy - it.second.energy }
        val transfer = minOf(totalEnergy, totalCapacity)
        if (transfer > 0 && sinks.isNotEmpty()) {
            var remaining = transfer
            for (sink in sinks) {
                if (remaining <= 0) break
                val canInsert = minOf(sink.second.maxInput, sink.second.maxEnergy - sink.second.energy, remaining)
                if (canInsert > 0) {
                    // 供給元からエネルギーを減らし、消費先に加算
                    var toInsert = canInsert
                    for (source in sources) {
                        if (toInsert <= 0) break
                        val take = minOf(source.second.energy, source.second.maxOutput, toInsert)
                        if (take > 0) {
                            source.second.energy -= take
                            sink.second.energy += take
                            toInsert -= take
                            remaining -= take
                        }
                    }
                }
            }
        }

        // 隣接するEnergyStorageへの転送処理
        for (dir in Direction.values()) {
            val neighborPos = pos.offset(dir)
            val neighborBe = world.getBlockEntity(neighborPos).get()
            if (neighborBe !is BaseEnergyTile) {
                val storage = BlockApiLookupWithDirection(EnergyStorage.SIDED).find(world, neighborPos, dir.opposite)
                if (storage != null && storage !is TREnergyStorage) {
                    val sendAmount = minOf(this.maxOutput, this.energy)
                    if (sendAmount > 0) {
                        Transaction.openOuter().use { transaction ->
                            val inserted = storage.insert(sendAmount, transaction)
                            if (inserted > 0) {
                                transaction.commit()
                                this.energy -= inserted.toInt()
                            }
                        }
                    }
                }
            }
        }
    }
}
