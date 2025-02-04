package com.aivle.project.controller;

import com.aivle.project.dto.EmployeeDto;
import com.aivle.project.dto.PaginationDto;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.enums.Dept;
import com.aivle.project.enums.Team;
import com.aivle.project.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final CrudLogsService crudLogsService;
    private final PaginationService paginationService;
    private final SignupService signupService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "user/login";
    }

//    @PostMapping("/signup")
//    public String user(EmployeeDto.Post memberDto, RedirectAttributes redirectAttributes){
//        try {
//            // 계정 생성
//            employeeService.join(memberDto);
//
//            // CRUD 작업 로깅
//            crudLogsService.logCrudOperation("create", "employee", "", "True", "Success");
//
//            return "redirect:/"; // 성공 시 메인 페이지로 이동
//        } catch (Exception e) {
//            // 실패 로그 기록
//            crudLogsService.logCrudOperation("create", "employee", "", "False", "Error: " + e.getMessage());
//
//            // 에러 메시지를 사용자에게 전달
//            redirectAttributes.addFlashAttribute("errorMessage", "ID 생성 중 오류가 발생했습니다. 다시 시도해주세요.");
//
//            return "redirect:/errorPage"; // 에러 발생 시 오류 페이지로 리다이렉트
//        }
//    }
//
//    @GetMapping("/signup")
//    public String signup(){
//        return "user/signup";
//    }

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
    public ResponseEntity<String> passwordEdit(@RequestBody EmployeeDto.Patch employeeDto, RedirectAttributes redirectAttributes) {
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


//    @GetMapping("/api/generateEmployeeId")
//    @ResponseBody // 반환값을 JSON으로 처리
//    public ResponseEntity<Map<String, String>> generateUserId(@RequestParam("year") int year) {
//        Map<String, String> response = new HashMap<>();
//        String employeeId = employeeService.makeNewEmployeeId(year+"");
//        response.put("employeeId", employeeId); // 예시 응답
//        return ResponseEntity.ok(response);
//    }

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

    @PostMapping("/admin/signup")
    public String adminEmployeeSignup(EmployeeDto.Post memberDto, RedirectAttributes redirectAttributes){
        try {
            // 계정 생성
            signupService.registerUser(memberDto);

            // CRUD 작업 로깅
            crudLogsService.logCrudOperation("create", "employee", "", "True", "Success");

            // 성공 메시지를 RedirectAttributes에 저장 (리다이렉트 후에도 유지됨)
            redirectAttributes.addFlashAttribute("message", "계정이 성공적으로 생성되었습니다.");

            return "redirect:/admin/employee-signup"; // 성공 시 admin 계정 가입 페이지로 이동
        } catch (Exception e) {
            // 실패 로그 기록
            crudLogsService.logCrudOperation("create", "employee", "", "False", "Error: " + e.getMessage());

            // 에러 메시지를 사용자에게 전달
            redirectAttributes.addFlashAttribute("errorMessage", "ID 생성 중 오류가 발생했습니다. 다시 시도해주세요.");

            return "redirect:/errorPage"; // 에러 발생 시 오류 페이지로 리다이렉트
        }
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


    @GetMapping("/api/getLoggedInUser")
    @ResponseBody
    public ResponseEntity<EmployeeDto.Get> getLoggedInUser() {
        try {
            EmployeeDto.Get loggedInUser = employeeService.getLoggedInUser();
            return ResponseEntity.ok(loggedInUser);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
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
