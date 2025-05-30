package com.ssafy.vibe.notion.domain;

import com.ssafy.vibe.common.domain.BaseEntity;
import com.ssafy.vibe.post.domain.PostEntity;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notion_upload")
public class NotionUploadEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "upload_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "notion_id", nullable = false)
	private NotionDatabaseEntity notionDatabase;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private PostEntity post;

	@Column(name = "post_url")
	private String postUrl;

	@Column(name = "upload_status")
	@Enumerated(EnumType.STRING)
	private UploadStatus uploadStatus;

	@Builder
	private NotionUploadEntity(
		UserEntity user, NotionDatabaseEntity notionDatabase,
		PostEntity post, String postUrl, UploadStatus uploadStatus
	) {
		this.user = user;
		this.notionDatabase = notionDatabase;
		this.post = post;
		this.postUrl = postUrl;
		this.uploadStatus = uploadStatus;
	}

	public static NotionUploadEntity createNotionUpload(
		PostEntity post, String postUrl, UploadStatus uploadStatus
	) {
		return NotionUploadEntity.builder()
			.user(post.getUser())
			.notionDatabase(post.getPrompt().getNotionDatabase())
			.post(post)
			.postUrl(postUrl)
			.uploadStatus(uploadStatus)
			.build();
	}
}
