package hsf302.group5.agriculturalproductsmanagement.controller;

import hsf302.group5.agriculturalproductsmanagement.entity.Role;
import hsf302.group5.agriculturalproductsmanagement.entity.User;
import hsf302.group5.agriculturalproductsmanagement.service.RoleServiceImpl;
import hsf302.group5.agriculturalproductsmanagement.service.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    private final UserServiceImpl userService;
    private final RoleServiceImpl roleService;

    public UserController(
            UserServiceImpl userService,
            RoleServiceImpl roleService
    ) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/register")
    public ModelAndView registerPage() {
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("user", new User());
        return mav;
    }

    @PostMapping("/register")
    public String registerPost(
            @Valid User user,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userService.getByPhoneNumber(user.getPhoneNumber()) != null) {
            model.addAttribute("phoneNumberExist", "Phone number is already in use");
            return "register";
        }

        // Get role and check if it exists
        Role userRole = roleService.getByRoleId(2);
        if (userRole == null) {
            model.addAttribute("error", "Default role not found. Please contact administrator.");
            return "register";
        }

        user.setStatus(true);
        user.setRole(userRole);
        userService.addUser(user);

        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        User user = userService.getUserByEmailAndPassword(email, password);

        if (user != null) {
            // Lưu user entity trực tiếp vào session
            session.setAttribute("account", user);

            // Nếu là admin chuyển về trang dashboard
            if (user.getRole().getRoleName().equalsIgnoreCase("admin")) {
                return "redirect:/admin/dashboard";
            }

            // Redirect về trang chủ
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng");
            return "login";
        }
    }

    // [POST/GET] /logout - Đăng xuất
    @GetMapping("/logout")
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
