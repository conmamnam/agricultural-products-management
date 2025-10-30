package hsf302.group5.agriculturalproductsmanagement.service;

import hsf302.group5.agriculturalproductsmanagement.entity.Order;
import hsf302.group5.agriculturalproductsmanagement.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order createOrder(Order order) {
        // Thiết lập các giá trị mặc định trước khi lưu
        order.setCreatedAt(LocalDateTime.now());
        // Giả định trạng thái ban đầu
        order.setOrderStatus("Pending");
        order.setPaymentStatus("Unpaid");

        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> getOrderById(int id) {
        return orderRepository.findById(id);
    }
}