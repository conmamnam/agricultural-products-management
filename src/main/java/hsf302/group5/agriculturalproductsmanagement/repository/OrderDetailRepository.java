package hsf302.group5.agriculturalproductsmanagement.repository;

import hsf302.group5.agriculturalproductsmanagement.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrder_OrderId(int orderId);
}