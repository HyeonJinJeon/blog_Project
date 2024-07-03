package com.example.blog_project.controller;

import com.example.blog_project.CookieUtil;
import com.example.blog_project.domain.Role;
import com.example.blog_project.domain.User;
import com.example.blog_project.service.BlogService;
import com.example.blog_project.service.RoleService;
import com.example.blog_project.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RoleService roleService;
    private final BlogService blogService;

    //멀티파트로 이미지를 저장할 경로를 초기화시켜놓음
    private static final String UPLOAD_DIR = "/Users/jeonhyeonjin/blog_project/";

    //회원가입
    @GetMapping("/signUp")
    public String signUp(Model model) {
        model.addAttribute("user", new User());
        return "user/signUp";
    }
    @PostMapping("/signUp")
    public String signUp(@ModelAttribute User user,  @RequestParam("profileImage") MultipartFile profileImage, RedirectAttributes redirectAttributes) {
        //@RequestParam은 폼 데이터를 추출할 때 HTTP POST 요청에서 폼 데이터에서 파라미터 값을 가져오는 데 사용됨
        // MultipartFile 프로필 이미지를 post로 받아와서 저장할 수 있도록 함
        String profileImagePath = null;
        if (!profileImage.isEmpty()) {
            try {
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                profileImagePath = UPLOAD_DIR + profileImage.getOriginalFilename();
                profileImage.transferTo(new File(profileImagePath));
                user.setProfileImageUrl(profileImagePath);
            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message", "이미지 업로드 실패!");
                return "redirect:/register";
            }
        }
        Role role = roleService.getRole(1L);
        user.getRoles().add(role);
        userService.createUser(user);
        blogService.createBlog(user.getUsername(), user.getUsername()+".log");
        return "redirect:/signIn";
    }

    //로그인
    @GetMapping("/signIn")
    public String signIn(Model model) {
        model.addAttribute("user", new User());
        return "user/signIn";
    }
    @PostMapping("/signIn")
    public String signIn(@ModelAttribute User user, HttpServletResponse response, HttpServletRequest request) {
        boolean isUser = userService.authenticateByEmail(user.getUsername(), user.getPassword());
        if(isUser){
            //관리자 Role을 가져온다
            Role adminRole = roleService.getRole(2L);
            //관리자 Role이 포함되어 있는지 안되어 있는지 확인한다.
            boolean isAdmin = userService.findUserByUsername(user.getUsername()).getRoles().contains(adminRole);

            //유저 정보를 쿠키에 넣는다 (원래는 세션이 바람직함)
            CookieUtil.create(response, "user", user.getUsername(), 7 * 24 * 60 * 60);
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("user")) {
                        System.out.println(cookie.getValue());
                    }
                }
            }

            if(isAdmin){ //만약 관리자의 Role이 있다면
                return "redirect:/admin";
            }else {
                return "redirect:/blog?username=" + user.getUsername();
            }
        }else{
            return "redirect:/signIn";
        }
    }

    @GetMapping("/main")
    public String main(Model model, HttpServletRequest request) {
        String username = CookieUtil.getValue(request, "user");
        User user = userService.findUserByUsername(username);
        model.addAttribute("user", user);
        return "/main";
    }

//    @GetMapping("/logout")
//    public String logOut(HttpServletResponse response) {
//        Cookie cookie = new Cookie("user", null);
//        cookie.setMaxAge(0);
//        cookie.setPath("/");
//        response.addCookie(cookie);
//
//        return "redirect:/signIn";
//    }
}
