package com.test.kotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * @author zhangbin by 28/04/2018 12:42
 * @since JDK1.8
 */
@SpringBootApplication
class TestKotlinApplication

fun main(args: Array<String>) {
    runApplication<TestKotlinApplication>(*args)
}