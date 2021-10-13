package com.mineinabyss.mobzy.spawning

import org.nield.kotlinstatistics.OpenDoubleRange
import org.nield.kotlinstatistics.WeightedDice
import java.util.concurrent.ThreadLocalRandom

/**
 * @see WeightedDice
 */
class MutableWeightedList<T>(val probabilities: Map<T,Double>) {

    private val sum = probabilities.values.sum()

    private val rangedDistribution = probabilities.let { prob ->
        var binStart = 0.0

        prob.asSequence().sortedBy { it.value }
            .map { it.key to OpenDoubleRange(binStart, it.value + binStart) }
            .onEach { binStart = it.second.endExclusive }
            .toMap()
    }

    /**
     * Randomly selects a `T` value with probability
     */
    fun roll() = ThreadLocalRandom.current().nextDouble(0.0, sum).let {
        rangedDistribution.asIterable().first { rng -> it in rng.value }.key
    }
}
