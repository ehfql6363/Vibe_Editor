package com.ssafy.vibe.snapshot.service.dto;

import java.time.Instant;

import com.ssafy.vibe.snapshot.domain.SnapshotEntity;
import com.ssafy.vibe.snapshot.domain.SnapshotType;

import lombok.Builder;

@Builder
public record SnapshotDTO(
	Long snapshotId,
	String snapshotName,
	SnapshotType snapshotType,
	Instant createdAt,
	Instant updatedAt
) {
	public static SnapshotDTO from(SnapshotEntity snapshot) {
		return SnapshotDTO
			.builder()
			.snapshotId(snapshot.getId())
			.snapshotName(snapshot.getSnapshotName())
			.snapshotType(snapshot.getSnapshotType())
			.createdAt(snapshot.getCreatedAt())
			.updatedAt(snapshot.getUpdatedAt())
			.build();
	}
}
