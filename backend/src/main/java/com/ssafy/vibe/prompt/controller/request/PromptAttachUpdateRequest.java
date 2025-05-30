package com.ssafy.vibe.prompt.controller.request;

import com.ssafy.vibe.prompt.service.command.PromptAttachUpdateCommand;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PromptAttachUpdateRequest {
	private Long attachId;

	@NotNull(message = "스냅샷 번호를 입력해주세요.")
	private Long snapshotId;

	@Size(max = 1000, message = "{max.length}")
	private String description;

	public PromptAttachUpdateCommand toCommand() {
		return PromptAttachUpdateCommand.builder()
			.attachId(this.attachId)
			.snapshotId(this.snapshotId)
			.description(description)
			.build();
	}
}
