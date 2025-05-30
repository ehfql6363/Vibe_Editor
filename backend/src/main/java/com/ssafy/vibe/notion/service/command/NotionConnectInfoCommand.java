package com.ssafy.vibe.notion.service.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotionConnectInfoCommand {
	private Long userId;
	private String notionSecretKey;
}
