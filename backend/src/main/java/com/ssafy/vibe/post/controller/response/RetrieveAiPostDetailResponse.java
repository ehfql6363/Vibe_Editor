package com.ssafy.vibe.post.controller.response;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveAiPostDetailResponse {
	private Long postId;
	private String postTitle;
	private String postContent;
	private String postUrl;
	private Long templateId;
	private Long promptId;
	private String usedAIBrand;
	private String usedAIModel;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;
}
