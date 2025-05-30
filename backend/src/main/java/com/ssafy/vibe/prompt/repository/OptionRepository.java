package com.ssafy.vibe.prompt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.vibe.prompt.domain.OptionEntity;

public interface OptionRepository extends JpaRepository<OptionEntity, Long> {
	Optional<OptionEntity> findById(Long id);

	List<OptionEntity> findAll();
}
