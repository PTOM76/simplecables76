package net.pitan76.simplecables76.compat

interface IEnergyStorage {
    val maxOutput: Long
    val maxInput: Long
    var energy: Long
    val maxEnergy: Long
    val canInput: Boolean
    val canOutput: Boolean

    fun insert(maxAmount: Long): Long
    fun extract(maxAmount: Long): Long
}
