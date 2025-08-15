package com.yoon.quickstart.tool

import org.springframework.ai.tool.annotation.Tool
import org.springframework.stereotype.Component

@Component
class WeatherTools {

  @Tool(description = "현재 온도를 섭씨로 조회")
  fun currentTempC(city: String): Double = when (city.lowercase()) {
    "seoul" -> 29.0
    else -> 25.0
  }
}
