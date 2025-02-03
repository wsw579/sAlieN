package com.aivle.project.controller;

import com.aivle.project.dto.EmployeeDto;
import com.aivle.project.dto.PaginationDto;
import com.aivle.project.entity.EmployeeEntity;
import com.aivle.project.entity.OpportunitiesEntity;
import com.aivle.project.entity.OrdersEntity;
import com.aivle.project.enums.Dept;
import com.aivle.project.enums.Team;
import com.aivle.project.service.CrudLogsService;
import com.aivle.project.service.EmployeeService;
import com.aivle.project.service.PaginationService;
import com.aivle.project.service.ProductsService;
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

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "user/login";
    }

    @PostMapping("/signup")
    public String user(EmployeeDto.Post memberDto){
        employeeService.join(memberDto);
        return "redirect:/";
    }

    @GetMapping("/signup")
    public String signup(){
        return "user/signup";
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
    public ResponseEntity<String> passwordEdit(@RequestBody EmployeeDto.Patch employeeDto) {
        try {
            // 비밀번호 변경 로직
            String employeeId = employeeService.editPassword(employeeDto);

            // 비밀번호 변경이 완료된 후, mypage/employeeId로 리다이렉트
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            // 오류 메시지 반환
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/password-find")
    public ResponseEntity<String> passwordFind(@RequestBody EmployeeDto.Patch employeeDto) {
        try {
            String employeeId = employeeService.findPassword(employeeDto);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    @GetMapping("/mypage/{employeeId}")
    public String mypage(@PathVariable("employeeId") String employeeId, Model model){
        EmployeeDto.Get employee = employeeService.findEmployeeById(employeeId);
        model.addAttribute("employee", employee);
        return "user/mypage";
    }


    @GetMapping("/api/generateEmployeeId")
    @ResponseBody // 반환값을 JSON으로 처리
    public ResponseEntity<Map<String, String>> generateUserId(@RequestParam("year") int year) {
        Map<String, String> response = new HashMap<>();
        String employeeId = employeeService.makeNewEmployeeId(year+"");
        response.put("employeeId", employeeId); // 예시 응답
        return ResponseEntity.ok(response);
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

    @PostMapping("/admin/signup")
    public String adminEmployeeSignup(EmployeeDto.Post memberDto){
        employeeService.join(memberDto);
        return "redirect:/admin/employee-signup";
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
        try {
            List<String> ids = request.get("ids");
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest().build(); // HTTP 400 응답
            }
            System.out.println("delete Employee Received IDs: " + ids);
            employeeService.deleteByIds(ids);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error during delete: " + e.getMessage());
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
