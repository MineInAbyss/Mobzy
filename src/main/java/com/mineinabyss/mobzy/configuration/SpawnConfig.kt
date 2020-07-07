package com.mineinabyss.mobzy.configuration

import com.charleskorn.kaml.Yaml
import com.mineinabyss.mobzy.spawning.regions.SpawnRegion
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.StringFormat
import org.bukkit.Material
import java.io.File

/**
 * A singleton object extending this class will contain info about serializable data
 */
abstract class SerializableConfig<T>(
        val serializer: KSerializer<T>,
        val format: StringFormat = Yaml.default
) {
    internal lateinit var file: File

    fun saveData() = file.writeText(format.stringify(serializer, this as T))

    fun loadData(): T = format.parse(serializer, file.readText()).also { data = it }
}
fun <T> SerializableConfig<T>.new(file: File): T {
    val newConfig = format.parse(serializer, file.readText())
    this.file = file
    return newConfig
}

@Serializable
class SpawnConfigData(
        val name: String,
        val icon: Material,
        val regions: List<SpawnRegion>
)

object SpawnConfigHolder: SerializableConfig<SpawnConfigData>(SpawnConfigData.serializer())

fun main() {
    SpawnConfigHolder.new(TODO())
}