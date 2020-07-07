package com.mineinabyss.mobzy.configuration

import com.charleskorn.kaml.Yaml
import com.mineinabyss.idofront.messaging.logInfo
import com.mineinabyss.idofront.messaging.logSuccess
import kotlinx.serialization.KSerializer
import kotlinx.serialization.StringFormat
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

abstract class ConfigHolder<T>(
        val plugin: JavaPlugin,
        val serializer: KSerializer<T>,
        val file: File,
        val format: StringFormat = Yaml.default
) : DataContaining<T> {
    init {
        logInfo("Registering configuration ${file.name}")
        if (!plugin.dataFolder.exists()) plugin.dataFolder.mkdir()
        if (!file.exists()) {
            file.parentFile.mkdirs()
            plugin.saveResource(file.name, false)
            logSuccess("${file.name} has been created")
        }
        logSuccess("Registered configuration: ${file.name}")
    }

    var data: T = loadData()

    final override fun saveData() = file.writeText(format.stringify(serializer, data))

    final override fun loadData(): T = format.parse(serializer, file.readText()).also { data = it }
}