package com.ssafy.vibe.notion.service.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotionRegisterDatabaseCommand {
	private Long userId;
	private String notionDatabaseName;
	private String notionDatabaseUid;
}
