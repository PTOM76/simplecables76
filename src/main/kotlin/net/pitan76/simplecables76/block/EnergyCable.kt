package net.pitan76.simplecables76.block

import net.pitan76.mcpitanlib.api.block.CompatBlockRenderType
import net.pitan76.mcpitanlib.api.block.CompatWaterloggable
import net.pitan76.mcpitanlib.api.block.args.RenderTypeArgs
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

    override fun onRightClick(e: BlockUseEvent): CompatActionResult {
        val blockEntity = e.blockEntity
        if (blockEntity is BaseEnergyTile) {
            if (e.isClient) return CompatActionResult.SUCCESS
            e.player.sendMessage(TextUtil.of("Energy: ${blockEntity.energy} / ${blockEntity.maxEnergy}"))
        }

        return super.onRightClick(e)
    }

    override fun onStateReplaced(e: StateReplacedEvent) {
        if (!e.isClient) {
            CableNetworkManager.onCableChanged(e.midohraWorld, e.midohraPos)
        }

        super.onStateReplaced(e)
    }

    override fun onPlaced(e: BlockPlacedEvent) {
        if (!e.isClient) {
            CableNetworkManager.onCableChanged(e.midohraWorld, e.midohraPos)
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
