package com.ssafy.vibe.post.service.dto;

import com.ssafy.vibe.post.domain.PostEntity;
import com.ssafy.vibe.post.domain.PostType;
import com.ssafy.vibe.prompt.domain.PromptEntity;
import com.ssafy.vibe.user.domain.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class PostSaveDTO {
	private PostEntity parentPost;
	private PromptEntity prompt;
	private UserEntity user;
	private String postTitle;
	private PostType postType;
	private String documentId;
	private String postContent;
	private boolean isModified;

	public static PostSaveDTO from(
		PostEntity parentPost,
		PromptEntity prompt,
		String postTitle,
		String postContent
	) {
		return PostSaveDTO.builder()
			.parentPost(parentPost)
			.prompt(prompt)
			.user(prompt.getUser())
			.postTitle(postTitle)
			.postType(prompt.getPostType())
			.documentId(null)
			.postContent(postContent)
			.build();
	}

	public PostEntity toEntity() {
		return PostEntity.builder()
			.parentPost(this.parentPost)
			.prompt(this.prompt)
			.user(this.user)
			.postTitle(this.postTitle)
			.postType(this.postType)
			.documentId(this.documentId)
			.postContent(this.postContent)
			.userAiProvider(this.prompt.getUserAiProvider())
			.build();
	}
}
