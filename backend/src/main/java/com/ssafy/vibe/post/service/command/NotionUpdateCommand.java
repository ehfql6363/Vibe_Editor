package com.ssafy.vibe.post.service.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotionUpdateCommand {
	private Long userId;
	private Long postId;
	private String postTitle;
	private String postContent;
}
