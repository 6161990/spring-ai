package com.yoon.quickstart.controller

import org.springframework.ai.chat.client.ChatClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AiController(
  private val chatClient: ChatClient
) {

  @GetMapping("/ask")
  fun ask(@RequestParam q: String): Map<String, Any?> {
    val answer = chatClient
      .prompt()
      .system("You are a concise assistant.")
      .user(q)
      .call()
      .content()
    return mapOf("question" to q, "answer" to answer)
  }
}

