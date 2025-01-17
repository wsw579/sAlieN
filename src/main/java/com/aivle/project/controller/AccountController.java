package com.aivle.project.controller;

import com.aivle.project.dto.AccountDto;
import com.aivle.project.entity.AccountEntity;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.repository.AccountRepository;
import com.aivle.project.repository.EmployeeRepository;
import com.aivle.project.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;


    // 계정 목록 페이지
    @GetMapping("/account")
    public String account(Model model) {
        List<AccountEntity> accounts = accountService.readAccount();

        // 데이터가 null이면 빈 리스트로 초기화
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        model.addAttribute("accounts", accounts);
        return "account/account_read";
    }

    // 계정 상세 페이지
    @GetMapping("/account/detail/{accountId}")
    public String accountDetail(@PathVariable Long accountId, Model model) {
        AccountEntity account = accountService.searchAccount(accountId);

        List<AccountEntity> accounts = accountRepository.findAll();
        List<EmployeeEntity> employee = employeeRepository.findAll();
        model.addAttribute("account", account);
        model.addAttribute("employee", employee);
        model.addAttribute("accounts", accounts);

        return "account/account_detail";
    }

    // 계정 생성 페이지 (초기값으로 페이지 생성)
    @GetMapping("/account/detail/create")
    public String accountCreate(Model model) {

        AccountEntity account = new AccountEntity();

        List<AccountEntity> accounts = accountRepository.findAll();
        List<EmployeeEntity> employee = employeeRepository.findAll();

        // 초기값 설정
        account.setAccountName("");
        account.setAccountType("");
        account.setWebsite("");
        account.setContact("");
        account.setBusinessType("");
        account.setAccountManager("");
        account.setAccountDetail("");
        account.setAddress("");
        account.setAccountManagerContact("");
        account.setAccountStatus("");
        account.setAccountCreatedDate(LocalDate.now());

        account.setEmployeeId(new EmployeeEntity());
        account.setParentAccount(new AccountEntity());

        model.addAttribute("account", account);
        model.addAttribute("employee", employee);
        model.addAttribute("accounts", accounts);

        return "account/account_detail";
    }

    // 새 계정 생성
    @PostMapping("/account/detail/create")
    public String accountCreateNew(@ModelAttribute AccountDto accountDto) {
            accountService.createAccount(accountDto);

            return "redirect:/account";
    }


    // 계정 정보 수정
    @PostMapping("/account/detail/{accountId}/update")
    public String accountUpdate(@PathVariable("accountId") Long accountId,  @ModelAttribute AccountDto accountDto) {
        accountService.updateAccount(accountId, accountDto);
        return "redirect:/account/detail/" + accountId;
    }

    // 단일 계정 삭제
    @GetMapping("/account/detail/{accountId}/delete")
    public String accountDeleteDetail(@PathVariable("accountId") Long accountId) {
        accountService.delete(accountId);
        return "redirect:/account";
    }

    // 다중 계정 삭제
    @PostMapping("/account/detail/delete")
    public ResponseEntity<Void> deleteAccounts(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        System.out.println("deleteAccounts Received IDs: " + ids);
        accountService.deleteByIds(ids);
        return ResponseEntity.ok().build();
    }


//    // 상위 계정 목록 조회 API
//    @GetMapping("/parents")
//    public ResponseEntity<List<AccountEntity>> getParentAccounts() {
//        List<AccountEntity> parentAccounts = accountService.getParentAccounts();
//        return ResponseEntity.ok(parentAccounts);
//    }

//    // 데이터 정리 API (관리자용)
//    @PostMapping("/clean-parents")
//    public ResponseEntity<Void> cleanInvalidParentAccounts() {
//        accountService.cleanInvalidParentAccounts();
//        return ResponseEntity.ok().build();
//    }

//        특정 상위 계정의 하위 계정 조회 API
//        @GetMapping("/{parentId}/children")
//        public ResponseEntity<List<AccountEntity>> getChildAccounts(@PathVariable Long parentId) {
//            List<AccountEntity> childAccounts = accountService.getChildAccount(parentId);
//            return ResponseEntity.ok(childAccounts);
//    }
//
//     새 계정 생성 API
//    @PostMapping("/create")
//    public ResponseEntity<AccountEntity> createNew(@RequestBody AccountDto dto) {
//        AccountEntity newAccount = accountService.createAccount(dto);
//        return ResponseEntity.ok(newAccount);
// }

}



