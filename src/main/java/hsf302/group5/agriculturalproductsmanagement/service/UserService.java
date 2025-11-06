package hsf302.group5.agriculturalproductsmanagement.service;

import hsf302.group5.agriculturalproductsmanagement.entity.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    User getByPhoneNumber(String phoneNumber);
    void addUser(User user);
    User getUserByEmailAndPassword(String email, String password);
    void updateProfile(User user);
    User findById(int userId);


    List<User> getAll();
}
