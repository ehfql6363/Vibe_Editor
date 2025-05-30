package com.ssafy.vibe.prompt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.vibe.prompt.domain.PromptEntity;
import com.ssafy.vibe.prompt.domain.PromptOptionEntity;

@Repository
public interface PromptOptionRepository extends JpaRepository<PromptOptionEntity, Long> {
	List<PromptOptionEntity> findByPromptAndIsDeletedFalse(PromptEntity prompt);

	List<PromptOptionEntity> findByPrompt(PromptEntity prompt);
}
