package hsf302.group5.agriculturalproductsmanagement.service;

import hsf302.group5.agriculturalproductsmanagement.entity.Product;
import hsf302.group5.agriculturalproductsmanagement.entity.ProductComboItem;

import java.util.List;

public interface ProductComboItemService {
    ProductComboItem getById(int id);
    public List<ProductComboItem> getItemsByComboId(int comboId);
    void save(ProductComboItem productComboItem);
    ProductComboItem getByComboAndComponent(Product combo, Product component);
    void delete(ProductComboItem productComboItem);
}
