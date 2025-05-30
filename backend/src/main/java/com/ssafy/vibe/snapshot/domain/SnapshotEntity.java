package com.ssafy.vibe.snapshot.domain;

import com.ssafy.vibe.common.domain.BaseEntity;
import com.ssafy.vibe.template.domain.TemplateEntity;
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
@Table(name = "snapshot")
public class SnapshotEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "snapshot_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "template_id", nullable = false)
	private TemplateEntity template;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@Column(name = "snapshot_name", nullable = false)
	private String snapshotName;

	@Column(name = "snapshot_type")
	@Enumerated(EnumType.STRING)
	private SnapshotType snapshotType;

	@Column(name = "content", columnDefinition = "mediumtext")
	private String snapshotContent;

	@Builder
	public SnapshotEntity(
		TemplateEntity template, UserEntity user,
		String snapshotName, SnapshotType snapshotType, String snapshotContent
	) {
		this.template = template;
		this.user = user;
		this.snapshotName = snapshotName;
		this.snapshotType = snapshotType;
		this.snapshotContent = snapshotContent;
	}

	public static SnapshotEntity createSnapshot(
		TemplateEntity template,
		String snapshotName, SnapshotType snapshotType, String snapshotContent
	) {
		return SnapshotEntity.builder()
			.template(template)
			.user(template.getUser())
			.snapshotName(snapshotName)
			.snapshotType(snapshotType)
			.snapshotContent(snapshotContent)
			.build();
	}

	public void updateSnapshotName(String snapshotName) {
		this.snapshotName = snapshotName;
	}
}
