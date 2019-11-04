package ru.stasal.fix

import io.allune.quickfixj.spring.boot.starter.EnableQuickFixJServer
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@EnableQuickFixJServer
@SpringBootApplication
class SimpleFixServerApplication

fun main(args: Array<String>) {
    runApplication<SimpleFixServerApplication>(*args)
}