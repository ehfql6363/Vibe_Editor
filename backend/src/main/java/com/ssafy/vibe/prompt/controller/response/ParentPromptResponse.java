package com.ssafy.vibe.prompt.controller.response;

import com.ssafy.vibe.prompt.service.dto.RetrievePromptDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ParentPromptResponse {
	private Long parentPromptId;
	private String parentPromptName;

	public static ParentPromptResponse from(RetrievePromptDTO retrievePromptDTO) {
		return ParentPromptResponse.builder()
			.parentPromptId(
				retrievePromptDTO.getParentPromptId() != null ?
					retrievePromptDTO.getParentPromptId() : null)
			.parentPromptName(
				retrievePromptDTO.getPromptName() != null ?
					retrievePromptDTO.getPromptName() : null)
			.build();
	}
}
