package com.ssafy.vibe.post.service.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRetrieveDetailCommand {
	private Long userId;
	private Long postId;
}
