package com.ssafy.vibe.post.service.dto;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.ssafy.vibe.notion.domain.NotionUploadEntity;
import com.ssafy.vibe.post.controller.response.RetrieveAiPostDetailResponse;
import com.ssafy.vibe.post.domain.PostEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRetrieveDetailDTO {
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

	public static PostRetrieveDetailDTO fromEntity(PostEntity postEntity, NotionUploadEntity notionUploadEntity) {
		// Null Safe 처리
		String aiBrand = null;
		String aiModel = null;
		if (postEntity.getUserAiProvider() != null &&
			postEntity.getUserAiProvider().getAiProvider() != null
		) {
			aiBrand = postEntity.getUserAiProvider().getAiProvider().getBrand() != null
				? postEntity.getUserAiProvider().getAiProvider().getBrand().toString()
				: null;
			aiModel = postEntity.getUserAiProvider().getAiProvider().getModel();
		}

		return new PostRetrieveDetailDTO(
			postEntity.getId(),
			postEntity.getPostTitle(),
			postEntity.getPostContent(),
			notionUploadEntity != null ? notionUploadEntity.getPostUrl() : null,
			postEntity.getPrompt().getTemplate().getId(),
			postEntity.getPrompt().getId(),
			aiBrand,
			aiModel,
			postEntity.getCreatedAt().atZone(ZoneId.of("UTC")),
			postEntity.getUpdatedAt().atZone(ZoneId.of("UTC"))
		);
	}

	public RetrieveAiPostDetailResponse toResponse() {
		return new RetrieveAiPostDetailResponse(
			postId,
			postTitle,
			postContent,
			postUrl,
			templateId,
			promptId,
			usedAIBrand,
			usedAIModel,
			createdAt,
			updatedAt);
	}
}
