package com.ssafy.vibe.snapshot.controller.request;

import com.ssafy.vibe.snapshot.domain.SnapshotType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateSnapshotRequest(
	@NotNull(message = "{required}")
	Long templateId,

	@NotBlank(message = "{required}")
	@Size(max = 255, message = "{max.length}")
	String snapshotName,

	@NotNull(message = "{required}")
	SnapshotType snapshotType,

	@NotBlank(message = "{required}")
	String snapshotContent
) {
}
