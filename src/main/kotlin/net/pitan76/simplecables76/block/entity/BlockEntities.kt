package net.pitan76.simplecables76.block.entity

import net.pitan76.mcpitanlib.api.tile.BlockEntityTypeBuilder
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity
import net.pitan76.mcpitanlib.midohra.block.BlockWrapper
import net.pitan76.mcpitanlib.midohra.block.SupplierBlockWrapper
import net.pitan76.mcpitanlib.midohra.block.entity.TypedBlockEntityTypeWrapper
import net.pitan76.simplecables76.SimpleCables
import net.pitan76.simplecables76.SimpleCables.Companion.registry
import net.pitan76.simplecables76.block.Blocks

object BlockEntities {
    lateinit var ENERGY_CABLE: TypedBlockEntityTypeWrapper<EnergyCableBlockEntity?>
    lateinit var COPPER_CABLE: TypedBlockEntityTypeWrapper<EnergyCableBlockEntity?>
    lateinit var IRON_CABLE: TypedBlockEntityTypeWrapper<EnergyCableBlockEntity?>
    lateinit var GOLD_CABLE: TypedBlockEntityTypeWrapper<EnergyCableBlockEntity?>

    @JvmStatic
    fun init() {
        ENERGY_CABLE = registry.registerBlockEntityType(
            SimpleCables._id("energy_cable"),
            create(::EnergyCableBlockEntity, Blocks.ENERGY_CABLE)
        )

        COPPER_CABLE = registry.registerBlockEntityType(
            SimpleCables._id("copper_cable"),
            create(::CopperCableBlockEntity, Blocks.COPPER_CABLE)
        )

        IRON_CABLE = registry.registerBlockEntityType(
            SimpleCables._id("iron_cable"),
            create(::IronCableBlockEntity, Blocks.IRON_CABLE)
        )

        GOLD_CABLE = registry.registerBlockEntityType(
            SimpleCables._id("gold_cable"),
            create(::GoldCableBlockEntity, Blocks.GOLD_CABLE)
        )
    }

    @JvmStatic
    fun <T : CompatBlockEntity?> create(factory: BlockEntityTypeBuilder.Factory<out T?>?, wrapper: BlockWrapper): net.pitan76.mcpitanlib.api.tile.v2.BlockEntityTypeBuilder<T?> {
        if (wrapper is SupplierBlockWrapper) {
            return net.pitan76.mcpitanlib.api.tile.v2.BlockEntityTypeBuilder.create<T?>(factory, wrapper)
        }

        val supplierWrapper: SupplierBlockWrapper = SupplierBlockWrapper.of{ wrapper.get() }
        return net.pitan76.mcpitanlib.api.tile.v2.BlockEntityTypeBuilder.create<T?>(factory, supplierWrapper)
    }
}