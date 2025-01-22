package com.aivle.project.repository;

import com.aivle.project.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    @Query("SELECT a FROM AccountEntity a ORDER BY a.accountCreatedDate DESC, a.accountId DESC")
    Page<AccountEntity> findAll(Pageable pageable);          //  페이지네이션
    Page<AccountEntity> findByAccountNameContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("SELECT a.accountId, a.accountName FROM AccountEntity a")
    List<Object[]> findAllAccountIdAndAccountName();

    // 특정 ID로 계정 조회
    AccountEntity findByAccountId(Long accountId);

    // 상태바 employeeId 연결
    @Query("SELECT COUNT(a) FROM AccountEntity a WHERE a.employeeId.employeeId = :employeeId")
    Long countAccountsByEmployeeId(@Param("employeeId") String employeeId);
}
