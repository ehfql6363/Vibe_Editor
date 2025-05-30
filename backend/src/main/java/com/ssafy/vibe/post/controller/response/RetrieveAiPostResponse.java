package com.ssafy.vibe.post.controller.response;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveAiPostResponse {
	private Long postId;
	private String postTitle;
	private String uploadStatus;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;
}
