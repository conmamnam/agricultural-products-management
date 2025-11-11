package hsf302.group5.agriculturalproductsmanagement.controller;

import hsf302.group5.agriculturalproductsmanagement.entity.Product;
import hsf302.group5.agriculturalproductsmanagement.service.CategoryService;
import hsf302.group5.agriculturalproductsmanagement.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomepageController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public HomepageController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping({"/", "/index", "/home"})
    public String homepage(HttpSession session,
                           Model model) {
        Object account = session.getAttribute("account");
        model.addAttribute("account", account);

        model.addAttribute("categories", categoryService.getAllCategories());

        List<Product> allProducts = productService.getAllProducts();
        List<Product> highlightProducts = allProducts.stream()
                .limit(4)
                .toList();
        List<Product> featuredProducts = allProducts.stream()
                .skip(highlightProducts.size())
                .limit(5)
                .toList();
        if (featuredProducts.isEmpty()) {
            featuredProducts = highlightProducts;
        }

        model.addAttribute("highlightProducts", highlightProducts);
        model.addAttribute("featuredProducts", featuredProducts);

        return "index";
    }

    @GetMapping("/about-us")
    public String aboutUs(HttpSession session, Model model) {
        Object account = session.getAttribute("account");
        model.addAttribute("account", account);
        return "about-us";
    }
}
