package com.yoon.quickstart.config

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.ai.document.Document
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile

@Configuration
class IngestConfig {

  @Bean
  @ConditionalOnProperty(name = ["app.ingest.enabled"], havingValue = "true", matchIfMissing = false)
  fun bootstrapIngest(vectorStore: VectorStore) = CommandLineRunner {
    val sourceDir = Path.of("quick-start/data")
    if (!Files.exists(sourceDir)) return@CommandLineRunner

    val docs = Files.walk(sourceDir)
      .filter { it.isRegularFile() && (it.extension.lowercase() in setOf("pdf")) }
      .flatMap { path -> extract(path).stream() }
      .toList()

    val chunks = TokenTextSplitter().apply(docs)
    if (chunks.isNotEmpty()) {
      vectorStore.add(chunks)
      println("✅ Ingested: ${docs.size} files, ${chunks.size} chunks")
    } else {
      println("ℹ️ No chunks produced.")
    }
  }

  private fun extract(path: Path): List<Document> =
    when (path.extension.lowercase()) {
      "pdf" -> listOf(
        Document(readPdf(path), mapOf("source" to path.toString(), "type" to "pdf"))
      )
      else -> emptyList()
    }

  private fun readPdf(path: Path): String =
    PDDocument.load(path.toFile()).use { doc ->
      PDFTextStripper().getText(doc)
    }

}

