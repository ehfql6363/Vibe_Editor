package com.ssafy.vibe.prompt.service.dto;

import com.ssafy.vibe.notion.domain.NotionDatabaseEntity;
import com.ssafy.vibe.post.domain.PostType;
import com.ssafy.vibe.prompt.domain.PromptEntity;
import com.ssafy.vibe.prompt.service.command.PromptSaveCommand;
import com.ssafy.vibe.template.domain.TemplateEntity;
import com.ssafy.vibe.user.domain.UserAiProviderEntity;
import com.ssafy.vibe.user.domain.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromptSaveDTO {
	private Long parentPromptId;
	private Long templateId;
	private Long userId;
	private String promptName;
	private String postType;
	private String comment;
	private Long notionDatabaseId;
	private Long userAIProviderId;

	public PromptSaveDTO(PromptSaveCommand command, UserEntity user) {
		this.parentPromptId = command.getParentPromptId();
		this.templateId = command.getTemplateId();
		this.userId = user.getId();
		this.promptName = command.getPromptName();
		this.postType = command.getPostType();
		this.comment = command.getComment();
		this.notionDatabaseId = command.getNotionDatabaseId();
		this.userAIProviderId = command.getUserAIProviderId();
	}

	public PromptEntity toEntity(
		PromptEntity prompt,
		TemplateEntity template,
		UserEntity user,
		NotionDatabaseEntity notionDatabase,
		UserAiProviderEntity userAiProvider) {
		return PromptEntity.builder()
			.parentPrompt(prompt)
			.template(template)
			.user(user)
			.promptName(this.promptName)
			.postType(PostType.valueOf(this.postType))
			.comment(this.comment)
			.notionDatabase(notionDatabase)
			.userAiProvider(userAiProvider)
			.build();
	}

}
