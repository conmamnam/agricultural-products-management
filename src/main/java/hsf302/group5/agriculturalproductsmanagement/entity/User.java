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

    @NotBlank(message = "Tên đầy đủ không được để trống")
    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Column(name = "phone_number", length = 20, nullable = false)
    @Size(min = 9, max = 15, message = "Số điện thoại không hợp lệ")
    private String phoneNumber;

    @Column(length = 100, nullable = false)
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Column(length = 255, nullable = false)
    @Size(min = 8, max = 255, message = "Mật khẩu phải lớn hơn 8 ký tự và tối đa 255")
    private String password;

    @NotBlank(message = "Địa chỉ không được để trống")
    @Column(length = 255, nullable = false)
    private String address;

    @Column(nullable = false)
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;
}
