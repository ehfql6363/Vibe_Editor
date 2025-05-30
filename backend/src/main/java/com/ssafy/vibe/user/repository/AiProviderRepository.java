package com.ssafy.vibe.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ssafy.vibe.user.domain.AiBrandName;
import com.ssafy.vibe.user.domain.AiProviderEntity;

@Repository
public interface AiProviderRepository extends JpaRepository<AiProviderEntity, Long> {

	@Query("select ape from AiProviderEntity ape where ape.brand = :brandName")
	List<AiProviderEntity> findByBrand(@Param("brandName") AiBrandName brandName);
}
