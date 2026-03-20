package net.pitan76.simplecables76.compat

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import team.reborn.energy.api.EnergyStorage
import kotlin.use

class EnergyStorageWrapper(var energyStorage: EnergyStorage) : IEnergyStorage {

    override val maxOutput: Long
        get() = Long.MAX_VALUE

    override val maxInput: Long
        get() = Long.MAX_VALUE

    override val maxEnergy: Long
        get() = energyStorage.capacity

    override val canInput: Boolean
        get() = energyStorage.supportsInsertion()

    override val canOutput: Boolean
        get() = energyStorage.supportsExtraction()

    override fun insert(maxAmount: Long): Long {
        Transaction.openOuter().use { transaction ->
            val inserted = energyStorage.insert(maxAmount, transaction)
            if (inserted > 0)
                transaction.commit()

            return inserted
        }
    }

    override fun extract(maxAmount: Long): Long {
        Transaction.openOuter().use { transaction ->
            val extracted = energyStorage.extract(maxAmount, transaction)
            if (extracted > 0)
                transaction.commit()

            return extracted
        }
    }

    override var energy: Long
        get() = energyStorage.amount
        set(value) {
            val delta = value - energyStorage.amount
            if (delta > 0) {
                Transaction.openOuter().use { transaction ->
                    val inserted = energyStorage.insert(delta, transaction)
                    if (inserted > 0) {
                        transaction.commit()
                    }
                }
            } else if (delta < 0) {
                Transaction.openOuter().use { transaction ->
                    val extracted = energyStorage.extract(-delta, transaction)
                    if (extracted > 0) {
                        transaction.commit()
                    }
                }
            }
        }
}
