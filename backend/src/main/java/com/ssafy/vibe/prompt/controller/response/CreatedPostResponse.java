package com.ssafy.vibe.prompt.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreatedPostResponse {
	private Long postId;
	private String postTitle;
	private String postContent;

	public static CreatedPostResponse from(Long postId, String postTitle, String postContent) {
		return CreatedPostResponse.builder()
			.postId(postId)
			.postTitle(postTitle)
			.postContent(postContent)
			.build();
	}
}
