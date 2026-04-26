package net.pitan76.simplecables76.block.entity

import net.pitan76.mcpitanlib.api.event.block.TileCreateEvent
import net.pitan76.mcpitanlib.api.event.nbt.ReadNbtArgs
import net.pitan76.mcpitanlib.api.event.nbt.WriteNbtArgs
import net.pitan76.mcpitanlib.api.tile.CompatBlockEntity
import net.pitan76.mcpitanlib.api.util.nbt.v2.NbtRWUtil
import net.pitan76.mcpitanlib.midohra.block.entity.BlockEntityTypeWrapper
import net.pitan76.simplecables76.compat.IEnergyStorage

abstract class AbstractEnergyBlockEntity : CompatBlockEntity {

    constructor(type: BlockEntityTypeWrapper, e: TileCreateEvent) : super(type, e)

    abstract val maxEnergy: Long

    val usableCapacity: Long
        get() = this.maxEnergy - this.energy

    abstract val maxOutput: Long

    abstract val maxInput: Long

    var energy: Long = 0

    val canInput: Boolean
        get() = maxInput > 0

    val canOutput: Boolean
        get() = maxOutput > 0

    private var energyStorage: IEnergyStorage? = null

    fun setEnergyStorage(energyStorage: IEnergyStorage?) {
        this.energyStorage = energyStorage
    }

    fun getEnergyStorage(): IEnergyStorage? {
        return energyStorage
    }

    fun hasEnergyStorage(): Boolean {
        return energyStorage != null
    }

    override fun writeNbt(args: WriteNbtArgs) {
        super.writeNbt(args)
        NbtRWUtil.putLong(args, "energy", this.energy)
    }

    override fun readNbt(args: ReadNbtArgs) {
        super.readNbt(args)
        this.energy = NbtRWUtil.getLongOrDefault(args, "energy", 0)
    }

    fun addEnergy(energy: Long): Boolean {
        if (canAddEnergy(energy)) {
            this.energy += energy
            return true
        }
        return false
    }

    fun removeEnergy(energy: Long): Boolean {
        return addEnergy(-energy)
    }

    fun canAddEnergy(energy: Long): Boolean {
        return this.maxEnergy > this.energy + energy && this.energy + energy >= 0
    }

    fun insertEnergy(amount: Long): Long {
        val usableCapacity = this.usableCapacity
        if (amount > usableCapacity) {
            this.energy += usableCapacity
            return usableCapacity
        }
        this.energy += amount
        return amount
    }

    fun extractEnergy(amount: Long): Long {
        if (amount > this.energy) {
            val energy = this.energy
            this.energy = 0
            return energy
        }
        this.energy -= amount
        return amount
    }
}