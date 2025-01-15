package com.aivle.project.controller;

import com.aivle.project.dto.AccountDto;
import com.aivle.project.entity.AccountEntity;
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

    // 계정 목록 페이지
    @GetMapping("/account")
    public String account(Model model) {
        List<AccountEntity> accounts = accountService.readAccount();
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
        model.addAttribute("account", account);
        return "account/account_detail";
    }

    // 계정 생성 페이지 (초기값으로 페이지 생성)
    @GetMapping("/account/detail/create")
    public String accountCreate(Model model) {
        AccountEntity account = new AccountEntity();

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

        model.addAttribute("account", account);
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
    public String accountUpdate(@PathVariable("accountId") Long accountId,
                                @ModelAttribute AccountDto accountDto) {
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





        @GetMapping("/parents")
        public ResponseEntity<List<AccountEntity>> getParentAccounts() {
            List<AccountEntity> parentAccounts = accountService.getParentAccounts();
            return ResponseEntity.ok(parentAccounts);
        }

        @PostMapping("/parent")
        public ResponseEntity<AccountEntity> createParentAccount(@RequestBody AccountDto accountDto) {
            AccountEntity newAccount = accountService.createAccount(accountDto);
            return ResponseEntity.ok(newAccount);
    }

}


