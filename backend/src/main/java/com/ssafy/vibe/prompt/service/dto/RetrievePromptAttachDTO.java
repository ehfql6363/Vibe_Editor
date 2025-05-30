package com.ssafy.vibe.prompt.service.dto;

import com.ssafy.vibe.prompt.domain.PromptAttachEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RetrievePromptAttachDTO {
	private Long attachId;
	private Long snapshotId;
	private String description;

	public static RetrievePromptAttachDTO from(PromptAttachEntity promptAttachEntity) {
		return RetrievePromptAttachDTO.builder()
			.attachId(promptAttachEntity.getId())
			.snapshotId(promptAttachEntity.getSnapshot().getId())
			.description(promptAttachEntity.getDescription())
			.build();
	}
}
