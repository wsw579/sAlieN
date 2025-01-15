package com.aivle.project.service;

import com.aivle.project.dto.AccountDto;
import com.aivle.project.entity.AccountEntity;
import com.aivle.project.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public AccountEntity createAccount(AccountDto dto) {
        AccountEntity accountEntity = new AccountEntity();

        // 기본 정보 설정
        accountEntity.setAccountName(dto.getAccountName());
        accountEntity.setAccountType(dto.getAccountType());
        accountEntity.setWebsite(dto.getWebsite());
        accountEntity.setContact(dto.getContact());
        accountEntity.setBusinessType(dto.getBusinessType());
        accountEntity.setAccountManager(dto.getAccountManager());
        accountEntity.setAccountDetail(dto.getAccountDetail());
        accountEntity.setAddress(dto.getAddress());
        accountEntity.setAccountManagerContact(dto.getAccountManagerContact());
        accountEntity.setAccountStatus(dto.getAccountStatus());
        accountEntity.setAccountCreatedDate(LocalDate.now());

        // 상위 계정 설정
        if (dto.getParentAccountId() != null) {
            AccountEntity parentAccount = accountRepository.findById(dto.getParentAccountId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent account not found"));
            accountEntity.setParentAccount(parentAccount);
        }

        return accountRepository.save(accountEntity);
    }



    private void setBasicAccountInfo(AccountEntity entity, AccountDto dto) {
        entity.setAccountName(dto.getAccountName());
        entity.setAccountType(dto.getAccountType());
        entity.setWebsite(dto.getWebsite());
        entity.setContact(dto.getContact());
        entity.setBusinessType(dto.getBusinessType());
        entity.setAccountManager(dto.getAccountManager());
        entity.setAccountDetail(dto.getAccountDetail());
        entity.setAddress(dto.getAddress());
        entity.setAccountManagerContact(dto.getAccountManagerContact());
        entity.setAccountStatus(dto.getAccountStatus());
        entity.setAccountCreatedDate(LocalDate.now());
    }



    // Read
    public List<AccountEntity> readAccount() {
        return accountRepository.findAllByOrderByCreatedDateAndIdDesc();
    }

    // Update
    // 하위 계정 업데이트 (상위 계정 변경 가능)
    public void updateAccount(Long accountId, AccountDto dto) {
        AccountEntity accountEntity = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        // 기본 필드 업데이트
        accountEntity.setAccountName(dto.getAccountName());
        accountEntity.setAccountType(dto.getAccountType());
        accountEntity.setWebsite(dto.getWebsite());
        accountEntity.setContact(dto.getContact());
        accountEntity.setBusinessType(dto.getBusinessType());
        accountEntity.setAccountManager(dto.getAccountManager());
        accountEntity.setAccountDetail(dto.getAccountDetail());
        accountEntity.setAddress(dto.getAddress());
        accountEntity.setAccountManagerContact(dto.getAccountManagerContact());
        accountEntity.setAccountStatus(dto.getAccountStatus());

        // 상위 계정 정보가 변경되었는지 확인
        if (dto.getParentAccountId() != null) {
            // 현재 상위 계정이 없거나 다른 상위 계정으로 변경되는 경우
            if (accountEntity.getParentAccount() == null ||
                    !dto.getParentAccountId().equals(accountEntity.getParentAccount().getAccountId())) {
                AccountEntity newParentAccount = accountRepository.findById(dto.getParentAccountId())
                        .orElseThrow(() -> new IllegalArgumentException("Parent Account not found"));
                accountEntity.setParentAccount(newParentAccount);
            }
        } else {
            // 상위 계정을 제거하는 경우
            accountEntity.setParentAccount(null);
        }

        accountRepository.save(accountEntity);
    }

    // Delete
    public void delete(Long accountId) {
        accountRepository.deleteById(accountId);
    }

    public void deleteByIds(List<Long> ids) {
        accountRepository.deleteAllById(ids);
    }

    // Search
    public AccountEntity searchAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(()->new IllegalArgumentException("error"));
    }

    // 상위 계정 목록 조회
    public List<AccountEntity> getParentAccounts() {
        return accountRepository.findByParentAccountIsNull();
    }


}





