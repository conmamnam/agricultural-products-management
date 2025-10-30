package hsf302.group5.agriculturalproductsmanagement.service;

import hsf302.group5.agriculturalproductsmanagement.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    Optional<Product> getProductById(int id);
    Product saveProduct(Product product);
    void deleteProduct(int id);
    List<Product> getProductsByCategoryId(int categoryId);
    List<Product> searchProducts(String keyword);
}
