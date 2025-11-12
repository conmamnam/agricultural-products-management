package hsf302.group5.agriculturalproductsmanagement.controller;

import hsf302.group5.agriculturalproductsmanagement.entity.*;
import hsf302.group5.agriculturalproductsmanagement.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final OrderDetailService orderDetailService;
    private final ProductComboItemService productComboItemService;

    public AdminController(ProductService productService,
                           UserService userService,
                           OrderService orderService,
                           CategoryService categoryService,
                           OrderDetailService orderDetailService,
                           ProductComboItemService productComboItemService) {
        this.productService = productService;
        this.userService = userService;
        this.orderService = orderService;
        this.categoryService = categoryService;
        this.orderDetailService = orderDetailService;
        this.productComboItemService = productComboItemService;
    }





    // ================= Login page =================
    @GetMapping("")
    public String dashboardLoginPage(HttpSession session, Model model) {
        return "admin/admin-login";
    }

    @PostMapping("/login")
    public String adminLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model
    ) {
        User user = userService.getUserByEmailAndPassword(email, password);

        if (user != null) {
            if (user.getRole().getRoleId() == 1) {
                session.setAttribute("adminInfo", user);
                return "redirect:/admin/dashboard";
            }
        }
        model.addAttribute("error", "Thông tin đăng nhập của Admin không đúng!!!");
        return "admin/admin-login";
    }

    private User checkingAdminRole(HttpSession session) {
        User user = (User) session.getAttribute("adminInfo");
        if (user != null && user.getRole().getRoleId() == 1) {
            return user;
        }
        return null;
    }

    // ================= Dashboard =================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        // Kiểm tra quyền admin
        User adminInfo = checkingAdminRole(session);
        if (adminInfo == null) {
            return "redirect:/admin";
        }
        model.addAttribute("adminInfo", adminInfo);

        // Thống kê tổng quan
        model.addAttribute("totalUsers", userService.getAllUser().size());
        model.addAttribute("totalProducts", productService.getAllProducts().size());
        model.addAttribute("totalOrders", orderService.getAll().size());
        model.addAttribute("activeMenu", "dashboard");

        // Lấy 5 user mới nhất
        List<User> recentUsers = userService.getAllUser()
                .stream()
                .sorted((a, b) -> b.getUserId() - a.getUserId())
                .limit(5)
                .collect(Collectors.toList());
        model.addAttribute("recentUsers", recentUsers);

        // Thêm dữ liệu cho biểu đồ đơn hàng theo trạng thái
        model.addAttribute("pendingCount", orderService.countOrdersByStatus("PENDING"));
        model.addAttribute("confirmedCount", orderService.countOrdersByStatus("CONFIRMED"));
        model.addAttribute("shippedCount", orderService.countOrdersByStatus("SHIPPED"));
        model.addAttribute("deliveredCount", orderService.countOrdersByStatus("DELIVERED"));
        model.addAttribute("cancelledCount", orderService.countOrdersByStatus("CANCELLED"));

        return "admin/admin-dashboard";
    }





    // ================= Orders =================
    @GetMapping("/orders")
    public String orders(
            HttpSession session,
            Model model,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo
    ) {
        User adminInfo = checkingAdminRole(session);
        if (adminInfo == null) {
            return "redirect:/admin";
        }

        int pageSize = 10; // số đơn hàng mỗi trang
        Page<Order> page;

        if (email != null && !email.trim().isEmpty()) {
            // Tìm kiếm theo email + phân trang
            page = orderService.searchPaginatedOrdersByEmail(email, pageNo, pageSize);
            model.addAttribute("email", email); // giữ giá trị tìm kiếm
        } else {
            // Lấy tất cả đơn hàng + phân trang
            page = orderService.getPaginatedOrders(pageNo, pageSize);
        }

        model.addAttribute("orders", page.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("activeMenu", "orders");
        model.addAttribute("page", "orders");
        model.addAttribute("adminInfo", adminInfo);

        return "admin/manage-order";
    }


    @GetMapping("/orders/detail/{id}")
    public String orderDetail(@PathVariable("id") int id,
                              HttpSession session,
                              Model model) {
        User adminInfo = checkingAdminRole(session);
        if (adminInfo == null) {
            return "redirect:/admin";
        }

        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(id);
        if (orderDetails == null || orderDetails.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy đơn hàng");
            return "redirect:/admin/orders";
        }

        model.addAttribute("adminInfo", adminInfo);
        model.addAttribute("orderDetails", orderDetails);
        model.addAttribute("activeMenu", "orders");
        return "admin/order-detail"; //
    }
    // Xử lý khi admin bấm “Cập nhật”
    @GetMapping("/orders/update-status")
    public String updateOrderStatusForm(
            @RequestParam("id") int orderId,
            HttpSession session,
            Model model
    ) {
        User adminInfo = checkingAdminRole(session);
        if (adminInfo == null) {
            return "redirect:/admin";
        }
        Order order = orderService.getOrderById(orderId).orElse(null);
        if (order == null) {
            return "redirect:/admin/orders";
        }
        model.addAttribute("order", order);

        model.addAttribute("adminInfo", adminInfo);
        model.addAttribute("statuses", List.of("PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"));
        model.addAttribute("page", "orders");
        return "admin/update-order-status";
    }

    @PostMapping("/orders/update-status")
    public String updateOrderStatus(
            @RequestParam("orderId") int orderId,
            @RequestParam("status") String status,
            RedirectAttributes redirectAttributes
    ) {
        orderService.updateOrderStatusById(orderId, status);
        redirectAttributes.addFlashAttribute("message", "Cập nhật trạng thái thành công!");
        return "redirect:/admin/orders/detail/" + orderId;
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") int id) {
        orderService.getOrderById(id).ifPresent(order -> orderService.getAll().remove(order));
        return "redirect:/admin/orders";
    }





    // ================= Products =================
    @GetMapping("/products")
    public String products(
            HttpSession session,
            Model model,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "keyword", required = false) String searchQuery
    ) {
        User adminInfo = checkingAdminRole(session);
        if (adminInfo == null) {
            return "redirect:/admin";
        }
        model.addAttribute("adminInfo", adminInfo);

        int pageSize = 10; // mỗi trang 10 sản phẩm
        Page<Product> page;

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            // nếu có tìm kiếm
            page = productService.searchPaginatedProducts(searchQuery, pageNo, pageSize);
            model.addAttribute("keyword", searchQuery);
        } else {
            // nếu không tìm kiếm
            page = productService.getPaginatedProducts(pageNo, pageSize);
        }

        List<Product> listProducts = page.getContent();
        model.addAttribute("products", listProducts);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", page.getTotalPages());

        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("activeMenu", "products");
        model.addAttribute("page", "products");

        return "admin/product/manage-product";
    }

    @GetMapping("/products/add")
    public String addProductPage(HttpSession session, Model model) {
        User adminInfo = checkingAdminRole(session);
        if (adminInfo == null) {
            return "redirect:/admin";
        }

        model.addAttribute("adminInfo", adminInfo);
        Product product = new Product();
        product.setCategory(new Category());
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());

        return "admin/product/create";
    }

    @PostMapping("/products/add")
    public String addProduct(
            @Valid Product product,
            BindingResult bindingResult,
            HttpSession session,
            Model model
    ) {
        User adminInfo = checkingAdminRole(session);
        if (adminInfo == null) {
            return "redirect:/admin";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/product/create";
        }

        productService.saveProduct(product);

        return "redirect:/admin/products";
    }

    @GetMapping("/products/combo")
    public String addProductComboPage(HttpSession session, Model model) {
        User adminInfo = checkingAdminRole(session);
        if (adminInfo == null) {
            return "redirect:/admin";
        }

        model.addAttribute("adminInfo", adminInfo);
        List<Product> comboProducts = productService.getProductsByCategoryId(8); // id 8 là dành riêng cho combo
        model.addAttribute("comboProducts", comboProducts);

        return "admin/product/combo";
    }

    @GetMapping("/products/combo/add-item/{id}")
    public String addProductComboItemPage(
            HttpSession session,
            Model model,
            @PathVariable("id") int id
    ) {
        User adminInfo = checkingAdminRole(session);
        if (adminInfo == null) {
            return "redirect:/admin";
        }

        Product productParent = productService.getProductById(id).orElse(null);
        if (productParent == null) {
            return "redirect:/admin/products";
        }

        List<ProductComboItem> comboItems = productComboItemService.getItemsByComboId(id);
        List<Product> productList = productService.getAllProducts();

        model.addAttribute("adminInfo", adminInfo);
        model.addAttribute("productParent", productParent);
        model.addAttribute("comboItems", comboItems);
        model.addAttribute("productList", productList);

        return "admin/product/combo-item-add";
    }

    @PostMapping("/admin/products/combo/add-item")
    public String addProductToCombo(
            @RequestParam("comboId") int comboId,
            @RequestParam("productId") int productId,
            @RequestParam("quantity") int quantity,
            Model model
    ) {
        Product item = productService.getProductById(productId).orElse(null);
        Product productParent = productService.getProductById(comboId).orElse(null);
        if (item == null || productParent == null) {
            model.addAttribute("error", "Lỗi sản phẩm");
            return "redirect:/admin/products/combo/add-item/" + comboId;
        }

        // Kiểm tra nếu sản phẩm đã tồn tại trong combo -> cập nhật số lượng
        ProductComboItem existing = productComboItemService.getByComboAndComponent(productParent, item);
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            productComboItemService.save(existing);
        } else {
            ProductComboItem pci = new ProductComboItem(productParent, item, quantity);
            productComboItemService.save(pci);
        }

        return "redirect:/admin/products/combo/add-item/" + comboId;
    }

    @GetMapping("/admin/products/combo/remove-item/{id}")
    public String removeProductFromCombo(@PathVariable("id") int id) {
        ProductComboItem pci = productComboItemService.getById(id);
        if (pci != null) {
            int comboId = pci.getCombo().getProductId();
            productComboItemService.delete(pci);
            return "redirect:/admin/products/combo/add-item/" + comboId;
        }
        return "redirect:/admin/products/combo";
    }


    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") int id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductPage(
            @PathVariable("id") int id,
            Model model,
            HttpSession session
    ) {
        User adminInfo = checkingAdminRole(session);
        if (adminInfo == null) {
            return "redirect:/admin";
        }
        Product product = productService.getProductById(id).orElse(null);
        if (product == null) {
            return "redirect:/admin/products";
        }
        model.addAttribute("product", product);
        model.addAttribute("adminInfo", adminInfo);
        return "admin/product/edit";
    }

    @PostMapping("/products/edit")
    public String editProductPage(
            @Valid Product product,
            BindingResult bindingResult,
            Model model,
            HttpSession session
    ) {
        User adminInfo = checkingAdminRole(session);
        if (adminInfo == null) {
            return "redirect:/admin";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("product", product);
            model.addAttribute("adminInfo", adminInfo);
            return "admin/product/edit";
        }
        productService.saveProduct(product);
        return "redirect:/admin/products";
    }

    // ================= Users =================
    @GetMapping("/users")
    public String users(
            Model model,
            HttpSession session,
            @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(value = "search", required = false) String searchQuery
    ) {
        User adminInfo = checkingAdminRole(session);
        if (adminInfo == null) {
            return "redirect:/admin";
        }
        model.addAttribute("adminInfo", adminInfo);

        int pageSize = 10; // mỗi trang 10 users
        Page<User> page;

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            // nếu có tìm kiếm
            page = userService.searchPaginatedUsersByFullName(searchQuery, pageNo, pageSize);
            model.addAttribute("search", searchQuery);
        } else {
            // nếu không tìm kiếm
            page = userService.getPaginatedUsers(pageNo, pageSize);
        }

        List<User> listUsers = page.getContent();
        model.addAttribute("users", listUsers);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPage", page.getTotalPages());

        model.addAttribute("newUser", new User());
        model.addAttribute("activeMenu", "users");
        model.addAttribute("page", "users");
        return "admin/user/manage-user";
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

    @GetMapping("/users/deactivate/{id}")
    public String deactivateUser(@PathVariable("id") int id, HttpSession session) {
        User adminInfo = checkingAdminRole(session);
        if (adminInfo == null) {
            return "redirect:/admin";
        }
        User existing = userService.findById(id);
        if (existing != null) {
            existing.setStatus(false);
            userService.saveUser(existing);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/activate/{id}")
    public String activeUser(@PathVariable("id") int id, HttpSession session) {
        User adminInfo = checkingAdminRole(session);
        if (adminInfo == null) {
            return "redirect:/admin";
        }
        User existing = userService.findById(id);
        if (existing != null) {
            existing.setStatus(true);
            userService.saveUser(existing);
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/logout")
    public String adminLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin";
    }
}
