package hsf302.group5.agriculturalproductsmanagement.service;

import hsf302.group5.agriculturalproductsmanagement.entity.Product;
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
    public ProductComboItem getById(int id) {
        return productComboItemRepository.findById(id).orElse(null);
    }

    @Override
    public List<ProductComboItem> getItemsByComboId(int comboId) {
        return productComboItemRepository.getAllByCombo_ProductId(comboId);
    }

    @Override
    public void save(ProductComboItem productComboItem) {
        productComboItemRepository.save(productComboItem);
    }

    @Override
    public ProductComboItem getByComboAndComponent(Product combo, Product component) {
        return productComboItemRepository.findProductComboItemByComboAndComponent(combo, component);
    }

    @Override
    public void delete(ProductComboItem productComboItem) {
        productComboItemRepository.delete(productComboItem);
    }
}
