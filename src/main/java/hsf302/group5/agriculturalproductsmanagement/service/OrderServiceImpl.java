package hsf302.group5.agriculturalproductsmanagement.service;

import hsf302.group5.agriculturalproductsmanagement.entity.Order;
import hsf302.group5.agriculturalproductsmanagement.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order createOrder(Order order) {
        boolean isNewOrder = order.getOrderId() == 0;

        if (isNewOrder) {
            order.setCreatedAt(LocalDateTime.now());
            if (order.getOrderStatus() == null || order.getOrderStatus().isBlank()) {
                order.setOrderStatus("Pending");
            }
            if (order.getPaymentStatus() == null || order.getPaymentStatus().isBlank()) {
                order.setPaymentStatus("Unpaid");
            }
        }

        return orderRepository.save(order);
    }

    @Override
    public Optional<Order> getOrderById(int id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) {
        return orderRepository.findByUser_UserId(userId);
    }

    @Override
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @Override
    public Order updateOrderStatus(int orderId, String orderStatus, String paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId));
        order.setOrderStatus(orderStatus);
        order.setPaymentStatus(paymentStatus);
        return orderRepository.save(order);
    }

    @Override
    public void updateOrderStatusById(int orderId, String newStatus) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setOrderStatus(newStatus);
            orderRepository.save(order);
        }
    }

    @Override
    public int countOrdersByStatus(String status) {
        return (int) orderRepository.findAll()
                .stream()
                .filter(o -> o.getOrderStatus().equalsIgnoreCase(status))
                .count();
    }

    @Override
    public List<Order> findByUserEmailContainingIgnoreCase(String email) {
        return orderRepository.findAll()
                .stream()
                .filter(order -> order.getUser().getEmail().toLowerCase().contains(email.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<Order> getPaginatedOrders(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<Order> searchPaginatedOrdersByEmail(String email, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return orderRepository.findByUserEmailContainingIgnoreCase(email, pageable);
    }


}