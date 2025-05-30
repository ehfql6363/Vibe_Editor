package com.ssafy.vibe.common.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@ConfigurationProperties(prefix = "spring.security.cors")
@Getter
public class CorsProperties {
	private List<String> allowedOrigins = new ArrayList<>();
}
