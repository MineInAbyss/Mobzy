package com.mineinabyss.mobzy.ai

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.minecraft.components.MobComponent
import com.mineinabyss.mobzy.api.helpers.entity.distanceSqrTo
import com.mineinabyss.mobzy.ecs.components.initialization.Model
import org.bukkit.entity.Entity
import kotlin.reflect.KProperty

class Condition

class ConditionBuilder {
    fun <T : GearyComponent> from(component: T.() -> Unit) {

    }

    infix fun <T> KProperty<T>.setTo(value: T) {

    }

    operator fun <T : Comparable<*>> KProperty<T>.compareTo(value: T): Int {
        TODO()
    }
}

class PostConditionBuilder {
    fun <T : GearyComponent> own(component: T.() -> Unit) {

    }

    infix fun <T> KProperty<T>.becomes(value: T) {

    }

    infix fun <T : Comparable<*>> KProperty<T>.approaches(value: T) {
        TODO()
    }

    infix fun <T : Comparable<*>> KProperty<T>.increasesTo(value: T) {
        TODO()
    }

    infix fun <T : Comparable<*>> KProperty<T>.decreasesTo(value: T) {
        TODO()
    }

}

fun conditions(init: ConditionBuilder.() -> Unit) {

}

fun postconditions(init: PostConditionBuilder.() -> Unit) {

}

@Suppress("UNREACHABLE_CODE")
fun createConditionTest() {
    conditions {
        Model::small setTo true
        Model::id < 0
        Model::id setTo 0
        from<Model> {
            id == 0
        }

    }
    postconditions {
        own<MobComponent> {
            val target: Entity = TODO()
            entity.distanceSqrTo(target)
        }
        Model::small becomes true

    }
}
