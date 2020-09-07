package com.mineinabyss.geary.ecs

interface GearyEntity {
    val gearyId: Int
}

class GearyEntityWrapper(override val gearyId: Int) : GearyEntity

inline fun geary(id: Int, run: GearyEntity.() -> Unit): GearyEntity =
        GearyEntityWrapper(id).apply(run)

inline fun geary(id: Int): GearyEntity = GearyEntityWrapper(id)