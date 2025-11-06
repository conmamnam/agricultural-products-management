package hsf302.group5.agriculturalproductsmanagement.controller;

import hsf302.group5.agriculturalproductsmanagement.entity.TransactionHistory;
import hsf302.group5.agriculturalproductsmanagement.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

/**
 * TransactionController - Quản lý lịch sử giao dịch (SESSION-BASED)
 * Hiển thị lịch sử giao dịch từ session
 */
@Controller
@RequestMapping("/transaction")
public class TransactionController {

    // [GET] /transaction/history - Xem lịch sử giao dịch
    @GetMapping("/history")
    public String viewTransactionHistory(HttpSession session, Model model) {
        User account = (User) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }

        // Lấy lịch sử giao dịch từ session
        @SuppressWarnings("unchecked")
        List<TransactionHistory> history = (List<TransactionHistory>) session.getAttribute("transactionHistory");
        
        if (history == null) {
            history = new ArrayList<>();
        }

        model.addAttribute("transactions", history);
        model.addAttribute("account", account);
        return "user/transaction-history";
    }
}


