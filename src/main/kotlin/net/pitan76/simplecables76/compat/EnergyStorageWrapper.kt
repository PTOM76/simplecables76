package net.pitan76.simplecables76.compat

import team.reborn.energy.api.EnergyStorage

class EnergyStorageWrapper(var energyStorage: EnergyStorage) : IEnergyStorage {

    override val maxOutput: Long
        get() = Long.MAX_VALUE

    override val maxInput: Long
        get() = Long.MAX_VALUE

    override val maxEnergy: Long
        get() = energyStorage.capacity

    override var energy: Long
        get() = energyStorage.amount
        set(value) {
            val delta = value - energyStorage.amount
            if (delta > 0) {
                energyStorage.insert(delta, null)
            } else if (delta < 0) {
                energyStorage.extract(-delta, null)
            }
        }
}
