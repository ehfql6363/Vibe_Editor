package com.ssafy.vibe.post.controller.request;

import com.ssafy.vibe.post.service.command.NotionUpdateCommand;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotionUpdateRequest {
	@NotBlank(message = "제목은 비어 있을 수 없습니다.")
	private String postTitle;

	@NotBlank(message = "내용은 비어 있을 수 없습니다.")
	private String postContent;

	public NotionUpdateCommand toCommand(
		Long userId,
		Long postId,
		NotionUpdateRequest request) {
		return NotionUpdateCommand.
			builder()
			.userId(userId)
			.postId(postId)
			.postTitle(request.getPostTitle())
			.postContent(request.getPostContent())
			.build();
	}
}
