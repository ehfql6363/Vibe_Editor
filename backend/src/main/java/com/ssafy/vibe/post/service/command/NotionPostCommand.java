package com.ssafy.vibe.post.service.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotionPostCommand {
	private Long userId;
	private Long postId;
}
