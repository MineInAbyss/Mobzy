package com.mineinabyss.mobzy.spawning

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.WorldMock
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.mobzy.MobzyConfig
import com.mineinabyss.mobzy.mobzyConfig
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockkStatic
import org.bukkit.Material
import org.junit.jupiter.api.BeforeEach

abstract class BukkitTest {
    lateinit var world: WorldMock
    lateinit var server: ServerMock
    open val customData = MobzyConfig.Data(
        chunkSpawnRad = 0..1,
        maxCommandSpawns = 3,
        playerGroupRadius = 20.0,
        spawnTaskDelay = 10.ticks,
        spawnHeightRange = 100,
    )

    @BeforeEach
    fun mockServer() {
        clearAllMocks()
        MockBukkit.unmock()
        world = WorldMock(Material.GRASS_BLOCK, 10)
        server = MockBukkit.mock().apply {
            addWorld(world)
        }

        mockkStatic(::mobzyConfig)
        every { mobzyConfig } returns customData
    }
}
