package com.aivle.project.controller;

import com.aivle.project.dto.EmployeeDto;
import com.aivle.project.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

@Controller
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

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
    public String passwordEdit(EmployeeDto.Patch employeeDto){
        String employeeId = employeeService.editPassword(employeeDto);
        return "redirect:/mypage/" + employeeId;
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

    @GetMapping("/employee-list")
    public String employeeList(Model model){
        List<EmployeeDto.Get> empList = employeeService.findAllEmployee();
        model.addAttribute("employeeList", empList);
        return "user/employee_list";
    }

    @GetMapping("/admin/employee-signup")
    public String adminEmployeeSignup(Model model){
        return "admin/signup";
    }
}
