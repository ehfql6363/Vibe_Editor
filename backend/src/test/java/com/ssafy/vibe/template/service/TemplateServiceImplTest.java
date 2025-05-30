package com.ssafy.vibe.template.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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
import com.ssafy.vibe.template.controller.request.CreateTemplateRequest;
import com.ssafy.vibe.template.controller.request.UpdateTemplateRequest;
import com.ssafy.vibe.template.domain.TemplateEntity;
import com.ssafy.vibe.template.repository.TemplateRepository;
import com.ssafy.vibe.template.service.dto.TemplateDTO;
import com.ssafy.vibe.user.domain.UserEntity;
import com.ssafy.vibe.user.repository.UserRepository;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Sql(scripts = "classpath:/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Disabled
class TemplateServiceImplTest {

	@Autowired
	TemplateService templateService;

	@Autowired
	UserRepository userRepository;
	@Autowired
	TemplateRepository templateRepository;

	UserEntity user, otherUser;
	TemplateEntity template;

	@BeforeEach
	void setUp() {
		user = userRepository.findById(1L).orElse(null);
		otherUser = userRepository.findById(2L).orElse(null);
		template = templateRepository.findById(1L).orElse(null);
	}

	@Test
	void 템플릿생성_성공() {
		CreateTemplateRequest request = new CreateTemplateRequest("템플릿이름");
		int templateCount = templateRepository.findByUserIdAndActive(user.getId()).size();
		templateService.createTemplate(user.getId(), request);
		assertEquals(templateCount + 1, templateRepository.findByUserIdAndActive(user.getId()).size());
	}

	@Test
	void 템플릿이름변경_성공() {
		assertEquals("Spring Boot API 서버 구축", template.getTemplateName());
		UpdateTemplateRequest request = new UpdateTemplateRequest("Spring Boot REST API");
		templateService.updateTemplate(user.getId(), template.getId(), request);
		assertEquals("Spring Boot REST API", template.getTemplateName());
	}

	@Test
	void 템플릿이름변경_실패_템플릿권한없음() {
		assertEquals("Spring Boot API 서버 구축", template.getTemplateName());
		UpdateTemplateRequest request = new UpdateTemplateRequest("템플릿이름수정");
		Assertions.assertThatThrownBy(
				() -> templateService.updateTemplate(otherUser.getId(), template.getId(), request))
			.isInstanceOf(NotFoundException.class)
			.hasMessage("템플릿이 존재하지 않습니다.");
	}

	@Test
	void 템플릿삭제_성공() {
		int templateCount = templateRepository.findByUserIdAndActive(user.getId()).size();
		templateService.deleteTemplate(user.getId(), template.getId());
		assertEquals(templateCount - 1, templateRepository.findByUserIdAndActive(user.getId()).size());
	}

	@Test
	void 템플릿삭제_실패_템플릿권한없음() {
		Assertions.assertThatThrownBy(() -> templateService.deleteTemplate(otherUser.getId(), template.getId()))
			.isInstanceOf(NotFoundException.class)
			.hasMessage("템플릿이 존재하지 않습니다.");
	}

	@Test
	void 템플릿목록조회_성공() {
		List<TemplateDTO> templates = templateService.getTemplateList(user.getId());
		assertFalse(templates.isEmpty());
	}
}
