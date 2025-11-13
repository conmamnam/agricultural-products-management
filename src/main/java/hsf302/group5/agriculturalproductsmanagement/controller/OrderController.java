package hsf302.group5.agriculturalproductsmanagement.controller;

import hsf302.group5.agriculturalproductsmanagement.entity.Order;
import hsf302.group5.agriculturalproductsmanagement.entity.OrderDetail;
import hsf302.group5.agriculturalproductsmanagement.entity.User;
import hsf302.group5.agriculturalproductsmanagement.service.OrderDetailService;
import hsf302.group5.agriculturalproductsmanagement.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * OrderController - Quản lý đơn hàng (CHỈ USER)
 * Admin không làm trong phần này
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final OrderDetailService orderDetailService;

    public OrderController(OrderService orderService, OrderDetailService orderDetailService) {
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
    }

    // 1. [GET] /order/my-orders - Danh sách đơn hàng của user
    @GetMapping("/my-orders")
    public String myOrders(HttpSession session, Model model) {
        User account = (User) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersByUserId(account.getUserId());
        model.addAttribute("orders", orders);
        model.addAttribute("account", account);
        return "user/order-list";
    }

    // 2. [GET] /order/detail/{id} - Chi tiết đơn hàng
    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable("id") int id,
                             HttpSession session,
                             Model model) {
        User account = (User) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }

        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        // Kiểm tra quyền: user chỉ xem đơn của mình
        if (order.getUser().getUserId() != account.getUserId()) {
            return "redirect:/order/my-orders";
        }

        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(id);
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("account", account);
        return "user/order-detail";
    }

    // 3. [POST] /order/pay/{id} - Thanh toán lại qua VNPay
    @PostMapping("/pay/{id}")
    public String payOrder(@PathVariable("id") int id,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User account = (User) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }

        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        if (order.getUser().getUserId() != account.getUserId()) {
            return "redirect:/order/my-orders";
        }

        if ("Paid".equalsIgnoreCase(order.getPaymentStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn hàng này đã được thanh toán.");
            return "redirect:/order/detail/" + id;
        }

        session.setAttribute("orderId", order.getOrderId());
        long amount = Math.round(order.getTotalPrice());
        return "redirect:/payment/create?orderId=" + order.getOrderId() + "&amount=" + amount;
    }

    // 4. [POST] /order/cancel/{id} - Hủy giao dịch
    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable("id") int id,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User account = (User) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }

        Order order = orderService.getOrderById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        if (order.getUser().getUserId() != account.getUserId()) {
            return "redirect:/order/my-orders";
        }

        if ("Paid".equalsIgnoreCase(order.getPaymentStatus())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn hàng đã thanh toán không thể hủy.");
            return "redirect:/order/detail/" + id;
        }

        orderService.updateOrderStatus(order.getOrderId(), "CANCELLED", "Cancelled");
        redirectAttributes.addFlashAttribute("successMessage", "Đã hủy giao dịch của đơn hàng #" + order.getOrderId());
        return "redirect:/order/detail/" + id;
    }
}