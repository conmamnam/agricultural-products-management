package hsf302.group5.agriculturalproductsmanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {

    @GetMapping({"/", "/index"})
    public String homepage(HttpSession session, Model model) {
        // Lấy thông tin người dùng trong session (nếu có)
        Object account = session.getAttribute("account");
        model.addAttribute("account", account);

        return "index"; // Trả về file templates/index.html
    }
}
