package com.ssafy.vibe.prompt.service.dto;

import com.ssafy.vibe.prompt.domain.OptionEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OptionItemDTO {
	private Long optionId;
	private String value;

	public static OptionItemDTO from(OptionEntity option) {
		return OptionItemDTO.builder()
			.optionId(option.getId())
			.value(option.getValue())
			.build();
	}
}
