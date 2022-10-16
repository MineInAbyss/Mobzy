package com.mineinabyss.mobzy.spawning

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.WorldMock
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.mobzy.MobzyConfig
import io.mockk.clearAllMocks
import org.bukkit.Material
import org.junit.jupiter.api.BeforeEach
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

abstract class BukkitTest {
    lateinit var world: WorldMock
    lateinit var server: ServerMock

    @BeforeEach
    fun mockServer() {
        stopKoin()
        startKoin {
            modules(module {
                single {
                    MobzyConfig(
                        chunkSpawnRad = 0..1,
                        maxCommandSpawns = 3,
                        playerGroupRadius = 20.0,
                        spawnTaskDelay = 10.ticks,
                        spawnHeightRange = 100,
                    )
                }
            })
        }

        clearAllMocks()
        MockBukkit.unmock()
        world = WorldMock(Material.GRASS_BLOCK, 10)
        server = MockBukkit.mock().apply {
            addWorld(world)
        }
    }
}
