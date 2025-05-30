package com.ssafy.vibe.template.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.vibe.common.exception.BadRequestException;
import com.ssafy.vibe.common.exception.ExceptionCode;
import com.ssafy.vibe.common.exception.NotFoundException;
import com.ssafy.vibe.template.controller.request.CreateTemplateRequest;
import com.ssafy.vibe.template.controller.request.UpdateTemplateRequest;
import com.ssafy.vibe.template.controller.response.TemplateDetailResponse;
import com.ssafy.vibe.template.domain.TemplateEntity;
import com.ssafy.vibe.template.repository.TemplateRepository;
import com.ssafy.vibe.template.service.dto.TemplateDTO;
import com.ssafy.vibe.user.domain.UserEntity;
import com.ssafy.vibe.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class TemplateServiceImpl implements TemplateService {

	private final TemplateRepository templateRepository;
	private final UserRepository userRepository;

	@Override
	public void createTemplate(Long userId, CreateTemplateRequest request) {
		UserEntity user = userRepository.findById(userId)
			.orElseThrow(() -> new BadRequestException(ExceptionCode.USER_NOT_FOUND));
		TemplateEntity template = TemplateEntity.createTemplate(user, request.templateName());
		templateRepository.save(template);
	}

	@Override
	public void updateTemplate(Long userId, Long templateId, UpdateTemplateRequest request) {
		TemplateEntity template = templateRepository.findByIdAndActive(userId, templateId)
			.orElseThrow(() -> new NotFoundException(ExceptionCode.TEMPLATE_NOT_FOUND));
		template.updateTemplateName(request.templateName());
		templateRepository.save(template);
	}

	@Override
	public void deleteTemplate(Long userId, Long templateId) {
		TemplateEntity template = templateRepository.findByIdAndActive(userId, templateId)
			.orElseThrow(() -> new NotFoundException(ExceptionCode.TEMPLATE_NOT_FOUND));
		template.setIsDeleted(true);
		// 템플릿에 종속된 스냅샷 비활성화
		template.getSnapshots().forEach(snapshot -> snapshot.setIsDeleted(true));
		// 템플릿에 종속된 프롬프트 비활성화
		template.getPrompts().forEach(prompt -> prompt.setIsDeleted(true));
		templateRepository.save(template);
	}

	@Override
	public List<TemplateDTO> getTemplateList(Long userId) {
		List<TemplateEntity> templates = templateRepository.findByUserIdAndActive(userId);

		return templates.stream().map(TemplateDTO::from).toList();
	}

	@Override
	public TemplateDetailResponse getTemplateDetail(Long userId, Long templateId) {
		TemplateEntity template = templateRepository.findByIdAndActive(userId, templateId)
			.orElseThrow(() -> new NotFoundException(ExceptionCode.TEMPLATE_NOT_FOUND));

		return TemplateDetailResponse.from(template);
	}
}
