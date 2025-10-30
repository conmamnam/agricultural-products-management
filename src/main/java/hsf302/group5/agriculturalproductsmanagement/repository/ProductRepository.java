package hsf302.group5.agriculturalproductsmanagement.repository;

import hsf302.group5.agriculturalproductsmanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
