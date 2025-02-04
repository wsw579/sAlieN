
package com.aivle.project.repository;

import com.aivle.project.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    @Query("SELECT a FROM AccountEntity a ORDER BY a.accountCreatedDate DESC, a.accountId ")
    // 페이지네이션
    //  모든 계정 조회
    @NonNull
    Page<AccountEntity> findAll(@NonNull Pageable pageable);
    // keyword 검색 시 대소문자 구분없이 계정명으로 조회
    Page<AccountEntity> findByAccountNameContainingIgnoreCase(String keyword, Pageable pageable);
    //  계정 상태
    List<AccountEntity> findByAccountStatus(String accountStatus);

    // 상태바
    //  employeeId 별 계정 수
    @Query("SELECT COUNT(a) FROM AccountEntity a WHERE a.employeeId.employeeId = :employeeId")
    Long countAccountsByEmployeeId(@Param("employeeId") String employeeId);

    // 그래프
    @Query("SELECT COUNT(a) FROM AccountEntity a WHERE YEAR(a.accountCreatedDate) = :year ")
    long countByYear(@Param("year") int year);

    @Query("SELECT MONTH(a.accountCreatedDate), COUNT(a) " +
            "FROM AccountEntity a " +
            "WHERE YEAR(a.accountCreatedDate) = :year AND a.accountStatus = 'Active' " +
            "GROUP BY MONTH(a.accountCreatedDate)")
    List<Object[]> getMonthlyAccount(@Param("year") int year);


    @Query("SELECT a.accountId, a.accountName FROM AccountEntity a")
    List<Object[]> findAllAccountIdAndAccountName();



    //    Page<AccountEntity> findByAccountNameContainingOrAccountTypeContaining(String name, String type, Pageable pageable);
}


