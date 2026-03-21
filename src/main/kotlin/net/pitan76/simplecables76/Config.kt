package net.pitan76.simplecables76

import net.pitan76.easyapi.FileControl
import net.pitan76.easyapi.config.JsonConfig
import java.io.File

object Config {
    private lateinit var configDir: File
    private const val FILENAME: String = "${SimpleCables.MOD_ID}.json"
    private val config = JsonConfig()

    val configFile: File
        get() = File(configDir, FILENAME)

    fun init(configDir: File) {
        if (this::configDir.isInitialized) return
        this.configDir = configDir

        defaultConfig()

        if (FileControl.fileExists(configFile)) {
            config.load(configFile)
        } else {
            config.save(configFile)
        }

        defaultConfig() // 既存設定以外をデフォルトに
    }

    fun reload(): Boolean {
        if (FileControl.fileExists(configFile)) {
            config.load(configFile)
            return true
        }
        return false
    }

    fun defaultConfig() {
        if (!config.has("energy.rebornEnergyConversionRate"))
            config.setDouble("energy.rebornEnergyConversionRate", 1.0)

        if (!config.has("energy.transferRate")) {
            config.setInt("energy.transferRate.energyCable", 512)
            config.setInt("energy.transferRate.copperCable", 256)
            config.setInt("energy.transferRate.ironCable", 512)
            config.setInt("energy.transferRate.goldCable", 1024)
        }
    }

    fun save() = config.save(configFile)

    val rebornEnergyConversionRate: Double
        get() = config.getDoubleOrDefault("energy.rebornEnergyConversionRate", 1.0)

    fun getEnergyTransferRate(cableType: String): Int {
        return config.getIntOrDefault("energy.transferRate.$cableType", when (cableType) {
            "energyCable" -> 512
            "copperCable" -> 256
            "ironCable" -> 512
            "goldCable" -> 1024
            else -> 512
        })
    }

    var energyCableTransferRate: Int
        get() = getEnergyTransferRate("energyCable")
        set(value) {
            config.setInt("energy.transferRate.energyCable", value)
        }

    var copperCableTransferRate: Int
        get() = getEnergyTransferRate("copperCable")
        set(value) {
            config.setInt("energy.transferRate.copperCable", value)
        }

    var ironCableTransferRate: Int
        get() = getEnergyTransferRate("ironCable")
        set(value) {
            config.setInt("energy.transferRate.ironCable", value)
        }

    var goldCableTransferRate: Int
        get() = getEnergyTransferRate("goldCable")
        set(value) {
            config.setInt("energy.transferRate.goldCable", value)
        }
}
