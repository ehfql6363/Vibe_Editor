package com.ssafy.vibe.user.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ssafy.vibe.auth.jwt.JwtUtil;
import com.ssafy.vibe.common.exception.BadRequestException;
import com.ssafy.vibe.common.exception.ExceptionCode;
import com.ssafy.vibe.common.exception.ServerException;
import com.ssafy.vibe.user.client.SsafyApiClient;
import com.ssafy.vibe.user.client.request.RetrieveSsafyUserInfoRequest;
import com.ssafy.vibe.user.client.request.RetrieveTokenRequest;
import com.ssafy.vibe.user.client.response.RetrieveSsafyUserInfoResponse;
import com.ssafy.vibe.user.client.response.RetrieveTokenResponse;
import com.ssafy.vibe.user.controller.request.UserLoginRequest;
import com.ssafy.vibe.user.controller.request.UserSignupRequest;
import com.ssafy.vibe.user.domain.ProviderName;
import com.ssafy.vibe.user.domain.UserEntity;
import com.ssafy.vibe.user.repository.UserRepository;
import com.ssafy.vibe.user.service.dto.UserInfoDTO;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserAiProviderService userAiProviderService;
	private final JwtUtil jwtUtil;
	private final SsafyApiClient ssafyApiClient;

	public UserInfoDTO getUserInfo(Long userId) {
		UserEntity userEntity = userRepository.findById(userId)
			.orElseThrow(() -> new BadRequestException(ExceptionCode.USER_NOT_FOUND));

		return UserInfoDTO.from(userEntity);
	}

	@Override
	@Transactional
	public String signup(UserSignupRequest request) {
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new RuntimeException("이미 가입된 유저입니다.");
		}
		UserEntity user = UserEntity.createUser(
			request.getUserName(),
			request.getEmail(),
			ProviderName.valueOf(request.getProviderName()),
			request.getProviderUid()
		);
		UserEntity savedUser = userRepository.save(user);
		return jwtUtil.createJwt(savedUser.getId()); // JWT 토큰 발급
	}

	@Override
	@Transactional
	public String login(UserLoginRequest request) {
		UserEntity user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new RuntimeException("유저 정보가 일치하지 않습니다."));
		user.updateLastLoginAt();
		return jwtUtil.createJwt(user.getId()); // 로그인 시 JWT 토큰 발급
	}

	@Override
	@Transactional
	public void ssafyLogin(HttpServletResponse httpServletResponse, String code) {
		log.info("\n[SSAFY_LOGIN] SSAFY 로그인 시도. code:{}", code);

		// 1. 인가 코드로 토큰 발급 요청
		log.debug("\n[SSAFY_LOGIN] 토큰 발급 요청 시작. code:{}", code);
		RetrieveTokenRequest tokenRequest = RetrieveTokenRequest.of(code);
		RetrieveTokenResponse tokenResponse = ssafyApiClient.retrieveToken(tokenRequest);
		log.info("\n[SSAFY_LOGIN] 토큰 발급 성공. accessToken:{}, refreshToken:{}",
			tokenResponse.getAccessToken(), tokenResponse.getRefreshToken());

		// 2. 토큰으로 SSAFY 사용자 정보 요청
		log.debug("\n[SSAFY_LOGIN] SSAFY 사용자 정보 요청 시작.");
		RetrieveSsafyUserInfoRequest userInfoRequest = RetrieveSsafyUserInfoRequest.of(
			tokenResponse.getAccessToken(),
			tokenResponse.getRefreshToken());
		RetrieveSsafyUserInfoResponse ssafyUser = ssafyApiClient.retrieveUserInfoWithAutoReissue(userInfoRequest);
		log.info("\n[SSAFY_LOGIN] SSAFY 사용자 정보 조회 성공. userId:{}, email:{}",
			ssafyUser.getUserId(), ssafyUser.getEmail());

		UserEntity user = userRepository.findByProviderNameAndProviderUid(ProviderName.ssafy, ssafyUser.getUserId())
			.orElse(null);

		if (user != null) {
			user.updateLastLoginAt();
			addHeader(httpServletResponse, user);
			log.info("[SSAFY_LOGIN] JWT 토큰 발급 및 헤더 추가 완료. userId:{}", user.getId());
			return;
		}
		// 3. 사용자 엔티티 생성 및 저장
		log.debug("\n[SSAFY_LOGIN] UserEntity 객체 생성 시도.");
		user = UserEntity.createUser(
			ssafyUser.getName(),
			ssafyUser.getEmail(),
			ProviderName.ssafy,
			ssafyUser.getUserId());

		UserEntity savedUser = userRepository.save(user);

		userAiProviderService.registerDefaultAPIKey(savedUser.getId());

		savedUser.updateLastLoginAt();
		log.info("\n[SSAFY_LOGIN] UserEntity 저장 완료. userId:{}", savedUser.getId());

		// 4. JWT 생성 및 응답 헤더에 추가
		addHeader(httpServletResponse, savedUser);
		log.info("\n[SSAFY_LOGIN] JWT 토큰 발급 및 헤더 추가 완료. userId:{}", savedUser.getId());
	}

	private void addHeader(HttpServletResponse httpServletResponse, UserEntity user) {
		try {
			String jwt = jwtUtil.createJwt(user.getId());
			String redirectUrl = "http://localhost:5013/callback?accessToken=" + jwt;
			httpServletResponse.sendRedirect(redirectUrl);
		} catch (IOException e) {
			throw new ServerException(ExceptionCode.SSAFY_JWT_TOKEN_REDIRECT_FAILED);
		}
	}

}
