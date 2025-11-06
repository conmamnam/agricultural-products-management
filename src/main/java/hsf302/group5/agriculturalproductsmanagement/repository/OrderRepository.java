package hsf302.group5.agriculturalproductsmanagement.repository;

import hsf302.group5.agriculturalproductsmanagement.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser_UserId(int userId);
}