package com.yoon.quickstart.controller

import com.yoon.quickstart.tool.WeatherTools
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AiController(
  private val chatClient: ChatClient,
  private val vectorStore: VectorStore
) {

  @GetMapping("/ask")
  fun ask(@RequestParam q: String, @RequestParam topK: Int): Map<String, Any?> {
    val answer = chatClient
      .prompt()
      .system("You are a concise assistant.")
      .user(q)
      .advisors(answerAdvisor(topK))
      .call()
      .content()
    return mapOf("question" to q, "answer" to answer)
  }

  data class Summary(val title: String, val bullets: List<String>)


  private fun answerAdvisor(topK: Int): QuestionAnswerAdvisor = QuestionAnswerAdvisor.builder(vectorStore)
    .searchRequest(
      SearchRequest
        .builder()
        .topK(topK) // Recell
        .similarityThreshold(0.5) // precisions
        .build()
    ).build()

  @GetMapping("/summarize")
  fun summarize(@RequestParam text: String): Summary? {
    return chatClient
      .prompt()
      .system(
        """
            You return strictly valid JSON for the Kotlin data class:
            data class Summary(val title: String, val bullets: List<String>)
            """.trimIndent()
      )
      .user("Summarize this into a title and 3 bullets with bullets * : $text")
      .call()
      .entity(Summary::class.java)
  }

  @GetMapping("/tool-ask")
  fun toolAsk(@RequestParam city: String): String? {
    return chatClient
      .prompt("How is weather at city")
      .tools(WeatherTools()) // 등록된 @Tool을 모델에 노출 근데 내가 쓰는 모델이 지원안해줌 유유
      .call()
      .content()
  }
}

