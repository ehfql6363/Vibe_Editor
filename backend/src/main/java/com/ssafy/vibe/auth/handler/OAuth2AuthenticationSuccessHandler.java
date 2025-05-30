package com.ssafy.vibe.auth.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.ssafy.vibe.auth.domain.CustomOAuth2User;
import com.ssafy.vibe.auth.jwt.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtUtil jwtUtil;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		CustomOAuth2User userInfo = (CustomOAuth2User)authentication.getPrincipal();
		Long userId = userInfo.getUserId();
		String token = jwtUtil.createJwt(userId);

		String redirectUrl = "http://localhost:5013/callback?accessToken=" + token;
		getRedirectStrategy().sendRedirect(request, response, redirectUrl);
	}
}
