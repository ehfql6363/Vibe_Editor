package com.ssafy.vibe.prompt.service.dto;

import com.ssafy.vibe.prompt.domain.PromptEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class RetrievePromptDTO {
	private Long parentPromptId;
	private Long templateId;
	private Long userId;
	private String promptName;
	private String postType;
	private String comment;
	private Long notionDatabaseId;
	private Long userAIProviderId;

	public static RetrievePromptDTO fromEntity(PromptEntity promptEntity) {
		return RetrievePromptDTO.builder()
			.parentPromptId(
				promptEntity.getParentPrompt() != null ?
					promptEntity.getParentPrompt().getId() : null)
			.templateId(promptEntity.getTemplate().getId())
			.userId(promptEntity.getUser().getId())
			.promptName(promptEntity.getPromptName())
			.postType(promptEntity.getPostType().toString())
			.comment(promptEntity.getComment())
			.notionDatabaseId(promptEntity.getNotionDatabase().getId())
			.userAIProviderId( // AI 모델 선택 전 생성한 프롬프트는 null 처리 필요
				promptEntity.getUserAiProvider() != null ?
					promptEntity.getUserAiProvider().getId() : null)
			.build();
	}
}
