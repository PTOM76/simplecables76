package net.pitan76.simplecables76.block

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.pitan76.mcpitanlib.api.registry.result.SupplierResult
import net.pitan76.mcpitanlib.api.tile.BlockEntityTypeBuilder.Factory
import net.pitan76.mcpitanlib.api.tile.v2.BlockEntityTypeBuilder
import net.pitan76.simplecables76.SimpleCables.Companion._id
import net.pitan76.simplecables76.SimpleCables.Companion.registry
import java.util.function.Consumer

object BlockEntities {
    lateinit var ENERGY_CABLE: SupplierResult<BlockEntityType<EnergyCableBlockEntity?>>

    @JvmStatic
    fun init() {
        ENERGY_CABLE = registry.registerBlockEntityType(_id("energy_cable"),
            create(::EnergyCableBlockEntity) {
                listOf(Blocks.ENERGY_CABLE.get(), Blocks.COPPER_CABLE.get(), Blocks.IRON_CABLE.get(), Blocks.GOLD_CABLE.get())
            }
        )
    }

    @JvmStatic
    fun <T : BlockEntity?> create(factory: Factory<out T?>?, wrapper: Consumer<List<Block>>): BlockEntityTypeBuilder<T?> {
        return BlockEntityTypeBuilder.create<T?>(factory, wrapper)
    }
}
