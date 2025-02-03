
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
    // 대소문자 구분없이 계정명으로 조회
    Page<AccountEntity> findByAccountNameContainingIgnoreCase(String keyword, Pageable pageable);


    Page<AccountEntity> findByAccountNameContainingOrAccountTypeContaining(String name, String type, Pageable pageable);
    List<AccountEntity> findByAccountStatus(String status);

    // 상태바

    //  employeeId 별 계정 수
    @Query("SELECT COUNT(a) FROM AccountEntity a WHERE a.employeeId.employeeId = :employeeIdCount")
    Long countAccountsByEmployeeId(@Param("employeeIdCount") String employeeIdCount);

    // 올해 생성된 계정 수를 반환
    @Query("SELECT COUNT(a) FROM AccountEntity a WHERE YEAR(a.accountCreatedDate) = :currentYear")
    long countAccountsCreatedThisYear(@Param("currentYear") int currentYear);

    // 작년에 생성된 계정 수를 반환
    @Query("SELECT COUNT(a) FROM AccountEntity a WHERE YEAR(a.accountCreatedDate) = :lastYear")
    long countAccountsCreatedLastYear(@Param("lastYear") int lastYear);

    // Active , Closed  상태별 count
    @Query("SELECT CAST(a.accountStatus AS string), COUNT(a) FROM AccountEntity  a GROUP BY a.accountStatus")
    List<Object[]> countAccountByStatus();

    // 차트 그래프
    @Query("SELECT MONTH(a.accountCreatedDate), COUNT(a) " +
            "FROM AccountEntity a " +
            "WHERE YEAR(a.accountCreatedDate) = :year AND a.accountStatus = 'Active' " +
            "GROUP BY MONTH(a.accountCreatedDate)")
    List<Object[]> getMonthlyAccount(@Param("year") int year);


    @Query("SELECT a.accountId, a.accountName FROM AccountEntity a")
    List<Object[]> findAllAccountIdAndAccountName();
}


