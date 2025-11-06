package hsf302.group5.agriculturalproductsmanagement.controller;

import hsf302.group5.agriculturalproductsmanagement.entity.Order;
import hsf302.group5.agriculturalproductsmanagement.service.OrderService;
import hsf302.group5.agriculturalproductsmanagement.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    // 1. [POST] /payment/create
    @PostMapping("/payment/create")
    public String createPayment(
            HttpServletRequest request
    ) {
        int orderId = 1;
        long amount = 150000;
        String orderInfo = "Thanh toan don hang " + orderId;

        String paymentUrl = paymentService.createVnPayPayment(orderId, amount, orderInfo, request);
        return "redirect:" + paymentUrl;
    }

    // Tạo thanh toán từ Cart
    @GetMapping("/payment/create")
    public String createPaymentFromCart(
            @RequestParam("orderId") int orderId,
            @RequestParam("amount") long amount,
            HttpServletRequest request
    ) {
        String orderInfo = "Thanh toan don hang #" + orderId;
        String paymentUrl = paymentService.createVnPayPayment(orderId, amount, orderInfo, request);
        return paymentUrl;
    }

    // 2. [GET] /payment/vnpayReturn
    @GetMapping("/payment/vnpayReturn")
    public String handleVnPayReturn(@RequestParam Map<String, String> params, Model model) {
        Map<String, String> result = paymentService.handleVnPayCallback(params);

        if ("success".equals(result.get("status"))) {

            model.addAttribute("message", "Thanh toán thành công!");
            model.addAttribute("orderId", result.get("orderId"));
            model.addAttribute("amount", result.get("amount"));
            model.addAttribute("bankCode", result.get("bankCode"));
            return "payment_success"; // View: payment_success.html
        } else {
            model.addAttribute("message", "Thanh toán thất bại!");
            return "payment_fail"; // View: payment_fail.html
        }
    }

    // 3. [GET] /payment/paymentform.html
    @GetMapping("/payment/paymentform.html")
    public String showPaymentForm() {
        return "payment_form"; // View: payment_form.html (trang nhận thông tin)
    }
}