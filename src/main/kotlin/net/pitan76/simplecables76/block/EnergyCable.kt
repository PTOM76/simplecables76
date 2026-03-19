package net.pitan76.simplecables76.block

import net.minecraft.world.phys.shapes.VoxelShape
import net.pitan76.mcpitanlib.api.block.CompatBlockRenderType
import net.pitan76.mcpitanlib.api.block.CompatWaterloggable
import net.pitan76.mcpitanlib.api.block.args.RenderTypeArgs
import net.pitan76.mcpitanlib.api.block.args.v2.OutlineShapeEvent
import net.pitan76.mcpitanlib.api.block.v2.CompatibleBlockSettings
import net.pitan76.mcpitanlib.api.event.block.AppendPropertiesArgs
import net.pitan76.mcpitanlib.api.event.block.BlockPlacedEvent
import net.pitan76.mcpitanlib.api.event.block.BlockUseEvent
import net.pitan76.mcpitanlib.api.event.block.StateReplacedEvent
import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.state.property.CompatProperties
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity
import net.pitan76.mcpitanlib.api.util.CompatActionResult
import net.pitan76.mcpitanlib.api.util.TextUtil
import net.pitan76.mcpitanlib.api.util.VoxelShapeUtil
import net.pitan76.mcpitanlib.midohra.util.math.BlockPos
import net.pitan76.mcpitanlib.midohra.util.math.Direction
import net.pitan76.mcpitanlib.midohra.world.World
import net.pitan76.simplecables76.compat.TREnergyStorage
import team.reborn.energy.api.EnergyStorage

class EnergyCable : AbstractCable, CompatWaterloggable {

    constructor(settings: CompatibleBlockSettings) : super(settings) {
        setDefaultState(defaultMidohraState
            .with(CompatProperties.UP, false)
            .with(CompatProperties.DOWN, false)
            .with(CompatProperties.NORTH, false)
            .with(CompatProperties.EAST, false)
            .with(CompatProperties.SOUTH, false)
            .with(CompatProperties.WEST, false)
        )
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
            e.player.sendMessage(TextUtil.of("Energy: ${blockEntity.energy} / ${blockEntity.maxEnergy}"))
        }

        return super.onRightClick(e)
    }


    // NORTH, SOUTH, EAST, WEST, UP, DOWNのプロパティを更新するための関数
    fun updateConnections(world: World, pos: BlockPos, tile: EnergyCableBlockEntity) {
        for (dir in listOf(Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST)) {
            val neighborPos = pos.offset(dir)
            val neighborTile = world.getBlockEntity(neighborPos).get()
            if (neighborTile is EnergyCableBlockEntity) {
                when (dir) {
                    Direction.UP -> {
                        world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.UP, true))
                    }
                    Direction.DOWN -> {
                        world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.DOWN, true))
                    }
                    Direction.NORTH -> {
                        world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.NORTH, true))
                    }
                    Direction.SOUTH -> {
                        world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.SOUTH, true))
                    }
                    Direction.WEST -> {
                        world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.WEST, true))
                    }
                    Direction.EAST -> {
                        world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.EAST, true))
                    }
                }
                continue
            }

            if (tile.getEnergyStorage() is TREnergyStorage) {
                EnergyStorage.SIDED.find(world.raw, neighborPos.toRaw(), dir.opposite.raw)?.let { storage ->
                    when (dir) {
                        Direction.UP -> {
                            world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.UP, true))
                        }

                        Direction.DOWN -> {
                            world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.DOWN, true))
                        }

                        Direction.NORTH -> {
                            world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.NORTH, true))
                        }

                        Direction.SOUTH -> {
                            world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.SOUTH, true))
                        }

                        Direction.WEST -> {
                            world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.WEST, true))
                        }

                        Direction.EAST -> {
                            world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.EAST, true))
                        }
                    }
                    continue
                }
            }

            if (neighborTile is BaseEnergyTile) {
                when (dir) {
                    Direction.UP -> {
                        world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.UP, true))
                    }

                    Direction.DOWN -> {
                        world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.DOWN, true))
                    }

                    Direction.NORTH -> {
                        world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.NORTH, true))
                    }

                    Direction.SOUTH -> {
                        world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.SOUTH, true))
                    }

                    Direction.WEST -> {
                        world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.WEST, true))
                    }

                    Direction.EAST -> {
                        world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.EAST, true))
                    }
                }
                continue
            }

            when (dir) {
                Direction.UP -> {
                    world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.UP, false))
                }

                Direction.DOWN -> {
                    world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.DOWN, false))
                }

                Direction.NORTH -> {
                    world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.NORTH, false))
                }

                Direction.SOUTH -> {
                    world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.SOUTH, false))
                }

                Direction.WEST -> {
                    world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.WEST, false))
                }

                Direction.EAST -> {
                    world.setBlockState(pos, world.getBlockState(pos).with(CompatProperties.EAST, false))
                }
            }
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
        args.addProperty(CompatProperties.UP, CompatProperties.DOWN, CompatProperties.NORTH,
            CompatProperties.EAST, CompatProperties.SOUTH, CompatProperties.WEST)
    }

    override fun getRenderType(args: RenderTypeArgs?): CompatBlockRenderType {
        return CompatBlockRenderType.MODEL
    }
}
