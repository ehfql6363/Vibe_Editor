package com.ssafy.vibe.user.domain;

import java.util.ArrayList;
import java.util.List;

import com.ssafy.vibe.common.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "ai_provider")
public class AiProviderEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ai_provider_id")
	private Long id;

	@Column(name = "brand", nullable = false)
	@Enumerated(EnumType.STRING)
	private AiBrandName brand;

	@Column(name = "model", nullable = false)
	private String model;

	@Builder.Default
	@OneToMany(mappedBy = "aiProvider")
	private List<UserAiProviderEntity> userAiProvider = new ArrayList<>();
}
