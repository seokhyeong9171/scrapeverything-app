package com.scrapeverything.app.data.model.groq

data class GroqChatRequest(
    val model: String = "llama-3.1-8b-instant",
    val messages: List<GroqMessage>,
    val temperature: Double = 0.3,
    val max_tokens: Int = 300
)

data class GroqMessage(
    val role: String,
    val content: String
)

data class GroqChatResponse(
    val choices: List<GroqChoice>
)

data class GroqChoice(
    val message: GroqMessage
)
