package com.ssafy.vibe.auth.service;

import static com.ssafy.vibe.common.exception.ExceptionCode.*;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.ssafy.vibe.auth.domain.CustomOAuth2User;
import com.ssafy.vibe.common.exception.AuthenticationException;
import com.ssafy.vibe.user.domain.ProviderName;
import com.ssafy.vibe.user.domain.UserEntity;
import com.ssafy.vibe.user.repository.UserRepository;
import com.ssafy.vibe.user.service.UserAiProviderService;
import com.ssafy.vibe.user.service.dto.UserDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthService extends DefaultOAuth2UserService {
	private final UserRepository userRepository;
	private final UserAiProviderService userAiProviderService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String providerName = userRequest.getClientRegistration().getRegistrationId();
		log.info("Provider Name: {}", providerName);

		Map<String, Object> attributes = oAuth2User.getAttributes();

		String userName = (String)attributes.get("name");
		String email;
		String providerUid;
		if (providerName.equals("google")) {
			email = (String)attributes.get("email");
			providerUid = (String)attributes.get("sub"); // Google 고유 ID
		} else if (providerName.equals("github")) {
			if (attributes.get("email") == null) {
				email = attributes.get("login").toString().concat("@github.com");
			} else {
				email = attributes.get("email").toString();
			}
			providerUid = String.valueOf(attributes.get("id")); // GitHub 고유 ID
		} else {
			throw new AuthenticationException(UNSUPPORTED_PROVIDER);
		}

		if (userName == null) {
			userName = email.split("@")[0];
		}

		UserDTO userDto = UserDTO.createUserDto(userName, email, ProviderName.valueOf(providerName), providerUid);
		UserEntity user = userRepository.findByProviderNameAndProviderUid(ProviderName.valueOf(providerName),
				providerUid)
			.orElseGet(() -> userRepository.save(UserEntity.from(userDto)));

		if (user.getLastLoginAt() == null) {
			// 기본 제공 AI 등록
			userAiProviderService.registerDefaultAPIKey(user.getId());
		}
		user.updateLastLoginAt();
		userRepository.save(user);
		
		return new CustomOAuth2User(user);
	}
}
