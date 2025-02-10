package com.aivle.project.repository;

import com.aivle.project.entity.ContractsEntity;
import com.aivle.project.enums.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContractsRepository extends JpaRepository<ContractsEntity, Long> {
    ContractsEntity findByContractId(Long contractId);

    // select 전용 쿼리
    @Query("SELECT c.contractId FROM ContractsEntity c")
    List<Long> findAllContractIds();

    @Query("SELECT c FROM ContractsEntity c " +
            "JOIN c.accountId a " +
            "WHERE a.accountName LIKE %:accountName%")
    Page<ContractsEntity> findByAccountNameLikeManager(@Param("accountName") String accountName, Pageable pageable);

    @Query("SELECT c FROM ContractsEntity c " +
              "JOIN c.employeeId e " +
              "JOIN c.accountId a " +
              "WHERE e.teamId = :teamId " +
              "AND a.accountName LIKE %:accountName%")
    Page<ContractsEntity> findByAccountNameLikeTeam(@Param("accountName") String accountName,
                                                   @Param("teamId") Team teamId,
                                                   Pageable pageable);
    @Query("SELECT c FROM ContractsEntity c " +
            "JOIN c.employeeId e " +
            "WHERE e.teamId = :teamId ")
    Page<ContractsEntity> findByTeamId(
            @Param("teamId") Team teamId,
            Pageable pageable);

    // 상태 수 세기
    @Query("SELECT CAST(c.contractStatus AS string), COUNT(c) FROM ContractsEntity c GROUP BY c.contractStatus")
    List<Object[]> countContractsByStatusManager();

    @Query("SELECT CAST(c.contractStatus AS string), COUNT(c) " +
            "FROM ContractsEntity c " +
            "JOIN c.employeeId e " +  // Employee와 JOIN
            "WHERE e.teamId = :teamId " + // employeeId 조건 추가
            "GROUP BY c.contractStatus")
    List<Object[]> countContractsByStatusTeam(@Param("teamId") Team teamId);

    // 차트 그래프
    @Query("SELECT MONTH(c.terminationDate), COUNT(c) " +
            "FROM ContractsEntity c " +
            "WHERE YEAR(c.terminationDate) = :year AND c.contractStatus = 'Activated' " +
            "GROUP BY MONTH(c.terminationDate)")
    List<Object[]> getMonthlyContractsManager(@Param("year") int year);

    @Query("SELECT MONTH(c.terminationDate), COUNT(c) " +
            "FROM ContractsEntity c " +
            "JOIN c.employeeId e " +
            "WHERE e.teamId = :teamId " +
            "AND YEAR(c.terminationDate) = :year AND c.contractStatus = 'Activated' " +
            "GROUP BY MONTH(c.terminationDate)")
    List<Object[]> getMonthlyContractsTeam(@Param("year") int year,
                                           @Param("teamId") Team teamId);

    @Modifying
    @Query(value = "UPDATE contracts SET file_data = :fileData, file_name = :fileName, mime_type = :mimeType WHERE contract_id = :contractId", nativeQuery = true)
    void updateFileData(@Param("contractId") Long contractId,
                        @Param("fileData") byte[] fileData,
                        @Param("fileName") String fileName,
                        @Param("mimeType") String mimeType);
}
