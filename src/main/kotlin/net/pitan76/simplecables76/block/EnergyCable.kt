package net.pitan76.simplecables76.block

import net.pitan76.mcpitanlib.api.block.CompatBlockRenderType
import net.pitan76.mcpitanlib.api.block.CompatWaterloggable
import net.pitan76.mcpitanlib.api.block.args.RenderTypeArgs
import net.pitan76.mcpitanlib.api.block.args.v2.OutlineShapeEvent
import net.pitan76.mcpitanlib.api.block.args.v2.PlacementStateArgs
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings
import net.pitan76.mcpitanlib.api.event.block.*
import net.pitan76.mcpitanlib.api.event.item.ItemAppendTooltipEvent
import net.pitan76.mcpitanlib.api.state.property.CompatProperties
import net.pitan76.mcpitanlib.api.text.CompatFormatting
import net.pitan76.mcpitanlib.api.text.CompatStyle
import net.pitan76.mcpitanlib.api.text.TextComponent
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity
import net.pitan76.mcpitanlib.api.util.CompatActionResult
import net.pitan76.mcpitanlib.api.util.DirectionBoolPropertyUtil
import net.pitan76.mcpitanlib.core.serialization.CompatMapCodec
import net.pitan76.mcpitanlib.core.serialization.codecs.CompatBlockMapCodecUtil
import net.pitan76.mcpitanlib.midohra.block.BlockState
import net.pitan76.mcpitanlib.midohra.fluid.FluidState
import net.pitan76.mcpitanlib.midohra.fluid.Fluids
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos
import net.pitan76.mcpitanlib.midohra.util.math.Direction
import net.pitan76.mcpitanlib.midohra.util.shape.VoxelShape
import net.pitan76.mcpitanlib.midohra.world.World
import net.pitan76.simplecables76.CableNetworkManager
import net.pitan76.simplecables76.Config
import net.pitan76.simplecables76.block.entity.AbstractEnergyBlockEntity
import net.pitan76.simplecables76.block.entity.EnergyCableBlockEntity
import net.pitan76.simplecables76.compat.RebornEnergyRegister

open class EnergyCable : AbstractCable, CompatWaterloggable {

    var speed: Int // ケーブルの伝達速度（例: 512.0 E/t）

    protected open val CODEC: CompatMapCodec<out EnergyCable> =
        CompatBlockMapCodecUtil.createCodec<EnergyCable> { settings: CompatibleBlockSettings ->
            EnergyCable(settings, Config.energyCableTransferRate)
        }

    override fun getCompatCodec(): CompatMapCodec<out EnergyCable> {
        return CODEC
    }

    constructor(settings: CompatibleBlockSettings, speed: Int) : super(settings) {
        setDefaultState(DirectionBoolPropertyUtil.clearAll(defaultMidohraState).with(CompatProperties.WATERLOGGED, false))
        this.speed = speed
    }

    constructor(settings: CompatibleBlockSettings) : this(settings, Config.energyCableTransferRate)

    override fun getOutlineShapeM(e: OutlineShapeEvent): VoxelShape {
        var shape = getCenterShape()

        if (e.get(CompatProperties.UP)) {
            shape = shape.union(
                VoxelShape.blockCuboid(5.0, 11.0, 5.0, 11.0, 16.0, 11.0))
        }
        if (e.get(CompatProperties.DOWN)) {
            shape = shape.union(
                VoxelShape.blockCuboid(5.0, 0.0, 5.0, 11.0, 5.0, 11.0))
        }
        if (e.get(CompatProperties.NORTH)) {
            shape = shape.union(
                VoxelShape.blockCuboid(5.0, 5.0, 0.0, 11.0, 11.0, 5.0))
        }
        if (e.get(CompatProperties.EAST)) {
            shape = shape.union(
                VoxelShape.blockCuboid(11.0, 5.0, 5.0, 16.0, 11.0, 11.0))
        }
        if (e.get(CompatProperties.SOUTH)) {
            shape = shape.union(
                VoxelShape.blockCuboid(5.0, 5.0, 11.0, 11.0, 11.0, 16.0))
        }
        if (e.get(CompatProperties.WEST)) {
            shape = shape.union(
                VoxelShape.blockCuboid(0.0, 5.0, 5.0, 5.0, 11.0, 11.0))
        }

        return shape;
    }

    fun getCenterShape(): VoxelShape {
        return VoxelShape.blockCuboid(5.0, 5.0, 5.0, 11.0, 11.0, 11.0)
    }

    override fun onRightClick(e: BlockUseEvent): CompatActionResult {
        val blockEntityWrapper = e.blockEntityWrapper
        val stack = e.stackM

        if (blockEntityWrapper.instanceOf(AbstractEnergyBlockEntity::class.java) && (stack.isEmpty || !stack.isBlockItem)) {
            val blockEntity = blockEntityWrapper.getCompatBlockEntity(AbstractEnergyBlockEntity::class.java)

            if (e.isClient) return e.success()
            e.player.sendMessage("Energy: ${blockEntity.energy} / ${blockEntity.maxEnergy}")
            CableNetworkManager.printLog(e.midohraWorld, e.midohraPos)
        }

        return super.onRightClick(e)
    }


    // NORTH, SOUTH, EAST, WEST, UP, DOWNのプロパティを更新するための関数
    fun updateConnections(world: World, pos: BlockPos, tile: EnergyCableBlockEntity) {
        if (!DirectionBoolPropertyUtil.hasAll(world.getBlockState(pos))) return

        for (dir in Direction.values()) {
            val neighborPos = pos.offset(dir)
            val neighborTile = world.getBlockEntity(neighborPos)
            if (neighborTile.instanceOf(EnergyCableBlockEntity::class.java)) {
                DirectionBoolPropertyUtil.setProperty(world, pos, dir, true)
                continue
            }

//            if (tile.getEnergyStorage() is TREnergyStorage) {
            RebornEnergyRegister.ENERGY_LOOKUP.find(world, neighborPos, dir.opposite)?.let { _ ->
                DirectionBoolPropertyUtil.setProperty(world, pos, dir, true)
                continue
            }
//            }

            if (neighborTile.instanceOf(AbstractEnergyBlockEntity::class.java)) {
                DirectionBoolPropertyUtil.setProperty(world, pos, dir, true)
                continue
            }

            DirectionBoolPropertyUtil.setProperty(world, pos, dir, false)
        }
    }

    override fun onStateReplaced(e: StateReplacedEvent) {
        if (!e.isClient) {
            CableNetworkManager.onCableChanged(e.midohraWorld, e.midohraPos)
//            CableNetworkManager.printLog(e.midohraWorld, e.midohraPos)
        }

        val cable = e.blockEntityWrapper.getCompatBlockEntity(EnergyCableBlockEntity::class.java)
        if (cable != null) {
            updateConnections(e.midohraWorld, e.midohraPos, cable)
        }

        super.onStateReplaced(e)
    }

    override fun onPlaced(e: BlockPlacedEvent) {
        if (!e.isClient) {
            CableNetworkManager.onCableChanged(e.midohraWorld, e.midohraPos)
//            CableNetworkManager.printLog(e.midohraWorld, e.midohraPos)
        }

        val cable = e.blockEntityWrapper.getCompatBlockEntity(EnergyCableBlockEntity::class.java)
        if (cable != null) {
            updateConnections(e.midohraWorld, e.midohraPos, cable)
        }

        super.onPlaced(e)
    }

    override fun createBlockEntity(e: TileCreateEvent): CompatBlockEntity {
        return EnergyCableBlockEntity(e, speed)
    }

    override fun isTick(): Boolean {
        return true
    }

    override fun appendProperties(args: AppendPropertiesArgs) {
        super.appendProperties(args)
        args.addAllDirectionBoolProperties()
        args.addProperty(CompatProperties.WATERLOGGED)
    }

    override fun getRenderType(args: RenderTypeArgs?): CompatBlockRenderType {
        return CompatBlockRenderType.MODEL
    }

    override fun getFluidStateM(args: FluidStateArgs): FluidState {
        if (CompatProperties.WATERLOGGED.get(args.state)) {
            return FluidState.water()
        }

        return super.getFluidStateM(args)
    }

    override fun getPlacementState(args: PlacementStateArgs?): BlockState? {
        if (args != null) {
            return this.defaultMidohraState.with(CompatProperties.WATERLOGGED,
                args.world.getFluid(args.pos).equals(Fluids.WATER))
        }

        return super.getPlacementState(args as PlacementStateArgs?)
    }

    override fun neighborUpdate(e: NeighborUpdateEvent?) {
        super.neighborUpdate(e)
        if (e == null) return

        if (!e.blockEntityWrapper.instanceOf(EnergyCableBlockEntity::class.java)) return

        updateConnections(e.midohraWorld, e.midohraPos, e.blockEntityWrapper.getCompatBlockEntity(EnergyCableBlockEntity::class.java))
    }

    override fun appendTooltip(e: ItemAppendTooltipEvent) {
        super.appendTooltip(e)
        val style = CompatStyle().withColor(CompatFormatting.AQUA);
        e.addTooltip(TextComponent.translatable("tooltip.simplecables76.energy_cable", speed).setStyle(style))
    }
}
