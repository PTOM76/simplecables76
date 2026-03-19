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
    }

    fun save() = config.save(configFile)

    val rebornEnergyConversionRate: Double
        get() = config.getDoubleOrDefault("energy.rebornEnergyConversionRate", 1.0)

}
