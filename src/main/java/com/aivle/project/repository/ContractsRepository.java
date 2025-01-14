package com.aivle.project.repository;

import com.aivle.project.entity.ContractsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContractsRepository extends JpaRepository<ContractsEntity, Long> {
    ContractsEntity findByContractId(Long contractId);

    @Query("SELECT c FROM ContractsEntity c WHERE c.contractDeleted = false ORDER BY c.startDate DESC, c.contractId DESC")
    List<ContractsEntity> findAllByOrderByCreatedDateAndIdDescActive();

    // 주문을 삭제 상태로 표시하는 메서드
    @Modifying
    @Query("UPDATE ContractsEntity c SET c.contractDeleted = true WHERE c.id = :contractId")
    void softDeleteById(@Param("contractId") Long contractId);

    @Modifying
    @Query("UPDATE ContractsEntity c SET c.contractDeleted = true WHERE c.id = :contractId")
    void softDeleteAllById(@Param("contractId") List<Long> contractId);
}
