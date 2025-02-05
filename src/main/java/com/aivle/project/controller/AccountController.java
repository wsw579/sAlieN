package com.aivle.project.controller;

import com.aivle.project.dto.AccountDto;
import com.aivle.project.entity.AccountEntity;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.repository.AccountRepository;
import com.aivle.project.repository.EmployeeRepository;
import com.aivle.project.service.AccountService;
import com.aivle.project.service.CrudLogsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final EmployeeRepository employeeRepository;
    private final CrudLogsService crudLogsService;
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @GetMapping("/account")
    public String account(Model model,
                          @RequestParam(value="page", defaultValue="1") int page,
                          @RequestParam(value="keyword", required=false) String keyword) {

        // 페이징 정보 가져오기
        Page<AccountEntity> paging = getPagingInfo(page, keyword);

        // 현재 로그인한 직원의 계정 수 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmployeeId = authentication.getName();


        // 페이징 관련 변수 설정
        int totalPages = paging.getTotalPages(); // 총 페이지
        int currentPage = page; // 현재 페이지 url 에서 page 변수 가져오기
        int startPage = getStartPage(currentPage);
        int endPage = getEndPage(currentPage, totalPages);
        int nextPage = getNextPage(currentPage, totalPages);
        long totalAccounts = accountService.getTotalAccountCount();
        long accountsThisYear = accountService.getAccountsCreatedThisYear();
        long accountsLastYear = accountService.getAccountsCreatedLastYear();
        long currentEmployeeAccountCount = accountService.getAccountCountForEmployee(currentEmployeeId);

        // 페이지 번호 리스트 생성
        List<Map<String, Object>> pageNumbers = getPageNumbers(startPage, endPage, currentPage);


        // 모델에 데이터 추가
        addDataToModel(model, paging, pageNumbers, currentPage, nextPage, totalPages, keyword, totalAccounts,
                currentEmployeeId, currentEmployeeAccountCount , accountsThisYear , accountsLastYear);

        return "account/account_read";
    }

    // 모델에 데이터 추가
    private void addDataToModel(Model model, Page<AccountEntity> paging,
                                List<Map<String, Object>> pageNumbers,
                                int currentPage, int nextPage, int totalPages, String keyword,
                                long totalAccounts, String currentEmployeeId, Long currentEmployeeAccountCount ,
                                long accountsThisYear , long accountsLastYear){

        model.addAttribute("paging", paging);
        model.addAttribute("pageNumbers", pageNumbers);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("nextPage", nextPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("totalAccounts", totalAccounts);
        model.addAttribute("currentEmployeeId", currentEmployeeId);
        model.addAttribute("currentEmployeeAccountCount", currentEmployeeAccountCount);
        model.addAttribute("accountsThisYear", accountsThisYear);
        model.addAttribute("accountsLastYear", accountsLastYear);
    }


    // 페이지 리스트 받아오기
    private List<Map<String, Object>> getPageNumbers(int startPage, int endPage, int currentPage) {
        return IntStream.rangeClosed(startPage, endPage)
                .mapToObj(pageNum -> {
                    Map<String, Object> pageInfo = new HashMap<>();
                    pageInfo.put("number", pageNum);
                    pageInfo.put("isCurrentPage", pageNum == currentPage);
                    return pageInfo;
                })
                .collect(Collectors.toList());

    }

    // 다음 페이지로 이동
    private int getNextPage(int currentPage, int totalPages) {
        return Math.min(currentPage + 1, totalPages);
    }

    // 시작페이지
    private int getStartPage(int currentPage) {
        return Math.max(1, currentPage - 5);
    }

    // 마지막 페이지
    private int getEndPage(int currentPage, int totalPages) {
        return Math.min(currentPage + 5, totalPages);
    }

    // 검색 keyword 유무 별 테이블 목록 조회
    private Page<AccountEntity> getPagingInfo(int page, String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return accountService.searchAccounts(keyword, PageRequest.of(page - 1, 10));
        } else {
            return accountService.readAccount(PageRequest.of(page - 1, 10));
        }
    }

    // 계정 상세 페이지
    @GetMapping("/account/detail/{accountId}")
    public String accountDetail(@PathVariable Long accountId, Model model) {
        AccountEntity account = accountService.searchAccount(accountId);

        List<EmployeeEntity> employee = employeeRepository.findAll();
        // 상세페이지에서 상위계정 조회시 Active 상태만 조회 됨
        List<AccountEntity> parent = accountRepository.findByAccountStatus("Active");

        model.addAttribute("account", account);
        model.addAttribute("employee", employee);
        model.addAttribute("parent", parent);

        System.out.println("Business Type: " + account.getBusinessType());
        System.out.println("Account Type: " + account.getAccountType());

        return "account/account_detail";
    }

    // 계정 생성 페이지 (초기값으로 페이지 생성)
    @GetMapping("/account/detail/create")
    public String accountCreate(Model model) {

        AccountEntity account = new AccountEntity();

        List<EmployeeEntity> employee = employeeRepository.findAll();
        List<AccountEntity> parent = accountRepository.findByAccountStatus("Active");

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
        model.addAttribute("parent", parent);

        return "account/account_detail";
    }

    // 새 계정 생성
    @PostMapping("/account/detail/create")
    public String accountCreateNew(@ModelAttribute @Valid AccountDto accountDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 유효성 검사 실패 시 에러 메시지 출력
            return "account/account_detail"; // 에러가 있으면 폼으로 다시 이동
        }

        accountService.createAccount(accountDto);

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("create", "parent", "", "True", "Success");

        return "redirect:/account";
    }


    // 계정 정보 수정
    @PostMapping("/account/detail/{accountId}/update")
    public String accountUpdate(@PathVariable("accountId") Long accountId,  @ModelAttribute @Valid AccountDto accountDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            // 유효성 검사 실패 시 에러 메시지 출력
            return "account/account_detail"; // 에러가 있으면 폼으로 다시 이동
        }

        accountService.updateAccount(accountId, accountDto);

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("update", "parent", "", "True", "Success");

        return "redirect:/account";
    }

    // 단일 계정 삭제
    @GetMapping("/account/detail/{accountId}/delete")
    public String accountDeleteDetail(@PathVariable("accountId") Long accountId) {
        accountService.delete(accountId);

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("delete", "parent", "", "True", "Success");

        return "redirect:/account";
    }

    // 다중 계정 삭제
    @PostMapping("/account/detail/delete")
    public ResponseEntity<Void> deleteAccounts(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        logger.info("deleteAccounts Received IDs: {} " , ids);
        accountService.deleteByIds(ids);

        // CRUD 작업 로깅
        crudLogsService.logCrudOperation("delete", "parent", "[]", "True", "Success");

        return ResponseEntity.ok().build();
    }


    @GetMapping("/account/bar-data")
    public ResponseEntity<Map<String, List<Integer>>> getBarData() {
        return ResponseEntity.ok(accountService.getBarData());
    }

    @GetMapping("/account/chart-data")
    public ResponseEntity<Map<String, List<Integer>>> getChartData() {
        return ResponseEntity.ok(accountService.getChartData());
    }

}



