package net.pitan76.simplecables76.compat

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant
import net.pitan76.simplecables76.Config
import net.pitan76.simplecables76.block.BaseEnergyTile
import team.reborn.energy.api.EnergyStorage
import kotlin.use

class TREnergyStorage(val tile: BaseEnergyTile) : SnapshotParticipant<Long?>(), EnergyStorage, IEnergyStorage {

    val usableCapacity: Long
        get() = (tile.usableCapacity / CONVERSION_RATE).toLong()

    override fun insert(maxAmount: Long, transaction: TransactionContext?): Long {
        if (maxAmount < this.usableCapacity) {
            updateSnapshots(transaction)
            return (tile.insertEnergy((maxAmount * CONVERSION_RATE).toLong()) / CONVERSION_RATE).toLong()
        }
        if (maxAmount > 0) {
            updateSnapshots(transaction)
            return (tile.insertEnergy((this.usableCapacity * CONVERSION_RATE).toLong()) / CONVERSION_RATE).toLong()
        }

        return 0
    }

    override fun extract(maxAmount: Long, transaction: TransactionContext?): Long {
        if (maxAmount < this.amount) {
            updateSnapshots(transaction)
            return (tile.extractEnergy((maxAmount * CONVERSION_RATE).toLong()) / CONVERSION_RATE).toLong()
        }
        if (this.amount > 0) {
            updateSnapshots(transaction)
            return (tile.extractEnergy(tile.energy) / CONVERSION_RATE).toLong()
        }

        return 0
    }

    override fun getAmount(): Long {
        return (tile.energy / CONVERSION_RATE).toLong()
    }

    override fun getCapacity(): Long {
        return (tile.maxEnergy / CONVERSION_RATE).toLong()
    }

    override fun createSnapshot(): Long {
        return tile.energy
    }

    override fun readSnapshot(snapshot: Long?) {
        if (snapshot == null) return
        tile.energy = snapshot
    }

    override val maxOutput: Long
        get() = tile.maxOutput

    override val maxInput: Long
        get() = tile.maxInput

    override val maxEnergy: Long
        get() = capacity

    override val canInput: Boolean
        get() = tile.canInput

    override val canOutput: Boolean
        get() = tile.canOutput

    override fun insert(maxAmount: Long): Long {
        Transaction.openOuter().use { transaction ->
            val inserted = insert(maxAmount, transaction)
            if (inserted > 0)
                transaction.commit()

            return inserted
        }
    }

    override fun extract(maxAmount: Long): Long {
        Transaction.openOuter().use { transaction ->
            val extracted = extract(maxAmount, transaction)
            if (extracted > 0)
                transaction.commit()

            return extracted
        }
    }

    override var energy: Long
        get() = tile.energy
        set(value) {
            tile.energy = value
        }

    companion object {
        val CONVERSION_RATE: Double = Config.rebornEnergyConversionRate
    }
}