package com.ssafy.vibe.snapshot.helper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ssafy.vibe.common.exception.ExceptionCode;
import com.ssafy.vibe.common.exception.NotFoundException;
import com.ssafy.vibe.snapshot.domain.SnapshotEntity;
import com.ssafy.vibe.snapshot.repository.SnapshotRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SnapshotHelper {

	private final SnapshotRepository snapshotRepository;

	public SnapshotEntity findSnapshotOrThrow(Long userId, Long snapshotId) {
		return snapshotRepository.findByIdAndActive(userId, snapshotId)
			.orElseThrow(() -> new NotFoundException(ExceptionCode.SNAPSHOT_NOT_FOUND));
	}

	public List<SnapshotEntity> findSnapshotList(Long userId, List<Long> snapshotIdList) {
		return snapshotRepository.findByIdInAndActive(userId, snapshotIdList);
	}
}
