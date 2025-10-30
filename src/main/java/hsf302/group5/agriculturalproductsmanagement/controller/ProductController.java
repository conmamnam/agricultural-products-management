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
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

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
        Optional<Product> product = productService.getProductById(id);
        if (product.isPresent()) {
            model.addAttribute("product", product.get());
            return "product_detail"; // View: product_detail.html
        }
        return "redirect:/product/all-product";
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
        List<Product> products = productService.searchProducts(keyword);
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        return "product_search_results"; // View: product_search_results.html
    }
}