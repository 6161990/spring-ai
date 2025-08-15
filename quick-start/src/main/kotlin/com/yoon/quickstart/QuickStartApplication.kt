package com.yoon.quickstart

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QuickStartApplication

fun main(args: Array<String>) {
  runApplication<QuickStartApplication>(*args)
}
