package com.aivle.project.service;

import com.aivle.project.dto.AccountDto;
import com.aivle.project.entity.AccountEntity;
import com.aivle.project.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    // Create
    public void createAccount(AccountDto dto) {
        System.out.println("createAccount 메서드 호출됨");
        System.out.println("DTO 내용: " + dto.toString());

        AccountEntity accountEntity = new AccountEntity();

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

        accountEntity.setEmployeeId(dto.getEmployeeId());
        accountEntity.setParentAccount(dto.getParentAccountId());

        accountRepository.save(accountEntity);

        System.out.println("계정 생성 완료: " + accountEntity.getAccountId());
    }


    // Read: 전체 계정 조회
    public List<AccountEntity> readAccount() {
        return accountRepository.findAllByOrderByAccountCreatedDateDescAccountIdDesc();
    }


    // Update
    // 하위 계정 업데이트 (상위 계정 변경 가능)
    @Transactional
    public void updateAccount(Long accountId, AccountDto dto) {
        AccountEntity accountEntity = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

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

        accountEntity.setEmployeeId(dto.getEmployeeId());
        accountEntity.setParentAccount(dto.getParentAccountId());

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
                .orElseThrow(() -> new IllegalArgumentException("error"));
    }






}







