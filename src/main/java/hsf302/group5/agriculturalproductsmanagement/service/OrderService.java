package hsf302.group5.agriculturalproductsmanagement.service;

import hsf302.group5.agriculturalproductsmanagement.entity.Order;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);
    Optional<Order> getOrderById(int id);

    List<Order> getAll();
    // Các dịch vụ khác liên quan đến order (ví dụ: cập nhật trạng thái)
}