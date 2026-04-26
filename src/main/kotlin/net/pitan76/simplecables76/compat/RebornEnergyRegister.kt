package net.pitan76.simplecables76.compat

import net.pitan76.mcpitanlib.api.lookup.block.BlockApiLookupWithDirection
import net.pitan76.simplecables76.block.entity.AbstractEnergyBlockEntity
import net.pitan76.simplecables76.block.entity.BlockEntities
import team.reborn.energy.api.EnergyStorage

object RebornEnergyRegister {

    val ENERGY_LOOKUP = BlockApiLookupWithDirection(EnergyStorage.SIDED)
//        (BlockApiLookupWithDirection::class.java.methods
//        .find { it.name == "ofDir" }!!
//        .invoke(null, EnergyStorage.SIDED)) as BlockApiLookupWithDirection<EnergyStorage>

    fun init() {
        println("Registering Reborn Energy Storage for Energy Cable")

        for (supplier in listOf(BlockEntities.ENERGY_CABLE, BlockEntities.COPPER_CABLE, BlockEntities.IRON_CABLE, BlockEntities.GOLD_CABLE)) {
            ENERGY_LOOKUP.registerForBlockEntityM({ blockEntity, _ ->
                if (blockEntity is AbstractEnergyBlockEntity) {
                    if (blockEntity.getEnergyStorage() is TREnergyStorage)
                        return@registerForBlockEntityM blockEntity.getEnergyStorage() as TREnergyStorage

                    if (!blockEntity.hasEnergyStorage())
                        blockEntity.setEnergyStorage(TREnergyStorage(blockEntity))
                }

                return@registerForBlockEntityM null
            }, supplier.get())
        }
    }
}
