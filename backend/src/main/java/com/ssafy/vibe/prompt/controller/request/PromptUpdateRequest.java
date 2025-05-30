package com.ssafy.vibe.prompt.controller.request;

import java.util.List;
import java.util.stream.Collectors;

import com.ssafy.vibe.prompt.service.command.PromptUpdateCommand;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PromptUpdateRequest {
	@Size(max = 255, message = "{max.length}")
	private String promptName;

	private String postType;

	@Size(max = 3000, message = "{max.length}")
	private String comment; // 사용자 코멘트는 필수 아닐 수 있음

	private List<PromptAttachUpdateRequest> promptAttachList;

	private List<Long> promptOptionList; // 예: "이모지 O, ~습니다 체"

	private Long notionDatabaseId;

	private Long userAIProviderId;

	public PromptUpdateCommand toCommand() {
		return PromptUpdateCommand.builder()
			.promptName(this.promptName)
			.postType(this.postType)
			.comment(this.comment)
			.promptAttachList(this.promptAttachList.stream()
				.map(PromptAttachUpdateRequest::toCommand)
				.collect(Collectors.toList()))
			.promptOptionList(this.promptOptionList)
			.notionDatabaseId(this.notionDatabaseId)
			.userAIProviderId(this.userAIProviderId)
			.build();
	}
}
