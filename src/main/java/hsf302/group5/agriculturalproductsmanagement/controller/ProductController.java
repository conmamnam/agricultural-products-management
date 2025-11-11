package hsf302.group5.agriculturalproductsmanagement.controller;

import hsf302.group5.agriculturalproductsmanagement.entity.Category;
import hsf302.group5.agriculturalproductsmanagement.entity.Product;
import hsf302.group5.agriculturalproductsmanagement.service.CategoryService;
import hsf302.group5.agriculturalproductsmanagement.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    // 1. [GET] /admin/product (Admin)
    @GetMapping("/admin/product")
    public String listProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "admin/product_list"; // View: admin/product_list.html
    }

    // 2. [GET] /admin/product/add (Admin)
    @GetMapping("/admin/product/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin/product_form"; // View: admin/product_form.html
    }

    // 3. [POST] /admin/product/save (Admin)
    @PostMapping("/admin/product/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            return "admin/product_form";
        }
        productService.saveProduct(product);
        return "redirect:/admin/product";
    }

    // 4. [GET] /admin/product/edit/{id} (Admin)
    @GetMapping("/admin/product/edit/{id}")
    public String showEditProductForm(@PathVariable("id") int id, Model model) {
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            return "admin/product_form"; // Tái sử dụng form
        }
        return "redirect:/admin/product";
    }

    // 5. [POST] /admin/product/edit/{id} (Admin)
    @PostMapping("/admin/product/edit/{id}")
    public String updateProduct(@PathVariable("id") int id, @Valid @ModelAttribute("product") Product product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);
            return "admin/product_form";
        }
        product.setProductId(id); // Đảm bảo ID được giữ nguyên
        productService.saveProduct(product);
        return "redirect:/admin/product";
    }

    // 6. [GET] /admin/product/delete/{id} (Admin)
    @GetMapping("/admin/product/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id) {
        productService.deleteProduct(id);
        return "redirect:/admin/product";
    }

    // 7. [GET] /product/category/{type} (User)
    @GetMapping("/product/category/{type}")
    public String showProductsByCategory(@PathVariable("type") int categoryId, Model model) {
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        model.addAttribute("products", products);
        return "product_category"; // View: product_category.html
    }

    // 8. [GET] /products/{id} (User)
    @GetMapping("/products/{id}")
    public String showProductDetail(@PathVariable("id") int id, Model model) {
        Optional<Product> productOpt = productService.getProductById(id);
        if (productOpt.isEmpty()) {
            return "redirect:/product/all-product";
        }

        Product product = productOpt.get();
        model.addAttribute("product", product);
        List<Product> relatedProducts = productService.getProductsByCategoryId(product.getCategory().getCategoryId())
                .stream()
                .filter(p -> p.getProductId() != product.getProductId())
                .limit(4)
                .toList();
        model.addAttribute("relatedProducts", relatedProducts);
        return "user/product-detail"; // View: user/product-detail.html
    }

    // 8b. [GET] /product/detail/{id} (User - legacy url)
    @GetMapping("/product/detail/{id}")
    public String showProductDetailLegacy(@PathVariable("id") int id) {
        return "redirect:/products/" + id;
    }

    // 9. [GET] /product/all-product (User)
    @GetMapping("/product/all-product")
    public String showAllProducts(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "product_all"; // View: product_all.html
    }

    // 10. [POST] /product/search (User)
    @PostMapping("/product/search")
    public String searchProducts(@RequestParam("keyword") String keyword, Model model) {
        String trimmedKeyword = keyword != null ? keyword.trim() : "";
        if (trimmedKeyword.isEmpty()) {
            return "redirect:/product/all-product";
        }

        List<Product> products = productService.searchProducts(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "product_all"; // Reuse general product list view
    }

    // 11. [GET] /api/products/suggest (User - ajax)
    @GetMapping("/api/products/suggest")
    @ResponseBody
    public List<Map<String, Object>> suggestProducts(@RequestParam("keyword") String keyword) {
        if (keyword == null) {
            return Collections.emptyList();
        }

        String trimmedKeyword = keyword.trim();
        if (trimmedKeyword.isEmpty()) {
            return Collections.emptyList();
        }

        String normalizedKeyword = normalize(trimmedKeyword);
        if (normalizedKeyword.isEmpty()) {
            return Collections.emptyList();
        }

        return productService.getAllProducts()
                .stream()
                .filter(product -> {
                    String normalizedName = normalize(product.getProductName());
                    return normalizedName.contains(normalizedKeyword);
                })
                .limit(8)
                .map(product -> {
                    Map<String, Object> suggestion = new HashMap<>();
                    suggestion.put("id", product.getProductId());
                    suggestion.put("name", product.getProductName());
                    suggestion.put("imageUrl", product.getImageUrl());
                    return suggestion;
                })
                .collect(Collectors.toList());
    }

    private String normalize(String input) {
        String normalized = Normalizer.normalize(input.toLowerCase(Locale.ROOT).trim(), Normalizer.Form.NFD);
        return Pattern.compile("\\p{M}+").matcher(normalized).replaceAll("");
    }
}