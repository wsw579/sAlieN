package com.aivle.project.advice;

import com.aivle.project.config.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collection;
import java.util.Iterator;

@ControllerAdvice
public class GlobalModelAttributeAdvice {
//    @ModelAttribute
//    public void addUserIdToModel(Model model) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
//            model.addAttribute("id", authentication.getName()); // 인증된 사용자 ID 전달
//        } else {
//            model.addAttribute("id", null); // 비로그인 상태
//        }
//    }
    @ModelAttribute
    public void addUserIdAndAuthoritiesToModel(HttpSession session, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            // ✅ 인증된 사용자의 정보 가져오기
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) principal;

                // ✅ 사용자 ID 및 이름 추가
                model.addAttribute("id", userDetails.getUsername()); // 로그인 ID
                model.addAttribute("name", userDetails.getName()); // 사용자 이름

                // ✅ 사용자 권한 가져오기
                Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                Iterator<? extends GrantedAuthority> iter = authorities.iterator();
                GrantedAuthority auth = iter.next();
                String role = auth.getAuthority();

                if(role.equals("ROLE_ADMIN")){
                    model.addAttribute("isAdmin", true); // 관리자 여부 추가
                }
            }
        } else {
            model.addAttribute("id", null); // 비로그인 상태
            model.addAttribute("name", null); // 이름도 null 처리
        }
    }

}
