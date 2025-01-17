package com.aivle.project.repository;

import com.aivle.project.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    @Query("SELECT a FROM AccountEntity a ORDER BY a.accountCreatedDate DESC, a.accountId DESC")
    List<AccountEntity> findAllByOrderByAccountCreatedDateDescAccountIdDesc();

    // 특정 ID로 계정 조회
    AccountEntity findByAccountId(Long accountId);

}





