package com.ssafy.vibe.snapshot.service;

import java.util.List;

import com.ssafy.vibe.snapshot.controller.request.CreateSnapshotRequest;
import com.ssafy.vibe.snapshot.controller.request.SearchSnapshotRequest;
import com.ssafy.vibe.snapshot.controller.request.UpdateSnapshotRequest;
import com.ssafy.vibe.snapshot.controller.response.SnapshotResponse;

public interface SnapshotService {
	void createSnapshot(Long userId, CreateSnapshotRequest request);

	void updateSnapshot(Long userId, Long snapshotId, UpdateSnapshotRequest request);

	void deleteSnapshot(Long userId, Long snapshotId);

	List<SnapshotResponse> getSnapshotList(Long userId, SearchSnapshotRequest request);

	SnapshotResponse getSnapshotDetail(Long userId, Long snapshotId);
}
