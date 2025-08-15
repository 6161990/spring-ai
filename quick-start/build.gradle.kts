description = "quick-start"

dependencies {
    implementation("org.springframework.ai:spring-ai-starter-model-ollama")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
    }
}
