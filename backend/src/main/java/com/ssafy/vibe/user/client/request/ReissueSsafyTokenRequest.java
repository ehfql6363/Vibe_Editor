package com.ssafy.vibe.user.client.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReissueSsafyTokenRequest {

	private String grantType;
	private String refreshToken;

	public static ReissueSsafyTokenRequest of(String refreshToken) {
		return new ReissueSsafyTokenRequest("authorization_code", refreshToken);
	}
}
