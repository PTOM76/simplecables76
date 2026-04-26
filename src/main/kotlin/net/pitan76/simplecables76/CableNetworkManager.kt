package net.pitan76.simplecables76

import net.minecraft.world.level.block.entity.BlockEntity
import net.pitan76.mcpitanlib.api.util.BlockEntityUtil
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos
import net.pitan76.mcpitanlib.midohra.util.math.Direction
import net.pitan76.mcpitanlib.midohra.world.World
import net.pitan76.simplecables76.block.entity.AbstractEnergyBlockEntity
import net.pitan76.simplecables76.block.entity.EnergyCableBlockEntity
import net.pitan76.simplecables76.compat.EnergyStorageWrapper
import net.pitan76.simplecables76.compat.IEnergyStorage
import net.pitan76.simplecables76.compat.RebornEnergyRegister
import net.pitan76.simplecables76.compat.TREnergyStorage
import java.util.UUID
import kotlin.collections.ArrayDeque
import kotlin.collections.MutableSet
import kotlin.collections.any
import kotlin.collections.filter
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty
import kotlin.collections.listOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.collections.plus
import kotlin.collections.set
import kotlin.collections.sumOf

/**
 * ケーブルネットワーク全体を管理する、キャッシュ機構つき
 */
object CableNetworkManager {
    // ネットワークIDごとにネットワーク情報（ケーブル、BlockEntity）を管理
    private val networkMap = mutableMapOf<UUID, CableNetwork>()

    // 各ケーブルの位置 -> ネットワークID
    private val cablePosToNetworkId = mutableMapOf<Pair<String, BlockPos>, UUID>()

    private fun getWorldId(world: World): String {
        return world.id.toString()
    }

    /**
     * ネットワーク情報
     */
    data class CableNetwork(
        val id: UUID,
        val cables: MutableSet<Pair<EnergyCableBlockEntity, IEnergyStorage>> = mutableSetOf(),
        val tiles: MutableSet<Pair<BlockEntity, IEnergyStorage>> = mutableSetOf()
    ) {
        fun tick() {
            val cables = this.cables
            val tiles = this.tiles
            if (tiles.isEmpty()) return

            // ケーブル間のエネルギー均等化
//            val totalCableEnergy = cables.sumOf { it.second.energy }
//        cables.shuffled()
//            if (cables.isNotEmpty() && totalCableEnergy > 0) {
//                val perCable = totalCableEnergy / cables.size
//                val remainder = totalCableEnergy % cables.size
//                var i = 0
//                for ((_, storage) in cables) {
//                    storage.energy = perCable + if (i < remainder) 1 else 0
//                    i++
//                }
//            }

            // 発電機など(供給装置) -> ケーブル
            // EnergyStorageに登録しているが、modによっては引き出せないことがあるので一応、実装しておく
            val providers = tiles.filter { (_, storage) -> storage.energy > 0 && storage.canOutput && storage.maxOutput > 0 }
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
            val consumers = tiles.filter { (_, storage) -> storage.energy < storage.maxEnergy && storage.canInput && storage.maxInput > 0 }
//                .filter { (tile, _) -> providers.none { it.first === tile } }
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
    }

    /**
     * 指定位置のケーブルが属するネットワークを取得（なければ探索）
     */
    fun getOrCreateNetwork(world: World, pos: BlockPos): CableNetwork {
        val key = getWorldId(world) to pos
        val networkId = cablePosToNetworkId[key]
        if (networkId != null) {
            networkMap[networkId]?.let {
                return it
            }
        }

        // ネットワーク探索
        return searchNetwork(world, pos)
    }

    fun clearCache() {
        networkMap.clear()
        cablePosToNetworkId.clear()
    }

    fun clearCache(world: World) {
        val worldId = getWorldId(world)
        val keysToRemove = cablePosToNetworkId.keys.filter { it.first == worldId }
        keysToRemove.forEach { cablePosToNetworkId.remove(it) }
        networkMap.clear()
    }

    /**
     * ネットワーク探索
     */
    fun searchNetwork(world: World, startPos: BlockPos): CableNetwork {
//        println("Searching network from ${startPos.toRaw().toString()}... ")

        val visited = mutableSetOf<BlockPos>()
        val queue = ArrayDeque<BlockPos>()
        val cables = mutableSetOf<Pair<EnergyCableBlockEntity, IEnergyStorage>>()
        val tiles = mutableSetOf<Pair<BlockEntity, IEnergyStorage>>()

        queue.add(startPos)

        while (queue.isNotEmpty()) {
            val currentPos = queue.removeFirst()
            if (!visited.add(currentPos)) continue
            val tile = world.getBlockEntity(currentPos).get()
            if (tile is EnergyCableBlockEntity) {
                // energyStorageがnullの場合は初期化する
                if (tile.getEnergyStorage() == null) {
                    tile.setEnergyStorage(TREnergyStorage(tile))
                }
                cables.add(tile to tile.getEnergyStorage()!!)

                for (dir in Direction.values()) {
                    val neighborPos = currentPos.offset(dir)
                    val neighborTile = world.getBlockEntity(neighborPos).get()
                    if (neighborTile is EnergyCableBlockEntity) {
//                        if (cables.none { it.first == neighborTile })
                        if (!visited.contains(neighborPos))
                            queue.add(neighborPos)
                    } else if (neighborTile != null) {
                        // 同じタイルが既に追加されていないかチェック
                        if (tiles.any { it.first === neighborTile }) continue

                        if (tile.getEnergyStorage() is TREnergyStorage) {
                            RebornEnergyRegister.ENERGY_LOOKUP.find(world, neighborPos, dir.opposite)?.let { storage ->
                                tiles.add(neighborTile to EnergyStorageWrapper(storage))
                            }
                        } else if (neighborTile is AbstractEnergyBlockEntity) {
                            if (neighborTile.getEnergyStorage() != null)
                                tiles.add(neighborTile to neighborTile.getEnergyStorage()!!)
                        }
                    }
                }
            }
        }

        val newId = UUID.randomUUID() // 新しいネットワークIDを生成

        // 既存ネットワークからケーブルを削除
        cables.forEach { (cable, storage) ->
            cable.networkId.let { oldId ->
                if (oldId != newId) {
                    networkMap[oldId]?.cables?.remove(cable to storage)
                }
            }
        }

        // ケーブルに新ネットワークIDを付与し、マップを更新
        cables.forEach { (cable, _) ->
            cable.networkId = newId
            val cablePos = cable.midohraPos
            cablePosToNetworkId[getWorldId(world) to cablePos] = newId
        }

        val network = CableNetwork(newId, cables, tiles)
        networkMap[newId] = network

        return network
    }

    /**
     * ケーブル設置/破壊/隣接更新などで呼ぶ
     * 影響範囲のネットワークのみ再構築
     */
    fun onCableChanged(world: World, pos: BlockPos) {
        // 自分と6方向のネットワークを再構築
        val positions = listOf(pos) + listOf(pos.offset(Direction.UP), pos.offset(Direction.DOWN), pos.offset(Direction.NORTH), pos.offset(
            Direction.SOUTH), pos.offset(Direction.WEST), pos.offset(Direction.EAST))

        positions.forEach { p ->
            val tile = world.getBlockEntity(p).get()
            if (tile is EnergyCableBlockEntity) {
                searchNetwork(world, p)
            }
        }
    }

    fun printLog(world: World, pos: BlockPos) {
        val network = getOrCreateNetwork(world, pos)

        println("Cable Network ID: ${network.id}")
        println("Cables (${network.cables.size}):")
        network.cables.forEach { (cable, storage) ->
            println("- ${cable.midohraPos.toRaw()}: ${storage.energy}/${storage.maxEnergy}")
        }
        println("Tiles (${network.tiles.size}):")
        network.tiles.forEach { (tile, storage) ->
            println("- ${BlockEntityUtil.getPos(tile)}: ${storage.energy}/${storage.maxEnergy}")
        }

        println()
    }

    /**
     * ネットワークIDからネットワークを取得
     */
    fun getNetworkById(id: UUID): CableNetwork? = networkMap[id]
}