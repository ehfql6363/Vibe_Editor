package com.ssafy.vibe.post.service.dto;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import com.ssafy.vibe.notion.domain.NotionUploadEntity;
import com.ssafy.vibe.post.controller.response.RetrieveAiPostResponse;
import com.ssafy.vibe.post.domain.PostEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRetrieveDTO {
	private Long postId;
	private String postTitle;
	private String uploadStatus;
	private ZonedDateTime createdAt;
	private ZonedDateTime updatedAt;

	public static PostRetrieveDTO fromEntity(PostEntity postEntity, NotionUploadEntity notionUploadEntity) {
		return new PostRetrieveDTO(
			postEntity.getId(),
			postEntity.getPostTitle(),
			notionUploadEntity == null ? "PENDING" : notionUploadEntity.getUploadStatus().toString(),
			postEntity.getCreatedAt().atZone(ZoneId.of("UTC")),
			postEntity.getUpdatedAt().atZone(ZoneId.of("UTC"))
		);
	}

	public RetrieveAiPostResponse toResponse() {
		return new RetrieveAiPostResponse(
			postId,
			postTitle,
			uploadStatus,
			createdAt,
			updatedAt
		);
	}
}
