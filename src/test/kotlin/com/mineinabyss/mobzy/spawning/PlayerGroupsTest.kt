package com.mineinabyss.mobzy.spawning

import be.seeseemelk.mockbukkit.entity.PlayerMock
import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.time.ticks
import io.kotest.matchers.collections.shouldContain
import io.mockk.every
import io.mockk.mockk
import org.bukkit.Location
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class PlayerGroupsTest: BukkitTest() {
    @BeforeAll
    fun setupConfig() {
        DI.add(mockk<MobzySpawnFeature.Context> {
            every { config } returns SpawnConfig(
                chunkSpawnRad = 0..1,
                maxCommandSpawns = 3,
                playerGroupRadius = 20.0,
                spawnTaskDelay = 10.ticks,
                spawnHeightRange = 100,
            )
        })
    }

    private fun playerAt(location: Location) = server.addPlayer().apply { this.location = location }

    fun populatePlayers(): List<PlayerMock> {
        val p1 = playerAt(Location(world, 0.0, 10.0, 0.0))
        val p2 = playerAt(Location(world, -10.0, 10.0, 0.0))

        val p3 = playerAt(Location(world, -31.0, 10.0, 0.0))

        val p4 = playerAt(Location(world, 15.0, 10.0, 15.0))
        val p5 = playerAt(Location(world, 30.0, 10.0, 5.0))

        return listOf(p1, p2, p3, p4, p5)
    }

//    @Test //TODO MockBukkit is broken here
    fun group() {
        val (p1, p2, p3, p4, p5) = populatePlayers()
        val groups = PlayerGroups.group(server.onlinePlayers)
        groups shouldContain listOf(p1, p2)
        groups shouldContain listOf(p3)
        groups shouldContain listOf(p4, p5)
    }
}
