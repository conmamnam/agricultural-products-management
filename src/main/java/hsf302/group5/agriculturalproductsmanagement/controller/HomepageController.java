package hsf302.group5.agriculturalproductsmanagement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {

    // ✅ Mapping cho "/" - "/index" - "/home"
    @GetMapping({"/", "/index", "/home"})
    public String homepage(HttpSession session, Model model) {
        Object account = session.getAttribute("account");
        model.addAttribute("account", account);
        return "index"; // tên file trong templates, KHÔNG có .html
    }

    // ✅ Trang Giới thiệu
    @GetMapping("/about-us")
    public String aboutUs(HttpSession session, Model model) {
        Object account = session.getAttribute("account");
        model.addAttribute("account", account);
        return "about-us"; // cũng nằm trong /templates
    }
}
