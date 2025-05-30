package com.ssafy.vibe.notion.service.dto;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.ssafy.vibe.notion.controller.response.RetrieveNotionDatabasesResponse;
import com.ssafy.vibe.notion.domain.NotionDatabaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveNotionDatabasesDTO {
	private Long notionDatabaseId;
	private String notionDatabaseName;
	private String notionDatabaseUid;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;

	public static RetrieveNotionDatabasesDTO fromEntity(NotionDatabaseEntity entity) {
		return new RetrieveNotionDatabasesDTO(
			entity.getId(),
			entity.getDatabaseName(),
			entity.getDatabaseUid(),
			entity.getCreatedAt().atZone(ZoneId.of("UTC")),
			entity.getUpdatedAt().atZone(ZoneId.of("UTC"))
		);
	}

	public RetrieveNotionDatabasesResponse toResponse() {
		return new RetrieveNotionDatabasesResponse(
			this.notionDatabaseId,
			this.notionDatabaseName,
			this.notionDatabaseUid,
			this.createdAt,
			this.updatedAt
		);
	}
}
