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
import techreborn.blockentity.cable.CableBlockEntity
import techreborn.blocks.cable.CableBlock

class EnergyCableBlockEntity : BaseEnergyTile, ExtendBlockEntityTicker<EnergyCableBlockEntity> {
    constructor(type: BlockEntityType<*>, e: TileCreateEvent) : super(type, e)

    constructor(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) : super(type, pos, state)

    constructor(e: TileCreateEvent) : this(BlockEntities.ENERGY_CABLE.get(), e)

    override val maxEnergy: Long
        get() = 1024
    override val maxOutput: Long
        get() = 256
    override val maxInput: Long
        get() = 256

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

        // ケーブル間のエネルギー均等化
        val totalCableEnergy = cables.sumOf { it.second.energy }
        if (cables.isNotEmpty() && totalCableEnergy > 0) {
            val perCable = totalCableEnergy / cables.size
            val remainder = totalCableEnergy % cables.size
            var i = 0
            for ((_, storage) in cables) {
                storage.energy = perCable + if (i < remainder) 1 else 0
                i++
            }
        }

        // タイル供給元(発電機など)からケーブルに
        val tileProviders = tiles.filter { (_, storage) -> storage.energy > 0 && storage.canOutput }
        for ((_, tileStorage) in tileProviders) {
            val totalCableCapacity = cables.sumOf { (_, s) -> s.maxEnergy - s.energy }
            if (totalCableCapacity <= 0) break

            val takeAmount = minOf(tileStorage.energy, totalCableCapacity)
            if (takeAmount > 0) {
                val extracted = tileStorage.extract(takeAmount)
                if (extracted > 0) {
                    // 吸収したエネルギーをケーブルに分配
                    var rem = extracted
                    for ((_, cableStorage) in cables) {
                        if (rem <= 0) break
                        val space = cableStorage.maxEnergy - cableStorage.energy
                        val give = minOf(space, rem)
                        cableStorage.energy += give
                        rem -= give
                    }
                }
            }
        }

        // ケーブルからタイル消費先(装置など)へ
        val tileConsumers = tiles.filter { (_, storage) ->
            storage.energy < storage.maxEnergy && storage.canInput
        }.filterNot { it in tileProviders }

        for ((_, tileStorage) in tileConsumers) {
            val capacity = tileStorage.maxEnergy - tileStorage.energy
            if (capacity <= 0) continue

            // ケーブルから出力可能な合計量を計算
            var available: Long = 0
            for ((cable, cableStorage) in cables) {
                available += minOf(cableStorage.energy, cable.maxOutput)
            }
            val pushAmount = minOf(capacity, available)
            if (pushAmount > 0) {
                val inserted = tileStorage.insert(pushAmount)
                if (inserted > 0) {
                    // 挿入分をケーブルから引く
                    var rem = inserted
                    for ((cable, cableStorage) in cables) {
                        if (rem <= 0) break
                        val take = minOf(cableStorage.energy, cable.maxOutput, rem)
                        if (take > 0) {
                            cableStorage.energy -= take
                            rem -= take
                        }
                    }
                }
            }
        }

        // ネットワーク外の隣接EnergyStorageへ
//        for ((cable, cableStorage) in cables) {
//            val cablePos = net.pitan76.mcpitanlib.midohra.util.math.BlockPos.of(cable.callGetPos())
//            for (dir in Direction.values()) {
//                val neighborPos = cablePos.offset(dir)
//                val neighborBe = world.getBlockEntity(neighborPos).get()
//                if (neighborBe !is EnergyCableBlockEntity) {
//                    if (tiles.any { it.first === neighborBe }) continue
//
//                    val storage = BlockApiLookupWithDirection(EnergyStorage.SIDED).find(world, neighborPos, dir.opposite)
//                    if (storage != null && storage !is TREnergyStorage && storage.supportsInsertion()) {
//                        val sendAmount = minOf(cable.maxOutput, cableStorage.energy)
//                        if (sendAmount > 0) {
//                            Transaction.openOuter().use { transaction ->
//                                val inserted = storage.insert(sendAmount, transaction)
//                                if (inserted > 0) {
//                                    transaction.commit()
//                                    cableStorage.energy -= inserted
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
    }
}
