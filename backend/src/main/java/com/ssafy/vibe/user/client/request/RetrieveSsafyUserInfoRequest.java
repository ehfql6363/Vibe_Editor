package com.ssafy.vibe.user.client.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RetrieveSsafyUserInfoRequest {
	private String accessToken;
	private String refreshToken;

	public static RetrieveSsafyUserInfoRequest of(String accessToken, String refreshToken) {
		return new RetrieveSsafyUserInfoRequest(accessToken, refreshToken);
	}
}
