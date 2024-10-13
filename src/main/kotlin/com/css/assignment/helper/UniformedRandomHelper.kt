package com.css.assignment.helper

import kotlin.random.Random

class UniformedRandomHelper {
    companion object {
        fun generateDispatchTime(): Long {
            return (3L..15L).random()
        }
    }
}