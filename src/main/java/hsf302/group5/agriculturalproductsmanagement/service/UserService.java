package hsf302.group5.agriculturalproductsmanagement.service;

import hsf302.group5.agriculturalproductsmanagement.entity.User;

public interface UserService {
    public User getByPhoneNumber(String phoneNumber);
    public void addUser(User user);
    public User getUserByEmailAndPassword(String email, String password);
}
