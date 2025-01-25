package com.aivle.project.repository;

import com.aivle.project.entity.ChatbotLogsEntity;
import com.aivle.project.entity.CrudLogsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrudLogsRepository  extends JpaRepository<CrudLogsEntity, Long> {

    List<CrudLogsEntity> findAllByOrderByLogsIdDesc(); // logsId 기준 내림차순
}
