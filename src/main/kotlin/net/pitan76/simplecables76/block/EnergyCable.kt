package net.pitan76.simplecables76.block

import net.minecraft.world.level.material.FluidState
import net.minecraft.world.phys.shapes.VoxelShape
import net.pitan76.mcpitanlib.api.block.CompatBlockRenderType
import net.pitan76.mcpitanlib.api.block.CompatWaterloggable
import net.pitan76.mcpitanlib.api.block.args.RenderTypeArgs
import net.pitan76.mcpitanlib.api.block.args.v2.OutlineShapeEvent
import net.pitan76.mcpitanlib.api.block.args.v2.PlacementStateArgs
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings
import net.pitan76.mcpitanlib.api.event.block.*
import net.pitan76.mcpitanlib.api.lookup.block.BlockApiLookupWithDirection
import net.pitan76.mcpitanlib.api.state.property.CompatProperties
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity
import net.pitan76.mcpitanlib.api.util.*
import net.pitan76.mcpitanlib.midohra.fluid.Fluids
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos
import net.pitan76.mcpitanlib.midohra.util.math.Direction
import net.pitan76.mcpitanlib.midohra.world.World
import team.reborn.energy.api.EnergyStorage
import techreborn.blocks.cable.CableBlock

class EnergyCable : AbstractCable, CompatWaterloggable {

    constructor(settings: CompatibleBlockSettings) : super(settings) {
        setDefaultState(DirectionBoolPropertyUtil.clearAll(defaultMidohraState))
    }

    override fun getOutlineShape(e: OutlineShapeEvent): VoxelShape {
        var shape = getCenterShape()

        if (e.get(CompatProperties.UP)) {
            shape = VoxelShapeUtil.union(shape,
                VoxelShapeUtil.blockCuboid(5.0, 11.0, 5.0,
                    11.0, 16.0, 11.0))
        }
        if (e.get(CompatProperties.DOWN)) {
            shape = VoxelShapeUtil.union(shape,
                VoxelShapeUtil.blockCuboid(5.0, 0.0, 5.0,
                    11.0, 5.0, 11.0))
        }
        if (e.get(CompatProperties.NORTH)) {
            shape = VoxelShapeUtil.union(shape,
                VoxelShapeUtil.blockCuboid(5.0, 5.0, 0.0,
                    11.0, 11.0, 5.0))
        }
        if (e.get(CompatProperties.EAST)) {
            shape = VoxelShapeUtil.union(shape,
                VoxelShapeUtil.blockCuboid(11.0, 5.0, 5.0,
                    16.0, 11.0, 11.0))
        }
        if (e.get(CompatProperties.SOUTH)) {
            shape = VoxelShapeUtil.union(shape,
                VoxelShapeUtil.blockCuboid(5.0, 5.0, 11.0,
                    11.0, 11.0, 16.0))
        }
        if (e.get(CompatProperties.WEST)) {
            shape = VoxelShapeUtil.union(shape,
                VoxelShapeUtil.blockCuboid(0.0, 5.0, 5.0,
                    5.0, 11.0, 11.0))
        }

        return shape
    }

    fun getCenterShape(): VoxelShape {
        return VoxelShapeUtil.blockCuboid(5.0, 5.0, 5.0, 11.0, 11.0, 11.0)
    }

    override fun onRightClick(e: BlockUseEvent): CompatActionResult {
        val blockEntity = e.blockEntity
        if (blockEntity is BaseEnergyTile) {
            if (e.isClient) return CompatActionResult.SUCCESS
            e.player.sendMessage("Energy: ${blockEntity.energy} / ${blockEntity.maxEnergy}")
        }

        return super.onRightClick(e)
    }


    // NORTH, SOUTH, EAST, WEST, UP, DOWNのプロパティを更新するための関数
    fun updateConnections(world: World, pos: BlockPos, tile: EnergyCableBlockEntity) {
        if (!DirectionBoolPropertyUtil.hasAll(world.getBlockState(pos))) return

        for (dir in Direction.values()) {
            val neighborPos = pos.offset(dir)
            val neighborTile = world.getBlockEntity(neighborPos).get()
            if (neighborTile is EnergyCableBlockEntity) {
                DirectionBoolPropertyUtil.setProperty(world, pos, dir, true)
                continue
            }

//            if (tile.getEnergyStorage() is TREnergyStorage) {
            BlockApiLookupWithDirection(EnergyStorage.SIDED).find(world, neighborPos, dir.opposite)?.let { _ ->
                DirectionBoolPropertyUtil.setProperty(world, pos, dir, true)
                continue
            }
//            }

            if (neighborTile is BaseEnergyTile) {
                DirectionBoolPropertyUtil.setProperty(world, pos, dir, true)
                continue
            }

            DirectionBoolPropertyUtil.setProperty(world, pos, dir, false)
        }
    }

    override fun onStateReplaced(e: StateReplacedEvent) {
        if (!e.isClient) {
            CableNetworkManager.onCableChanged(e.midohraWorld, e.midohraPos)
        }

        val cable = e.blockEntity as? EnergyCableBlockEntity
        if (cable != null) {
            updateConnections(e.midohraWorld, e.midohraPos, cable)
        }

        super.onStateReplaced(e)
    }

    override fun onPlaced(e: BlockPlacedEvent) {
        if (!e.isClient) {
            CableNetworkManager.onCableChanged(e.midohraWorld, e.midohraPos)
        }

        val cable = e.blockEntity as? EnergyCableBlockEntity
        if (cable != null) {
            updateConnections(e.midohraWorld, e.midohraPos, cable)
        }

        super.onPlaced(e)
    }

    override fun createBlockEntity(e: TileCreateEvent): CompatBlockEntity {
        return EnergyCableBlockEntity(e)
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

    override fun getFluidState(args: FluidStateArgs?): FluidState? {
        if (args != null && CompatProperties.WATERLOGGED.get(args.state)) {
            return FluidUtil.getStillWater()
        }

        return super.getFluidState(args)
    }

    override fun getPlacementState(args: PlacementStateArgs?): net.pitan76.mcpitanlib.midohra.block.BlockState? {
        if (args != null) {
            return this.defaultMidohraState.with(CompatProperties.WATERLOGGED,
                FluidStateUtil.getFluidWrapper(args.world, args.pos) == Fluids.WATER)
        }

        return super.getPlacementState(args as PlacementStateArgs?)
    }

    override fun neighborUpdate(e: NeighborUpdateEvent?) {
        super.neighborUpdate(e)
        if (e == null) return

        val blockEntity = e.blockEntity
        if (blockEntity !is EnergyCableBlockEntity) return

        updateConnections(e.midohraWorld, e.midohraPos, blockEntity)
    }
}
