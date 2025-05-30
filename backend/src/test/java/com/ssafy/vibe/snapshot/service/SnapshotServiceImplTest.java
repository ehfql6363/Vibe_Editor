package com.ssafy.vibe.snapshot.service;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.vibe.common.exception.NotFoundException;
import com.ssafy.vibe.snapshot.controller.request.CreateSnapshotRequest;
import com.ssafy.vibe.snapshot.controller.request.UpdateSnapshotRequest;
import com.ssafy.vibe.snapshot.controller.response.SnapshotResponse;
import com.ssafy.vibe.snapshot.domain.SnapshotEntity;
import com.ssafy.vibe.snapshot.domain.SnapshotType;
import com.ssafy.vibe.snapshot.repository.SnapshotRepository;
import com.ssafy.vibe.template.domain.TemplateEntity;
import com.ssafy.vibe.template.repository.TemplateRepository;
import com.ssafy.vibe.user.domain.UserEntity;
import com.ssafy.vibe.user.repository.UserRepository;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Sql(scripts = "classpath:/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Disabled
class SnapshotServiceImplTest {

	UserEntity user, otherUser;
	TemplateEntity template;
	SnapshotEntity snapshot;

	@Autowired
	private SnapshotService snapshotService;

	@Autowired
	private SnapshotRepository snapshotRepository;
	@Autowired
	private TemplateRepository templateRepository;
	@Autowired
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		user = userRepository.findById(1L).orElse(null);
		otherUser = userRepository.findById(2L).orElse(null);
		template = templateRepository.findById(1L).orElse(null);
		snapshot = snapshotRepository.findById(1L).orElse(null);
	}

	@Test
	void 스냅샷생성_성공() {
		CreateSnapshotRequest request = new CreateSnapshotRequest(template.getId(), "새로운 스냅샷", SnapshotType.BLOCK,
			"코드 내용");
		int beforeCount = snapshotRepository.findByUserIdAndActive(user.getId()).size();

		snapshotService.createSnapshot(user.getId(), request);

		int afterCount = snapshotRepository.findByUserIdAndActive(user.getId()).size();
		assertEquals(beforeCount + 1, afterCount);
	}

	@Test
	void 스냅샷이름변경_성공() {
		assertEquals("Application.java", snapshot.getSnapshotName());

		UpdateSnapshotRequest request = new UpdateSnapshotRequest("변경된 스냅샷 이름");

		snapshotService.updateSnapshot(user.getId(), snapshot.getId(), request);

		SnapshotEntity updatedSnapshot = snapshotRepository.findById(snapshot.getId()).orElse(null);
		assertEquals("변경된 스냅샷 이름", updatedSnapshot.getSnapshotName());
	}

	@Test
	void 스냅샷이름변경_실패_스냅샷권한없음() {
		UpdateSnapshotRequest request = new UpdateSnapshotRequest("변경 실패할 이름");

		Assertions.assertThatThrownBy(
				() -> snapshotService.updateSnapshot(otherUser.getId(), snapshot.getId(), request))
			.isInstanceOf(NotFoundException.class)
			.hasMessage("스냅샷이 존재하지 않습니다.");
	}

	@Test
	void 스냅샷삭제_성공() {
		int beforeCount = snapshotRepository.findByUserIdAndActive(user.getId()).size();

		snapshotService.deleteSnapshot(user.getId(), snapshot.getId());

		int afterCount = snapshotRepository.findByUserIdAndActive(user.getId()).size();
		assertEquals(beforeCount - 1, afterCount);
	}

	@Test
	void 스냅샷삭제_실패_스냅샷권한없음() {
		Assertions.assertThatThrownBy(() -> snapshotService.deleteSnapshot(otherUser.getId(), snapshot.getId()))
			.isInstanceOf(NotFoundException.class)
			.hasMessage("스냅샷이 존재하지 않습니다.");
	}

	@Test
	void 스냅샷상세조회_성공() {
		SnapshotResponse detail = snapshotService.getSnapshotDetail(user.getId(), snapshot.getId());

		assertEquals(snapshot.getSnapshotName(), detail.snapshotName());
		assertEquals(snapshot.getSnapshotType(), detail.snapshotType());
	}
}