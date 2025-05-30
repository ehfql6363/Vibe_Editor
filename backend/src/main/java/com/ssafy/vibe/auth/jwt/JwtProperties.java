package com.ssafy.vibe.auth.jwt;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "spring.security.jwt")
@Getter
@Setter
public class JwtProperties {
	private String secretKey;
	private Long accessTokenExpiration;
	private Long refreshTokenExpiration;
	private List<String> passUrls = new ArrayList<>();
	private List<String> adminUrls = new ArrayList<>();
}
