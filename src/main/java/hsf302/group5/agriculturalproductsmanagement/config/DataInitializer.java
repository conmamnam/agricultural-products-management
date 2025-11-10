package hsf302.group5.agriculturalproductsmanagement.config;

import hsf302.group5.agriculturalproductsmanagement.entity.Category;
import hsf302.group5.agriculturalproductsmanagement.entity.Product;
import hsf302.group5.agriculturalproductsmanagement.entity.Role;
import hsf302.group5.agriculturalproductsmanagement.entity.User;
import hsf302.group5.agriculturalproductsmanagement.repository.CategoryRepository;
import hsf302.group5.agriculturalproductsmanagement.repository.ProductRepository;
import hsf302.group5.agriculturalproductsmanagement.repository.RoleRepository;
import hsf302.group5.agriculturalproductsmanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public DataInitializer(
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            RoleRepository roleRepository,
            UserRepository userRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize data here if needed

        // Role
        Role adminRole = getOrCreateRole("admin");
        getOrCreateRole("user");

        // Admin account
        if (!adminAccountExists()) {
            User admin = new User("Vua trái cây", "0991111111", "Street 000", "admin@gmail.com", "12345678");
            admin.setRole(adminRole);
            userRepository.save(admin);
        }

        // Category
        Category berry = getOrCreateCategory("Quả mọng"); // nho, kiwi, thanh long
        getOrCreateCategory("Quả có múi"); // cam, chanh... (dự phòng)
        Category stoneFruit = getOrCreateCategory("Quả hạch"); // mận
        Category melon = getOrCreateCategory("Quả dưa"); // dưa hấu, dưa lưới
        Category tropical = getOrCreateCategory("Trái cây nhiệt đới"); // chuối, mít, dứa, dừa
        Category pome = getOrCreateCategory("Quả táo – lê – lựu"); // táo, lê, lựu
        Category exotic = getOrCreateCategory("Trái cây vị chua đặc trưng"); // cóc, ổi

        if (productRepository.count() == 0) {
            // ------------------- Product list -------------------

            // Quả mọng
            Product nho = new Product(
                    "Nho Mẫu Đơn Nội Địa Trung",
                    "Nho mẫu đơn nội địa Trung 500g",
                    250000,
                    10,
                    "https://cdnv2.tgdd.vn/bhx-static/bhx/Products/Images/8788/312875/bhx/nho-mau-don-noi-dia-trung-500g_202506160955228843.jpg",
                    berry
            );

            Product kiwi = new Product(
                    "Kiwi Vàng New Zealand",
                    "Kiwi vàng New Zealand hộp 8 trái",
                    180000,
                    12,
                    "https://cdnv2.tgdd.vn/bhx-static/bhx/Products/Images/8788/316300/bhx/kiwi-vang-new-zealand-hop-8-trai_202510071603340318.jpg",
                    berry
            );

            Product thanhLong = new Product(
                    "Thanh Long Ruột Đỏ",
                    "Thanh long ruột đỏ 1kg",
                    30000,
                    25,
                    "https://cdnv2.tgdd.vn/bhx-static/bhx/Products/Images/8788/234065/bhx/thanh-long-ruot-do-1kg_202510211436279432.jpg",
                    berry
            );


            // Quả dưa
            Product duaHau = new Product(
                    "Dưa Hấu Đỏ",
                    "Dưa hấu đỏ (2.5 - 2.7kg)",
                    40000,
                    15,
                    "https://cdnv2.tgdd.vn/bhx-static/bhx/Products/Images/8788/358118/bhx/dua-hau-do-trai-25-27kg-1-trai-clone_202510170938106836.jpg",
                    melon
            );

            Product duaLuoi = new Product(
                    "Dưa Lưới Ruột Cam",
                    "Dưa lưới tròn ruột cam 1 trái",
                    65000,
                    10,
                    "https://cdn.tgdd.vn/Products/Images/8788/226931/bhx/dua-luoi-tron-ruot-cam-202401051458158510.jpg",
                    melon
            );


            // Quả nhiệt đới
            Product chuoi = new Product(
                    "Chuối Nam Mỹ",
                    "Chuối già giống Nam Mỹ 1kg",
                    10000,
                    20,
                    "https://cdnv2.tgdd.vn/bhx-static/bhx/Products/Images/8788/313132/bhx/chuoi-gia-giong-nam-my-1kg_202505201047413543.jpg",
                    tropical
            );

            Product mit = new Product(
                    "Mít Thái",
                    "Mít Thái 1kg",
                    30000,
                    20,
                    "https://cdn.tgdd.vn/Products/Images/8788/313137/bhx/mit-thai-1-kg-202308151310238228.jpg",
                    tropical
            );

            Product duaXiem = new Product(
                    "Dừa Xiêm",
                    "Dừa xiêm tiện lợi 1 trái",
                    15000,
                    30,
                    "https://cdnv2.tgdd.vn/bhx-static/bhx/Products/Images/8788/345511/bhx/dua-xiem-tien-loi-1-trai-clone_202509131300382421.jpg",
                    tropical
            );

            Product thom = new Product(
                    "Thơm (Dứa)",
                    "Thơm trái nguyên vỏ",
                    18000,
                    25,
                    "https://cdnv2.tgdd.vn/bhx-static/bhx/Products/Images/8785/233930/bhx/cdntgddvnproductsimages8785233930bhxthom-trai-nguyen-vo-onl-202312261016260219_202409041613531137.jpg",
                    tropical
            );


            // Quả táo – lê – lựu
            Product tao = new Product(
                    "Táo Nam Phi",
                    "Táo Nam Phi giòn ngọt túi 1kg",
                    85000,
                    10,
                    "https://cdnv2.tgdd.vn/bhx-static/bhx/Products/Images/8788/338730/bhx/tao-nam-phi-gion-ngot-tui-1kg_202508061708408384.jpg",
                    pome
            );

            Product luu = new Product(
                    "Lựu Ngọt",
                    "Lựu ngọt hạt mềm nội địa Trung",
                    95000,
                    10,
                    "https://cdnv2.tgdd.vn/bhx-static/bhx/Products/Images/8788/342636/bhx/luu-ngot-hat-mem-noi-dia-trung_202509121120135311.jpg",
                    pome
            );

            Product le = new Product(
                    "Lê Dương Trung Quốc",
                    "Lê Dương nhập khẩu Trung Quốc 1kg",
                    60000,
                    12,
                    "https://cdnv2.tgdd.vn/bhx-static/bhx/Products/Images/8788/234055/bhx/le-duong-nhap-khau-trung-quoc-1kg_202509221042067717.jpg",
                    pome
            );


            // Quả hạch
            Product man = new Product(
                    "Mận An Phước",
                    "Mận An Phước túi 500g",
                    45000,
                    10,
                    "https://cdn.tgdd.vn/Products/Images/8788/232502/bhx/man-an-phuoc-tui-500g-202012171420096493.jpg",
                    stoneFruit
            );


            // Trái cây chua đặc trưng
            Product coc = new Product(
                    "Cóc Giống Thái",
                    "Cóc giống Thái 500g",
                    35000,
                    15,
                    "https://cdnv2.tgdd.vn/bhx-static/bhx/Products/Images/8788/339552/bhx/coc-giong-thai-500g_202510231555147565.jpg",
                    exotic
            );


            // Save all products
            List<Product> products = Arrays.asList(
                    nho, kiwi, thanhLong,
                    duaHau, duaLuoi,
                    chuoi, mit, duaXiem, thom,
                    tao, luu, le,
                    man, coc
            );
            products.forEach(productRepository::save);
        }

    }

    private Role getOrCreateRole(String roleName) {
        return roleRepository.findAll().stream()
                .filter(role -> roleName.equalsIgnoreCase(role.getRoleName()))
                .findFirst()
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
    }

    private boolean adminAccountExists() {
        return userRepository.findAll().stream()
                .anyMatch(user -> "admin@gmail.com".equalsIgnoreCase(user.getEmail()));
    }

    private Category getOrCreateCategory(String categoryName) {
        return categoryRepository.findAll().stream()
                .filter(category -> categoryName.equalsIgnoreCase(category.getCategoryName()))
                .findFirst()
                .orElseGet(() -> categoryRepository.save(new Category(categoryName)));
    }
}
