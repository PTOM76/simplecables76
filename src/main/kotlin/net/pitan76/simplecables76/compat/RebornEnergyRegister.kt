package net.pitan76.simplecables76.compat

import net.pitan76.mcpitanlib.api.lookup.block.BlockApiLookupWithDirection
import net.pitan76.simplecables76.block.BaseEnergyTile
import net.pitan76.simplecables76.block.BlockEntities
import team.reborn.energy.api.EnergyStorage

object RebornEnergyRegister {
    fun init() {
        println("Registering Reborn Energy Storage for Energy Cable")
        BlockApiLookupWithDirection(EnergyStorage.SIDED).registerForBlockEntityM({ blockEntity, _ ->
            if (blockEntity is BaseEnergyTile) {
                if (blockEntity.getEnergyStorage() is TREnergyStorage)
                    return@registerForBlockEntityM blockEntity.getEnergyStorage() as TREnergyStorage

                if (!blockEntity.hasEnergyStorage())
                    blockEntity.setEnergyStorage(TREnergyStorage(blockEntity))
            }

            return@registerForBlockEntityM null
        }, BlockEntities.ENERGY_CABLE.get())
    }
}
