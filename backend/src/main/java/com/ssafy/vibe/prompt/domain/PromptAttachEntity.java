package com.ssafy.vibe.prompt.domain;

import com.ssafy.vibe.common.domain.BaseEntity;
import com.ssafy.vibe.snapshot.domain.SnapshotEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "prompt_attach")
public class PromptAttachEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "attach_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "prompt_id", nullable = false)
	private PromptEntity prompt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "snapshot_id", nullable = false)
	private SnapshotEntity snapshot;

	@Column(name = "description")
	private String description;

	public void updateDescription(String description) {
		this.description = description;
	}
}
