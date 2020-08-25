package com.mineinabyss.geary.ecs

import kotlin.time.ExperimentalTime
import kotlin.time.measureTime


//@ExperimentalTime
//fun main() {
//    test("Add") {
//        Engine.addSystem(TemptSystem)
//        for (i in 1..100000 step 10) {
//            Engine.addComponent(i, TemptBehavior(listOf()))
//        }
//    }
//    test("Iterate") {
//        repeat(10000000) {
//            Engine.tick()
//        }
//    }
//}

@ExperimentalTime
inline fun test(name: String, block: () -> Unit) = println("$name took: ${measureTime { block() }.inMilliseconds}ms")