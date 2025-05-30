package com.ssafy.vibe.snapshot.controller.response;

import java.time.Instant;

import com.ssafy.vibe.snapshot.domain.SnapshotEntity;
import com.ssafy.vibe.snapshot.domain.SnapshotType;

import lombok.Builder;

@Builder
public record SnapshotResponse(
	Long snapshotId,
	String snapshotName,
	SnapshotType snapshotType,
	String snapshotContent,
	Instant createdAt,
	Instant updatedAt
) {
	public static SnapshotResponse from(SnapshotEntity snapshot) {
		return SnapshotResponse
			.builder()
			.snapshotId(snapshot.getId())
			.snapshotName(snapshot.getSnapshotName())
			.snapshotType(snapshot.getSnapshotType())
			.snapshotContent(snapshot.getSnapshotContent())
			.createdAt(snapshot.getCreatedAt())
			.updatedAt(snapshot.getUpdatedAt())
			.build();
	}
}
