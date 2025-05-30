package com.ssafy.vibe.snapshot.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSnapshotRequest(
	@NotBlank(message = "{required}")
	@Size(max = 255, message = "{max.length}")
	String snapshotName
) {
}
