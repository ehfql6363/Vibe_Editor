package com.ssafy.vibe.prompt.service.command;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class PromptSaveCommand {
	private Long parentPromptId;
	private Long templateId;
	private String promptName;
	private String postType;
	private String comment;
	private List<SnapshotCommand> promptAttachList;
	private List<Long> promptOptionList;
	private Long notionDatabaseId;
	private Long userAIProviderId;
}
