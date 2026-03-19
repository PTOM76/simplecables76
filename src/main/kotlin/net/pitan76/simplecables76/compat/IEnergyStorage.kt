package net.pitan76.simplecables76.compat

interface IEnergyStorage {
    val maxOutput: Long
    val maxInput: Long
    var energy: Long
    val maxEnergy: Long
}
