package com.ssafy.vibe.prompt.controller.response;

import com.ssafy.vibe.prompt.service.dto.RetrievePromptAttachDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PromptAttachRespnose {
	private Long attachId;
	private Long snapshotId;
	private String description;

	public static PromptAttachRespnose from(RetrievePromptAttachDTO promptAttachDTO) {
		return PromptAttachRespnose.builder()
			.attachId(promptAttachDTO.getAttachId())
			.snapshotId(promptAttachDTO.getSnapshotId())
			.description(promptAttachDTO.getDescription())
			.build();
	}
}
