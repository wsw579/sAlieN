package com.aivle.project.service;

import com.aivle.project.dto.AccountDto;
import com.aivle.project.entity.AccountEntity;
import com.aivle.project.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.time.Year;
import java.util.stream.IntStream;


@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    Logger logger = Logger.getLogger(getClass().getName());

    // Create
    public void createAccount(AccountDto dto) {
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

        if (dto.getParentAccountId() != null) {
            accountEntity.setParentAccount(dto.getParentAccountId());
        } else {
            accountEntity.setParentAccount(null);
        }

        accountRepository.save(accountEntity);

        logger.info("계정 생성 완료: " + accountEntity.getAccountType() + accountEntity.getBusinessType() + accountEntity.getAccountId());
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

    // 계정 삭제
    public void delete(Long accountId) {
        accountRepository.deleteById(accountId);
    }
    public void deleteByIds(List<Long> ids) {
        accountRepository.deleteAllById(ids);
    }

    // 계정 상세 페이지
    public AccountEntity searchAccount(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("error"));
    }


   // 테이블 keyword 검색
    public Page<AccountEntity> searchAccounts(String keyword, PageRequest pageRequest) {
        return accountRepository.findByAccountNameContainingIgnoreCase(keyword, pageRequest);
    }

   // 테이블 조회
    public Page<AccountEntity> readAccount(PageRequest pageRequest) {
        return accountRepository.findAll(pageRequest);
    }


    // 상태바
    // 저장된 모든 계정 수
    public long getTotalAccountCount() {
        return accountRepository.count();
    }

    // 로그인한 employee 담당 계정 수
    public Long getAccountCountForEmployee(String employeeId) {
        return accountRepository.countAccountsByEmployeeId(employeeId);
    }

    // 올해 생성한 계정 수
    public long getAccountsCreatedThisYear() {
        int currentYear = Year.now().getValue();
        return accountRepository.countByYear(currentYear);
    }

    // 작년에 생성한 계정 수
    public long getAccountsCreatedLastYear() {
        int lastYear = Year.now().getValue() - 1;
        return accountRepository.countByYear(lastYear);
    }

    // 그래프
    // Bar and Chart Data
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Map<String, List<Integer>> getBarData() {
        return getYearlyData(true);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public Map<String, List<Integer>> getChartData() {
        return getYearlyData(false);
    }

    private Map<String, List<Integer>> getYearlyData(boolean accumulate) {
        int currentYear = LocalDate.now().getYear();
        int lastYear = currentYear - 1;

        List<Integer> lastYearData = initializeMonthlyData();
        List<Integer> currentYearData = initializeMonthlyData();

        populateMonthlyData(lastYear, lastYearData);
        populateMonthlyData(currentYear, currentYearData);

        if (accumulate) {
            accumulateMonthlyData(lastYearData);
            accumulateMonthlyDataUntilCurrentMonth(currentYearData);
        }

        Map<String, List<Integer>> yearlyData = new HashMap<>();
        yearlyData.put("lastYearData", lastYearData);
        yearlyData.put("currentYearData", currentYearData);

        return yearlyData;
    }

    private List<Integer> initializeMonthlyData() {
        return IntStream.range(0, 12).mapToObj(i -> 0).collect(Collectors.toList());
    }

    private void populateMonthlyData(int year, List<Integer> monthlyData) {
        accountRepository.getMonthlyAccount(year).forEach(row -> {
            int month = ((Number) row[0]).intValue() - 1;
            int count = ((Number) row[1]).intValue();
            monthlyData.set(month, count);
        });


        // 현재 연도인 경우, 현재 월 이후의 데이터를 0으로 설정
        if (year == LocalDate.now().getYear()) {
            int currentMonth = LocalDate.now().getMonthValue();
            for (int i = currentMonth+1; i < monthlyData.size(); i++) {
                monthlyData.set(i, 0);
            }
        }
    }

    private void accumulateMonthlyData(List<Integer> monthlyData) {
        for (int i = 1; i < monthlyData.size(); i++) {
            monthlyData.set(i, monthlyData.get(i) + monthlyData.get(i - 1));
        }
    }

    private void accumulateMonthlyDataUntilCurrentMonth(List<Integer> monthlyData) {
        int currentMonth = LocalDate.now().getMonthValue();
        for (int i = 1; i < currentMonth; i++) {
            monthlyData.set(i, monthlyData.get(i) + monthlyData.get(i - 1));
        }
        // 현재 월 이후의 데이터는 0으로 유지
        for (int i = currentMonth+1; i < monthlyData.size(); i++) {
            monthlyData.set(i, 0);
        }
    }

    // detail  select 를 위한 이름 id 불러오기
    public List<AccountDto> getAllAccountIdsAndNames() {
        List<Object[]> results = accountRepository.findAllAccountIdAndAccountName();
        return results.stream()
                .map(result -> {
                    AccountDto dto = new AccountDto();
                    dto.setAccountId((Long) result[0]);
                    dto.setAccountName((String) result[1]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Optional<AccountEntity> findAccountByName(String companyName) {
        return accountRepository.findByAccountName(companyName);
    }

}







