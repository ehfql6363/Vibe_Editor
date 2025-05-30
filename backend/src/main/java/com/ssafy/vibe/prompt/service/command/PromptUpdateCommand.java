package com.ssafy.vibe.prompt.service.command;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class PromptUpdateCommand {
	private String promptName;
	private String postType;
	private String comment;
	private List<PromptAttachUpdateCommand> promptAttachList;
	private List<Long> promptOptionList;
	private Long notionDatabaseId;
	private Long userAIProviderId;
}
