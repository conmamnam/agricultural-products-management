package hsf302.group5.agriculturalproductsmanagement.service;

import hsf302.group5.agriculturalproductsmanagement.entity.ProductComboItem;

import java.util.List;

public interface ProductComboItemService {
    public List<ProductComboItem> getItemsByComboId(int comboId);
}
