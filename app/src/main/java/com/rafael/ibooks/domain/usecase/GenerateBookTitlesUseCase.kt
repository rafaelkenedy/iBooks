package com.rafael.ibooks.domain.usecase

import com.rafael.ibooks.data.remote.model.GeminiGenerateContentRequest
import com.rafael.ibooks.data.remote.model.GeminiGenerateContentRequest.Content
import com.rafael.ibooks.data.remote.model.GeminiGenerateContentRequest.Content.Part
import com.rafael.ibooks.domain.repository.IGeminiRepository

class GenerateBookTitlesUseCase(
    private val repository: IGeminiRepository
) {
    private companion object {
        val FIXED_PROMPT = """
        Por favor, sugira 20 títulos de **livros reais e publicados** sobre o tema “tecnologia e sociedade moderna”.
        Garanta que cada título corresponda a uma obra existente, disponível em livrarias ou bibliotecas, e amplamente reconhecida.
        Retorne **apenas** um objeto JSON com uma única chave "suggestions", cujo valor seja um array de 20 strings.
        Não inclua explicações, comentários ou quaisquer outras chaves.

        Formato esperado:
        {
          "suggestions": [
            "Título de Exemplo 1",
            "Título de Exemplo 2",
            …,
            "Título de Exemplo 20"
          ]
        }
    """.trimIndent()
    }

    suspend operator fun invoke(): List<String> {
        val request = GeminiGenerateContentRequest(
            contents = listOf(
                Content(
                    parts = listOf(
                        Part(text = FIXED_PROMPT)
                    )
                )
            )
        )
        return repository.generateBookTitles(request)
    }
}
