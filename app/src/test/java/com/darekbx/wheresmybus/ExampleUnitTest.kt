package com.darekbx.wheresmybus

import org.junit.Test

import org.junit.Assert.*
import kotlin.system.measureNanoTime
import kotlin.time.measureTime

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Test
    fun addition_isCorrect1() {
        val test = Test("aaa")
        println(measureTime {
            var a = 0
            (0..100000).forEach {
                val name = test::class
                a += test.a.length
            }
        })
    }

    @Test
    fun addition_isCorrect2() {
        val test = Test("aaa")
        println(measureTime {
            var a = 0
            (0..100000).forEach {
                val name = test.javaClass.name
                a += name.length
            }
        })
    }

    @org.junit.Test
    fun addition_isCorrect3() {
        val test = Test("aaa")
        println(measureTime {
            var a = 0
            (0..100000).forEach {
                val name = test.a
                a += name.length
            }
        })
    }
}

data class Test(val a: String)