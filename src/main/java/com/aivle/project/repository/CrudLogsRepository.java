package com.aivle.project.repository;

import com.aivle.project.entity.CrudLogsEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CrudLogsRepository  extends JpaRepository<CrudLogsEntity, Long> {

    // ✅ 특정 키워드를 포함하는 로그 검색 + 페이징 지원
    @Query("SELECT c FROM CrudLogsEntity c WHERE c.userId LIKE %:keyword% ORDER BY c.logsId DESC")
    Page<CrudLogsEntity> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
