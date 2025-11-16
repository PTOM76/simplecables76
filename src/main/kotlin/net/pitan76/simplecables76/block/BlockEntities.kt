package net.pitan76.simplecables76.block

import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.pitan76.mcpitanlib.api.registry.result.SupplierResult
import net.pitan76.mcpitanlib.api.tile.v2.BlockEntityTypeBuilder
import net.pitan76.mcpitanlib.api.tile.BlockEntityTypeBuilder.Factory
import net.pitan76.mcpitanlib.midohra.block.SupplierBlockWrapper
import net.pitan76.simplecables76.SimpleCables._id
import net.pitan76.simplecables76.SimpleCables.registry

object BlockEntities {
    lateinit var ENERGY_CABLE: SupplierResult<BlockEntityType<EnergyCableBlockEntity?>>

    @JvmStatic
    fun init() {
        ENERGY_CABLE =
            registry.registerBlockEntityType(_id("energy_cable")
                , create(::EnergyCableBlockEntity, SupplierBlockWrapper.of(Blocks.ENERGY_CABLE))
            );
    }

    @JvmStatic
    fun <T : BlockEntity?> create(factory: Factory<out T?>?, wrapper: SupplierBlockWrapper): BlockEntityTypeBuilder<T?> {
        return BlockEntityTypeBuilder.create<T?>(factory, wrapper);
    }
}
