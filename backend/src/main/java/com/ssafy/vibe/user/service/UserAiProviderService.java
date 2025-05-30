package com.ssafy.vibe.user.service;

import java.util.List;

import com.ssafy.vibe.user.controller.request.UserAiCreateRequest;
import com.ssafy.vibe.user.controller.request.UserAiUpdateRequest;
import com.ssafy.vibe.user.controller.response.UserAiResponse;

public interface UserAiProviderService {
	void registerDefaultAPIKey(Long userId);

	void registerUserAPIKey(Long userId, UserAiCreateRequest request);

	void updateUserAPIKey(Long userId, UserAiUpdateRequest request);

	List<UserAiResponse> getAiProviderList(Long userId);
}
