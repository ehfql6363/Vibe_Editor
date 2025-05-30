package com.ssafy.vibe.prompt.service.dto;

public record AiChatInputDTO(
	String model,
	Boolean isDefault,
	Double temperature,
	String apiKey,
	String systemPrompt,
	String userPrompt
) {
}
