package com.ssafy.vibe.auth.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.ssafy.vibe.user.domain.UserEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {
	private final UserEntity user;
	private final Map<String, Object> attributes;

	public CustomOAuth2User(UserEntity user) {
		this(user, Collections.emptyMap());
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getName() {
		return user.getUserName(); // 또는 providerUid
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return new ArrayList<>();
	}

	public Long getUserId() {
		return user.getId();
	}
}