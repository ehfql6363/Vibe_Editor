package com.ssafy.vibe.post.controller.request;

import com.ssafy.vibe.post.service.command.NotionPostCommand;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotionPostRequest {
	private Long postId;

	public NotionPostCommand toCommand() {
		return NotionPostCommand.builder()
			.postId(this.postId)
			.build();
	}
}