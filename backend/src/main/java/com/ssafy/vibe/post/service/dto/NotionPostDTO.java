package com.ssafy.vibe.post.service.dto;

import com.ssafy.vibe.post.controller.response.NotionPostResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotionPostDTO {
	private String postUrl;

	public NotionPostResponse toResponse() {
		return NotionPostResponse.builder()
			.postUrl(this.postUrl)
			.build();
	}
}
