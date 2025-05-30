package com.ssafy.vibe.snapshot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ssafy.vibe.snapshot.domain.SnapshotEntity;

@Repository
public interface SnapshotRepository extends JpaRepository<SnapshotEntity, Long> {

	@Query("""
		select se
		from SnapshotEntity se
		where
			se.user.id = :userId
			and se.isDeleted = false
		order by se.updatedAt desc
		""")
	List<SnapshotEntity> findByUserIdAndActive(@Param("userId") Long userId);

	@Query("""
		select se
		from SnapshotEntity se
		where
			se.user.id = :userId
		    and se.id = :snapshotId
		    and se.isDeleted = false
		""")
	Optional<SnapshotEntity> findByIdAndActive(
		@Param("userId") Long userId,
		@Param("snapshotId") Long snapshotId
	);

	@Query("""
		select se
		from SnapshotEntity se
		where
			se.user.id = :userId
		    and se.id in :snapshotIdList
		    and se.isDeleted = false
		order by se.updatedAt desc
		""")
	List<SnapshotEntity> findByIdInAndActive(
		@Param("userId") Long userId,
		@Param("snapshotIdList") List<Long> snapshotIdList
	);

	Optional<SnapshotEntity> findById(Long snapshotId);
}
