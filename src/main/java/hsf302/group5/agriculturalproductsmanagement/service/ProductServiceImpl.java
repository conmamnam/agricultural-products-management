package hsf302.group5.agriculturalproductsmanagement.service;

import hsf302.group5.agriculturalproductsmanagement.entity.Product;
import hsf302.group5.agriculturalproductsmanagement.repository.ProductRepository;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void addProduct(Product product) {
        productRepository.save(product);
    }
}
