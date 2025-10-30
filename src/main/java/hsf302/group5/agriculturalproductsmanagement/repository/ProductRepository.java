package hsf302.group5.agriculturalproductsmanagement.repository;

import hsf302.group5.agriculturalproductsmanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    List<Product> findByCategory_CategoryId(int categoryId);
    List<Product> findByProductNameContainingIgnoreCase(String keyword);
}
