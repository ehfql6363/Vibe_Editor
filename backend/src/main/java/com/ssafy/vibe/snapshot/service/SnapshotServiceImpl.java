package com.ssafy.vibe.snapshot.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.vibe.common.exception.ExceptionCode;
import com.ssafy.vibe.common.exception.NotFoundException;
import com.ssafy.vibe.snapshot.controller.request.CreateSnapshotRequest;
import com.ssafy.vibe.snapshot.controller.request.SearchSnapshotRequest;
import com.ssafy.vibe.snapshot.controller.request.UpdateSnapshotRequest;
import com.ssafy.vibe.snapshot.controller.response.SnapshotResponse;
import com.ssafy.vibe.snapshot.domain.SnapshotEntity;
import com.ssafy.vibe.snapshot.helper.SnapshotHelper;
import com.ssafy.vibe.snapshot.repository.SnapshotRepository;
import com.ssafy.vibe.template.domain.TemplateEntity;
import com.ssafy.vibe.template.repository.TemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class SnapshotServiceImpl implements SnapshotService {

	private final SnapshotRepository snapshotRepository;
	private final TemplateRepository templateRepository;
	private final SnapshotHelper snapshotHelper;

	@Override
	public void createSnapshot(Long userId, CreateSnapshotRequest request) {
		TemplateEntity template = templateRepository.findByIdAndActive(userId, request.templateId())
			.orElseThrow(() -> new NotFoundException(ExceptionCode.TEMPLATE_NOT_FOUND));

		SnapshotEntity snapshot = SnapshotEntity.createSnapshot(template, request.snapshotName(),
			request.snapshotType(),
			request.snapshotContent());
		snapshotRepository.save(snapshot);
	}

	@Override
	public void updateSnapshot(Long userId, Long snapshotId, UpdateSnapshotRequest request) {
		SnapshotEntity snapshot = snapshotHelper.findSnapshotOrThrow(userId, snapshotId);
		snapshot.updateSnapshotName(request.snapshotName());
		snapshotRepository.save(snapshot);
	}

	@Override
	public void deleteSnapshot(Long userId, Long snapshotId) {
		SnapshotEntity snapshot = snapshotHelper.findSnapshotOrThrow(userId, snapshotId);
		snapshot.setIsDeleted(true);
		snapshotRepository.save(snapshot);
	}

	@Override
	public List<SnapshotResponse> getSnapshotList(Long userId, SearchSnapshotRequest request) {
		List<SnapshotEntity> snapshotList = snapshotHelper.findSnapshotList(userId, request.snapshotIdList());

		return snapshotList.stream()
			.map(SnapshotResponse::from)
			.toList();
	}

	@Override
	public SnapshotResponse getSnapshotDetail(Long userId, Long snapshotId) {
		SnapshotEntity snapshot = snapshotHelper.findSnapshotOrThrow(userId, snapshotId);

		return SnapshotResponse.from(snapshot);
	}
}
