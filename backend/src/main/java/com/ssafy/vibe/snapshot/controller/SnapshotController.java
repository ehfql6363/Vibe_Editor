package com.ssafy.vibe.snapshot.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.vibe.auth.domain.UserPrincipal;
import com.ssafy.vibe.common.schema.BaseResponse;
import com.ssafy.vibe.snapshot.controller.request.CreateSnapshotRequest;
import com.ssafy.vibe.snapshot.controller.request.SearchSnapshotRequest;
import com.ssafy.vibe.snapshot.controller.request.UpdateSnapshotRequest;
import com.ssafy.vibe.snapshot.controller.response.SnapshotResponse;
import com.ssafy.vibe.snapshot.service.SnapshotServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/snapshot")
public class SnapshotController {

	private final SnapshotServiceImpl snapshotService;

	@Operation(
		summary = "스냅샷 등록",
		description = "지정한 템플릿에 스냅샷 등록"
	)
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> createSnapshot(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@Valid @RequestBody CreateSnapshotRequest request) {
		snapshotService.createSnapshot(userPrincipal.getUserId(), request);
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@Operation(
		summary = "스냅샷 이름 수정"
	)
	@PutMapping("/{snapshotId}")
	public ResponseEntity<?> updateSnapshot(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@PathVariable("snapshotId") Long snapshotId,
		@Valid @RequestBody UpdateSnapshotRequest request) {
		snapshotService.updateSnapshot(userPrincipal.getUserId(), snapshotId, request);
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@Operation(
		summary = "스냅샷 삭제"
	)
	@DeleteMapping("/{snapshotId}")
	public ResponseEntity<?> deleteTemplate(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@PathVariable("snapshotId") Long snapshotId) {
		snapshotService.deleteSnapshot(userPrincipal.getUserId(), snapshotId);
		return ResponseEntity.ok(BaseResponse.success(null));
	}

	@Operation(
		summary = "스냅샷 목록 조회",
		description = "스냅샷 ID에 해당하는 상세 정보"
	)
	@PostMapping("/list")
	public ResponseEntity<?> getSnapshotList(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@Valid @RequestBody SearchSnapshotRequest request) {
		List<SnapshotResponse> response = snapshotService.getSnapshotList(userPrincipal.getUserId(), request);
		return ResponseEntity.ok(BaseResponse.success(response));
	}

	@Operation(
		summary = "스냅샷 상세 조회",
		description = "스냅샷 ID에 해당하는 상세 정보"
	)
	@GetMapping("/{snapshotId}")
	public ResponseEntity<?> getSnapshotDetail(
		@AuthenticationPrincipal UserPrincipal userPrincipal,
		@PathVariable("snapshotId") Long snapshotId) {
		SnapshotResponse response = snapshotService.getSnapshotDetail(userPrincipal.getUserId(), snapshotId);
		return ResponseEntity.ok(BaseResponse.success(response));
	}
}
