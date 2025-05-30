package com.ssafy.vibe.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.vibe.user.domain.ProviderName;
import com.ssafy.vibe.user.domain.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findById(Long userId);

	Optional<UserEntity> findByProviderNameAndProviderUid(ProviderName providerName, String providerUid);

	Optional<UserEntity> findByEmail(String email);
}
