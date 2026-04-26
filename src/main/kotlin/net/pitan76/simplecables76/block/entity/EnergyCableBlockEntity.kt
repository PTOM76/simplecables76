package net.pitan76.simplecables76.block.entity

import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs
import net.pitan76.mcpitanlib.api.event.tile.TileTickEvent
import net.pitan76.mcpitanlib.api.tile.ExtendBlockEntityTicker
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityTypeWrapper
import net.pitan76.simplecables76.CableNetworkManager
import net.pitan76.simplecables76.Config
import java.util.*

open class EnergyCableBlockEntity : AbstractEnergyBlockEntity, ExtendBlockEntityTicker<EnergyCableBlockEntity> {
    constructor(type: BlockEntityTypeWrapper, e: TileCreateEvent, speed: Int): super(type, e) {
        this.speed = speed
    }

    constructor(type: BlockEntityTypeWrapper, e: TileCreateEvent): this(type, e, Config.energyCableTransferRate)

//    constructor(type: BlockEntityType<*>, pos: BlockPos, state: BlockState, speed: Int): super(type, pos, state) {
//        this.speed = speed
//    }

//    constructor(type: BlockEntityType<*>, pos: BlockPos, state: BlockState): this(type, pos, state, Config.energyCableTransferRate)

    constructor(e: TileCreateEvent, speed: Int): this(BlockEntities.ENERGY_CABLE, e, speed)

    constructor(e: TileCreateEvent): this(e, Config.energyCableTransferRate)

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

        // ネットワークで1つのケーブルだけがtick毎に処理する
        val isMaster = network.cables.firstOrNull()?.first == this
        if (!isMaster) return

        network.tick()
    }

    override fun writeNbt(args: WriteNbtArgs) {
        super.writeNbt(args)
    }

    override fun readNbt(args: ReadNbtArgs) {
        super.readNbt(args)
    }
}
