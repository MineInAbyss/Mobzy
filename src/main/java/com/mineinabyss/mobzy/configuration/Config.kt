package com.mineinabyss.mobzy.configuration

import com.charleskorn.kaml.Yaml
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.success
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

interface DataContaining<T> { //TODO expand this to be more useful for other configs, not just main plugin one
    /** Saves the current serialized data */
    fun saveData()

    /** Discards current data and re-reads and serializes it */
    fun loadData(): T
}

/**
 * Holds the config for a plugin's main configuration file. Ensures that [getConfig()][JavaPlugin.getConfig] will
 * return information since the last time the plugin was saved. This is limited to Yaml and should only be used
 *
 */
abstract class PluginConfigHolder<T>(
        val plugin: JavaPlugin,
        val serializer: KSerializer<T>,
        val file: File?,
        val format: StringFormat = Yaml.default
) : DataContaining<T> {
    var data: T = loadData()

    final override fun saveData() {
        file.writer()
        plugin.config.loadFromString(format.stringify(serializer, data))
        plugin.saveConfig()
    }

    final override fun loadData(): T {
        plugin.reloadConfig()
        val data = format.parse(serializer, plugin.config.saveToString())
        this.data = data
        return data
    }
}

abstract class PluginConfig<T>(private val holder: PluginConfigHolder<T>) : DataContaining<T> by holder {
    fun reload(sender: CommandSender = holder.plugin.server.consoleSender) {
        val context = ReloadContext(sender)
        reload().invoke(context)
    }

    protected abstract fun reload(): ReloadContext.() -> Unit
}

class ReloadContext(
        val sender: CommandSender
) {
    val consoleSender = Bukkit.getConsoleSender()

    fun attempt(success: String, fail: String, block: () -> Unit) {
        try {
            block()
            sender.success(success)
            if (sender != consoleSender) consoleSender.success(success)
        } catch (e: Exception) {
            sender.error(fail)
            if (sender != consoleSender) consoleSender.error(fail)
            e.printStackTrace()
            return
        }
    }

}