package com.ssafy.vibe.prompt.service.dto;

import com.ssafy.vibe.prompt.domain.PromptAttachEntity;
import com.ssafy.vibe.prompt.domain.PromptEntity;
import com.ssafy.vibe.snapshot.domain.SnapshotEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PromptAttachDTO {
	private Long promptId;
	private Long snapshotId;
	private String description;

	public PromptAttachEntity toEntity(PromptEntity prompt, SnapshotEntity snapshot) {
		return PromptAttachEntity.builder()
			.prompt(prompt)
			.snapshot(snapshot)
			.description(this.description)
			.build();
	}
}
