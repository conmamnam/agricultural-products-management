package hsf302.group5.agriculturalproductsmanagement.controller;

import hsf302.group5.agriculturalproductsmanagement.entity.Order;
import hsf302.group5.agriculturalproductsmanagement.entity.OrderDetail;
import hsf302.group5.agriculturalproductsmanagement.entity.Product;
import hsf302.group5.agriculturalproductsmanagement.service.OrderService;
import hsf302.group5.agriculturalproductsmanagement.service.PaymentService;
import hsf302.group5.agriculturalproductsmanagement.service.OrderDetailService;
import hsf302.group5.agriculturalproductsmanagement.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Optional;
import java.util.List;

@Controller
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;
    private final ProductService productService;

    public PaymentController(PaymentService paymentService,
                             OrderService orderService,
                             OrderDetailService orderDetailService,
                             ProductService productService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
        this.productService = productService;
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
    public String handleVnPayReturn(@RequestParam Map<String, String> params,
                                    HttpSession session,
                                    Model model) {
        Map<String, String> result = paymentService.handleVnPayCallback(params);

        if ("success".equals(result.get("status"))) {
            // Lấy orderId từ vnp_TxnRef (format: orderId_timestamp)
            String txnRef = result.get("orderId");
            int orderId = Integer.parseInt(txnRef.split("_")[0]);
            
            // Cập nhật Order trong database
            Optional<Order> orderOpt = orderService.getOrderById(orderId);
            if (orderOpt.isPresent()) {
                orderService.updateOrderStatus(orderId, "Confirmed", "Paid");

                // Giảm số lượng tồn kho của từng sản phẩm sau khi thanh toán thành công
                List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);
                for (OrderDetail orderDetail : orderDetails) {
                    Product product = orderDetail.getProduct();
                    if (product != null) {
                        int currentStock = product.getStock();
                        int quantityPurchased = orderDetail.getQuantity();
                        int updatedStock = Math.max(0, currentStock - quantityPurchased);
                        product.setStock(updatedStock);
                        productService.saveProduct(product);
                    }
                }
                
                session.removeAttribute("orderId");
            }

            model.addAttribute("message", "Thanh toán thành công!");
            model.addAttribute("orderId", txnRef);
            model.addAttribute("amount", result.get("amount"));
            model.addAttribute("bankCode", result.get("bankCode"));
            return "payment_success";
        } else {
            model.addAttribute("message", "Thanh toán thất bại!");
            return "payment_fail";
        }
    }

    // 3. [GET] /payment/paymentform.html
    @GetMapping("/payment/paymentform.html")
    public String showPaymentForm() {
        return "payment_form"; // View: payment_form.html (trang nhận thông tin)
    }

}