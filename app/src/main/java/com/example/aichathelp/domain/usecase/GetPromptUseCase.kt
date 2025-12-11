package com.example.aichathelp.domain.usecase

import com.example.aichathelp.domain.model.PromptType
import com.example.aichathelp.domain.repository.PromptRepository
import javax.inject.Inject

class GetPromptUseCase @Inject constructor(
    private val promptRepository: PromptRepository,
) {
    operator fun invoke(type: PromptType): String = when (type) {
        PromptType.PROFESSIONAL -> promptRepository.getFsmPrompt()
        PromptType.CREATIVE -> promptRepository.getCreativePrompt()
    }
}
