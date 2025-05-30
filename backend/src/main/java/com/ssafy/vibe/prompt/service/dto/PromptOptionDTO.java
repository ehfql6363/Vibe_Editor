package com.ssafy.vibe.prompt.service.dto;

import com.ssafy.vibe.prompt.domain.OptionEntity;
import com.ssafy.vibe.prompt.domain.PromptEntity;
import com.ssafy.vibe.prompt.domain.PromptOptionEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PromptOptionDTO {
	private Long promptId;
	private Long optionId;

	public static PromptOptionDTO from(Long promptId, Long optionId) {
		return PromptOptionDTO.builder()
			.promptId(promptId)
			.optionId(optionId)
			.build();
	}

	public static PromptOptionEntity toEntity(
		PromptEntity prompt,
		OptionEntity opption) {
		return PromptOptionEntity.builder()
			.prompt(prompt)
			.option(opption)
			.build();
	}
}
