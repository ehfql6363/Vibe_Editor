package com.ssafy.vibe.prompt.controller.request;

import com.ssafy.vibe.prompt.service.command.GeneratePostCommand;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PostGenerateRequest {
	@NotNull(message = "포스트를 생성할 프롬프트 id를 입력해주세요.")
	private Long promptId;

	public GeneratePostCommand toCommand() {
		return GeneratePostCommand.builder()
			.promptId(this.promptId)
			.build();
	}
}
