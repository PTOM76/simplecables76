package net.pitan76.simplecables76.block.entity

import net.pitan76.mcpitanlib.api.tile.BlockEntityTypeBuilder
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity
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
            create(::EnergyCableBlockEntity, SupplierBlockWrapper.of { Blocks.ENERGY_CABLE.get() })
        )

        COPPER_CABLE = registry.registerBlockEntityType(
            SimpleCables._id("copper_cable"),
            create(::CopperCableBlockEntity, SupplierBlockWrapper.of { Blocks.COPPER_CABLE.get() })
        )

        IRON_CABLE = registry.registerBlockEntityType(
            SimpleCables._id("iron_cable"),
            create(::IronCableBlockEntity, SupplierBlockWrapper.of { Blocks.IRON_CABLE.get() })
        )

        GOLD_CABLE = registry.registerBlockEntityType(
            SimpleCables._id("gold_cable"),
            create(::GoldCableBlockEntity, SupplierBlockWrapper.of { Blocks.GOLD_CABLE.get() })
        )
    }

    @JvmStatic
    fun <T : CompatBlockEntity?> create(factory: BlockEntityTypeBuilder.Factory<out T?>?, wrapper: SupplierBlockWrapper): net.pitan76.mcpitanlib.api.tile.v2.BlockEntityTypeBuilder<T?> {
        return net.pitan76.mcpitanlib.api.tile.v2.BlockEntityTypeBuilder.create(factory, wrapper)
    }
}