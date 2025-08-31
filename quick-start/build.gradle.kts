plugins {
    id("org.springframework.boot")
    kotlin("jvm")
    kotlin("plugin.spring")
}

java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

repositories {
    mavenCentral()
}

dependencies {
    // BOM
    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.5.4"))
    implementation(platform("org.springframework.ai:spring-ai-bom:1.0.1"))

    // 기본
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Ollama 스타터 (정식 1.0.x)
    implementation("org.springframework.ai:spring-ai-starter-model-ollama")

    // 벡터 스토어: PGVector
//    implementation("org.springframework.ai:spring-ai-starter-vector-store-pgvector")  // 1.0.x 정식 명칭 :contentReference[oaicite:0]{index=0}

    // 벡터 스토어: Qdrant
    implementation("org.springframework.ai:spring-ai-starter-vector-store-qdrant")

    // RAG용 Advisor (QuestionAnswerAdvisor 등)
    implementation("org.springframework.ai:spring-ai-advisors-vector-store")

    // PDF 텍스트 추출
    implementation("org.apache.pdfbox:pdfbox:2.0.30")

    // Markdown 파서(텍스트 추출용)
    implementation("com.vladsch.flexmark:flexmark-all:0.64.8")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(kotlin("test"))
}

tasks.test { useJUnitPlatform() }
