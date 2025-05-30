package com.ssafy.vibe.prompt.service.command;

import com.ssafy.vibe.prompt.service.dto.PromptAttachDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class SnapshotCommand {
	private Long snapshotId;
	private String description;

	public PromptAttachDTO toDTO(
		Long promptId,
		Long snapshotId) {
		return PromptAttachDTO.builder()
			.promptId(promptId)
			.snapshotId(snapshotId)
			.description(this.getDescription())
			.build();
	}
}
