package com.aivle.project.controller;

import com.aivle.project.dto.EmployeeDto;
import com.aivle.project.dto.PaginationDto;
import com.aivle.project.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final CrudLogsService crudLogsService;
    private final PaginationService paginationService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "user/login";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return "redirect:/";
    }

    @GetMapping("/mypage")
    public String mypage(){
        return "user/mypage";
    }

    @PostMapping("/password-edit")
    @ResponseBody
    public ResponseEntity<String> passwordEdit(@RequestBody @Valid EmployeeDto.Patch employeeDto, RedirectAttributes redirectAttributes) {
        try {
            // 비밀번호 변경 로직
            String employeeId = employeeService.editPassword(employeeDto);

            // CRUD 작업 로깅
            crudLogsService.logCrudOperation("update", "employee", employeeDto.getEmployeeId(), "True", "Success");

            // 비밀번호 변경이 완료된 후, mypage/employeeId로 리다이렉트
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            // CRUD 작업 로깅
            crudLogsService.logCrudOperation("update", "employee", employeeDto.getEmployeeId(), "False", "Error: " + e.getMessage());
            // 오류 메시지 반환
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/password-find")
    public ResponseEntity<String> passwordFind(@RequestBody EmployeeDto.Patch employeeDto, RedirectAttributes redirectAttributes) {
        try {
            String employeeId = employeeService.findPassword(employeeDto);

            // CRUD 작업 로깅
            crudLogsService.logCrudOperation("update", "employee", employeeDto.getEmployeeId(), "True", "Success");
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            // CRUD 작업 로깅
            crudLogsService.logCrudOperation("update", "employee", employeeDto.getEmployeeId(), "False", "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @GetMapping("/mypage/{employeeId}")
    public String mypage(@PathVariable("employeeId") String employeeId, Model model){
        EmployeeDto.Get employee = employeeService.findEmployeeById(employeeId);
        model.addAttribute("employee", employee);
        return "user/mypage";
    }

    // 멤버 페이지
    @GetMapping("/employee-list")
    public String employeeList(@RequestParam Map<String, String> params, Model model){
        int page = Integer.parseInt(params.getOrDefault("page", "0"));
        int size = Integer.parseInt(params.getOrDefault("size", "10"));
        String search = params.getOrDefault("search", "");

        // 서비스에서 페이지 데이터 가져오기
        Page<EmployeeDto.Get> employeePage = employeeService.findAllEmployee(page, size, search);
        long numberOfElements = employeePage.getTotalElements();
        System.out.println(numberOfElements); // 테스트 용
        // 페이지네이션 데이터 생성
        PaginationDto<EmployeeDto.Get> paginationDto = paginationService.createPaginationData(employeePage, page, 5);

        // Model에 데이터 추가
        model.addAttribute("pagination", paginationDto);

        // 검색 및 정렬 데이터
        model.addAttribute("search", search); // 검색어
        return "user/employee_list";
    }

    @GetMapping("/admin/employee-signup")
    public String adminEmployeeSignup(Model model){
        return "admin/signup";
    }

    @GetMapping("/admin/employee-detail/{employeeId}")
    public String employeeDetail(@PathVariable("employeeId") String employeeId, Model model){
        EmployeeDto.Get employee = employeeService.findEmployeeById(employeeId);
        model.addAttribute("employee", employee);
        return "admin/employee_detail";
    }

    @GetMapping("/admin/employee-password-reset/{employeeId}")
    public ResponseEntity<Map<String, Object>> resetPassword(@PathVariable String employeeId) {
        Map<String, Object> response = new HashMap<>();

        boolean resetSuccess = employeeService.resetEmployeePassword(employeeId); // 비밀번호 초기화 로직

        if (resetSuccess) {
            response.put("success", true);
        } else {
            response.put("success", false);
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/employee/delete")
    public ResponseEntity<Void> deleteAccounts(@RequestBody Map<String, List<String>> request) {
        List<String> ids = request.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().build(); // HTTP 400 응답
        }
        try {
            System.out.println("delete Employee Received IDs: " + ids);
            employeeService.deleteByIds(ids);

            // 개별 ID에 대해 성공 로그 기록
            for (String id : ids) {
                crudLogsService.logCrudOperation("delete", "employee", id, "True", "Success");
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error during delete: " + e.getMessage());

            // 개별 ID에 대해 실패 로그 기록
            for (String id : ids) {
                crudLogsService.logCrudOperation("delete", "employee", id, "False", "Error: " + e.getMessage());
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // HTTP 500 응답
        }
    }

    // 부서 정보 추가
    @GetMapping("/employees")
    public String listEmployees(Model model) {
        List<EmployeeDto.GetId> employees = employeeService.getAllEmployeeIdsAndNamesAndDepartmentIds();
        System.out.println("Controller: Employees size = " + employees.size());
        model.addAttribute("employees", employees);
        return "employeeList";

    }

}
