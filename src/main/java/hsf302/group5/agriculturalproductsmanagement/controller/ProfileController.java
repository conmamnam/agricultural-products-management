package hsf302.group5.agriculturalproductsmanagement.controller;

import hsf302.group5.agriculturalproductsmanagement.entity.User;
import hsf302.group5.agriculturalproductsmanagement.service.UserServiceImpl;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfileController {

    private final UserServiceImpl userService;

    public ProfileController(UserServiceImpl userService) {
        this.userService = userService;
    }

    // Hiển thị trang hồ sơ
    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        User account = (User) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        model.addAttribute("account", account);
        return "profile";
    }

    // Hiển thị form cập nhật hồ sơ
    @GetMapping("/profile/update")
    public String updateProfilePage(HttpSession session, Model model) {
        User account = (User) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }
        model.addAttribute("account", account);
        return "profile-update";
    }

    // Lưu cập nhật hồ sơ
    @PostMapping("/profile/update")
    public String updateProfile(@Valid User accountForm,
                                BindingResult result,
                                HttpSession session,
                                Model model) {
        if (result.hasErrors()) {
            model.addAttribute("account", accountForm);
            return "profile-update";
        }

        User currentUser = (User) session.getAttribute("account");
        if (currentUser == null) {
            return "redirect:/login";
        }

        // ✅ Cập nhật thông tin
        currentUser.setFullName(accountForm.getFullName());
        currentUser.setAddress(accountForm.getAddress());
        currentUser.setPhoneNumber(accountForm.getPhoneNumber());

        // ✅ Lưu xuống DB
        userService.updateProfile(currentUser);

        // ✅ Cập nhật lại session với dữ liệu mới từ DB
        session.setAttribute("account", userService.findById(currentUser.getUserId()));

        return "redirect:/profile";
    }
}
