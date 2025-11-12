package hsf302.group5.agriculturalproductsmanagement.controller;

import hsf302.group5.agriculturalproductsmanagement.entity.Order;
import hsf302.group5.agriculturalproductsmanagement.entity.User;
import hsf302.group5.agriculturalproductsmanagement.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * TransactionController - Hiển thị lịch sử đơn hàng/giao dịch từ database
 */
@Controller
@RequestMapping("/transaction")
public class TransactionController {

    private final OrderService orderService;

    public TransactionController(OrderService orderService) {
        this.orderService = orderService;
    }

    // [GET] /transaction/history - Xem lịch sử giao dịch
    @GetMapping("/history")
    public String viewTransactionHistory(HttpSession session, Model model) {
        User account = (User) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersByUserId(account.getUserId());
        List<Order> successfulOrders = orders.stream()
                .filter(order -> "Paid".equalsIgnoreCase(order.getPaymentStatus()))
                .collect(Collectors.toList());
        session.removeAttribute("transactionHistory");
        model.addAttribute("orders", successfulOrders);
        model.addAttribute("account", account);
        return "user/transaction-history";
    }
}


