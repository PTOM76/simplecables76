package net.pitan76.simplecables76.block

import java.util.UUID
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos
import net.pitan76.mcpitanlib.midohra.util.math.Direction
import net.pitan76.mcpitanlib.midohra.util.math.Vector3i
import net.pitan76.mcpitanlib.midohra.world.World

/**
 * ケーブルネットワーク全体を管理する、キャッシュ機構つき
 */
object CableNetworkManager {
    // ネットワークIDごとにネットワーク情報（ケーブル、BlockEntity）を管理
    private val networkMap = mutableMapOf<UUID, CableNetwork>()

    // BlockPosじゃなくてVector3iで管理するのは、MCPitanLib側のBlockPosはhashCodeがクソだったので次回から多分なおしてるはず、応急処置ですｗ
    // TODO: 次回のMPLにしたら、こっちもBlockPosに戻す
    // 各ケーブルの位置 -> ネットワークID
    private val cablePosToNetworkId = mutableMapOf<Pair<String, Vector3i>, UUID>()

    private fun getWorldId(world: World): String {
        return world.id.toString()
    }

    /**
     * ネットワーク情報
     */
    data class CableNetwork(
        val id: UUID,
        val cables: MutableSet<EnergyCableBlockEntity> = mutableSetOf(),
        val tiles: MutableSet<BaseEnergyTile> = mutableSetOf()
    )

    /**
     * 指定位置のケーブルが属するネットワークを取得（なければ探索）
     */
    fun getOrCreateNetwork(world: World, pos: BlockPos): CableNetwork {
        val key = getWorldId(world) to Vector3i.of(pos)
        val networkId = cablePosToNetworkId[key]
        if (networkId != null) {
            networkMap[networkId]?.let {
                return it
            }
        }

        // ネットワーク探索
        return searchNetwork(world, pos)
    }

    /**
     * ネットワーク探索
     */
    fun searchNetwork(world: World, startPos: BlockPos): CableNetwork {
        val visited = mutableSetOf<BlockPos>()
        val queue = ArrayDeque<BlockPos>()
        val cables = mutableSetOf<EnergyCableBlockEntity>()
        val tiles = mutableSetOf<BaseEnergyTile>()
        queue.add(startPos)
        while (queue.isNotEmpty()) {
            val currentPos = queue.removeFirst()
            if (!visited.add(currentPos)) continue
            val tile = world.getBlockEntity(currentPos).get()
            if (tile is EnergyCableBlockEntity) {
                cables.add(tile)
                for (dir in listOf(Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)) {
                    val neighborPos = currentPos.offset(dir)
                    val neighborTile = world.getBlockEntity(neighborPos).get()
                    if (neighborTile is EnergyCableBlockEntity && neighborTile !in cables) {
                        queue.add(neighborPos)
                    } else if (neighborTile is BaseEnergyTile && neighborTile !is EnergyCableBlockEntity) {
                        tiles.add(neighborTile)
                    }
                }
            }
        }

        val newId = UUID.randomUUID() // 新しいネットワークIDを生成

        // 既存ネットワークからケーブルを削除
        cables.forEach { cable ->
            cable.networkId.let { oldId ->
                if (oldId != newId) {
                    networkMap[oldId]?.cables?.remove(cable)
                }
            }
        }

        // ケーブルに新ネットワークIDを付与し、マップを更新
        cables.forEach { cable ->
            cable.networkId = newId
            val cablePos = BlockPos.of(cable.callGetPos())
            cablePosToNetworkId[getWorldId(world) to Vector3i.of(cablePos)] = newId
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
        val positions = listOf(pos) + listOf(pos.offset(Direction.UP), pos.offset(Direction.DOWN), pos.offset(Direction.NORTH), pos.offset(Direction.SOUTH), pos.offset(Direction.WEST), pos.offset(Direction.EAST))

        positions.forEach { p ->
            val tile = world.getBlockEntity(p).get()
            if (tile is EnergyCableBlockEntity) {
                searchNetwork(world, p)
            }
        }
    }

    /**
     * ネットワークIDからネットワークを取得
     */
    fun getNetworkById(id: UUID): CableNetwork? = networkMap[id]
}

