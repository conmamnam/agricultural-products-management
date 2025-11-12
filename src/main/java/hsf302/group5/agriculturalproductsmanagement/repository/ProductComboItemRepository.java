package hsf302.group5.agriculturalproductsmanagement.repository;

import hsf302.group5.agriculturalproductsmanagement.entity.Product;
import hsf302.group5.agriculturalproductsmanagement.entity.ProductComboItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductComboItemRepository extends JpaRepository<ProductComboItem, Integer> {

    List<ProductComboItem> getAllByCombo_ProductId(int comboProductId);

    ProductComboItem findProductComboItemByComboAndComponent(Product combo, Product component);
}
