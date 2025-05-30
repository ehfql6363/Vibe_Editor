package com.ssafy.vibe.notion.controller.response;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotionDatabaseResponse {
	private String notionDatabaseId;
	private String notionDatabaseName;
	private String notionDatabaseUid;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;
}
