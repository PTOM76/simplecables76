package net.pitan76.simplecables76.compat

import net.pitan76.simplecables76.block.BaseEnergyTile
import net.pitan76.simplecables76.block.BlockEntities
import team.reborn.energy.api.EnergyStorage

object RebornEnergyRegister {
    fun init() {
        EnergyStorage.SIDED.registerForBlockEntity({ blockEntity, _ ->
            if (blockEntity !is BaseEnergyTile) return@registerForBlockEntity null

            if (!blockEntity.hasEnergyStorage())
                blockEntity.setEnergyStorage(TREnergyStorage(blockEntity))

            if (blockEntity.getEnergyStorage() is TREnergyStorage)
                return@registerForBlockEntity blockEntity.getEnergyStorage() as TREnergyStorage

            return@registerForBlockEntity null
        }, BlockEntities.ENERGY_CABLE.get())
    }
}
