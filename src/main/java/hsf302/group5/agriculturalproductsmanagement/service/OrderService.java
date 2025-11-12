package hsf302.group5.agriculturalproductsmanagement.service;

import hsf302.group5.agriculturalproductsmanagement.entity.Order;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);
    Optional<Order> getOrderById(int id);
    List<Order> getOrdersByUserId(int userId);

    List<Order> getAll();
    Order updateOrderStatus(int orderId, String orderStatus, String paymentStatus);
    void updateOrderStatusById(int orderId, String newStatus);
    int countOrdersByStatus(String status);
    List<Order> findByUserEmailContainingIgnoreCase(String email);
    // Lấy tất cả đơn hàng phân trang
    Page<Order> getPaginatedOrders(int pageNo, int pageSize);
    // Tìm kiếm theo email + phân trang
    Page<Order> searchPaginatedOrdersByEmail(String email, int pageNo, int pageSize);


}