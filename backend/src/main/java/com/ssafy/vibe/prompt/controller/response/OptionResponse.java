package com.ssafy.vibe.prompt.controller.response;

import java.util.List;

import com.ssafy.vibe.prompt.service.dto.OptionItemDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OptionResponse {
	private String optionName;
	private List<OptionItemDTO> optionItems;

	public static OptionResponse from(String optionName, List<OptionItemDTO> optionItems) {
		return OptionResponse.builder()
			.optionName(optionName)
			.optionItems(optionItems)
			.build();
	}
}
