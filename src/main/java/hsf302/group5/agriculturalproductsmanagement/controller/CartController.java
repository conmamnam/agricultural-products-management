package hsf302.group5.agriculturalproductsmanagement.controller;

import hsf302.group5.agriculturalproductsmanagement.entity.*;
import hsf302.group5.agriculturalproductsmanagement.service.OrderDetailService;
import hsf302.group5.agriculturalproductsmanagement.service.OrderService;
import hsf302.group5.agriculturalproductsmanagement.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * CartController - Quản lý giỏ hàng
 * Giỏ hàng lưu trong SESSION (không lưu database)
 * Session timeout: 1 giờ
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductService productService;
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;

    public CartController(ProductService productService, OrderService orderService, OrderDetailService orderDetailService) {
        this.productService = productService;
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
    }

    // 1. [GET] /cart - Xem giỏ hàng
    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        User account = (User) session.getAttribute("account");
        List<CartItem> cart = getCart(session);

        double total = cart.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        model.addAttribute("account", account);
        
        // Hiển thị thông báo lỗi/thành công (nếu có)
        String errorMessage = (String) session.getAttribute("errorMessage");
        String successMessage = (String) session.getAttribute("successMessage");
        
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }
        
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
            session.removeAttribute("successMessage");
        }
        
        return "user/cart";
    }

    // 2. [POST] /cart/add - Thêm sản phẩm vào giỏ
    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") int productId,
                           @RequestParam(value = "quantity", defaultValue = "1") int quantity,
                           HttpSession session,
                           Model model) {
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));

        // Kiểm tra số lượng không được <= 0
        if (quantity <= 0) {
            session.setAttribute("errorMessage", "Số lượng phải lớn hơn 0!");
            return "redirect:/cart";
        }

        List<CartItem> cart = getCart(session);

        // Kiểm tra xem sản phẩm đã có trong giỏ chưa
        CartItem existingItem = cart.stream()
                .filter(item -> item.getProduct().getProductId() == productId)
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            // Đã có → Tăng số lượng
            int newQuantity = existingItem.getQuantity() + quantity;
            
            // Kiểm tra không vượt quá tồn kho
            if (newQuantity > product.getStock()) {
                session.setAttribute("errorMessage", 
                    "Không thể thêm! Sản phẩm '" + product.getProductName() + 
                    "' chỉ còn " + product.getStock() + " sản phẩm trong kho.");
                return "redirect:/cart";
            }
            
            existingItem.setQuantity(newQuantity);
        } else {
            // Chưa có → Kiểm tra số lượng trước khi thêm
            if (quantity > product.getStock()) {
                session.setAttribute("errorMessage", 
                    "Không thể thêm! Sản phẩm '" + product.getProductName() + 
                    "' chỉ còn " + product.getStock() + " sản phẩm trong kho.");
                return "redirect:/cart";
            }
            
            cart.add(new CartItem(product, quantity));
        }

        session.setAttribute("cart", cart);
        session.setAttribute("successMessage", "Đã thêm sản phẩm vào giỏ hàng!");
        return "redirect:/cart";
    }

    // 3. [POST] /cart/update - Cập nhật số lượng
    @PostMapping("/update")
    public String updateCart(@RequestParam("productId") int productId,
                            @RequestParam("quantity") int quantity,
                            HttpSession session) {
        List<CartItem> cart = getCart(session);

        CartItem cartItem = cart.stream()
                .filter(item -> item.getProduct().getProductId() == productId)
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            Product product = cartItem.getProduct();
            
            // Kiểm tra số lượng phải > 0
            if (quantity <= 0) {
                session.setAttribute("errorMessage", "Số lượng phải lớn hơn 0!");
                return "redirect:/cart";
            }
            
            // Kiểm tra không vượt quá tồn kho
            if (quantity > product.getStock()) {
                session.setAttribute("errorMessage", 
                    "Sản phẩm '" + product.getProductName() + 
                    "' chỉ còn " + product.getStock() + " sản phẩm trong kho. Không thể cập nhật!");
                return "redirect:/cart";
            }
            
            cartItem.setQuantity(quantity);
            session.setAttribute("successMessage", "Đã cập nhật số lượng!");
        }

        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // 4. [POST] /cart/remove - Xóa sản phẩm khỏi giỏ
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("productId") int productId,
                                HttpSession session) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getProduct().getProductId() == productId);
        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // 5. [GET] /cart/checkout - Trang checkout
    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        User account = (User) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }

        List<CartItem> cart = getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        double total = cart.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();

        model.addAttribute("cart", cart);
        model.addAttribute("total", total);
        model.addAttribute("account", account);
        return "user/checkout";
    }

    // 6. [POST] /cart/place-order - Đặt hàng (VNPay)
    @PostMapping("/place-order")
    public String placeOrder(@RequestParam("paymentMethod") String paymentMethod,
                            HttpSession session) {
        User account = (User) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }

        List<CartItem> cart = getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        // Tính tổng tiền
        double totalPrice = cart.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();

        // Tạo Order
        Order order = new Order();
        order.setUser(account);
        order.setTotalPrice(totalPrice);
        order.setPaymentMethod(paymentMethod);
        order.setOrderStatus("Pending");
        order.setPaymentStatus("Unpaid");

        Order savedOrder = orderService.createOrder(order);

        // Tạo OrderDetails
        for (CartItem item : cart) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(savedOrder);
            orderDetail.setProduct(item.getProduct());
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setPrice(item.getProduct().getPrice());
            orderDetailService.saveOrderDetail(orderDetail);
        }

            // Lưu transaction vào session (lịch sử giao dịch)
            TransactionHistory transaction = new TransactionHistory(
                    savedOrder.getOrderId(),
                    totalPrice,
                    paymentMethod,
                    savedOrder.getOrderStatus(),
                    savedOrder.getPaymentStatus(),
                    LocalDateTime.now()
            );
            addTransactionToHistory(session, transaction);

            // Xóa giỏ hàng khỏi session
            session.removeAttribute("cart");

            // Nếu chọn VNPay → Redirect đến trang thanh toán VNPay
            if ("VNPay".equals(paymentMethod)) {
                // Lưu orderId vào session để xử lý sau khi thanh toán
                session.setAttribute("orderId", savedOrder.getOrderId());
                return "redirect:/payment/create?orderId=" + savedOrder.getOrderId() + "&amount=" + (long)totalPrice;
            }

            // Nếu không phải VNPay → Redirect đến trang chi tiết đơn hàng
            return "redirect:/order/detail/" + savedOrder.getOrderId();
    }

        /**
         * Helper method: Lấy giỏ hàng từ session
         * Nếu chưa có → Tạo mới
         */
        @SuppressWarnings("unchecked")
        private List<CartItem> getCart(HttpSession session) {
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
                session.setAttribute("cart", cart);
            }
            return cart;
        }

        /**
         * Helper method: Thêm transaction vào lịch sử (session)
         */
        @SuppressWarnings("unchecked")
        private void addTransactionToHistory(HttpSession session, TransactionHistory transaction) {
            List<TransactionHistory> history = (List<TransactionHistory>) session.getAttribute("transactionHistory");
            if (history == null) {
                history = new ArrayList<>();
            }
            // Thêm transaction mới vào đầu list (giao dịch mới nhất hiển thị đầu tiên)
            history.add(0, transaction);
            session.setAttribute("transactionHistory", history);
        }
    }

