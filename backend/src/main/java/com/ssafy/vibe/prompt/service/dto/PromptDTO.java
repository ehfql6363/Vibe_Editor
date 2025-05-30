package com.ssafy.vibe.prompt.service.dto;

import java.time.Instant;

import com.ssafy.vibe.prompt.domain.PromptEntity;

import lombok.Builder;

@Builder
public record PromptDTO(
	Long promptId,
	String promptName,
	Instant createdAt,
	Instant updatedAt
) {
	public static PromptDTO from(PromptEntity prompt) {
		return PromptDTO
			.builder()
			.promptId(prompt.getId())
			.promptName(prompt.getPromptName())
			.createdAt(prompt.getCreatedAt())
			.updatedAt(prompt.getUpdatedAt())
			.build();
	}
}
