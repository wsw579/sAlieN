package com.aivle.project.repository;

import com.aivle.project.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    AccountEntity findByAccountId(Long accountId);

    @Query("SELECT a FROM AccountEntity a ORDER BY a.accountCreatedDate DESC, a.accountId DESC")
    List<AccountEntity> findAllByOrderByCreatedDateAndIdDesc();
    List<AccountEntity> findByParentAccountIsNull();

}


