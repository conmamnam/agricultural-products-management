package hsf302.group5.agriculturalproductsmanagement.service;

import hsf302.group5.agriculturalproductsmanagement.entity.User;
import hsf302.group5.agriculturalproductsmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public void addUser(User user) {
        userRepository.save(user);
    }
}
