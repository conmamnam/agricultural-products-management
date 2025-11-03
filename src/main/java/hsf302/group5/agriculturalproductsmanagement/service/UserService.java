package hsf302.group5.agriculturalproductsmanagement.service;

import hsf302.group5.agriculturalproductsmanagement.entity.User;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;

public interface UserService {
    User getByPhoneNumber(String phoneNumber);
    void addUser(User user);
    User getUserByEmailAndPassword(String email, String password);

    // ➕ Thêm 2 hàm mới cho ProfileController
    void updateProfile(@Valid User user);
    User findById(int userId);


    List<User> getAll();
}
