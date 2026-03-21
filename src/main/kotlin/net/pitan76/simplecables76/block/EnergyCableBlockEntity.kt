package net.pitan76.simplecables76.block

import java.util.UUID
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker
import net.pitan76.simplecables76.CableNetworkManager

class EnergyCableBlockEntity : BaseEnergyTile, ExtendBlockEntityTicker<EnergyCableBlockEntity> {
    constructor(type: BlockEntityType<*>, e: TileCreateEvent, speed: Int): super(type, e) {
        this.speed = speed
    }

    constructor(type: BlockEntityType<*>, e: TileCreateEvent): this(type, e, 512)

    constructor(type: BlockEntityType<*>, pos: BlockPos, state: BlockState, speed: Int): super(type, pos, state) {
        this.speed = speed
    }

    constructor(type: BlockEntityType<*>, pos: BlockPos, state: BlockState): this(type, pos, state, 512)

    constructor(e: TileCreateEvent, speed: Int): this(BlockEntities.ENERGY_CABLE.get(), e, speed)

    constructor(e: TileCreateEvent): this(e, 512)

    val speed: Int

    override val maxEnergy: Long
        get() = speed.toLong() * 4
    override val maxOutput: Long
        get() = speed.toLong()
    override val maxInput: Long
        get() = speed.toLong()

    // キャッシュ用のネットワークID。CableNetworkManagerで管理する
    var networkId: UUID = UUID.randomUUID()

    override fun tick(e: TileTickEvent<EnergyCableBlockEntity>) {
        if (e.isClient) return

        val world = e.midohraWorld
        val pos = e.midohraPos

        // CableNetworkManagerでネットワーク取得
        val network = CableNetworkManager.getOrCreateNetwork(world, pos)
        if (network.cables.firstOrNull()?.first != this) return

        val cables = network.cables
        val tiles = network.tiles
        if (tiles.isEmpty()) return

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

        // 発電機など(供給装置) -> ケーブル
        // EnergyStorageに登録しているが、modによっては引き出せないことがあるので一応、実装しておく
        val providers = tiles.filter { (_, storage) -> storage.energy > 0 && storage.canOutput }
        for ((_, storage) in providers) {
            val totalCapacity = cables.sumOf { (_, s) -> s.maxEnergy - s.energy }
            if (totalCapacity <= 0) break

            // 発電機から出力可能な量とケーブルの空き容量の両方を考慮して、実際に引き出す量を決定
            val takeAmount = minOf(storage.energy, totalCapacity)
            if (takeAmount <= 0) continue

            // 発電機からエネルギーを引き出して0より大きい場合はケーブルに分配
            val extracted = storage.extract(takeAmount)
            if (extracted <= 0) continue

            // エネルギーをケーブルに分配
            var remaining = extracted
            for ((_, cableStorage) in cables) {
                if (remaining <= 0) break
                val space = cableStorage.maxEnergy - cableStorage.energy
                val give = minOf(space, remaining)
                cableStorage.energy += give
                remaining -= give
            }
       }

        // ケーブル -> 装置(消費装置)
        val consumers = tiles.filter { (_, storage) ->
            storage.energy < storage.maxEnergy && storage.canInput
        }

        for ((_, storage) in consumers) {
            val capacity = storage.maxEnergy - storage.energy
            if (capacity <= 0) continue

            // ケーブルから出力可能な合計量を計算
            var available: Long = 0
            for ((cable, cableStorage) in cables) {
                available += minOf(cableStorage.energy, cable.maxOutput)
            }
            if (available <= 0) continue // 挿入可能な量がない場合はスキップ

            val pushAmount = minOf(capacity, available)
            if (pushAmount <= 0) continue // 挿入する量がない場合はスキップ

            val inserted = storage.insert(pushAmount)
            if (inserted <= 0) continue // 挿入できた量がない場合はスキップ

            // 挿入分をケーブルから引く
            var remaining = inserted
            for ((cable, cableStorage) in cables) {
                if (remaining <= 0) break
                val take = minOf(cableStorage.energy, cable.maxOutput, remaining)
                if (take > 0) {
                    cableStorage.energy -= take
                    remaining -= take
                }
            }
        }
    }

    override fun writeNbt(args: WriteNbtArgs) {
        super.writeNbt(args)
    }

    override fun readNbt(args: ReadNbtArgs) {
        super.readNbt(args)
    }
}
