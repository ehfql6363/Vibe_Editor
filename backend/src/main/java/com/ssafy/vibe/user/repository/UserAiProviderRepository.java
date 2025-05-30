package com.ssafy.vibe.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ssafy.vibe.user.domain.AiBrandName;
import com.ssafy.vibe.user.domain.UserAiProviderEntity;

@Repository
public interface UserAiProviderRepository extends JpaRepository<UserAiProviderEntity, Long> {

	@Query("""
			select uae
			from UserAiProviderEntity uae
			join fetch UserEntity ue
				on uae.user = ue
			join fetch AiProviderEntity ape
				on uae.aiProvider = ape
			where
				ue.id = :userId
				and ape.brand = :brand
				and uae.isDefault = false
				and uae.isDeleted = false
		""")
	List<UserAiProviderEntity> findCustomUserAiProviderByBrand(
		@Param("userId") Long userId, @Param("brand") AiBrandName brand
	);

	@Query("""
			select uae
			from UserAiProviderEntity uae
			join fetch UserEntity ue
				on uae.user = ue
			where
				ue.id = :userId
				and uae.isDeleted = false
		""")
	List<UserAiProviderEntity> findUserAiProviderByUserId(@Param("userId") Long userId);
}
