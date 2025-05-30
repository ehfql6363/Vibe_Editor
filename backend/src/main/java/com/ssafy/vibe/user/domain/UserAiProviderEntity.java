package com.ssafy.vibe.user.domain;

import java.util.ArrayList;
import java.util.List;

import com.ssafy.vibe.common.domain.BaseEntity;
import com.ssafy.vibe.post.domain.PostEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "user_ai_provider")
public class UserAiProviderEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_ai_provider_id")
	private Long id;

	@Column(name = "api_key")
	private String apiKey;

	@Builder.Default
	@Column(name = "temperature", nullable = false)
	private Double temperature = 0.5;

	@Builder.Default
	@Column(name = "is_default", nullable = false)
	private Boolean isDefault = true;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ai_provider_id", nullable = false)
	private AiProviderEntity aiProvider;

	@Builder.Default
	@OneToMany(mappedBy = "userAiProvider")
	private List<PostEntity> postEntities = new ArrayList<>();

	public static UserAiProviderEntity createUserAiProvider(
		String apiKey, Boolean isDefault, UserEntity user, AiProviderEntity aiProvider
	) {
		return UserAiProviderEntity.builder()
			.apiKey(apiKey)
			.isDefault(isDefault)
			.user(user)
			.aiProvider(aiProvider)
			.build();
	}

	public void updateApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
}
