package com.ssafy.vibe.prompt.controller.request;

import java.util.List;
import java.util.stream.Collectors;

import com.ssafy.vibe.prompt.service.command.PromptSaveCommand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PromptSaveRequest {
	private Long parentPromptId;

	@NotNull(message = "템플릿 번호를 입력해주세요.")
	private Long templateId;

	@Size(max = 255, message = "{max.length}")
	private String promptName;

	@NotBlank(message = "작성할 포스트 유형을 선택해주세요.")
	private String postType;

	@Size(max = 3000, message = "{max.length}")
	private String comment; // 사용자 코멘트는 필수 아닐 수 있음

	private List<PromptAttachSaveRequest> promptAttachList;

	private List<Long> promptOptionList; // 예: "이모지 O, ~습니다 체"

	private Long notionDatabaseId;

	private Long userAIProviderId;

	public PromptSaveCommand toCommand() {
		return PromptSaveCommand.builder()
			.parentPromptId(this.parentPromptId)
			.templateId(this.templateId)
			.promptName(this.promptName)
			.postType(this.postType)
			.comment(this.comment)
			.promptAttachList(this.promptAttachList.stream()
				.map(PromptAttachSaveRequest::toCommand)
				.collect(Collectors.toList()))
			.promptOptionList(this.promptOptionList)
			.notionDatabaseId(this.notionDatabaseId)
			.userAIProviderId(this.userAIProviderId)
			.build();
	}
}