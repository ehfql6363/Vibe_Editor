package com.ssafy.vibe.prompt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ssafy.vibe.prompt.domain.PromptAttachEntity;
import com.ssafy.vibe.prompt.domain.PromptEntity;

@Repository
public interface PromptAttachRepository extends JpaRepository<PromptAttachEntity, Long> {
	List<PromptAttachEntity> findByPromptAndIsDeletedFalse(PromptEntity prompt);

	List<PromptAttachEntity> findByPrompt(PromptEntity prompt);
}
