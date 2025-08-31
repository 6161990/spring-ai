package com.yoon.quickstart.config

import io.qdrant.client.QdrantClient
import io.qdrant.client.grpc.Collections
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile

@Configuration
class IngestConfig {

  private val log = LoggerFactory.getLogger(IngestConfig::class.java)
  private val collectionName = "ai_docs"
  private val dimension = 768L

  @Bean
  @Order(0)
  fun ensureQdrantCollection(qdrantClient: QdrantClient) = ApplicationRunner {
    val exists = runCatching {
      qdrantClient.getCollectionInfoAsync(collectionName).get()
    }.isSuccess

    if (!exists) {
      val params = Collections.VectorParams.newBuilder()
        .setSize(dimension)
        .setDistance(Collections.Distance.Cosine)
        .build()
      val vectorsConfig = Collections.VectorsConfig.newBuilder().setParams(params).build()

      qdrantClient.createCollectionAsync(
        Collections.CreateCollection.newBuilder()
          .setCollectionName(collectionName)
          .setVectorsConfig(vectorsConfig)
          .build()
      ).get()
      log.info("✅ Qdrant collection created: $collectionName")
    } else {
      log.info("✅ Qdrant collection exists: $collectionName")
    }
  }

  @Bean
  @Order(1)
  fun bootstrapIngest(vectorStore: VectorStore) = CommandLineRunner {
    val sourceDir = Path.of("data")
    if (!Files.exists(sourceDir)) return@CommandLineRunner

    val docs = Files.walk(sourceDir)
      .filter { it.isRegularFile() && (it.extension.lowercase() in setOf("pdf")) }
      .flatMap { path -> extract(path).stream() }
      .toList()

    val chunks = TokenTextSplitter().apply(docs)
    if (chunks.isNotEmpty()) {
      vectorStore.add(chunks)
      log.info("✅ Ingested: ${docs.size} files, ${chunks.size} chunks")
    } else {
      log.info("ℹ️ No chunks produced.")
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

