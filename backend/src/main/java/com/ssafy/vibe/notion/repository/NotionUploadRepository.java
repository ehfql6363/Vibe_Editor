package com.ssafy.vibe.notion.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.vibe.notion.domain.NotionUploadEntity;

public interface NotionUploadRepository extends JpaRepository<NotionUploadEntity, Long> {
	Optional<NotionUploadEntity> findFirstByPostIdOrderByCreatedAtDesc(Long postId);
}
