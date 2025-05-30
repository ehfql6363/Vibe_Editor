package com.ssafy.vibe.notion.controller.response;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveNotionDatabasesResponse {
	private Long notionDatabaseId;
	private String notionDatabaseName;
	private String notionDatabaseUid;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;
}
