package hsf302.group5.agriculturalproductsmanagement.controller;

import hsf302.group5.agriculturalproductsmanagement.entity.User;
import hsf302.group5.agriculturalproductsmanagement.entity.Product;
import hsf302.group5.agriculturalproductsmanagement.entity.Order;
import hsf302.group5.agriculturalproductsmanagement.service.CategoryService;
import hsf302.group5.agriculturalproductsmanagement.service.OrderService;
import hsf302.group5.agriculturalproductsmanagement.service.ProductService;
import hsf302.group5.agriculturalproductsmanagement.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;
    private final CategoryService categoryService;

    public AdminController(ProductService productService,
                           UserService userService,
                           OrderService orderService,
                           CategoryService categoryService) {
        this.productService = productService;
        this.userService = userService;
        this.orderService = orderService;
        this.categoryService = categoryService;
    }

    // ================= Dashboard =================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userService.getAll().size());
        model.addAttribute("totalProducts", productService.getAllProducts().size());
        model.addAttribute("totalOrders", orderService.getAll().size());
        model.addAttribute("activeMenu", "dashboard");
        return "admin/admindashboard"; // tên file fragment con, không phải layout
    }

    // ================= Orders =================
    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.getAll());
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("page", "orders");
        return "admin/admindashboard";
    }

    @PostMapping("/orders/update")
    public String updateOrderStatus(@RequestParam("id") int id,
                                    @RequestParam("status") String status) {
        orderService.getOrderById(id).ifPresent(order -> {
            order.setOrderStatus(status); // tên trường status trong entity
            orderService.createOrder(order); // lưu lại
        });
        return "redirect:/admin/orders";
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") int id) {
        orderService.getOrderById(id).ifPresent(order -> orderService.getAll().remove(order));
        return "redirect:/admin/orders";
    }

    // ================= Products =================
    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("activeMenu", "products");
        model.addAttribute("page", "products");
        return "admin/admindashboard";
    }

    @PostMapping("/products/add")
    public String addProduct(@RequestParam("productName") String productName,
                             @RequestParam("price") Double price,
                             @RequestParam("stock") Integer stock,
                             @RequestParam("imageUrl") String imageUrl,
                             @RequestParam("category.categoryId") int categoryId) {

        Product p = new Product();
        p.setProductName(productName);
        p.setPrice(price);
        p.setStock(stock);
        p.setImageUrl(imageUrl);
        p.setCategory(categoryService.getCategoryById(categoryId));
        productService.saveProduct(p);

        return "redirect:/admin/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    // ================= Users =================
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAll());
        model.addAttribute("newUser", new User());
        model.addAttribute("activeMenu", "users");
        model.addAttribute("page", "users");
        return "admin/admindashboard";
    }

    @PostMapping("/users/add")
    public String addUser(@ModelAttribute("newUser") User newUser) {
        userService.addUser(newUser);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/update")
    public String updateUser(@RequestParam("userId") int userId,
                             @RequestParam("fullName") String fullName,
                             @RequestParam("email") String email,
                             @RequestParam("phoneNumber") String phoneNumber) {
        User existing = userService.findById(userId);
        if (existing != null) {
            existing.setFullName(fullName);
            existing.setEmail(email);
            existing.setPhoneNumber(phoneNumber);
            userService.updateProfile(existing);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") int id) {
        User existing = userService.findById(id);
        if (existing != null) {
            userService.getAll().remove(existing);
        }
        return "redirect:/admin/users";
    }
}
