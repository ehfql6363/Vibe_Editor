package com.ssafy.vibe.snapshot.controller.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record SearchSnapshotRequest(
	@NotEmpty(message = "{required}")
	List<Long> snapshotIdList
) {
}
