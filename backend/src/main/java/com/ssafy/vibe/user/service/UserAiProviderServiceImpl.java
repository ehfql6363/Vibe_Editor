package com.ssafy.vibe.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.vibe.common.exception.BadRequestException;
import com.ssafy.vibe.common.exception.ExceptionCode;
import com.ssafy.vibe.common.util.Aes256Util;
import com.ssafy.vibe.prompt.service.AiChatService;
import com.ssafy.vibe.prompt.service.AiChatServiceFactory;
import com.ssafy.vibe.user.controller.request.UserAiCreateRequest;
import com.ssafy.vibe.user.controller.request.UserAiUpdateRequest;
import com.ssafy.vibe.user.controller.response.UserAiResponse;
import com.ssafy.vibe.user.domain.AiBrandName;
import com.ssafy.vibe.user.domain.AiProviderEntity;
import com.ssafy.vibe.user.domain.UserAiProviderEntity;
import com.ssafy.vibe.user.domain.UserEntity;
import com.ssafy.vibe.user.helper.UserHelper;
import com.ssafy.vibe.user.repository.AiProviderRepository;
import com.ssafy.vibe.user.repository.UserAiProviderRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@RequiredArgsConstructor
@Slf4j
@Service
public class UserAiProviderServiceImpl implements UserAiProviderService {

	private final UserAiProviderRepository userAiProviderRepository;
	private final AiProviderRepository aiProviderRepository;
	private final Aes256Util aes256Util;
	private final UserHelper userHelper;
	private final AiChatServiceFactory aiChatServiceFactory;

	@Override
	public void registerDefaultAPIKey(Long userId) {
		UserEntity user = userHelper.getUser(userId);

		// 기본 제공 AI 지급
		for (AiBrandName brand : AiBrandName.values()) {
			List<AiProviderEntity> aiProviders = aiProviderRepository.findByBrand(brand);
			aiProviders.forEach(aiProvider -> {
				UserAiProviderEntity userAiProvider = UserAiProviderEntity.createUserAiProvider(
					null, true, user, aiProvider
				);
				userAiProviderRepository.save(userAiProvider);
			});
		}
	}

	@Override
	public void registerUserAPIKey(Long userId, UserAiCreateRequest request) {
		UserEntity user = userHelper.getUser(userId);
		List<AiProviderEntity> aiProviders = aiProviderRepository.findByBrand(request.brand());
		if (aiProviders.isEmpty()) {
			throw new BadRequestException(ExceptionCode.AI_BRAND_NOT_FOUND);
		}

		// 커스텀 브랜드는 1개만 등록 가능
		List<UserAiProviderEntity> userAiProviders = userAiProviderRepository.findCustomUserAiProviderByBrand(
			userId, request.brand()
		);
		if (!userAiProviders.isEmpty()) {
			throw new BadRequestException(ExceptionCode.DUPLICATED_AI_BRAND);
		}

		// 브랜드 API Key 유효성 검사
		AiChatService aiChatService = aiChatServiceFactory.get(request.brand());
		aiChatService.validateApiKey(request.apiKey());

		String encryptedApiKey = aes256Util.encrypt(request.apiKey());
		aiProviders.forEach(aiProvider -> {
			UserAiProviderEntity userAiProvider = UserAiProviderEntity.createUserAiProvider(
				encryptedApiKey, false, user, aiProvider
			);
			userAiProviderRepository.save(userAiProvider);
		});
	}

	@Override
	public void updateUserAPIKey(Long userId, UserAiUpdateRequest request) {
		String encryptedApiKey = aes256Util.encrypt(request.apiKey());
		List<UserAiProviderEntity> userAiProviders = userAiProviderRepository.findCustomUserAiProviderByBrand(
			userId, request.brand()
		);
		if (userAiProviders.isEmpty()) {
			throw new BadRequestException(ExceptionCode.AI_BRAND_NOT_FOUND);
		}

		userAiProviders.forEach(userAiProvider ->
			userAiProvider.updateApiKey(encryptedApiKey)
		);
	}

	@Override
	public List<UserAiResponse> getAiProviderList(Long userId) {
		UserEntity user = userHelper.getUser(userId);
		List<UserAiProviderEntity> userAiProviders = userAiProviderRepository.findUserAiProviderByUserId(user.getId());

		return userAiProviders.stream().map(UserAiResponse::from).toList();
	}
}
