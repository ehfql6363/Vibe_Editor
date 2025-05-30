package com.ssafy.vibe.post.domain;

import com.ssafy.vibe.common.domain.BaseEntity;
import com.ssafy.vibe.prompt.domain.PromptEntity;
import com.ssafy.vibe.user.domain.UserAiProviderEntity;
import com.ssafy.vibe.user.domain.UserEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "ai_post")
public class PostEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "post_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_post_id")
	private PostEntity parentPost;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prompt_id", nullable = false)
	private PromptEntity prompt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@Column(name = "title", nullable = false)
	private String postTitle;

	@Column(name = "post_type")
	@Enumerated(EnumType.STRING)
	private PostType postType;

	@Column(name = "document_id")
	private String documentId;

	@Column(name = "content")
	private String postContent;

	@Column(name = "is_modified")
	private boolean isModified = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_ai_provider_id", nullable = false)
	private UserAiProviderEntity userAiProvider;

	public void updateTitleAndContent(String newTitle, String newContent) {
		if (newTitle == null || newTitle.trim().isEmpty()) {
			throw new IllegalArgumentException("제목은 비어 있을 수 없습니다.");
		}
		if (newContent == null || newContent.trim().isEmpty()) {
			throw new IllegalArgumentException("내용은 비어 있을 수 없습니다.");
		}

		this.postTitle = newTitle.trim();
		this.postContent = newContent.trim();
		this.isModified = true; // 수정된 게시글임을 명시
	}

	public void delete() {
		super.setIsDeleted(true);
	}
}
