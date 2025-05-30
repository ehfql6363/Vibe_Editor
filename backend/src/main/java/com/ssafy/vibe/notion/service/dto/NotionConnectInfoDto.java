package com.ssafy.vibe.notion.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NotionConnectInfoDto {
	private Long userId;
	private String notionSecretKey;
}
