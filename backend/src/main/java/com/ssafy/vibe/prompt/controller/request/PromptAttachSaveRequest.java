package com.ssafy.vibe.prompt.controller.request;

import com.ssafy.vibe.prompt.service.command.SnapshotCommand;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PromptAttachSaveRequest {
	@NotNull(message = "스냅샷 번호를 입력해주세요.")
	private Long snapshotId;

	@Size(max = 1000, message = "{max.length}")
	private String description;

	public SnapshotCommand toCommand() {
		return SnapshotCommand.builder()
			.snapshotId(this.snapshotId)
			.description(this.description)
			.build();
	}
}
