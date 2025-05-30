package com.ssafy.vibe.user.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.ssafy.vibe.common.exception.ExceptionCode;
import com.ssafy.vibe.common.exception.ServerException;
import com.ssafy.vibe.user.client.request.ReissueSsafyTokenRequest;
import com.ssafy.vibe.user.client.request.RetrieveSsafyUserInfoRequest;
import com.ssafy.vibe.user.client.request.RetrieveTokenRequest;
import com.ssafy.vibe.user.client.response.ReissueSsafyTokenResponse;
import com.ssafy.vibe.user.client.response.RetrieveSsafyUserInfoResponse;
import com.ssafy.vibe.user.client.response.RetrieveTokenResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SsafyApiClient {

	private final WebClient webClient;

	@Value("${ssafy.client-id}")
	private String clientId;

	@Value("${ssafy.secret-key}")
	private String clientSecret;

	@Value("${ssafy.redirect_url}")
	private String redirectUrl;

	@Value("${ssafy.base_url}")
	private String baseUrl;

	public RetrieveSsafyUserInfoResponse retrieveUserInfoWithAutoReissue(RetrieveSsafyUserInfoRequest ssafyRequest) {
		try {
			log.info("\n[SSAFY_USERINFO] AccessToken으로 유저 정보 조회 시도...");
			return retrieveUserInfo(ssafyRequest); // 기존 방식 호출
		} catch (WebClientResponseException e) {
			if (e.getStatusCode().value() == 401 || e.getStatusCode().value() == 403) {
				log.info("\n[SSAFY_TOKEN_REISSUE] accessToken 만료, refreshToken으로 토큰 재발급 시도...");
				ReissueSsafyTokenRequest reissueRequest = ReissueSsafyTokenRequest.of(
					ssafyRequest.getRefreshToken());
				ReissueSsafyTokenResponse reissueResponse = this.reissueToken(reissueRequest);

				log.info("\n[SSAFY_TOKEN_REISSUE] accessToken 재발급 성공. 재시도 진행...");
				RetrieveSsafyUserInfoRequest newUserInfoRequest = RetrieveSsafyUserInfoRequest.of(
					reissueResponse.getAccessToken(),
					reissueResponse.getRefreshToken());
				return retrieveUserInfo(newUserInfoRequest);
			}
			log.error("\n[SSAFY_USERINFO_ERROR] 유저 정보 조회 실패. code={}, message={}", e.getStatusCode(), e.getMessage());
			throw new ServerException(ExceptionCode.SSAFY_RETRIEVE_USERINFO_FAILED);
		}
	}

	public RetrieveTokenResponse retrieveToken(RetrieveTokenRequest ssafyRequest) {
		try {
			log.info("\n[SSAFY_OAUTH] 토큰 발급 요청 시작. When:{} Where:{} Data:{}", System.currentTimeMillis(), baseUrl,
				ssafyRequest);

			RetrieveTokenResponse response = webClient.post().uri(uriBuilder -> uriBuilder
					.scheme("https")
					.host(baseUrl)
					.path("/ssafy/oauth2/token")
					.build())
				.header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
				.body(BodyInserters.fromFormData("grant_type", ssafyRequest.getGrantType())
					.with("client_id", clientId)
					.with("client_secret", clientSecret)
					.with("redirect_uri", redirectUrl)
					.with("code", ssafyRequest.getCode()))
				.retrieve()
				.bodyToMono(RetrieveTokenResponse.class)
				.block();

			log.info("\n[SSAFY_OAUTH] 토큰 발급 성공. When:{} Result:{}", System.currentTimeMillis(), response);

			return response;
		} catch (WebClientResponseException e) {
			log.error("\n[SSAFY_OAUTH_ERROR] 토큰 발급 실패. code={}, message={}", e.getStatusCode(), e.getMessage());
			throw new ServerException(ExceptionCode.SSAFY_RETRIEVE_TOKEN_FAILED);
		}
	}

	public ReissueSsafyTokenResponse reissueToken(ReissueSsafyTokenRequest ssafyRequest) {
		try {
			log.info("\n[SSAFY_REISSUE] 토큰 재발급 요청 시작. When:{} refreshToken:{}", System.currentTimeMillis(),
				ssafyRequest.getRefreshToken());

			ReissueSsafyTokenResponse response = webClient.post().uri(uriBuilder -> uriBuilder
					.scheme("https")
					.host(baseUrl)
					.path("/ssafy/oauth2/token")
					.build())
				.header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
				.body(BodyInserters.fromFormData("grant_type", ssafyRequest.getGrantType())
					.with("client_id", clientId)
					.with("client_secret", clientSecret)
					.with("redirect_uri", redirectUrl)
					.with("refresh_token", ssafyRequest.getRefreshToken()))
				.retrieve()
				.bodyToMono(ReissueSsafyTokenResponse.class)
				.block();

			log.info("\n[SSAFY_REISSUE] 토큰 재발급 성공. When:{} accessToken:{}", System.currentTimeMillis(),
				response.getAccessToken());
			return response;
		} catch (WebClientResponseException e) {
			log.error("\n[SSAFY_REISSUE_ERROR] 토큰 재발급 실패. code={}, message={}", e.getStatusCode(), e.getMessage());
			throw new ServerException(ExceptionCode.SSAFY_REISSUE_TOKEN_FAILED);
		}
	}

	private RetrieveSsafyUserInfoResponse retrieveUserInfo(RetrieveSsafyUserInfoRequest ssafyRequest) {
		log.info("\n[SSAFY_USERINFO] 유저 정보 요청 시작. When:{} Access-Token:{}", System.currentTimeMillis(),
			ssafyRequest.getAccessToken());

		RetrieveSsafyUserInfoResponse response = webClient.get().uri(uriBuilder -> uriBuilder
				.scheme("https")
				.host(baseUrl)
				.path("/ssafy/resources/userInfo")
				.build())
			.headers(httpHeaders -> {
				httpHeaders.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
				httpHeaders.add("Authorization", "Bearer " + ssafyRequest.getAccessToken());
			})
			.retrieve()
			.bodyToMono(RetrieveSsafyUserInfoResponse.class)
			.block();

		log.info("\n[SSAFY_USERINFO] 유저 정보 응답. When:{} Result:{}", System.currentTimeMillis(), response);

		return response;
	}
}