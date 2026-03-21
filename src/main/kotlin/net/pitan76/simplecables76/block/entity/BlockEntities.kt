package net.pitan76.simplecables76.block.entity

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.pitan76.mcpitanlib.api.registry.result.SupplierResult
import net.pitan76.mcpitanlib.api.tile.BlockEntityTypeBuilder
import net.pitan76.mcpitanlib.midohra.block.SupplierBlockWrapper
import net.pitan76.simplecables76.SimpleCables
import net.pitan76.simplecables76.SimpleCables.Companion.registry
import net.pitan76.simplecables76.block.Blocks
import java.util.function.Consumer

object BlockEntities {
    lateinit var ENERGY_CABLE: SupplierResult<BlockEntityType<EnergyCableBlockEntity?>>
    lateinit var COPPER_CABLE: SupplierResult<BlockEntityType<EnergyCableBlockEntity?>>
    lateinit var IRON_CABLE: SupplierResult<BlockEntityType<EnergyCableBlockEntity?>>
    lateinit var GOLD_CABLE: SupplierResult<BlockEntityType<EnergyCableBlockEntity?>>


    @JvmStatic
    fun init() {
//        ENERGY_CABLE = registry.registerBlockEntityType(_id("energy_cable"),
//            create(::EnergyCableBlockEntity) {
//                listOf(Blocks.ENERGY_CABLE.get(), Blocks.COPPER_CABLE.get(), Blocks.IRON_CABLE.get(), Blocks.GOLD_CABLE.get())
//            }
//        )

        ENERGY_CABLE = registry.registerBlockEntityType(
            SimpleCables._id("energy_cable"),
            create(::EnergyCableBlockEntity, SupplierBlockWrapper.of(Blocks.ENERGY_CABLE))
        )

        COPPER_CABLE = registry.registerBlockEntityType(
            SimpleCables._id("copper_cable"),
            create(::CopperCableBlockEntity, SupplierBlockWrapper.of(Blocks.COPPER_CABLE))
        )

        IRON_CABLE = registry.registerBlockEntityType(
            SimpleCables._id("iron_cable"),
            create(::IronCableBlockEntity, SupplierBlockWrapper.of(Blocks.IRON_CABLE))
        )

        GOLD_CABLE = registry.registerBlockEntityType(
            SimpleCables._id("gold_cable"),
            create(::GoldCableBlockEntity, SupplierBlockWrapper.of(Blocks.GOLD_CABLE))
        )
    }

    @JvmStatic
    fun <T : BlockEntity?> create(factory: BlockEntityTypeBuilder.Factory<out T?>?, wrapper: Consumer<List<Block>>): net.pitan76.mcpitanlib.api.tile.v2.BlockEntityTypeBuilder<T?> {
        return net.pitan76.mcpitanlib.api.tile.v2.BlockEntityTypeBuilder.create<T?>(factory, wrapper)
    }

    @JvmStatic
    fun <T : BlockEntity?> create(factory: BlockEntityTypeBuilder.Factory<out T?>?, wrapper: SupplierBlockWrapper): net.pitan76.mcpitanlib.api.tile.v2.BlockEntityTypeBuilder<T?> {
        return net.pitan76.mcpitanlib.api.tile.v2.BlockEntityTypeBuilder.create<T?>(factory, wrapper)
    }
}