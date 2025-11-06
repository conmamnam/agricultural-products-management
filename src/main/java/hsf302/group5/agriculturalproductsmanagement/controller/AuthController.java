package hsf302.group5.agriculturalproductsmanagement.controller;

import hsf302.group5.agriculturalproductsmanagement.entity.User;
import hsf302.group5.agriculturalproductsmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * AuthController - Xử lý đăng nhập/đăng xuất
 */
@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // [POST] /auth/login - Đăng nhập (có lưu session)
    @PostMapping("/auth/login")
    public String login(@RequestParam String email, 
                       @RequestParam String password, 
                       HttpSession session,
                       Model model) {
        User user = userService.getUserByEmailAndPassword(email, password);
        
        if (user != null) {
            // Lưu user entity trực tiếp vào session
            session.setAttribute("account", user);
            
            // Kiểm tra role và redirect tương ứng
            if (user.getRole() != null && "admin".equalsIgnoreCase(user.getRole().getRoleName())) {
                return "redirect:/admin/dashboard";
            }
            
            // User thường redirect về trang chủ
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng");
            return "login";
        }
    }

    // [POST/GET] /logout - Đăng xuất
    @PostMapping("/logout")
    public String logoutPost(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logoutGet(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}

