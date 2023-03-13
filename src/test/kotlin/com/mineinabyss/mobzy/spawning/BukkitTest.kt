package com.mineinabyss.mobzy.spawning

import be.seeseemelk.mockbukkit.MockBukkit
import be.seeseemelk.mockbukkit.ServerMock
import be.seeseemelk.mockbukkit.WorldMock
import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.time.ticks
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.bukkit.Material
import org.junit.jupiter.api.BeforeEach

abstract class BukkitTest {
    lateinit var world: WorldMock
    lateinit var server: ServerMock

    @BeforeEach
    fun mockServer() {
        clearAllMocks()
        MockBukkit.unmock()
        world = WorldMock(Material.GRASS_BLOCK, 10)
        server = MockBukkit.mock().apply {
            addWorld(world)
        }
    }
}
