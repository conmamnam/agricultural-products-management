package hsf302.group5.agriculturalproductsmanagement.service;

import hsf302.group5.agriculturalproductsmanagement.entity.ProductComboItem;
import hsf302.group5.agriculturalproductsmanagement.repository.ProductComboItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductComboItemImpl implements ProductComboItemService {

    private final ProductComboItemRepository productComboItemRepository;

    public ProductComboItemImpl(ProductComboItemRepository productComboItemRepository) {
        this.productComboItemRepository = productComboItemRepository;
    }

    @Override
    public List<ProductComboItem> getItemsByComboId(int comboId) {
        return productComboItemRepository.getAllByCombo_ProductId(comboId);
    }
}
