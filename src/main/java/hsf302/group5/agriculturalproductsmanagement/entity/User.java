package hsf302.group5.agriculturalproductsmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private int userId;

    // 🟩 Cho phép cập nhật trong hồ sơ
    @NotBlank(message = "Tên đầy đủ không được để trống")
    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Size(min = 9, max = 15, message = "Số điện thoại không hợp lệ")
    @Column(name = "phone_number", length = 20, nullable = false)
    private String phoneNumber;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Column(length = 255, nullable = false)
    private String address;

    // ⚙️ Không validate khi cập nhật hồ sơ
    @NotBlank(message = "Email không được để trống")
    @Column(length = 100, nullable = false)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean status = true;

    // Sửa thành EAGER để tránh lỗi LazyInitializationException
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
}
