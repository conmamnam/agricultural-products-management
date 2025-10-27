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

    public UserController(UserServiceImpl userService, RoleServiceImpl roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    // ========== ĐĂNG KÝ ==========
    @GetMapping("/register")
    public ModelAndView registerPage() {
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("user", new User());
        return mav;
    }

    @PostMapping("/register")
    public String registerPost(@Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (userService.getByPhoneNumber(user.getPhoneNumber()) != null) {
            model.addAttribute("phoneNumberExist", "Số điện thoại đã được sử dụng");
            return "register";
        }
        user.setStatus(true);
        user.setRole(roleService.getByRoleId(2)); // ROLE_CUSTOMER
        userService.addUser(user);
        model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "redirect:/login";
    }

    // ========== ĐĂNG NHẬP ==========
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String email,
                              @RequestParam String password,
                              HttpSession session,
                              Model model) {
        User user = userService.getUserByEmailAndPassword(email, password);
        if (user != null) {
            session.setAttribute("account", user);
            return "redirect:/index";
        } else {
            model.addAttribute("error", "Sai email hoặc mật khẩu!");
            return "login";
        }
    }

    // ========== ĐĂNG XUẤT ==========
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
