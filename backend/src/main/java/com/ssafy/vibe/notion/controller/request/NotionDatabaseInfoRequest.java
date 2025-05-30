package com.ssafy.vibe.notion.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotionDatabaseInfoRequest {
	@NotBlank
	private String notionDatabaseName;

	@NotBlank
	private String notionDatabaseUid;
}
