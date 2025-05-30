package com.ssafy.vibe.prompt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.vibe.prompt.domain.PromptEntity;

@Repository
public interface PromptRepository extends JpaRepository<PromptEntity, Long> {
	Optional<PromptEntity> findById(Long id);
}
